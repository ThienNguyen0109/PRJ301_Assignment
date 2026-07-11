package daos;

import dto.AdminWalletTransactionRow;
import enums.TransactionType;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import models.WalletTransaction;

public class AdminWalletTransactionDAO {
    private static final String SELECT = "SELECT t, w, a FROM WalletTransaction t JOIN t.wallet w JOIN w.account a ";

    public List<AdminWalletTransactionRow> search(EntityManager em, String keyword, TransactionType type,
            Date startDate, Date endDate) {
        String key = trim(keyword).toLowerCase();
        String jpql = SELECT
                + "WHERE (:key = '' OR LOWER(t.transactionId) LIKE :pattern OR LOWER(a.fullName) LIKE :pattern "
                + "OR LOWER(a.email) LIKE :pattern OR LOWER(COALESCE(t.description, '')) LIKE :pattern) "
                + (type == null ? "" : "AND t.type = :type ")
                + (startDate == null ? "" : "AND t.createdAt >= :startDate ")
                + (endDate == null ? "" : "AND t.createdAt < :endDate ")
                + "ORDER BY t.createdAt DESC";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class)
                .setParameter("key", key)
                .setParameter("pattern", "%" + key + "%");
        if (type != null) {
            query.setParameter("type", type);
        }
        if (startDate != null) {
            query.setParameter("startDate", new java.sql.Timestamp(startDate.getTime()));
        }
        if (endDate != null) {
            query.setParameter("endDate", new java.sql.Timestamp(endDate.toLocalDate().plusDays(1).atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()));
        }
        return map(query.getResultList());
    }

    public AdminWalletTransactionRow findDetail(EntityManager em, String id) {
        List<Object[]> rows = em.createQuery(SELECT + "WHERE t.transactionId = :id", Object[].class)
                .setParameter("id", trim(id))
                .setMaxResults(1)
                .getResultList();
        return rows.isEmpty() ? null : map(rows).get(0);
    }

    private List<AdminWalletTransactionRow> map(List<Object[]> rows) {
        List<AdminWalletTransactionRow> result = new ArrayList<>();
        for (Object[] row : rows) {
            WalletTransaction t = (WalletTransaction) row[0];
            models.Wallet w = (models.Wallet) row[1];
            models.Account a = (models.Account) row[2];
            result.add(new AdminWalletTransactionRow(t.getTransactionId(), w.getWalletId(), a.getFullName(),
                    a.getEmail(), t.getAmount(), t.getType(), t.getDescription(), t.getCreatedAt()));
        }
        return result;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
