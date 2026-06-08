package models;

import java.io.Serializable;

/**
 * Entity class representing a Category
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private String categoryId;
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId='" + categoryId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

