package models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class representing a Vehicle
 */
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private String vehicleId;
    private String modelId;
    private String stationId;
    private String licensePlate;
    private String color;
    private Integer batteryLevel;
    private VehicleStatus status;
    private Timestamp createdAt;

    public Vehicle() {
    }

    public Vehicle(String modelId, String stationId, String licensePlate, String color, Integer batteryLevel, VehicleStatus status) {
        this.modelId = modelId;
        this.stationId = stationId;
        this.licensePlate = licensePlate;
        this.color = color;
        this.batteryLevel = batteryLevel;
        this.status = status;
    }

    public Vehicle(String vehicleId, String modelId, String stationId, String licensePlate, String color,
                   Integer batteryLevel, VehicleStatus status, Timestamp createdAt) {
        this.vehicleId = vehicleId;
        this.modelId = modelId;
        this.stationId = stationId;
        this.licensePlate = licensePlate;
        this.color = color;
        this.batteryLevel = batteryLevel;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId='" + vehicleId + '\'' +
                ", modelId='" + modelId + '\'' +
                ", stationId='" + stationId + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", batteryLevel=" + batteryLevel +
                ", status=" + status +
                '}';
    }
}
