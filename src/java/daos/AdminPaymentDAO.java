package daos;

import dto.AdminPaymentRow;
import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.PaymentType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import models.Payment;

public class AdminPaymentDAO {
    private static final String SELECT = "SELECT p, r, a FROM Payment p JOIN p.rental r JOIN r.customer a ";
    public List<AdminPaymentRow> search(EntityManager em, String keyword, PaymentMethod method, PaymentType type, PaymentStatus status) {
        String key = trim(keyword).toLowerCase(); StringBuilder jpql = new StringBuilder(SELECT + "WHERE 1 = 1 ");
        if (!key.isEmpty()) jpql.append("AND (LOWER(p.paymentId) LIKE :keyword OR LOWER(p.rentalId) LIKE :keyword OR LOWER(a.fullName) LIKE :keyword OR LOWER(a.email) LIKE :keyword OR LOWER(COALESCE(p.transactionCode, '')) LIKE :keyword) ");
        if (method != null) jpql.append("AND p.paymentMethod = :method "); if (type != null) jpql.append("AND p.paymentType = :type "); if (status != null) jpql.append("AND p.status = :status ");
        jpql.append("ORDER BY p.paymentDate DESC, p.paymentId DESC");
        TypedQuery<Object[]> query = em.createQuery(jpql.toString(), Object[].class);
        if (!key.isEmpty()) query.setParameter("keyword", "%" + key + "%"); if (method != null) query.setParameter("method", method); if (type != null) query.setParameter("type", type); if (status != null) query.setParameter("status", status);
        return map(query.getResultList());
    }
    public AdminPaymentRow findDetail(EntityManager em, String paymentId) {
        List<Object[]> rows = em.createQuery(SELECT + "WHERE p.paymentId = :paymentId", Object[].class).setParameter("paymentId", trim(paymentId)).setMaxResults(1).getResultList();
        return rows.isEmpty() ? null : map(rows).get(0);
    }
    public Payment findForUpdate(EntityManager em, String paymentId) { return em.find(Payment.class, trim(paymentId), LockModeType.PESSIMISTIC_WRITE); }
    private List<AdminPaymentRow> map(List<Object[]> rows) { List<AdminPaymentRow> result = new ArrayList<>(); for (Object[] row : rows) { Payment p = (Payment) row[0]; models.Account a = (models.Account) row[2]; result.add(new AdminPaymentRow(p.getPaymentId(), p.getRentalId(), a.getFullName(), a.getEmail(), p.getAmount(), p.getPaymentMethod(), p.getPaymentType(), p.getStatus(), p.getTransactionCode(), p.getPaymentDate())); } return result; }
    private String trim(String value) { return value == null ? "" : value.trim(); }
}
