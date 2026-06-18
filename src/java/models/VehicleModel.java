package models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a Vehicle_Model
 */
@Entity
@Table(name = "Vehicle_Model")
public class VehicleModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "model_id", columnDefinition = "uniqueidentifier")
    private String modelId;
    @Column(name = "category_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String categoryId;
    @Column(name = "name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String name;
    @Column(name = "brand", length = 100, columnDefinition = "NVARCHAR(100)")
    private String brand;
    @Column(name = "seat_count")
    private Integer seatCount;
    @Column(name = "price_per_day", precision = 10, scale = 2)
    private Double pricePerDay;
    @Lob
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", insertable = false, updatable = false)
    private Category category;

    public VehicleModel() {
    }

    public VehicleModel(String categoryId, String name, String brand, Integer seatCount, Double pricePerDay, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.brand = brand;
        this.seatCount = seatCount;
        this.pricePerDay = pricePerDay;
        this.description = description;
    }

    public VehicleModel(String modelId, String categoryId, String name, String brand, Integer seatCount, Double pricePerDay, String description) {
        this.modelId = modelId;
        this.categoryId = categoryId;
        this.name = name;
        this.brand = brand;
        this.seatCount = seatCount;
        this.pricePerDay = pricePerDay;
        this.description = description;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "VehicleModel{" +
                "modelId='" + modelId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", seatCount=" + seatCount +
                ", pricePerDay=" + pricePerDay +
                '}';
    }
}

