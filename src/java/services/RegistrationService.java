package services;

import daos.AccountDAO;
import daos.IAccountDAO;
import daos.IWalletDAO;
import daos.WalletDAO;
import enums.Role;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import models.Account;
import models.Wallet;

/**
 * Service for handling user registration with OTP verification.
 */
public class RegistrationService {
    private static final Logger LOGGER = Logger.getLogger(RegistrationService.class.getName());
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final int PASSWORD_MIN_LENGTH = 6;

    private IAccountDAO accountDAO = new AccountDAO();
    private IWalletDAO walletDAO = new WalletDAO();

    public Map<String, Object> validateRegistrationData(String fullName, String email, String password, String phone) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("message", "");

        if (isBlank(fullName)) {
            return invalid(result, "Họ và tên không được để trống");
        }

        if (fullName.trim().length() < 3) {
            return invalid(result, "Họ và tên phải có ít nhất 3 ký tự");
        }

        if (isBlank(email)) {
            return invalid(result, "Email không được để trống");
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return invalid(result, "Email không hợp lệ");
        }

        if (accountDAO.isEmailExists(email.trim())) {
            return invalid(result, "Email này đã được đăng ký");
        }

        if (password == null || password.isEmpty()) {
            return invalid(result, "Mật khẩu không được để trống");
        }

        if (password.length() < PASSWORD_MIN_LENGTH) {
            return invalid(result, "Mật khẩu phải có ít nhất " + PASSWORD_MIN_LENGTH + " ký tự");
        }

        if (!isBlank(phone) && !phone.trim().matches("\\d{10,11}")) {
            return invalid(result, "Số điện thoại không hợp lệ");
        }

        return result;
    }

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
                result.put("message", "Lỗi khi tạo tài khoản. Vui lòng thử lại.");
                return result;
            }

            Account createdAccount = accountDAO.getAccountByEmail(email.trim());
            if (createdAccount == null) {
                result.put("message", "Lỗi khi lấy thông tin tài khoản.");
                return result;
            }

            Wallet wallet = new Wallet(createdAccount.getAccountId());
            if (!walletDAO.createWallet(wallet)) {
                LOGGER.log(Level.WARNING, "Failed to create wallet for account: " + createdAccount.getAccountId());
            }

            result.put("success", true);
            result.put("message", "Đăng ký thành công!");
            result.put("accountId", createdAccount.getAccountId());

            LOGGER.log(Level.INFO, "Account registered successfully: " + email);
        } catch (Exception ex) {
            result.put("message", "Có lỗi xảy ra: " + ex.getMessage());
            LOGGER.log(Level.SEVERE, "Error registering account: " + ex.getMessage(), ex);
        }

        return result;
    }

    private Map<String, Object> invalid(Map<String, Object> result, String message) {
        result.put("valid", false);
        result.put("message", message);
        return result;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
