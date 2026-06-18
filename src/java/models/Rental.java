package models;

import enums.RentalStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
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
 * Entity class representing a Rental
 */
@Entity
@Table(name = "Rental")
public class Rental implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rental_id", columnDefinition = "uniqueidentifier")
    private String rentalId;
    @Column(name = "customer_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String customerId;
    @Column(name = "vehicle_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String vehicleId;
    @Column(name = "pickup_station_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String pickupStationId;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "total_days")
    private Integer totalDays;
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RentalStatus status;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "account_id", insertable = false, updatable = false)
    private Account customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicle_id", insertable = false, updatable = false)
    private Vehicle vehicle;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_station_id", referencedColumnName = "station_id", insertable = false, updatable = false)
    private Station pickupStation;

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

    public Account getCustomer() {
        return customer;
    }

    public void setCustomer(Account customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Station getPickupStation() {
        return pickupStation;
    }

    public void setPickupStation(Station pickupStation) {
        this.pickupStation = pickupStation;
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


