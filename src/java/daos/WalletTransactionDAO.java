package daos;

import models.WalletTransaction;
import enums.TransactionType;
import utils.DBUtils;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Wallet Transaction entity
 */
public class WalletTransactionDAO {
    private static final Logger LOGGER = Logger.getLogger(WalletTransactionDAO.class.getName());

    /**
     * Create new wallet transaction
     */
    public boolean createTransaction(WalletTransaction transaction) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "INSERT INTO Wallet_Transaction (wallet_id, amount, type, description) " +
                         "VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, transaction.getWalletId());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getType().getValue());
            stmt.setString(4, transaction.getDescription());

            int result = stmt.executeUpdate();
            return result > 0;
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(null, stmt, conn);
        }

        return false;
    }

    /**
     * Get transaction history by wallet ID
     */
    public List<WalletTransaction> getTransactionsByWalletId(String walletId) {
        List<WalletTransaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT transaction_id, wallet_id, amount, type, description, created_at " +
                         "FROM Wallet_Transaction WHERE wallet_id = ? " +
                         "ORDER BY created_at DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, walletId);

            rs = stmt.executeQuery();

            while (rs.next()) {
                WalletTransaction transaction = new WalletTransaction();
                transaction.setTransactionId(rs.getString("transaction_id"));
                transaction.setWalletId(rs.getString("wallet_id"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setType(TransactionType.fromValue(rs.getString("type")));
                transaction.setDescription(rs.getString("description"));
                transaction.setCreatedAt(rs.getTimestamp("created_at"));
                transactions.add(transaction);
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return transactions;
    }

    /**
     * Get transaction by ID
     */
    public WalletTransaction getTransactionById(String transactionId) {
        WalletTransaction transaction = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT transaction_id, wallet_id, amount, type, description, created_at " +
                         "FROM Wallet_Transaction WHERE transaction_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, transactionId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                transaction = new WalletTransaction();
                transaction.setTransactionId(rs.getString("transaction_id"));
                transaction.setWalletId(rs.getString("wallet_id"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setType(TransactionType.fromValue(rs.getString("type")));
                transaction.setDescription(rs.getString("description"));
                transaction.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return transaction;
    }

    /**
     * Close database resources
     */
    private void closeResources(ResultSet rs, PreparedStatement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error closing resources", ex);
        }
    }
}

