package daos;

import models.Account;
import enums.Role;
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
     * Check if email already exists
     * @param email User email
     * @return true if email exists, false otherwise
     */
    @Override
    public boolean isEmailExists(String email) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM Account WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);

            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return false;
    }

    /**
     * Create new account
     * @param account Account object to create
     * @return true if created successfully, false otherwise
     */
    @Override
    public boolean createAccount(Account account) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "INSERT INTO Account (email, password, full_name, phone, is_verified, role, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, account.getEmail());
            stmt.setString(2, account.getPassword());
            stmt.setString(3, account.getFullName());
            stmt.setString(4, account.getPhone());
            stmt.setBoolean(5, account.getIsVerified() != null ? account.getIsVerified() : true);
            stmt.setString(6, account.getRole() != null ? account.getRole().getValue() : Role.CUSTOMER.getValue());
            stmt.setString(7, account.getStatus() != null ? account.getStatus() : "ACTIVE");

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
     * Update account password by email
     * @param email User email
     * @param newPassword New password
     * @return true if updated successfully, false otherwise
     */
    @Override
    public boolean updatePasswordByEmail(String email, String newPassword) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "UPDATE Account SET password = ? WHERE email = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);
            stmt.setString(2, email);

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

