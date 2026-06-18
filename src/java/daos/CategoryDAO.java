package daos;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Category;
import utils.JPAUtil;

/**
 * Data Access Object for Category using JPA.
 */
public class CategoryDAO implements ICategoryDAO {
    private static final Logger LOGGER = Logger.getLogger(CategoryDAO.class.getName());

    @Override
    public List<Category> getAllCategories() {
        try {
            return JPAUtil.execute(em -> em.createQuery(
                    "SELECT c FROM Category c ORDER BY c.name",
                    Category.class)
                    .getResultList());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Could not load categories with JPA", ex);
            return Collections.emptyList();
        }
    }
}
