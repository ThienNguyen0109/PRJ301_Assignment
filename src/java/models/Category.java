package models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class representing a Category
 */
@Entity
@Table(name = "Category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "category_id", columnDefinition = "uniqueidentifier")
    private String categoryId;
    @Column(name = "name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
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

