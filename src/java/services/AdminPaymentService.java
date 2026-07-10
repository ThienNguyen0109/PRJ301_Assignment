package services;

import daos.AdminPaymentDAO;
import dto.AdminPaymentRow;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.PaymentType;
import java.sql.Timestamp;
import java.util.List;
import models.Payment;
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
            return null;
        });
    }
    private <E extends Enum<E>> E parse(String value, Class<E> type, String label) { if (value == null || value.trim().isEmpty() || "ALL".equalsIgnoreCase(value.trim())) return null; try { return Enum.valueOf(type, value.trim().toUpperCase()); } catch (IllegalArgumentException ex) { throw new IllegalArgumentException(label + " is invalid."); } }
    private void required(String value, String label) { if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(label + " is required."); }
}
