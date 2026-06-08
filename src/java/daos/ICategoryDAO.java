package daos;

import java.util.List;
import models.Category;

/**
 * Interface for Category data access
 */
public interface ICategoryDAO {
    List<Category> getAllCategories();
}

