package models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class representing a Vehicle_Maintenance
 */
public class VehicleMaintenance implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maintenanceId;
    private String vehicleId;
    private String description;
    private Timestamp maintenanceDate;
    private MaintenanceStatus status;

    public VehicleMaintenance() {
    }

    public VehicleMaintenance(String vehicleId, String description, MaintenanceStatus status) {
        this.vehicleId = vehicleId;
        this.description = description;
        this.status = status;
    }

    public VehicleMaintenance(String maintenanceId, String vehicleId, String description,
                              Timestamp maintenanceDate, MaintenanceStatus status) {
        this.maintenanceId = maintenanceId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.maintenanceDate = maintenanceDate;
        this.status = status;
    }

    public String getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(String maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(Timestamp maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public MaintenanceStatus getStatus() {
        return status;
    }

    public void setStatus(MaintenanceStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VehicleMaintenance{" +
                "maintenanceId='" + maintenanceId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", status=" + status +
                ", maintenanceDate=" + maintenanceDate +
                '}';
    }
}
