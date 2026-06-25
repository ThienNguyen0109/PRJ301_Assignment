package services;

import daos.IReturnDAO;
import daos.ReturnDAO;
import dto.ReturnConfirmationResult;
import dto.ReturnRentalDTO;
import enums.IncidentSeverity;
import enums.MaintenanceStatus;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.PaymentType;
import enums.RentalStatus;
import enums.VehicleCondition;
import enums.VehicleStatus;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import models.IncidentReport;
import models.Payment;
import models.Rental;
import models.RentalStatusHistory;
import models.Vehicle;
import models.VehicleMaintenance;
import utils.JPAUtil;

public class ReturnService {
    private final IReturnDAO returnDAO;

    public ReturnService() { this(new ReturnDAO()); }
    public ReturnService(IReturnDAO returnDAO) { this.returnDAO = returnDAO; }

    public List<ReturnRentalDTO> searchRentedRentals(String keyword, Date endDate) {
        return returnDAO.searchRentedRentals(keyword, endDate);
    }

    public ReturnRentalDTO findRentalDetail(String rentalId) {
        return returnDAO.findRentalDetail(rentalId);
    }

    public ReturnConfirmationResult confirmReturn(String rentalId, int batteryLevel, VehicleCondition condition,
            PaymentMethod lateFeePaymentMethod, String notes, String damageDescription, IncidentSeverity severity) {
        validateInput(batteryLevel, condition, damageDescription, severity);
        return JPAUtil.executeInTransaction(em -> {
            Rental rental = requireRentedRental(em, rentalId);
            Vehicle vehicle = returnDAO.findVehicleForUpdate(em, rental.getVehicleId());
            if (vehicle == null) {
                throw new IllegalStateException("Không tìm thấy xe của đơn thuê.");
            }
            if (vehicle.getStatus() != VehicleStatus.RENTED) {
                throw new IllegalStateException("Chỉ xe ở trạng thái RENTED mới được xác nhận trả.");
            }

            boolean damaged = condition == VehicleCondition.DAMAGED;
            Date actualReturnDate = Date.valueOf(LocalDate.now());
            BigDecimal lateFee = calculateLateFee(rental, vehicle, actualReturnDate);
            vehicle.setBatteryLevel(batteryLevel);
            rental.setActualReturnDate(actualReturnDate);
            rental.setLateFee(lateFee);
            rental.setStatus(RentalStatus.COMPLETED);
            String lateFeeOrderId = createLateFeePaymentIfNeeded(em, rental.getRentalId(), lateFee, lateFeePaymentMethod);
            if (damaged) {
                String description = combineDescription(damageDescription, notes);
                em.persist(new IncidentReport(UUID.randomUUID().toString(), rental.getRentalId(),
                        vehicle.getVehicleId(), description, severity, now()));
                em.persist(new VehicleMaintenance(UUID.randomUUID().toString(), vehicle.getVehicleId(),
                        description, now(), MaintenanceStatus.PENDING));
                vehicle.setStatus(VehicleStatus.MAINTENANCE);
            } else {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
            }
            em.persist(new RentalStatusHistory(UUID.randomUUID().toString(), rental.getRentalId(),
                    RentalStatus.COMPLETED, now()));
            return new ReturnConfirmationResult(damaged, lateFee, lateFeePaymentMethod, lateFeeOrderId);
        });
    }

    private Rental requireRentedRental(EntityManager em, String rentalId) {
        if (rentalId == null || rentalId.trim().isEmpty()) {
            throw new IllegalArgumentException("Rental ID không được để trống.");
        }
        Rental rental = returnDAO.findRentalForUpdate(em, rentalId.trim());
        if (rental == null) {
            throw new IllegalStateException("Không tìm thấy đơn thuê.");
        }
        if (rental.getStatus() != RentalStatus.RENTED) {
            throw new IllegalStateException("Chỉ đơn thuê ở trạng thái RENTED mới được trả xe.");
        }
        return rental;
    }

    private void validateInput(int batteryLevel, VehicleCondition condition, String damage,
            IncidentSeverity severity) {
        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("Mức pin phải từ 0 đến 100.");
        }
        if (condition == null) {
            throw new IllegalArgumentException("Vui lòng chọn tình trạng xe.");
        }
        if (condition == VehicleCondition.DAMAGED) {
            if (damage == null || damage.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập mô tả hư hỏng.");
            }
            if (severity == null) {
                throw new IllegalArgumentException("Vui lòng chọn mức độ hư hỏng.");
            }
        }
    }

    private String combineDescription(String damage, String notes) {
        String result = damage == null ? "" : damage.trim();
        if (notes != null && !notes.trim().isEmpty()) {
            result += " | Ghi chú: " + notes.trim();
        }
        return result;
    }

    private BigDecimal calculateLateFee(Rental rental, Vehicle vehicle, Date actualReturnDate) {
        if (rental.getEndDate() == null || actualReturnDate == null) {
            return BigDecimal.ZERO;
        }
        long lateDays = ChronoUnit.DAYS.between(rental.getEndDate().toLocalDate(), actualReturnDate.toLocalDate());
        if (lateDays <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal pricePerDay = BigDecimal.ZERO;
        if (vehicle.getModel() != null && vehicle.getModel().getPricePerDay() != null) {
            pricePerDay = BigDecimal.valueOf(vehicle.getModel().getPricePerDay());
        }
        return pricePerDay.multiply(BigDecimal.valueOf(lateDays)).setScale(2, RoundingMode.HALF_UP);
    }

    public void completeLateFeeVNPayPayment(String orderId, String transactionNo) {
        updateLateFeeVNPayPayment(orderId, transactionNo, PaymentStatus.SUCCESS);
    }

    public void failLateFeeVNPayPayment(String orderId, String transactionNo) {
        updateLateFeeVNPayPayment(orderId, transactionNo, PaymentStatus.FAILED);
    }

    private String createLateFeePaymentIfNeeded(EntityManager em, String rentalId, BigDecimal lateFee,
            PaymentMethod lateFeePaymentMethod) {
        if (lateFee == null || lateFee.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        PaymentMethod method = lateFeePaymentMethod == null ? PaymentMethod.CASH : lateFeePaymentMethod;
        PaymentStatus status = method == PaymentMethod.CASH ? PaymentStatus.SUCCESS : PaymentStatus.PENDING;
        String orderId = (method == PaymentMethod.CASH ? "CASH_LATE_" : "VNPAY_LATE_") + System.currentTimeMillis();
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setRentalId(rentalId);
        payment.setAmount(lateFee.doubleValue());
        payment.setPaymentMethod(method);
        payment.setPaymentType(PaymentType.LATE_FEE);
        payment.setStatus(status);
        payment.setTransactionCode(orderId);
        payment.setPaymentDate(status == PaymentStatus.PENDING ? null : now());
        em.persist(payment);
        return method == PaymentMethod.VNPAY ? orderId : null;
    }

    private void updateLateFeeVNPayPayment(String orderId, String transactionNo, PaymentStatus status) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return;
        }
        JPAUtil.executeInTransaction(em -> {
            List<Payment> payments = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.transactionCode = :orderId "
                    + "AND p.paymentType = :type AND p.paymentMethod = :method "
                    + "AND p.status = :pending",
                    Payment.class)
                    .setParameter("orderId", orderId)
                    .setParameter("type", PaymentType.LATE_FEE)
                    .setParameter("method", PaymentMethod.VNPAY)
                    .setParameter("pending", PaymentStatus.PENDING)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setMaxResults(1)
                    .getResultList();
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                payment.setStatus(status);
                payment.setTransactionCode(transactionNo != null && !transactionNo.trim().isEmpty()
                        ? transactionNo.trim()
                        : orderId);
                payment.setPaymentDate(now());
            }
            return null;
        });
    }

    private Timestamp now() { return new Timestamp(System.currentTimeMillis()); }
}
