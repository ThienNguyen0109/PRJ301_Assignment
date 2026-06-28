package daos;

import enums.Role;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import models.Account;
import models.Wallet;

public class AdminAccountDAO {

    public List<Account> search(EntityManager em, String keyword, Role role, String status) {
        StringBuilder jpql = new StringBuilder("SELECT a FROM Account a WHERE 1 = 1 ");
        if (!isBlank(keyword)) {
            jpql.append("AND (LOWER(a.fullName) LIKE :keyword OR LOWER(a.email) LIKE :keyword OR a.phone LIKE :phone) ");
        }
        if (role != null) {
            jpql.append("AND a.role = :role ");
        }
        if (!isBlank(status)) {
            jpql.append("AND a.status = :status ");
        }
        jpql.append("ORDER BY a.createdAt DESC, a.fullName ASC");

        TypedQuery<Account> query = em.createQuery(jpql.toString(), Account.class);
        if (!isBlank(keyword)) {
            String normalized = "%" + keyword.trim().toLowerCase() + "%";
            query.setParameter("keyword", normalized);
            query.setParameter("phone", "%" + keyword.trim() + "%");
        }
        if (role != null) {
            query.setParameter("role", role);
        }
        if (!isBlank(status)) {
            query.setParameter("status", status.trim());
        }
        return query.getResultList();
    }

    public Account findById(EntityManager em, String accountId) {
        return accountId == null ? null : em.find(Account.class, accountId);
    }

    public boolean emailExists(EntityManager em, String email, String excludeAccountId) {
        String jpql = "SELECT COUNT(a) FROM Account a WHERE LOWER(a.email) = :email "
                + (isBlank(excludeAccountId) ? "" : "AND a.accountId <> :excludeId");
        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                .setParameter("email", email == null ? "" : email.trim().toLowerCase());
        if (!isBlank(excludeAccountId)) {
            query.setParameter("excludeId", excludeAccountId.trim());
        }
        return query.getSingleResult() > 0;
    }

    public void create(EntityManager em, Account account) {
        em.persist(account);
    }

    public Account update(EntityManager em, Account account) {
        return em.merge(account);
    }

    public void createWallet(EntityManager em, Wallet wallet) {
        em.persist(wallet);
    }

    public boolean hasWallet(EntityManager em, String accountId) {
        Long count = em.createQuery("SELECT COUNT(w) FROM Wallet w WHERE w.accountId = :accountId", Long.class)
                .setParameter("accountId", accountId)
                .getSingleResult();
        return count > 0;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
