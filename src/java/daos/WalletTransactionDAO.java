package daos;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.WalletTransaction;
import utils.JPAUtil;

/**
 * Data Access Object for Wallet Transaction entity using JPA.
 */
public class WalletTransactionDAO {
    private static final Logger LOGGER = Logger.getLogger(WalletTransactionDAO.class.getName());

    public boolean createTransaction(WalletTransaction transaction) {
        try {
            return JPAUtil.executeInTransaction(em -> {
                applyTransactionDefaults(transaction);
                em.persist(transaction);
                return true;
            });
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not create wallet transaction with JPA", ex);
            return false;
        }
    }

    public List<WalletTransaction> getTransactionsByWalletId(String walletId) {
        try {
            return JPAUtil.execute(em -> em.createQuery(
                    "SELECT t FROM WalletTransaction t WHERE t.wallet.walletId = :walletId ORDER BY t.createdAt DESC",
                    WalletTransaction.class)
                    .setParameter("walletId", walletId)
                    .getResultList());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not load wallet transactions with JPA", ex);
            return Collections.emptyList();
        }
    }

    public WalletTransaction getTransactionById(String transactionId) {
        try {
            return JPAUtil.execute(em -> em.find(WalletTransaction.class, transactionId));
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not find wallet transaction with JPA", ex);
            return null;
        }
    }

    private void applyTransactionDefaults(WalletTransaction transaction) {
        if (transaction.getTransactionId() == null || transaction.getTransactionId().trim().isEmpty()) {
            transaction.setTransactionId(UUID.randomUUID().toString());
        }
        if (transaction.getCreatedAt() == null) {
            transaction.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        }
    }

}
