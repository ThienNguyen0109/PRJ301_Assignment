package services;

import daos.AccountDAO;
import daos.IAccountDAO;
import daos.IWalletDAO;
import daos.WalletDAO;
import models.Account;
import enums.Role;
import models.Wallet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Service for handling user registration with OTP verification
 */
public class RegistrationService {
    private static final Logger LOGGER = Logger.getLogger(RegistrationService.class.getName());

    private IAccountDAO accountDAO = new AccountDAO();
    private IWalletDAO walletDAO = new WalletDAO();

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final int PASSWORD_MIN_LENGTH = 6;

    /**
     * Validate registration input data
     * @param fullName Full name
     * @param email Email address
     * @param password Password
     * @param phone Phone number
     * @return Map containing validation result and error message
     */
    public Map<String, Object> validateRegistrationData(String fullName, String email, String password, String phone) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("message", "");

        if (fullName == null || fullName.trim().isEmpty()) {
            result.put("valid", false);
            result.put("message", "Há» vÃ  tÃªn khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
            return result;
        }

        if (fullName.trim().length() < 3) {
            result.put("valid", false);
            result.put("message", "Há» vÃ  tÃªn pháº£i cÃ³ Ã­t nháº¥t 3 kÃ½ tá»±");
            return result;
        }

        if (email == null || email.trim().isEmpty()) {
            result.put("valid", false);
            result.put("message", "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
            return result;
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            result.put("valid", false);
            result.put("message", "Email khÃ´ng há»£p lá»‡");
            return result;
        }

        if (accountDAO.isEmailExists(email.trim())) {
            result.put("valid", false);
            result.put("message", "Email nÃ y Ä‘Ã£ Ä‘Æ°á»£c Ä‘Äƒng kÃ½");
            return result;
        }

        if (password == null || password.isEmpty()) {
            result.put("valid", false);
            result.put("message", "Máº­t kháº©u khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
            return result;
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            result.put("valid", false);
            result.put("message", "Máº­t kháº©u pháº£i cÃ³ Ã­t nháº¥t " + PASSWORD_MIN_LENGTH + " kÃ½ tá»±");
            return result;
        }

        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.matches("\\d{10,11}")) {
                result.put("valid", false);
                result.put("message", "Sá»‘ Ä‘iá»‡n thoáº¡i khÃ´ng há»£p lá»‡");
                return result;
            }
        }

        return result;
    }

    /**
     * Register account after OTP verification
     * @param fullName Full name
     * @param email Email address
     * @param password Password
     * @param phone Phone number
     * @return Map containing registration result and message
     */
    public Map<String, Object> registerAccount(String fullName, String email, String password, String phone) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "");
        result.put("accountId", null);

        try {
            Account account = new Account();
            account.setFullName(fullName.trim());
            account.setEmail(email.trim());
            account.setPassword(password);
            account.setPhone(phone != null ? phone.trim() : "");
            account.setIsVerified(true);
            account.setRole(Role.CUSTOMER);
            account.setStatus("ACTIVE");

            if (!accountDAO.createAccount(account)) {
                result.put("message", "Lá»—i khi táº¡o tÃ i khoáº£n. Vui lÃ²ng thá»­ láº¡i.");
                return result;
            }

            Account createdAccount = accountDAO.getAccountByEmail(email.trim());
            if (createdAccount == null) {
                result.put("message", "Lá»—i khi láº¥y thÃ´ng tin tÃ i khoáº£n.");
                return result;
            }

            Wallet wallet = new Wallet(createdAccount.getAccountId());
            if (!walletDAO.createWallet(wallet)) {
                LOGGER.log(Level.WARNING, "Failed to create wallet for account: " + createdAccount.getAccountId());
            }

            result.put("success", true);
            result.put("message", "ÄÄƒng kÃ½ thÃ nh cÃ´ng!");
            result.put("accountId", createdAccount.getAccountId());

            LOGGER.log(Level.INFO, "Account registered successfully: " + email);
        } catch (Exception ex) {
            result.put("message", "CÃ³ lá»—i xáº£y ra: " + ex.getMessage());
            LOGGER.log(Level.SEVERE, "Error registering account: " + ex.getMessage(), ex);
        }

        return result;
    }
}

