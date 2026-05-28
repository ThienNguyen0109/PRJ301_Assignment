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
}
