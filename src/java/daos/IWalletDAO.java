package daos;

import models.Wallet;

/**
 * Interface for Wallet Data Access Object
 * Defines contract for all wallet database operations
 */
public interface IWalletDAO {
    
    /**
     * Create new wallet for account
     * @param wallet Wallet object
     * @return true if created successfully, false otherwise
     */
    boolean createWallet(Wallet wallet);
    
    /**
     * Get wallet by account ID
     * @param accountId Account ID
     * @return Wallet object if found, null otherwise
     */
    Wallet getWalletByAccountId(String accountId);
    
    /**
     * Update wallet balance
     * @param walletId Wallet ID
     * @param newBalance New balance amount
     * @return true if updated successfully, false otherwise
     */
    boolean updateWalletBalance(String walletId, Double newBalance);
}

