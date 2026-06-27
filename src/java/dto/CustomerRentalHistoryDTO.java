package dto;

import enums.RentalStatus;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class CustomerRentalHistoryDTO {
    private final String rentalId;
    private final String vehicleModel;
    private final String licensePlate;
    private final String stationName;
    private final Date startDate;
    private final Date endDate;
    private final Date actualReturnDate;
    private final Integer totalDays;
    private final BigDecimal totalAmount;
    private final BigDecimal lateFee;
    private final BigDecimal extraChargeTotal;
    private final RentalStatus status;
    private final Timestamp createdAt;

    public CustomerRentalHistoryDTO(String rentalId, String vehicleModel, String licensePlate,
            String stationName, Date startDate, Date endDate, Date actualReturnDate,
            Integer totalDays, BigDecimal totalAmount, BigDecimal lateFee, BigDecimal extraChargeTotal,
            RentalStatus status, Timestamp createdAt) {
        this.rentalId = rentalId;
        this.vehicleModel = vehicleModel;
        this.licensePlate = licensePlate;
        this.stationName = stationName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.actualReturnDate = actualReturnDate;
        this.totalDays = totalDays;
        this.totalAmount = totalAmount;
        this.lateFee = lateFee;
        this.extraChargeTotal = extraChargeTotal;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getRentalId() { return rentalId; }
    public String getVehicleModel() { return vehicleModel; }
    public String getLicensePlate() { return licensePlate; }
    public String getStationName() { return stationName; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public Date getActualReturnDate() { return actualReturnDate; }
    public Integer getTotalDays() { return totalDays; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getLateFee() { return lateFee; }
    public BigDecimal getExtraChargeTotal() { return extraChargeTotal; }
    public RentalStatus getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
}
