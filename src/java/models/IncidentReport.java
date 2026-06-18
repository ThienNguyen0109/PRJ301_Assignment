package models;

import enums.IncidentSeverity;
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
 * Entity class representing an Incident_Report
 */
@Entity
@Table(name = "Incident_Report")
public class IncidentReport implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "incident_id", columnDefinition = "uniqueidentifier")
    private String incidentId;
    @Column(name = "rental_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String rentalId;
    @Column(name = "vehicle_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String vehicleId;
    @Lob
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private IncidentSeverity severity;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", referencedColumnName = "rental_id", insertable = false, updatable = false)
    private Rental rental;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicle_id", insertable = false, updatable = false)
    private Vehicle vehicle;

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

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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


