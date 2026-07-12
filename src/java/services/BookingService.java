package services;

import dto.BookingDetail;
import dto.BookingQuote;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.RentalStatus;
import enums.TransactionType;
import enums.VehicleStatus;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import models.Account;
import models.Discount;
import models.Payment;
import models.Rental;
import models.RentalDiscount;
import models.RentalStatusHistory;
import models.Vehicle;
import models.Wallet;
import models.WalletTransaction;
import realtime.RealtimeEventPublisher;
import utils.JPAUtil;

/**
 * Handles booking calculation and transactional booking payment using JPA.
 */
public class BookingService {
    private static final Logger LOGGER = Logger.getLogger(BookingService.class.getName());
    public static final String INVALID_DISCOUNT_MESSAGE = "Mã giảm giá không hợp lệ, đã hết hạn hoặc đã hết lượt dùng.";
    public static final String INSUFFICIENT_WALLET_MESSAGE = "Số dư ví không đủ để thanh toán.";
    public static final String ACTIVE_RENTAL_MESSAGE = "Bạn đang có một đơn thuê đang hoạt động. Vui lòng hoàn tất hoặc hủy đơn hiện tại trước khi đặt xe mới.";

    public BookingQuote createQuote(String customerId, String vehicleId, Date startDate, Date endDate, String discountCode)
            throws SQLException {
        validateDates(startDate, endDate);

        EntityManager em = null;
        try {
            em = JPAUtil.getEntityManager();
            ensureCustomerHasNoActiveRental(em, customerId, false);
            BookingQuote quote = loadVehicleQuote(em, customerId, vehicleId, startDate, endDate);
            if (!isVehicleAvailable(em, vehicleId, startDate, endDate)) {
                throw new SQLException("Xe da duoc dat trong khoang thoi gian nay.");
            }
            applyDiscount(em, quote, discountCode, false);
            return quote;
        } finally {
            close(em);
        }
    }

    public BookingDetail payByWallet(Account customer, BookingQuote quote)
            throws SQLException {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            validateDates(quote.getStartDate(), quote.getEndDate());
            ensureCustomerHasNoActiveRental(em, customer.getAccountId(), true);
            if (!isVehicleAvailable(em, quote.getVehicleId(), quote.getStartDate(), quote.getEndDate())) {
                throw new SQLException("Xe đã được đặt trong khoảng thời gian này.");
            }

            BookingQuote freshQuote = loadVehicleQuote(em, customer.getAccountId(), quote.getVehicleId(),
                    quote.getStartDate(), quote.getEndDate());
            applyDiscount(em, freshQuote, quote.getDiscountCode(), true);

            Wallet wallet = loadWalletForUpdate(em, customer.getAccountId());
            if (wallet == null) {
                throw new SQLException("Không tìm thấy ví của bạn.");
            }
            if (wallet.getBalance() == null || wallet.getBalance() < freshQuote.getFinalAmount()) {
                throw new SQLException(INSUFFICIENT_WALLET_MESSAGE);
            }

            Rental rental = createRental(em, freshQuote, RentalStatus.BOOKED);
            if (freshQuote.getDiscountId() != null) {
                createRentalDiscount(em, rental.getRentalId(), freshQuote.getDiscountId());
            }

            wallet.setBalance(wallet.getBalance() - freshQuote.getFinalAmount());
            wallet.setUpdatedAt(now());
            createWalletTransaction(em, wallet.getWalletId(), freshQuote.getFinalAmount(),
                    "Thanh toán booking " + rental.getRentalId() + " - " + freshQuote.getVehicleModelName());
            Payment payment = createPayment(em, rental.getRentalId(), freshQuote.getFinalAmount(),
                    PaymentMethod.WALLET, PaymentStatus.SUCCESS, "WALLET" + System.currentTimeMillis());
            updateVehicleStatus(em, freshQuote.getVehicleId(), VehicleStatus.RENTED);
            createRentalStatusHistory(em, rental.getRentalId(), RentalStatus.BOOKED);

            tx.commit();
            publishBookingSuccess(customer.getAccountId(), freshQuote, PaymentMethod.WALLET);
            return buildDetail(rental.getRentalId(), payment.getPaymentId(), PaymentMethod.WALLET,
                    PaymentStatus.SUCCESS, "WALLET", freshQuote);
        } catch (SQLException ex) {
            rollback(tx);
            throw ex;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        } finally {
            close(em);
        }
    }

    public String createPendingVNPayPayment(BookingQuote quote, String orderId)
            throws SQLException {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            validateDates(quote.getStartDate(), quote.getEndDate());
            ensureCustomerHasNoActiveRental(em, quote.getCustomerId(), true);
            if (!isVehicleAvailable(em, quote.getVehicleId(), quote.getStartDate(), quote.getEndDate())) {
                throw new SQLException("Xe đã được đặt trong khoảng thời gian này.");
            }

            BookingQuote freshQuote = loadVehicleQuote(em, quote.getCustomerId(), quote.getVehicleId(),
                    quote.getStartDate(), quote.getEndDate());
            applyDiscount(em, freshQuote, quote.getDiscountCode(), true);

            Rental rental = createRental(em, freshQuote, RentalStatus.BOOKED);
            if (freshQuote.getDiscountId() != null) {
                createRentalDiscount(em, rental.getRentalId(), freshQuote.getDiscountId());
            }
            Payment payment = createPayment(em, rental.getRentalId(), freshQuote.getFinalAmount(),
                    PaymentMethod.VNPAY, PaymentStatus.PENDING, orderId);
            createRentalStatusHistory(em, rental.getRentalId(), RentalStatus.BOOKED);

            tx.commit();
            RealtimeEventPublisher.admin("PAYMENT_PENDING", "Pending VNPay booking",
                    "A customer started a VNPay booking payment.");
            return payment.getPaymentId();
        } catch (SQLException ex) {
            rollback(tx);
            throw ex;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        } finally {
            close(em);
        }
    }

    public BookingDetail completeVNPayBooking(Account customer, BookingQuote quote, String orderId, String transactionNo)
            throws SQLException {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            Payment payment = getPendingPaymentForUpdate(em, orderId);
            if (payment == null) {
                throw new SQLException("Không tìm thấy payment pending.");
            }

            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionCode(transactionNo != null && !transactionNo.isEmpty() ? transactionNo : orderId);
            payment.setPaymentDate(now());
            updateVehicleStatus(em, quote.getVehicleId(), VehicleStatus.RENTED);

            tx.commit();
            publishBookingSuccess(customer.getAccountId(), quote, PaymentMethod.VNPAY);
            return buildDetail(payment.getRentalId(), payment.getPaymentId(), PaymentMethod.VNPAY,
                    PaymentStatus.SUCCESS, transactionNo, quote);
        } catch (SQLException ex) {
            rollback(tx);
            throw ex;
        } catch (RuntimeException ex) {
            rollback(tx);
            throw ex;
        } finally {
            close(em);
        }
    }

    public void failPendingVNPayPayment(String orderId, String transactionNo) {
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = JPAUtil.getEntityManager();
            tx = em.getTransaction();
            tx.begin();

            Payment payment = getPendingPaymentForUpdate(em, orderId);
            if (payment != null) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setTransactionCode(transactionNo != null && !transactionNo.isEmpty() ? transactionNo : orderId);
                payment.setPaymentDate(now());

                restoreRentalDiscounts(em, payment.getRentalId());
                updateRentalStatus(em, payment.getRentalId(), RentalStatus.CANCELLED);
                createRentalStatusHistory(em, payment.getRentalId(), RentalStatus.CANCELLED);
            }

            tx.commit();
            RealtimeEventPublisher.admin("PAYMENT_FAILED", "VNPay payment failed",
                    "A booking payment was marked as failed.");
        } catch (Exception ex) {
            rollback(tx);
            LOGGER.log(Level.WARNING, "Could not mark VNPay booking payment failed: " + orderId, ex);
        } finally {
            close(em);
        }
    }

    private BookingQuote loadVehicleQuote(EntityManager em, String customerId, String vehicleId, Date startDate, Date endDate)
            throws SQLException {
        List<Vehicle> vehicles = em.createQuery(
                "SELECT v FROM Vehicle v " +
                "WHERE v.vehicleId = :vehicleId " +
                "AND (v.status = :availableStatus OR v.status = :rentedStatus)",
                Vehicle.class)
                .setParameter("vehicleId", vehicleId)
                .setParameter("availableStatus", VehicleStatus.AVAILABLE)
                .setParameter("rentedStatus", VehicleStatus.RENTED)
                .setMaxResults(1)
                .getResultList();
        if (vehicles.isEmpty()) {
            throw new SQLException("Không tìm thấy xe đang khả dụng.");
        }

        Vehicle vehicle = vehicles.get(0);
        int totalDays = (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
        double pricePerDay = vehicle.getModel() != null && vehicle.getModel().getPricePerDay() != null
                ? vehicle.getModel().getPricePerDay()
                : 0.0;
        double originalAmount = pricePerDay * totalDays;

        BookingQuote quote = new BookingQuote();
        quote.setCustomerId(customerId);
        quote.setVehicleId(vehicle.getVehicleId());
        quote.setStationId(vehicle.getStationId());
        quote.setLicensePlate(vehicle.getLicensePlate());
        quote.setStationName(vehicle.getStation() != null ? vehicle.getStation().getName() : null);
        quote.setStationAddress(vehicle.getStation() != null ? vehicle.getStation().getAddress() : null);
        quote.setVehicleModelName(vehicle.getModel() != null ? vehicle.getModel().getName() : null);
        quote.setStartDate(startDate);
        quote.setEndDate(endDate);
        quote.setTotalDays(totalDays);
        quote.setPricePerDay(pricePerDay);
        quote.setOriginalAmount(originalAmount);
        quote.setDiscountAmount(0.0);
        quote.setFinalAmount(originalAmount);
        return quote;
    }

    private void applyDiscount(EntityManager em, BookingQuote quote, String discountCode, boolean consume)
            throws SQLException {
        if (discountCode == null || discountCode.trim().isEmpty()) {
            return;
        }

        javax.persistence.TypedQuery<Discount> query = em.createQuery(
                "SELECT d FROM Discount d " +
                "WHERE UPPER(d.code) = UPPER(:code) " +
                "AND d.expiredAt >= :now " +
                "AND d.quantity > 0",
                Discount.class)
                .setParameter("code", discountCode.trim())
                .setParameter("now", now())
                .setMaxResults(1);
        if (consume) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        List<Discount> discounts = query.getResultList();

        if (discounts.isEmpty()) {
            throw new SQLException(INVALID_DISCOUNT_MESSAGE);
        }

        Discount discount = discounts.get(0);
        int percent = discount.getDiscountPercent();
        double discountAmount = quote.getOriginalAmount() * percent / 100.0;
        quote.setDiscountCode(discount.getCode());
        quote.setDiscountId(discount.getDiscountId());
        quote.setDiscountPercent(percent);
        quote.setDiscountAmount(discountAmount);
        quote.setFinalAmount(Math.max(0.0, quote.getOriginalAmount() - discountAmount));

        if (consume) {
            if (discount.getQuantity() == null || discount.getQuantity() <= 0) {
                throw new SQLException(INVALID_DISCOUNT_MESSAGE);
            }
            discount.setQuantity(discount.getQuantity() - 1);
        }
    }

    private boolean isVehicleAvailable(EntityManager em, String vehicleId, Date startDate, Date endDate) {
        Long count = em.createQuery(
                "SELECT COUNT(r) FROM Rental r " +
                "WHERE r.vehicleId = :vehicleId " +
                "AND (r.status = :booked OR r.status = :rented) " +
                "AND r.startDate < :endDate " +
                "AND r.endDate > :startDate",
                Long.class)
                .setParameter("vehicleId", vehicleId)
                .setParameter("booked", RentalStatus.BOOKED)
                .setParameter("rented", RentalStatus.RENTED)
                .setParameter("endDate", endDate)
                .setParameter("startDate", startDate)
                .getSingleResult();
        return count == null || count == 0;
    }

    private void ensureCustomerHasNoActiveRental(EntityManager em, String customerId, boolean lock)
            throws SQLException {
        Query query = em.createQuery(
                "SELECT r FROM Rental r " +
                "WHERE r.customerId = :customerId " +
                "AND (r.status = :booked OR r.status = :rented)")
                .setParameter("customerId", customerId)
                .setParameter("booked", RentalStatus.BOOKED)
                .setParameter("rented", RentalStatus.RENTED)
                .setMaxResults(1);
        if (lock) {
            query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        }
        if (!query.getResultList().isEmpty()) {
            throw new SQLException(ACTIVE_RENTAL_MESSAGE);
        }
    }

    private Wallet loadWalletForUpdate(EntityManager em, String accountId) {
        List<Wallet> wallets = em.createQuery(
                "SELECT w FROM Wallet w WHERE w.accountId = :accountId",
                Wallet.class)
                .setParameter("accountId", accountId)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1)
                .getResultList();
        return wallets.isEmpty() ? null : wallets.get(0);
    }

    private Rental createRental(EntityManager em, BookingQuote quote, RentalStatus status) {
        Rental rental = new Rental();
        rental.setRentalId(newId());
        rental.setCustomerId(quote.getCustomerId());
        rental.setVehicleId(quote.getVehicleId());
        rental.setPickupStationId(quote.getStationId());
        rental.setStartDate(quote.getStartDate());
        rental.setEndDate(quote.getEndDate());
        rental.setTotalDays(quote.getTotalDays());
        rental.setTotalAmount(BigDecimal.valueOf(quote.getFinalAmount()));
        rental.setStatus(status);
        rental.setCreatedAt(now());
        em.persist(rental);
        em.flush();
        return rental;
    }

    private void createRentalDiscount(EntityManager em, String rentalId, String discountId) {
        RentalDiscount rentalDiscount = new RentalDiscount();
        rentalDiscount.setRentalDiscountId(newId());
        rentalDiscount.setRentalId(rentalId);
        rentalDiscount.setDiscountId(discountId);
        em.persist(rentalDiscount);
    }

    private void createWalletTransaction(EntityManager em, String walletId, double amount, String description) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionId(newId());
        transaction.setWalletId(walletId);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.PAYMENT);
        transaction.setDescription(description);
        transaction.setCreatedAt(now());
        em.persist(transaction);
    }

    private Payment createPayment(EntityManager em, String rentalId, double amount, PaymentMethod method,
            PaymentStatus status, String transactionCode) {
        Payment payment = new Payment();
        payment.setPaymentId(newId());
        payment.setRentalId(rentalId);
        payment.setAmount(amount);
        payment.setPaymentMethod(method);
        payment.setStatus(status);
        payment.setTransactionCode(transactionCode);
        payment.setPaymentDate(status == PaymentStatus.PENDING ? null : now());
        em.persist(payment);
        return payment;
    }

    private Payment getPendingPaymentForUpdate(EntityManager em, String orderId) {
        List<Payment> payments = em.createQuery(
                "SELECT p FROM Payment p WHERE p.transactionCode = :orderId AND p.status = :status",
                Payment.class)
                .setParameter("orderId", orderId)
                .setParameter("status", PaymentStatus.PENDING)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .setMaxResults(1)
                .getResultList();
        return payments.isEmpty() ? null : payments.get(0);
    }

    private void updateRentalStatus(EntityManager em, String rentalId, RentalStatus status) {
        Rental rental = em.find(Rental.class, rentalId, LockModeType.PESSIMISTIC_WRITE);
        if (rental != null) {
            rental.setStatus(status);
        }
    }

    private void updateVehicleStatus(EntityManager em, String vehicleId, VehicleStatus status) {
        Vehicle vehicle = em.find(Vehicle.class, vehicleId, LockModeType.PESSIMISTIC_WRITE);
        if (vehicle != null) {
            vehicle.setStatus(status);
        }
    }

    private void restoreRentalDiscounts(EntityManager em, String rentalId) {
        List<RentalDiscount> rentalDiscounts = em.createQuery(
                "SELECT rd FROM RentalDiscount rd WHERE rd.rentalId = :rentalId",
                RentalDiscount.class)
                .setParameter("rentalId", rentalId)
                .getResultList();
        for (RentalDiscount rentalDiscount : rentalDiscounts) {
            Discount discount = em.find(Discount.class, rentalDiscount.getDiscountId(), LockModeType.PESSIMISTIC_WRITE);
            if (discount != null) {
                discount.setQuantity((discount.getQuantity() == null ? 0 : discount.getQuantity()) + 1);
            }
        }
    }

    private void createRentalStatusHistory(EntityManager em, String rentalId, RentalStatus status) {
        RentalStatusHistory history = new RentalStatusHistory();
        history.setHistoryId(newId());
        history.setRentalId(rentalId);
        history.setStatus(status);
        history.setChangedAt(now());
        em.persist(history);
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

    private void publishBookingSuccess(String customerId, BookingQuote quote, PaymentMethod method) {
        String vehicleName = quote != null && quote.getVehicleModelName() != null
                ? quote.getVehicleModelName()
                : "Vehicle";
        RealtimeEventPublisher.staff("RENTAL_BOOKED", "New paid booking",
                vehicleName + " is waiting for pickup.");
        RealtimeEventPublisher.admin("PAYMENT_CHANGED", "Payment completed",
                "A " + method + " booking payment was completed.");
        RealtimeEventPublisher.admin("ADMIN_METRICS_CHANGED", "Admin metrics updated",
                "Financial and fleet data have changed.");
        RealtimeEventPublisher.all("VEHICLE_AVAILABILITY_CHANGED", "Vehicle availability changed",
                vehicleName + " availability has been updated.");
        RealtimeEventPublisher.customer(customerId, "RENTAL_BOOKED", "Booking confirmed",
                "Your booking has been confirmed.");
    }

    private void validateDates(Date startDate, Date endDate) throws SQLException {
        if (startDate == null || endDate == null) {
            throw new SQLException("Vui lòng chọn ngày bắt đầu và ngày kết thúc.");
        }
        if (endDate.before(startDate)) {
            throw new SQLException("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu.");
        }
    }

    private String newId() {
        return UUID.randomUUID().toString();
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    private void rollback(EntityTransaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }

    private void close(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
