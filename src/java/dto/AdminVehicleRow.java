package dto;

import enums.VehicleStatus;

/** Row projection used by the admin vehicle inventory list. */
public class AdminVehicleRow {
    private final String vehicleId;
    private final String modelId;
    private final String modelName;
    private final String categoryName;
    private final String stationId;
    private final String stationName;
    private final String licensePlate;
    private final String color;
    private final Integer batteryLevel;
    private final VehicleStatus status;

    public AdminVehicleRow(String vehicleId, String modelId, String modelName, String categoryName,
            String stationId, String stationName, String licensePlate, String color,
            Integer batteryLevel, VehicleStatus status) {
        this.vehicleId = vehicleId;
        this.modelId = modelId;
        this.modelName = modelName;
        this.categoryName = categoryName;
        this.stationId = stationId;
        this.stationName = stationName;
        this.licensePlate = licensePlate;
        this.color = color;
        this.batteryLevel = batteryLevel;
        this.status = status;
    }

    public String getVehicleId() { return vehicleId; }
    public String getModelId() { return modelId; }
    public String getModelName() { return modelName; }
    public String getCategoryName() { return categoryName; }
    public String getStationId() { return stationId; }
    public String getStationName() { return stationName; }
    public String getLicensePlate() { return licensePlate; }
    public String getColor() { return color; }
    public Integer getBatteryLevel() { return batteryLevel; }
    public VehicleStatus getStatus() { return status; }
}
