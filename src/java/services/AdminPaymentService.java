package services;

import daos.AdminPaymentDAO;
import dto.AdminPaymentRow;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.PaymentType;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import models.Discount;
import models.ExtraCharge;
import models.Payment;
import models.Rental;
import models.RentalDiscount;
import models.RentalStatusHistory;
import models.Vehicle;
import enums.ExtraChargeStatus;
import enums.RentalStatus;
import enums.VehicleStatus;
import utils.JPAUtil;

/** Controlled administrator operations for payment records. */
public class AdminPaymentService {
    private final AdminPaymentDAO paymentDAO = new AdminPaymentDAO();
    public List<AdminPaymentRow> search(String keyword, String method, String type, String status) { return JPAUtil.execute(em -> paymentDAO.search(em, keyword, parse(method, PaymentMethod.class, "Payment method"), parse(type, PaymentType.class, "Payment type"), parse(status, PaymentStatus.class, "Payment status"))); }
    public AdminPaymentRow findDetail(String paymentId) { return JPAUtil.execute(em -> paymentDAO.findDetail(em, paymentId)); }
    public void markFailed(String paymentId) { updatePendingStatus(paymentId, PaymentStatus.FAILED, false); }
    public void confirmCashPayment(String paymentId) { updatePendingStatus(paymentId, PaymentStatus.SUCCESS, true); }

    private void updatePendingStatus(String paymentId, PaymentStatus target, boolean requireCash) {
        required(paymentId, "Payment ID");
        JPAUtil.executeInTransaction(em -> {
            Payment payment = paymentDAO.findForUpdate(em, paymentId);
            if (payment == null) throw new IllegalArgumentException("Payment not found.");
            if (payment.getStatus() != PaymentStatus.PENDING) throw new IllegalStateException("Only a pending payment can be updated.");
            if (requireCash && payment.getPaymentMethod() != PaymentMethod.CASH) throw new IllegalStateException("Only a pending CASH payment can be confirmed manually.");
            payment.setStatus(target); payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
            if (payment.getPaymentType() == PaymentType.BOOKING) {
                updateBookingAfterPayment(em, payment, target);
            } else {
                updateExtraChargeAfterPayment(em, payment, target);
            }
            return null;
        });
    }

    private void updateBookingAfterPayment(javax.persistence.EntityManager em, Payment payment, PaymentStatus target) {
        Rental rental = em.find(Rental.class, payment.getRentalId(), javax.persistence.LockModeType.PESSIMISTIC_WRITE);
        if (rental == null) {
            throw new IllegalStateException("Related rental not found.");
        }
        Vehicle vehicle = em.find(Vehicle.class, rental.getVehicleId(), javax.persistence.LockModeType.PESSIMISTIC_WRITE);

        if (target == PaymentStatus.SUCCESS) {
            if (rental.getStatus() != RentalStatus.BOOKED) {
                throw new IllegalStateException("Only a BOOKED rental can be confirmed as paid.");
            }
            if (vehicle != null) {
                vehicle.setStatus(VehicleStatus.RENTED);
            }
            return;
        }

        if (target == PaymentStatus.FAILED) {
            if (rental.getStatus() != RentalStatus.BOOKED) {
                throw new IllegalStateException("Only a BOOKED rental can be failed.");
            }
            restoreRentalDiscounts(em, rental.getRentalId());
            rental.setStatus(RentalStatus.CANCELLED);
            if (vehicle != null && vehicle.getStatus() == VehicleStatus.RENTED) {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
            }
            em.persist(new RentalStatusHistory(UUID.randomUUID().toString(), rental.getRentalId(),
                    RentalStatus.CANCELLED, new Timestamp(System.currentTimeMillis())));
        }
    }

    private void updateExtraChargeAfterPayment(javax.persistence.EntityManager em, Payment payment, PaymentStatus target) {
        if (payment.getChargeId() == null || payment.getChargeId().trim().isEmpty()) {
            return;
        }
        ExtraCharge charge = em.find(ExtraCharge.class, payment.getChargeId(), javax.persistence.LockModeType.PESSIMISTIC_WRITE);
        if (charge == null) {
            return;
        }
        if (target == PaymentStatus.SUCCESS) {
            charge.setStatus(ExtraChargeStatus.PAID);
            charge.setPaidAt(new Timestamp(System.currentTimeMillis()));
        } else if (target == PaymentStatus.FAILED && charge.getStatus() == ExtraChargeStatus.PENDING) {
            charge.setStatus(ExtraChargeStatus.UNPAID);
            charge.setPaidAt(null);
        }
    }

    private void restoreRentalDiscounts(javax.persistence.EntityManager em, String rentalId) {
        List<RentalDiscount> rentalDiscounts = em.createQuery(
                "SELECT rd FROM RentalDiscount rd WHERE rd.rentalId = :rentalId", RentalDiscount.class)
                .setParameter("rentalId", rentalId)
                .getResultList();
        for (RentalDiscount rentalDiscount : rentalDiscounts) {
            Discount discount = em.find(Discount.class, rentalDiscount.getDiscountId(), javax.persistence.LockModeType.PESSIMISTIC_WRITE);
            if (discount != null) {
                discount.setQuantity((discount.getQuantity() == null ? 0 : discount.getQuantity()) + 1);
            }
        }
    }

    private <E extends Enum<E>> E parse(String value, Class<E> type, String label) { if (value == null || value.trim().isEmpty() || "ALL".equalsIgnoreCase(value.trim())) return null; try { return Enum.valueOf(type, value.trim().toUpperCase()); } catch (IllegalArgumentException ex) { throw new IllegalArgumentException(label + " is invalid."); } }
    private void required(String value, String label) { if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(label + " is required."); }
}
