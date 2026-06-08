package services;

import dto.BookingDetail;
import dto.BookingQuote;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.RentalStatus;
import enums.VehicleStatus;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Account;
import utils.DBUtils;

/**
 * Handles booking calculation and transactional booking payment.
 */
public class BookingService {
    private static final Logger LOGGER = Logger.getLogger(BookingService.class.getName());
    public static final String INVALID_DISCOUNT_MESSAGE = "Mã giảm giá không hợp lệ, đã hết hạn hoặc đã hết lượt dùng.";
    public static final String INSUFFICIENT_WALLET_MESSAGE = "Số dư ví không đủ để thanh toán.";
    public static final String ACTIVE_RENTAL_MESSAGE = "Bạn đang có một đơn thuê đang hoạt động. Vui lòng hoàn tất hoặc hủy đơn hiện tại trước khi đặt xe mới.";

    public BookingQuote createQuote(String customerId, String vehicleId, Date startDate, Date endDate, String discountCode)
            throws SQLException, ClassNotFoundException {
        validateDates(startDate, endDate);

        try (Connection conn = DBUtils.getConnection()) {
            ensureCustomerHasNoActiveRental(conn, customerId);
            BookingQuote quote = loadVehicleQuote(conn, customerId, vehicleId, startDate, endDate);
            applyDiscount(conn, quote, discountCode, false);
            return quote;
        }
    }

    public BookingDetail payByWallet(Account customer, BookingQuote quote)
            throws SQLException, ClassNotFoundException {
        Connection conn = null;
        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            validateDates(quote.getStartDate(), quote.getEndDate());
            ensureCustomerHasNoActiveRental(conn, customer.getAccountId());
            if (!isVehicleAvailable(conn, quote.getVehicleId(), quote.getStartDate(), quote.getEndDate())) {
                throw new SQLException("Xe đã được đặt trong khoảng thời gian này.");
            }

            BookingQuote freshQuote = loadVehicleQuote(conn, customer.getAccountId(), quote.getVehicleId(),
                    quote.getStartDate(), quote.getEndDate());
            applyDiscount(conn, freshQuote, quote.getDiscountCode(), true);

            WalletSnapshot wallet = loadWalletForUpdate(conn, customer.getAccountId());
            if (wallet == null) {
                throw new SQLException("Không tìm thấy ví của bạn.");
            }
            if (wallet.balance < freshQuote.getFinalAmount()) {
                throw new SQLException(INSUFFICIENT_WALLET_MESSAGE);
            }

            String rentalId = insertRental(conn, freshQuote, RentalStatus.BOOKED);
            if (freshQuote.getDiscountId() != null) {
                insertRentalDiscount(conn, rentalId, freshQuote.getDiscountId());
            }

            updateWalletBalance(conn, wallet.walletId, wallet.balance - freshQuote.getFinalAmount());
            insertWalletTransaction(conn, wallet.walletId, freshQuote.getFinalAmount(),
                    "Thanh toán booking " + rentalId + " - " + freshQuote.getVehicleModelName());
            String paymentId = insertPayment(conn, rentalId, freshQuote.getFinalAmount(),
                    PaymentMethod.WALLET, PaymentStatus.SUCCESS, "WALLET" + System.currentTimeMillis());
            updateVehicleStatus(conn, freshQuote.getVehicleId(), VehicleStatus.RENTED);
            insertRentalStatusHistory(conn, rentalId, RentalStatus.BOOKED);

            conn.commit();
            return buildDetail(rentalId, paymentId, PaymentMethod.WALLET, PaymentStatus.SUCCESS, "WALLET", freshQuote);
        } catch (SQLException | ClassNotFoundException ex) {
            rollbackQuietly(conn);
            throw ex;
        } finally {
            closeQuietly(conn);
        }
    }

    public String createPendingVNPayPayment(BookingQuote quote, String orderId)
            throws SQLException, ClassNotFoundException {
        Connection conn = null;
        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            validateDates(quote.getStartDate(), quote.getEndDate());
            ensureCustomerHasNoActiveRental(conn, quote.getCustomerId());
            if (!isVehicleAvailable(conn, quote.getVehicleId(), quote.getStartDate(), quote.getEndDate())) {
                throw new SQLException("Xe đã được đặt trong khoảng thời gian này.");
            }

            BookingQuote freshQuote = loadVehicleQuote(conn, quote.getCustomerId(), quote.getVehicleId(),
                    quote.getStartDate(), quote.getEndDate());
            applyDiscount(conn, freshQuote, quote.getDiscountCode(), true);

            String rentalId = insertRental(conn, freshQuote, RentalStatus.BOOKED);
            if (freshQuote.getDiscountId() != null) {
                insertRentalDiscount(conn, rentalId, freshQuote.getDiscountId());
            }
            String paymentId = insertPayment(conn, rentalId, freshQuote.getFinalAmount(),
                    PaymentMethod.VNPAY, PaymentStatus.PENDING, orderId);
            insertRentalStatusHistory(conn, rentalId, RentalStatus.BOOKED);

            conn.commit();
            return paymentId;
        } catch (SQLException | ClassNotFoundException ex) {
            rollbackQuietly(conn);
            throw ex;
        } finally {
            closeQuietly(conn);
        }
    }

    public BookingDetail completeVNPayBooking(Account customer, BookingQuote quote, String orderId, String transactionNo)
            throws SQLException, ClassNotFoundException {
        Connection conn = null;
        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            String rentalId = getPendingPaymentRentalId(conn, orderId);
            if (rentalId == null) {
                throw new SQLException("Không tìm thấy payment pending.");
            }

            String paymentId = updatePendingPayment(conn, orderId, PaymentStatus.SUCCESS, transactionNo);
            updateVehicleStatus(conn, quote.getVehicleId(), VehicleStatus.RENTED);

            conn.commit();
            return buildDetail(rentalId, paymentId, PaymentMethod.VNPAY, PaymentStatus.SUCCESS, transactionNo, quote);
        } catch (SQLException | ClassNotFoundException ex) {
            rollbackQuietly(conn);
            throw ex;
        } finally {
            closeQuietly(conn);
        }
    }

    public void failPendingVNPayPayment(String orderId, String transactionNo) {
        Connection conn = null;
        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false);

            String rentalId = getPendingPaymentRentalId(conn, orderId);
            updatePendingPayment(conn, orderId, PaymentStatus.FAILED, transactionNo);
            if (rentalId != null) {
                restoreRentalDiscounts(conn, rentalId);
                updateRentalStatus(conn, rentalId, RentalStatus.CANCELLED);
                insertRentalStatusHistory(conn, rentalId, RentalStatus.CANCELLED);
            }

            conn.commit();
        } catch (Exception ex) {
            rollbackQuietly(conn);
            LOGGER.log(Level.WARNING, "Could not mark VNPay booking payment failed: " + orderId, ex);
        } finally {
            closeQuietly(conn);
        }
    }

    private BookingQuote loadVehicleQuote(Connection conn, String customerId, String vehicleId, Date startDate, Date endDate)
            throws SQLException {
        String sql = "SELECT v.vehicle_id, v.station_id, v.license_plate, s.name AS station_name, " +
                "vm.name AS model_name, vm.price_per_day " +
                "FROM Vehicle v " +
                "INNER JOIN Vehicle_Model vm ON v.model_id = vm.model_id " +
                "INNER JOIN Station s ON v.station_id = s.station_id " +
                "WHERE v.vehicle_id = ? AND v.status = 'AVAILABLE'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Không tìm thấy xe đang khả dụng.");
                }

                int totalDays = (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
                double pricePerDay = rs.getDouble("price_per_day");
                double originalAmount = pricePerDay * totalDays;

                BookingQuote quote = new BookingQuote();
                quote.setCustomerId(customerId);
                quote.setVehicleId(rs.getString("vehicle_id"));
                quote.setStationId(rs.getString("station_id"));
                quote.setStationName(rs.getString("station_name"));
                quote.setLicensePlate(rs.getString("license_plate"));
                quote.setVehicleModelName(rs.getString("model_name"));
                quote.setStartDate(startDate);
                quote.setEndDate(endDate);
                quote.setTotalDays(totalDays);
                quote.setPricePerDay(pricePerDay);
                quote.setOriginalAmount(originalAmount);
                quote.setDiscountAmount(0.0);
                quote.setFinalAmount(originalAmount);
                return quote;
            }
        }
    }

    private void applyDiscount(Connection conn, BookingQuote quote, String discountCode, boolean consume)
            throws SQLException {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            return;
        }

        String sql = "SELECT discount_id, code, discount_percent, quantity " +
                "FROM Discount " +
                "WHERE UPPER(code) = UPPER(?) AND expired_at >= GETDATE() AND quantity > 0";

        if (consume) {
            sql = "SELECT discount_id, code, discount_percent, quantity FROM Discount WITH (UPDLOCK) " +
                    "WHERE UPPER(code) = UPPER(?) AND expired_at >= GETDATE() AND quantity > 0";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, discountCode.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException(INVALID_DISCOUNT_MESSAGE);
                }

                int percent = rs.getInt("discount_percent");
                double discountAmount = quote.getOriginalAmount() * percent / 100.0;
                quote.setDiscountCode(rs.getString("code"));
                quote.setDiscountId(rs.getString("discount_id"));
                quote.setDiscountPercent(percent);
                quote.setDiscountAmount(discountAmount);
                quote.setFinalAmount(Math.max(0.0, quote.getOriginalAmount() - discountAmount));
            }
        }

        if (consume) {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE Discount SET quantity = quantity - 1 WHERE discount_id = ? AND quantity > 0")) {
                stmt.setString(1, quote.getDiscountId());
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException(INVALID_DISCOUNT_MESSAGE);
                }
            }
        }
    }

    private boolean isVehicleAvailable(Connection conn, String vehicleId, Date startDate, Date endDate)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM Rental " +
                "WHERE vehicle_id = ? AND status IN ('BOOKED', 'RENTED') " +
                "AND start_date < ? AND end_date > ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicleId);
            stmt.setDate(2, endDate);
            stmt.setDate(3, startDate);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        }
    }

    private void ensureCustomerHasNoActiveRental(Connection conn, String customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Rental WITH (UPDLOCK, HOLDLOCK) " +
                "WHERE customer_id = ? AND status IN ('BOOKED', 'RENTED')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException(ACTIVE_RENTAL_MESSAGE);
                }
            }
        }
    }

    private WalletSnapshot loadWalletForUpdate(Connection conn, String accountId) throws SQLException {
        String sql = "SELECT wallet_id, balance FROM Wallet WITH (UPDLOCK) WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new WalletSnapshot(rs.getString("wallet_id"), rs.getDouble("balance"));
            }
        }
    }

    private String insertRental(Connection conn, BookingQuote quote, RentalStatus status) throws SQLException {
        String sql = "INSERT INTO Rental (customer_id, vehicle_id, pickup_station_id, start_date, end_date, total_days, total_amount, status) " +
                "OUTPUT INSERTED.rental_id VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, quote.getCustomerId());
            stmt.setString(2, quote.getVehicleId());
            stmt.setString(3, quote.getStationId());
            stmt.setDate(4, quote.getStartDate());
            stmt.setDate(5, quote.getEndDate());
            stmt.setInt(6, quote.getTotalDays());
            stmt.setDouble(7, quote.getFinalAmount());
            stmt.setString(8, status.getValue());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
                throw new SQLException("Không thể tạo booking.");
            }
        }
    }

    private void insertRentalDiscount(Connection conn, String rentalId, String discountId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Rental_Discount (rental_id, discount_id) VALUES (?, ?)")) {
            stmt.setString(1, rentalId);
            stmt.setString(2, discountId);
            stmt.executeUpdate();
        }
    }

    private void updateWalletBalance(Connection conn, String walletId, double balance) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Wallet SET balance = ?, updated_at = GETDATE() WHERE wallet_id = ?")) {
            stmt.setDouble(1, balance);
            stmt.setString(2, walletId);
            stmt.executeUpdate();
        }
    }

    private void insertWalletTransaction(Connection conn, String walletId, double amount, String description)
            throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Wallet_Transaction (wallet_id, amount, type, description) VALUES (?, ?, 'PAYMENT', ?)")) {
            stmt.setString(1, walletId);
            stmt.setDouble(2, amount);
            stmt.setString(3, description);
            stmt.executeUpdate();
        }
    }

    private String insertPayment(Connection conn, String rentalId, double amount, PaymentMethod method,
            PaymentStatus status, String transactionCode) throws SQLException {
        String sql = "INSERT INTO Payment (rental_id, amount, payment_method, status, transaction_code, payment_date) " +
                "OUTPUT INSERTED.payment_id VALUES (?, ?, ?, ?, ?, CASE WHEN ? = 'PENDING' THEN NULL ELSE GETDATE() END)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rentalId);
            stmt.setDouble(2, amount);
            stmt.setString(3, method.getValue());
            stmt.setString(4, status.getValue());
            stmt.setString(5, transactionCode);
            stmt.setString(6, status.getValue());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
                throw new SQLException("Không thể tạo payment.");
            }
        }
    }

    private String getPendingPaymentRentalId(Connection conn, String orderId) throws SQLException {
        String sql = "SELECT rental_id FROM Payment WHERE transaction_code = ? AND status = 'PENDING'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("rental_id");
                }
                return null;
            }
        }
    }

    private String updatePendingPayment(Connection conn, String orderId, PaymentStatus status, String transactionNo)
            throws SQLException {
        String sql = "UPDATE Payment SET status = ?, transaction_code = ?, payment_date = GETDATE() " +
                "OUTPUT INSERTED.payment_id WHERE transaction_code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.getValue());
            stmt.setString(2, transactionNo != null && !transactionNo.isEmpty() ? transactionNo : orderId);
            stmt.setString(3, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
                throw new SQLException("Không tìm thấy payment pending.");
            }
        }
    }

    private void updateRentalStatus(Connection conn, String rentalId, RentalStatus status) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Rental SET status = ? WHERE rental_id = ?")) {
            stmt.setString(1, status.getValue());
            stmt.setString(2, rentalId);
            stmt.executeUpdate();
        }
    }

    private void updateVehicleStatus(Connection conn, String vehicleId, VehicleStatus status) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Vehicle SET status = ? WHERE vehicle_id = ?")) {
            stmt.setString(1, status.getValue());
            stmt.setString(2, vehicleId);
            stmt.executeUpdate();
        }
    }

    private void restoreRentalDiscounts(Connection conn, String rentalId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Discount SET quantity = quantity + 1 " +
                "WHERE discount_id IN (SELECT discount_id FROM Rental_Discount WHERE rental_id = ?)")) {
            stmt.setString(1, rentalId);
            stmt.executeUpdate();
        }
    }

    private void insertRentalStatusHistory(Connection conn, String rentalId, RentalStatus status) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Rental_Status_History (rental_id, status) VALUES (?, ?)")) {
            stmt.setString(1, rentalId);
            stmt.setString(2, status.getValue());
            stmt.executeUpdate();
        }
    }

    private BookingDetail buildDetail(String rentalId, String paymentId, PaymentMethod method,
            PaymentStatus status, String transactionCode, BookingQuote quote) {
        BookingDetail detail = new BookingDetail();
        detail.setRentalId(rentalId);
        detail.setPaymentId(paymentId);
        detail.setPaymentMethod(method);
        detail.setPaymentStatus(status);
        detail.setTransactionCode(transactionCode);
        detail.setQuote(quote);
        return detail;
    }

    private void validateDates(Date startDate, Date endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            throw new SQLException("Vui lòng chọn ngày bắt đầu và ngày kết thúc.");
        }
        if (endDate.before(startDate)) {
            throw new SQLException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not rollback booking transaction", ex);
            }
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.WARNING, "Could not close booking connection", ex);
            }
        }
    }

    private static class WalletSnapshot {
        private final String walletId;
        private final double balance;

        private WalletSnapshot(String walletId, double balance) {
            this.walletId = walletId;
            this.balance = balance;
        }
    }
}
