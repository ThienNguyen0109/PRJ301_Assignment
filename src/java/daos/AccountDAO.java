package daos;

import models.Account;
import models.Role;
import utils.DBUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Account entity
 * Handles all database operations related to Account
 */
public class AccountDAO implements IAccountDAO {
    private static final Logger LOGGER = Logger.getLogger(AccountDAO.class.getName());

    /**
     * Find account by email and password (for login)
     * @param email User email
     * @param password User password
     * @return Account object if found and credentials match, null otherwise
     */
    public Account getAccountByEmailAndPassword(String email, String password) {
        Account account = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT account_id, email, password, full_name, phone, is_verified, role, status, created_at " +
                         "FROM Account WHERE email = ? AND password = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            if (rs.next()) {
                account = new Account();
                account.setAccountId(rs.getString("account_id"));
                account.setEmail(rs.getString("email"));
                account.setPassword(rs.getString("password"));
                account.setFullName(rs.getString("full_name"));
                account.setPhone(rs.getString("phone"));
                account.setIsVerified(rs.getBoolean("is_verified"));
                account.setRole(Role.fromValue(rs.getString("role")));
                account.setStatus(rs.getString("status"));
                account.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return account;
    }

    /**
     * Find account by email only
     * @param email User email
     * @return Account object if found, null otherwise
     */
    public Account getAccountByEmail(String email) {
        Account account = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT account_id, email, password, full_name, phone, is_verified, role, status, created_at " +
                         "FROM Account WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                account = new Account();
                account.setAccountId(rs.getString("account_id"));
                account.setEmail(rs.getString("email"));
                account.setPassword(rs.getString("password"));
                account.setFullName(rs.getString("full_name"));
                account.setPhone(rs.getString("phone"));
                account.setIsVerified(rs.getBoolean("is_verified"));
                account.setRole(Role.fromValue(rs.getString("role")));
                account.setStatus(rs.getString("status"));
                account.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return account;
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
