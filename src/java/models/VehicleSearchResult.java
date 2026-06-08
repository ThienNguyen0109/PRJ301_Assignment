package models;

import java.io.Serializable;

/**
 * DTO for grouped vehicle search results by vehicle model
 */
public class VehicleSearchResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String modelId;
    private String modelName;
    private String brand;
    private Integer remaining;
    private Double pricePerDay;
    private Integer seatCount;
    private String thumbnailImage;
    private String stationId;
    private String stationName;
    private String stationAddress;

    public VehicleSearchResult() {
    }

    public VehicleSearchResult(String modelId, String modelName, String brand, Integer remaining,
                               Double pricePerDay, Integer seatCount, String thumbnailImage) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.brand = brand;
        this.remaining = remaining;
        this.pricePerDay = pricePerDay;
        this.seatCount = seatCount;
        this.thumbnailImage = thumbnailImage;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationAddress() {
        return stationAddress;
    }

    public void setStationAddress(String stationAddress) {
        this.stationAddress = stationAddress;
    }
}
