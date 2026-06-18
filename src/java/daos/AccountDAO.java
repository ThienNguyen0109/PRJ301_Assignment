package daos;

import enums.Role;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Account;
import utils.JPAUtil;

/**
 * Data Access Object for Account entity using JPA.
 */
public class AccountDAO implements IAccountDAO {
    private static final Logger LOGGER = Logger.getLogger(AccountDAO.class.getName());

    @Override
    public Account getAccountByEmailAndPassword(String email, String password) {
        try {
            List<Account> accounts = JPAUtil.execute(em -> em.createQuery(
                    "SELECT a FROM Account a WHERE a.email = :email AND a.password = :password",
                    Account.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .setMaxResults(1)
                    .getResultList());
            return accounts.isEmpty() ? null : accounts.get(0);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not find account by email and password with JPA", ex);
            return null;
        }
    }

    @Override
    public Account getAccountByEmail(String email) {
        try {
            List<Account> accounts = JPAUtil.execute(em -> em.createQuery(
                    "SELECT a FROM Account a WHERE a.email = :email",
                    Account.class)
                    .setParameter("email", email)
                    .setMaxResults(1)
                    .getResultList());
            return accounts.isEmpty() ? null : accounts.get(0);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not find account by email with JPA", ex);
            return null;
        }
    }

    @Override
    public boolean isEmailExists(String email) {
        try {
            Long count = JPAUtil.execute(em -> em.createQuery(
                    "SELECT COUNT(a) FROM Account a WHERE a.email = :email",
                    Long.class)
                    .setParameter("email", email)
                    .getSingleResult());
            return count != null && count > 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not check email existence with JPA", ex);
            return false;
        }
    }

    @Override
    public boolean createAccount(Account account) {
        try {
            return JPAUtil.executeInTransaction(em -> {
                applyAccountDefaults(account);
                em.persist(account);
                return true;
            });
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not create account with JPA", ex);
            return false;
        }
    }

    @Override
    public boolean updatePasswordByEmail(String email, String newPassword) {
        try {
            int updated = JPAUtil.executeInTransaction(em -> em.createQuery(
                    "UPDATE Account a SET a.password = :password WHERE a.email = :email")
                    .setParameter("password", newPassword)
                    .setParameter("email", email)
                    .executeUpdate());
            return updated > 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not update account password with JPA", ex);
            return false;
        }
    }

    private void applyAccountDefaults(Account account) {
        if (account.getAccountId() == null || account.getAccountId().trim().isEmpty()) {
            account.setAccountId(UUID.randomUUID().toString());
        }
        if (account.getIsVerified() == null) {
            account.setIsVerified(true);
        }
        if (account.getRole() == null) {
            account.setRole(Role.CUSTOMER);
        }
        if (account.getStatus() == null || account.getStatus().trim().isEmpty()) {
            account.setStatus("ACTIVE");
        }
    }

}
