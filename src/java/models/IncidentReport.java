package models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class representing an Incident_Report
 */
public class IncidentReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String incidentId;
    private String rentalId;
    private String vehicleId;
    private String description;
    private IncidentSeverity severity;
    private Timestamp createdAt;

    public IncidentReport() {
    }

    public IncidentReport(String rentalId, String vehicleId, String description, IncidentSeverity severity) {
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.severity = severity;
    }

    public IncidentReport(String incidentId, String rentalId, String vehicleId, String description,
                          IncidentSeverity severity, Timestamp createdAt) {
        this.incidentId = incidentId;
        this.rentalId = rentalId;
        this.vehicleId = vehicleId;
        this.description = description;
        this.severity = severity;
        this.createdAt = createdAt;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
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

    public IncidentSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(IncidentSeverity severity) {
        this.severity = severity;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "IncidentReport{" +
                "incidentId='" + incidentId + '\'' +
                ", rentalId='" + rentalId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", severity=" + severity +
                ", createdAt=" + createdAt +
                '}';
    }
}
