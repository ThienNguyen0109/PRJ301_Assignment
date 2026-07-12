package daos;

import models.Account;

/**
 * Interface for Account Data Access Object
 * Defines contract for all account database operations
 */
public interface IAccountDAO {
    
    /**
     * Find account by email and password (for login)
     * @param email User email
     * @param password User password
     * @return Account object if found and credentials match, null otherwise
     */
    Account getAccountByEmailAndPassword(String email, String password);
    
    /**
     * Find account by email only
     * @param email User email
     * @return Account object if found, null otherwise
     */
    Account getAccountByEmail(String email);
    
    /**
     * Check if email already exists
     * @param email User email
     * @return true if email exists, false otherwise
     */
    boolean isEmailExists(String email);
    
    /**
     * Create new account
     * @param account Account object to create
     * @return true if created successfully, false otherwise
     */
    boolean createAccount(Account account);

    /**
     * Update account password by email
     * @param email User email
     * @param newPassword New password
     * @return true if updated successfully, false otherwise
     */
    boolean updatePasswordByEmail(String email, String newPassword);

    /**
     * Update account phone number
     * @param accountId Account id
     * @param phone New phone number
     * @return true if updated successfully, false otherwise
     */
    boolean updatePhone(String accountId, String phone);
}

