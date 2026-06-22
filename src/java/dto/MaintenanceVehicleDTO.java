package dto;

import enums.MaintenanceStatus;
import enums.VehicleStatus;
import java.sql.Timestamp;

public class MaintenanceVehicleDTO {
    private final String maintenanceId;
    private final String vehicleId;
    private final String vehicleModel;
    private final String licensePlate;
    private final String description;
    private final Timestamp maintenanceDate;
    private final MaintenanceStatus maintenanceStatus;
    private final VehicleStatus vehicleStatus;

    public MaintenanceVehicleDTO(String maintenanceId, String vehicleId, String vehicleModel,
            String licensePlate, String description, Timestamp maintenanceDate,
            MaintenanceStatus maintenanceStatus, VehicleStatus vehicleStatus) {
        this.maintenanceId = maintenanceId;
        this.vehicleId = vehicleId;
        this.vehicleModel = vehicleModel;
        this.licensePlate = licensePlate;
        this.description = description;
        this.maintenanceDate = maintenanceDate;
        this.maintenanceStatus = maintenanceStatus;
        this.vehicleStatus = vehicleStatus;
    }

    public String getMaintenanceId() { return maintenanceId; }
    public String getVehicleId() { return vehicleId; }
    public String getVehicleModel() { return vehicleModel; }
    public String getLicensePlate() { return licensePlate; }
    public String getDescription() { return description; }
    public Timestamp getMaintenanceDate() { return maintenanceDate; }
    public MaintenanceStatus getMaintenanceStatus() { return maintenanceStatus; }
    public VehicleStatus getVehicleStatus() { return vehicleStatus; }
}
