package services;

import daos.AdminCategoryDAO;
import java.util.List;
import java.util.UUID;
import models.Category;
import utils.JPAUtil;

public class AdminCategoryService {
    private final AdminCategoryDAO categoryDAO = new AdminCategoryDAO();

    public List<Category> search(String keyword) {
        return JPAUtil.execute(em -> categoryDAO.search(em, keyword));
    }

    public Category findById(String categoryId) {
        return JPAUtil.execute(em -> categoryDAO.findById(em, categoryId));
    }

    public void create(String name) {
        validateName(name);
        JPAUtil.executeInTransaction(em -> {
            if (categoryDAO.nameExists(em, name, null)) {
                throw new IllegalArgumentException("Category name already exists.");
            }
            Category category = new Category(UUID.randomUUID().toString(), trim(name));
            categoryDAO.create(em, category);
            return null;
        });
    }

    public void update(String categoryId, String name) {
        required(categoryId, "Category ID");
        validateName(name);
        JPAUtil.executeInTransaction(em -> {
            Category category = categoryDAO.findById(em, categoryId);
            if (category == null) {
                throw new IllegalArgumentException("Category not found.");
            }
            if (categoryDAO.nameExists(em, name, categoryId)) {
                throw new IllegalArgumentException("Category name already exists.");
            }
            category.setName(trim(name));
            return null;
        });
    }

    public void delete(String categoryId) {
        required(categoryId, "Category ID");
        JPAUtil.executeInTransaction(em -> {
            Category category = categoryDAO.findById(em, categoryId);
            if (category == null) {
                throw new IllegalArgumentException("Category not found.");
            }
            if (categoryDAO.hasModels(em, categoryId)) {
                throw new IllegalStateException("Cannot delete a category that is used by vehicle models.");
            }
            categoryDAO.delete(em, category);
            return null;
        });
    }

    private void validateName(String name) {
        required(name, "Category name");
        if (trim(name).length() > 100) {
            throw new IllegalArgumentException("Category name must be at most 100 characters.");
        }
    }

    private void required(String value, String label) {
        if (trim(value).isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
