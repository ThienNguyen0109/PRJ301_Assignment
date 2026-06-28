package services;

import daos.AdminAccountDAO;
import enums.Role;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import models.Account;
import models.Wallet;
import utils.JPAUtil;

public class AdminAccountService {
    private final AdminAccountDAO accountDAO = new AdminAccountDAO();

    public List<Account> search(String keyword, String roleValue, String status) {
        Role role = isBlank(roleValue) || "ALL".equals(roleValue) ? null : Role.fromValue(roleValue);
        String normalizedStatus = isBlank(status) || "ALL".equals(status) ? null : status.trim();
        return JPAUtil.execute(em -> accountDAO.search(em, keyword, role, normalizedStatus));
    }

    public Account findById(String accountId) {
        return JPAUtil.execute(em -> accountDAO.findById(em, accountId));
    }

    public void create(String fullName, String email, String phone, String password,
            String confirmPassword, String roleValue, String status) {
        validateRequired(fullName, "Full name");
        validateRequired(email, "Email");
        validateRequired(password, "Password");
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password confirmation does not match.");
        }
        Role role = Role.fromValue(roleValue);
        String normalizedStatus = normalizeStatus(status);

        JPAUtil.executeInTransaction(em -> {
            if (accountDAO.emailExists(em, email, null)) {
                throw new IllegalArgumentException("Email already exists.");
            }
            String accountId = UUID.randomUUID().toString();
            Account account = new Account();
            account.setAccountId(accountId);
            account.setFullName(fullName.trim());
            account.setEmail(email.trim().toLowerCase());
            account.setPhone(blankToNull(phone));
            account.setPassword(password);
            account.setRole(role);
            account.setStatus(normalizedStatus);
            account.setIsVerified(true);
            account.setCreatedAt(now());
            accountDAO.create(em, account);

            if (role == Role.CUSTOMER) {
                Wallet wallet = new Wallet(UUID.randomUUID().toString(), accountId, 0.0, now());
                accountDAO.createWallet(em, wallet);
            }
            return null;
        });
    }

    public void update(String accountId, String currentAdminId, String fullName, String phone,
            String roleValue, String status) {
        validateRequired(accountId, "Account ID");
        validateRequired(fullName, "Full name");
        Role role = Role.fromValue(roleValue);
        String normalizedStatus = normalizeStatus(status);

        JPAUtil.executeInTransaction(em -> {
            Account account = accountDAO.findById(em, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Account not found.");
            }
            if (account.getAccountId().equals(currentAdminId) && !"ACTIVE".equals(normalizedStatus)) {
                throw new IllegalArgumentException("Admin cannot lock the current session account.");
            }
            account.setFullName(fullName.trim());
            account.setPhone(blankToNull(phone));
            account.setRole(role);
            account.setStatus(normalizedStatus);
            accountDAO.update(em, account);

            if (role == Role.CUSTOMER && !accountDAO.hasWallet(em, accountId)) {
                accountDAO.createWallet(em, new Wallet(UUID.randomUUID().toString(), accountId, 0.0, now()));
            }
            return null;
        });
    }

    public void updateStatus(String accountId, String currentAdminId, String status) {
        validateRequired(accountId, "Account ID");
        String normalizedStatus = normalizeStatus(status);
        JPAUtil.executeInTransaction(em -> {
            Account account = accountDAO.findById(em, accountId);
            if (account == null) {
                throw new IllegalArgumentException("Account not found.");
            }
            if (account.getAccountId().equals(currentAdminId) && !"ACTIVE".equals(normalizedStatus)) {
                throw new IllegalArgumentException("Admin cannot lock the current session account.");
            }
            account.setStatus(normalizedStatus);
            return null;
        });
    }

    private String normalizeStatus(String status) {
        if (isBlank(status)) {
            return "ACTIVE";
        }
        String normalized = status.trim().toUpperCase();
        if (!"ACTIVE".equals(normalized) && !"INACTIVE".equals(normalized) && !"LOCKED".equals(normalized)) {
            throw new IllegalArgumentException("Invalid account status.");
        }
        return normalized;
    }

    private void validateRequired(String value, String label) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
}
