package services;

import daos.IReturnDAO;
import daos.ReturnDAO;
import dto.ReturnConfirmationResult;
import enums.ExtraChargeStatus;
import enums.ExtraChargeType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import models.IncidentReport;
import models.ExtraCharge;
import models.Payment;
import models.Rental;
import models.RentalStatusHistory;
import models.Vehicle;
import models.VehicleMaintenance;
import utils.JPAUtil;

public class ReturnService {
    private final IReturnDAO returnDAO;
    private final ExtraChargeService extraChargeService = new ExtraChargeService();

    public ReturnService() { this(new ReturnDAO()); }
    public ReturnService(IReturnDAO returnDAO) { this.returnDAO = returnDAO; }

    public List<ReturnRentalDTO> searchRentedRentals(String keyword, Date endDate) {
        return returnDAO.searchRentedRentals(keyword, endDate);
    }

    public ReturnRentalDTO findRentalDetail(String rentalId) {
        return returnDAO.findRentalDetail(rentalId);
    }

    public ReturnConfirmationResult confirmReturn(String rentalId, int batteryLevel, VehicleCondition condition,
            PaymentMethod extraChargePaymentMethod, BigDecimal damageFee,
            String notes, String damageDescription, IncidentSeverity severity) {
        validateInput(batteryLevel, condition, damageDescription, severity);
        final BigDecimal damageFeeAmount = damageFee == null ? BigDecimal.ZERO : damageFee;
        if (damageFeeAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Damage fee khong duoc am.");
        }
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
            List<ExtraCharge> extraCharges = new ArrayList<>();
            ExtraCharge lateCharge = extraChargeService.createCharge(em, rental.getRentalId(), null,
                    ExtraChargeType.LATE_FEE, lateFee, "Late return fee",
                    initialChargeStatus(lateFee, extraChargePaymentMethod));
            if (lateCharge != null) {
                extraCharges.add(lateCharge);
            }

            BigDecimal normalizedDamageFee = damaged ? damageFeeAmount : BigDecimal.ZERO;
            if (damaged) {
                String description = combineDescription(damageDescription, notes);
                String incidentId = UUID.randomUUID().toString();
                em.persist(new IncidentReport(incidentId, rental.getRentalId(),
                        vehicle.getVehicleId(), description, severity, now()));
                em.persist(new VehicleMaintenance(UUID.randomUUID().toString(), vehicle.getVehicleId(),
                        description, now(), MaintenanceStatus.PENDING));
                ExtraCharge damageCharge = extraChargeService.createCharge(em, rental.getRentalId(), incidentId,
                        ExtraChargeType.DAMAGE_FEE, normalizedDamageFee, description,
                        initialChargeStatus(normalizedDamageFee, extraChargePaymentMethod));
                if (damageCharge != null) {
                    extraCharges.add(damageCharge);
                }
                vehicle.setStatus(VehicleStatus.MAINTENANCE);
            } else {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
            }
            PaymentMethod method = extraChargePaymentMethod == null ? PaymentMethod.CASH : extraChargePaymentMethod;
            BigDecimal extraChargeTotal = totalExtraCharge(extraCharges);
            String extraChargeOrderId = createExtraChargePaymentIfNeeded(em, rental.getRentalId(), extraCharges, method);
            em.persist(new RentalStatusHistory(UUID.randomUUID().toString(), rental.getRentalId(),
                    RentalStatus.COMPLETED, now()));
            return new ReturnConfirmationResult(damaged, lateFee, normalizedDamageFee, method,
                    extraChargeOrderId, extraChargeTotal);
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
        updateExtraChargeVNPayPayment(orderId, transactionNo, PaymentStatus.SUCCESS);
    }

    public void failLateFeeVNPayPayment(String orderId, String transactionNo) {
        updateExtraChargeVNPayPayment(orderId, transactionNo, PaymentStatus.FAILED);
    }

    private ExtraChargeStatus initialChargeStatus(BigDecimal amount, PaymentMethod method) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ExtraChargeStatus.CANCELLED;
        }
        return method == PaymentMethod.VNPAY ? ExtraChargeStatus.PENDING : ExtraChargeStatus.PAID;
    }

    private String createExtraChargePaymentIfNeeded(EntityManager em, String rentalId,
            List<ExtraCharge> charges, PaymentMethod paymentMethod) {
        BigDecimal total = totalExtraCharge(charges);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        PaymentMethod method = paymentMethod == null ? PaymentMethod.CASH : paymentMethod;
        PaymentStatus status = method == PaymentMethod.CASH ? PaymentStatus.SUCCESS : PaymentStatus.PENDING;
        String orderId = (method == PaymentMethod.CASH ? "CASH_CHARGE_" : "CHARGE_") + System.currentTimeMillis();
        if (method == PaymentMethod.CASH) {
            for (ExtraCharge charge : charges) {
                createPayment(em, rentalId, charge.getAmount(), method, status,
                        mapPaymentType(charge.getChargeType()), charge.getChargeId(), orderId + "_" + charge.getChargeId());
            }
            return null;
        }

        PaymentType paymentType = charges.size() == 1 ? mapPaymentType(charges.get(0).getChargeType()) : PaymentType.OTHER;
        String chargeId = charges.size() == 1 ? charges.get(0).getChargeId() : null;
        createPayment(em, rentalId, total, method, status, paymentType, chargeId, orderId);
        return orderId;
    }

    private Payment createPayment(EntityManager em, String rentalId, BigDecimal amount, PaymentMethod method,
            PaymentStatus status, PaymentType type, String chargeId, String orderId) {
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setRentalId(rentalId);
        payment.setAmount(amount.doubleValue());
        payment.setPaymentMethod(method);
        payment.setPaymentType(type);
        payment.setChargeId(chargeId);
        payment.setStatus(status);
        payment.setTransactionCode(orderId);
        payment.setPaymentDate(status == PaymentStatus.PENDING ? null : now());
        em.persist(payment);
        return payment;
    }

    private void updateExtraChargeVNPayPayment(String orderId, String transactionNo, PaymentStatus status) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return;
        }
        JPAUtil.executeInTransaction(em -> {
            List<Payment> payments = em.createQuery(
                    "SELECT p FROM Payment p WHERE p.transactionCode = :orderId "
                    + "AND p.paymentMethod = :method AND p.status = :pending",
                    Payment.class)
                    .setParameter("orderId", orderId)
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
                updateRelatedExtraCharges(em, payment, status);
            }
            return null;
        });
    }

    private void updateRelatedExtraCharges(EntityManager em, Payment payment, PaymentStatus paymentStatus) {
        if (payment.getChargeId() != null) {
            ExtraCharge charge = extraChargeService.findForUpdate(em, payment.getChargeId());
            updateChargeAfterPayment(charge, paymentStatus);
            return;
        }
        List<ExtraCharge> charges = em.createQuery(
                "SELECT c FROM ExtraCharge c WHERE c.rentalId = :rentalId AND c.status = :status",
                ExtraCharge.class)
                .setParameter("rentalId", payment.getRentalId())
                .setParameter("status", ExtraChargeStatus.PENDING)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
        for (ExtraCharge charge : charges) {
            updateChargeAfterPayment(charge, paymentStatus);
        }
    }

    private void updateChargeAfterPayment(ExtraCharge charge, PaymentStatus paymentStatus) {
        if (paymentStatus == PaymentStatus.SUCCESS) {
            extraChargeService.markPaid(charge);
        } else {
            extraChargeService.markUnpaid(charge);
        }
    }

    private BigDecimal totalExtraCharge(List<ExtraCharge> charges) {
        BigDecimal total = BigDecimal.ZERO;
        if (charges != null) {
            for (ExtraCharge charge : charges) {
                if (charge != null && charge.getAmount() != null) {
                    total = total.add(charge.getAmount());
                }
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private PaymentType mapPaymentType(ExtraChargeType type) {
        if (type == ExtraChargeType.DAMAGE_FEE) {
            return PaymentType.DAMAGE_FEE;
        }
        if (type == ExtraChargeType.CLEANING_FEE) {
            return PaymentType.CLEANING_FEE;
        }
        if (type == ExtraChargeType.LOST_ACCESSORY) {
            return PaymentType.LOST_ACCESSORY;
        }
        if (type == ExtraChargeType.LATE_FEE) {
            return PaymentType.LATE_FEE;
        }
        return PaymentType.OTHER;
    }

    private Timestamp now() { return new Timestamp(System.currentTimeMillis()); }
}
