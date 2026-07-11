package daos;

import java.util.List;
import javax.persistence.EntityManager;
import models.Category;

public class AdminCategoryDAO {
    public List<Category> search(EntityManager em, String keyword) {
        String key = trim(keyword).toLowerCase();
        return em.createQuery(
                "SELECT c FROM Category c WHERE :key = '' OR LOWER(c.name) LIKE :pattern ORDER BY c.name",
                Category.class)
                .setParameter("key", key)
                .setParameter("pattern", "%" + key + "%")
                .getResultList();
    }

    public Category findById(EntityManager em, String categoryId) {
        String id = trim(categoryId);
        return id.isEmpty() ? null : em.find(Category.class, id);
    }

    public boolean nameExists(EntityManager em, String name, String excludeId) {
        String excludedId = trim(excludeId);
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE LOWER(c.name) = :name "
                + "AND (:excludedId = '' OR c.categoryId <> :excludedId)", Long.class)
                .setParameter("name", trim(name).toLowerCase())
                .setParameter("excludedId", excludedId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public boolean hasModels(EntityManager em, String categoryId) {
        Long count = em.createQuery(
                "SELECT COUNT(m) FROM VehicleModel m WHERE m.categoryId = :categoryId", Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
        return count != null && count > 0;
    }

    public void create(EntityManager em, Category category) {
        em.persist(category);
    }

    public void delete(EntityManager em, Category category) {
        em.remove(category);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
