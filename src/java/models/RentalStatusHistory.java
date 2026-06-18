package models;

import enums.RentalStatus;
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
 * Entity class representing a Rental_Status_History
 */
@Entity
@Table(name = "Rental_Status_History")
public class RentalStatusHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "history_id", columnDefinition = "uniqueidentifier")
    private String historyId;
    @Column(name = "rental_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String rentalId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RentalStatus status;
    @Column(name = "changed_at")
    private Timestamp changedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", referencedColumnName = "rental_id", insertable = false, updatable = false)
    private Rental rental;

    public RentalStatusHistory() {
    }

    public RentalStatusHistory(String rentalId, RentalStatus status) {
        this.rentalId = rentalId;
        this.status = status;
    }

    public RentalStatusHistory(String historyId, String rentalId, RentalStatus status, Timestamp changedAt) {
        this.historyId = historyId;
        this.rentalId = rentalId;
        this.status = status;
        this.changedAt = changedAt;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public Timestamp getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Timestamp changedAt) {
        this.changedAt = changedAt;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    @Override
    public String toString() {
        return "RentalStatusHistory{" +
                "historyId='" + historyId + '\'' +
                ", rentalId='" + rentalId + '\'' +
                ", status=" + status +
                ", changedAt=" + changedAt +
                '}';
    }
}


