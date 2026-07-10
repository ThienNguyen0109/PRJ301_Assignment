package daos;

import dto.AdminWalletRow;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import models.WalletTransaction;

public class AdminWalletDAO {
    public List<AdminWalletRow> search(EntityManager em, String keyword) {
        String key = trim(keyword).toLowerCase();
        List<Object[]> rows = em.createQuery("SELECT w, a FROM Wallet w JOIN w.account a WHERE :key = '' OR LOWER(a.fullName) LIKE :pattern OR LOWER(a.email) LIKE :pattern OR a.phone LIKE :pattern ORDER BY w.updatedAt DESC", Object[].class)
                .setParameter("key", key).setParameter("pattern", "%" + key + "%").getResultList();
        return map(rows);
    }
    public AdminWalletRow findDetail(EntityManager em, String walletId) {
        List<Object[]> rows = em.createQuery("SELECT w, a FROM Wallet w JOIN w.account a WHERE w.walletId = :walletId", Object[].class).setParameter("walletId", trim(walletId)).setMaxResults(1).getResultList();
        return rows.isEmpty() ? null : map(rows).get(0);
    }
    public List<WalletTransaction> findTransactions(EntityManager em, String walletId) {
        return em.createQuery("SELECT t FROM WalletTransaction t WHERE t.walletId = :walletId ORDER BY t.createdAt DESC", WalletTransaction.class).setParameter("walletId", walletId).getResultList();
    }
    private List<AdminWalletRow> map(List<Object[]> rows) { List<AdminWalletRow> result = new ArrayList<>(); for (Object[] row : rows) { models.Wallet w = (models.Wallet) row[0]; models.Account a = (models.Account) row[1]; result.add(new AdminWalletRow(w.getWalletId(), w.getAccountId(), a.getFullName(), a.getEmail(), a.getPhone(), w.getBalance(), w.getUpdatedAt())); } return result; }
    private String trim(String value) { return value == null ? "" : value.trim(); }
}
