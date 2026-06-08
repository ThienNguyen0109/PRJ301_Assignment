package models;

import enums.RentalStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Entity class representing a Rental
 */
public class Rental implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rentalId;
    private String customerId;
    private String vehicleId;
    private String pickupStationId;
    private Date startDate;
    private Date endDate;
    private Integer totalDays;
    private BigDecimal totalAmount;
    private RentalStatus status;
    private Timestamp createdAt;

    public Rental() {
    }

    public Rental(String customerId, String vehicleId, String pickupStationId, Date startDate, Date endDate,
                  Integer totalDays, BigDecimal totalAmount, RentalStatus status) {
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.pickupStationId = pickupStationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Rental(String rentalId, String customerId, String vehicleId, String pickupStationId, Date startDate,
                  Date endDate, Integer totalDays, BigDecimal totalAmount, RentalStatus status, Timestamp createdAt) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.pickupStationId = pickupStationId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPickupStationId() {
        return pickupStationId;
    }

    public void setPickupStationId(String pickupStationId) {
        this.pickupStationId = pickupStationId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "rentalId='" + rentalId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", totalDays=" + totalDays +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}


