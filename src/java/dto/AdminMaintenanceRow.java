package dto;

import enums.MaintenanceStatus;
import enums.VehicleStatus;
import java.sql.Timestamp;

public class AdminMaintenanceRow {
    private final String maintenanceId;
    private final String vehicleId;
    private final String modelName;
    private final String licensePlate;
    private final String stationName;
    private final String description;
    private final Timestamp maintenanceDate;
    private final MaintenanceStatus status;
    private final VehicleStatus vehicleStatus;

    public AdminMaintenanceRow(String maintenanceId, String vehicleId, String modelName, String licensePlate,
            String stationName, String description, Timestamp maintenanceDate, MaintenanceStatus status,
            VehicleStatus vehicleStatus) {
        this.maintenanceId = maintenanceId;
        this.vehicleId = vehicleId;
        this.modelName = modelName;
        this.licensePlate = licensePlate;
        this.stationName = stationName;
        this.description = description;
        this.maintenanceDate = maintenanceDate;
        this.status = status;
        this.vehicleStatus = vehicleStatus;
    }

    public String getMaintenanceId() { return maintenanceId; }
    public String getVehicleId() { return vehicleId; }
    public String getModelName() { return modelName; }
    public String getLicensePlate() { return licensePlate; }
    public String getStationName() { return stationName; }
    public String getDescription() { return description; }
    public Timestamp getMaintenanceDate() { return maintenanceDate; }
    public MaintenanceStatus getStatus() { return status; }
    public VehicleStatus getVehicleStatus() { return vehicleStatus; }
    public boolean isCanComplete() { return status == MaintenanceStatus.PENDING; }
}
