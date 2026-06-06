package models;

import java.io.Serializable;

/**
 * Entity class representing a Vehicle_Model
 */
public class VehicleModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String modelId;
    private String categoryId;
    private String name;
    private String brand;
    private Integer seatCount;
    private Double pricePerDay;
    private String description;

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
