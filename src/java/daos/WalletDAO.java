package daos;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Wallet;
import utils.JPAUtil;

/**
 * Data Access Object for Wallet entity using JPA.
 */
public class WalletDAO implements IWalletDAO {
    private static final Logger LOGGER = Logger.getLogger(WalletDAO.class.getName());

    @Override
    public boolean createWallet(Wallet wallet) {
        try {
            return JPAUtil.executeInTransaction(em -> {
                applyWalletDefaults(wallet);
                em.persist(wallet);
                return true;
            });
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not create wallet with JPA", ex);
            return false;
        }
    }

    @Override
    public Wallet getWalletByAccountId(String accountId) {
        try {
            List<Wallet> wallets = JPAUtil.execute(em -> em.createQuery(
                    "SELECT w FROM Wallet w WHERE w.account.accountId = :accountId",
                    Wallet.class)
                    .setParameter("accountId", accountId)
                    .setMaxResults(1)
                    .getResultList());
            return wallets.isEmpty() ? null : wallets.get(0);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not find wallet by account id with JPA", ex);
            return null;
        }
    }

    @Override
    public boolean updateWalletBalance(String walletId, Double newBalance) {
        try {
            int updated = JPAUtil.executeInTransaction(em -> em.createQuery(
                    "UPDATE Wallet w SET w.balance = :balance, w.updatedAt = :updatedAt WHERE w.walletId = :walletId")
                    .setParameter("balance", newBalance)
                    .setParameter("updatedAt", new Timestamp(System.currentTimeMillis()))
                    .setParameter("walletId", walletId)
                    .executeUpdate());
            return updated > 0;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not update wallet balance with JPA", ex);
            return false;
        }
    }

    private void applyWalletDefaults(Wallet wallet) {
        if (wallet.getWalletId() == null || wallet.getWalletId().trim().isEmpty()) {
            wallet.setWalletId(UUID.randomUUID().toString());
        }
        if (wallet.getBalance() == null) {
            wallet.setBalance(0.0);
        }
        if (wallet.getUpdatedAt() == null) {
            wallet.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        }
    }

}
