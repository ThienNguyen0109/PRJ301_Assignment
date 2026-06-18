package models;

import enums.MaintenanceStatus;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a Vehicle_Maintenance
 */
@Entity
@Table(name = "Vehicle_Maintenance")
public class VehicleMaintenance implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "maintenance_id", columnDefinition = "uniqueidentifier")
    private String maintenanceId;
    @Column(name = "vehicle_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String vehicleId;
    @Lob
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;
    @Column(name = "maintenance_date")
    private Timestamp maintenanceDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MaintenanceStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicle_id", insertable = false, updatable = false)
    private Vehicle vehicle;

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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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


