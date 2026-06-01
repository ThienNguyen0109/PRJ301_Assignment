package daos;

import models.Wallet;
import utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Wallet entity
 * Handles all database operations related to Wallet
 */
public class WalletDAO implements IWalletDAO {
    private static final Logger LOGGER = Logger.getLogger(WalletDAO.class.getName());

    /**
     * Create new wallet for account
     * @param wallet Wallet object
     * @return true if created successfully, false otherwise
     */
    @Override
    public boolean createWallet(Wallet wallet) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "INSERT INTO Wallet (account_id, balance) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, wallet.getAccountId());
            stmt.setDouble(2, wallet.getBalance());

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
     * Get wallet by account ID
     * @param accountId Account ID
     * @return Wallet object if found, null otherwise
     */
    @Override
    public Wallet getWalletByAccountId(String accountId) {
        Wallet wallet = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT wallet_id, account_id, balance, updated_at FROM Wallet WHERE account_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, accountId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                wallet = new Wallet();
                wallet.setWalletId(rs.getString("wallet_id"));
                wallet.setAccountId(rs.getString("account_id"));
                wallet.setBalance(rs.getDouble("balance"));
                wallet.setUpdatedAt(rs.getTimestamp("updated_at"));
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return wallet;
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
