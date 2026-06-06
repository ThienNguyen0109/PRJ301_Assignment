package models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class representing a Rental_Status_History
 */
public class RentalStatusHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private String historyId;
    private String rentalId;
    private RentalStatus status;
    private Timestamp changedAt;

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
