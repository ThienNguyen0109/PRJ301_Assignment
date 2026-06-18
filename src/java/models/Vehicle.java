package models;

import enums.VehicleStatus;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a Vehicle
 */
@Entity
@Table(name = "Vehicle")
public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "vehicle_id", columnDefinition = "uniqueidentifier")
    private String vehicleId;
    @Column(name = "model_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String modelId;
    @Column(name = "station_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String stationId;
    @Column(name = "license_plate", unique = true, length = 20)
    private String licensePlate;
    @Column(name = "color", length = 50, columnDefinition = "NVARCHAR(50)")
    private String color;
    @Column(name = "battery_level")
    private Integer batteryLevel;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private VehicleStatus status;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", referencedColumnName = "model_id", insertable = false, updatable = false)
    private VehicleModel model;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", referencedColumnName = "station_id", insertable = false, updatable = false)
    private Station station;

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

    public VehicleModel getModel() {
        return model;
    }

    public void setModel(VehicleModel model) {
        this.model = model;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
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


