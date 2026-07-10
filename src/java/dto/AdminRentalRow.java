package dto;

import enums.RentalStatus;
import enums.VehicleStatus;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/** Read model for the administrator rental list and detail pages. */
public class AdminRentalRow {
    private final String rentalId, customerId, customerName, customerEmail, customerPhone;
    private final String vehicleId, licensePlate, vehicleModelName, stationName;
    private final Date startDate, endDate, actualReturnDate;
    private final Integer totalDays;
    private final BigDecimal totalAmount, lateFee;
    private final RentalStatus status;
    private final VehicleStatus vehicleStatus;
    private final Timestamp createdAt;

    public AdminRentalRow(String rentalId, String customerId, String customerName, String customerEmail,
            String customerPhone, String vehicleId, String licensePlate, String vehicleModelName,
            String stationName, Date startDate, Date endDate, Date actualReturnDate, Integer totalDays,
            BigDecimal totalAmount, BigDecimal lateFee, RentalStatus status, VehicleStatus vehicleStatus,
            Timestamp createdAt) {
        this.rentalId = rentalId; this.customerId = customerId; this.customerName = customerName;
        this.customerEmail = customerEmail; this.customerPhone = customerPhone; this.vehicleId = vehicleId;
        this.licensePlate = licensePlate; this.vehicleModelName = vehicleModelName; this.stationName = stationName;
        this.startDate = startDate; this.endDate = endDate; this.actualReturnDate = actualReturnDate;
        this.totalDays = totalDays; this.totalAmount = totalAmount; this.lateFee = lateFee;
        this.status = status; this.vehicleStatus = vehicleStatus; this.createdAt = createdAt;
    }
    public String getRentalId() { return rentalId; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public String getVehicleId() { return vehicleId; }
    public String getLicensePlate() { return licensePlate; }
    public String getVehicleModelName() { return vehicleModelName; }
    public String getStationName() { return stationName; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public Date getActualReturnDate() { return actualReturnDate; }
    public Integer getTotalDays() { return totalDays; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getLateFee() { return lateFee; }
    public RentalStatus getStatus() { return status; }
    public boolean isCanCancel() { return status == RentalStatus.BOOKED; }
    public VehicleStatus getVehicleStatus() { return vehicleStatus; }
    public Timestamp getCreatedAt() { return createdAt; }
}
