package dto;
import enums.IncidentSeverity;
import java.sql.Timestamp;
public class AdminIncidentRow {
    private final String incidentId, rentalId, vehicleId, licensePlate, vehicleModelName, description;
    private final IncidentSeverity severity; private final Timestamp createdAt;
    public AdminIncidentRow(String incidentId, String rentalId, String vehicleId, String licensePlate, String vehicleModelName, String description, IncidentSeverity severity, Timestamp createdAt) { this.incidentId=incidentId; this.rentalId=rentalId; this.vehicleId=vehicleId; this.licensePlate=licensePlate; this.vehicleModelName=vehicleModelName; this.description=description; this.severity=severity; this.createdAt=createdAt; }
    public String getIncidentId(){return incidentId;} public String getRentalId(){return rentalId;} public String getVehicleId(){return vehicleId;} public String getLicensePlate(){return licensePlate;} public String getVehicleModelName(){return vehicleModelName;} public String getDescription(){return description;} public IncidentSeverity getSeverity(){return severity;} public Timestamp getCreatedAt(){return createdAt;}
}
