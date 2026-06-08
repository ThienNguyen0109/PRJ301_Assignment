package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Category;
import utils.DBUtils;

/**
 * Data Access Object for Category
 */
public class CategoryDAO implements ICategoryDAO {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT category_id, name FROM Category ORDER BY name";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getString("category_id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Database driver not found", ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "SQL error: " + ex.getMessage(), ex);
        } finally {
            closeResources(rs, stmt, conn);
        }

        return categories;
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
