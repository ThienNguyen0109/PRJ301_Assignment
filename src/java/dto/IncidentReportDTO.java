package dto;

import enums.IncidentSeverity;
import java.sql.Timestamp;

public class IncidentReportDTO {
    private final String incidentId;
    private final String rentalId;
    private final String vehicleModel;
    private final String licensePlate;
    private final String description;
    private final IncidentSeverity severity;
    private final Timestamp createdAt;

    public IncidentReportDTO(String incidentId, String rentalId, String vehicleModel,
            String licensePlate, String description, IncidentSeverity severity, Timestamp createdAt) {
        this.incidentId = incidentId;
        this.rentalId = rentalId;
        this.vehicleModel = vehicleModel;
        this.licensePlate = licensePlate;
        this.description = description;
        this.severity = severity;
        this.createdAt = createdAt;
    }

    public String getIncidentId() { return incidentId; }
    public String getRentalId() { return rentalId; }
    public String getVehicleModel() { return vehicleModel; }
    public String getLicensePlate() { return licensePlate; }
    public String getDescription() { return description; }
    public IncidentSeverity getSeverity() { return severity; }
    public Timestamp getCreatedAt() { return createdAt; }
}
