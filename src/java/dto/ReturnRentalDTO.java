package dto;

import enums.RentalStatus;
import enums.VehicleStatus;
import java.math.BigDecimal;
import java.sql.Date;

public class ReturnRentalDTO {
    private final String rentalId;
    private final String customerName;
    private final String email;
    private final String phone;
    private final String vehicleModel;
    private final String licensePlate;
    private final Integer batteryLevel;
    private final Date startDate;
    private final Date endDate;
    private final BigDecimal totalAmount;
    private final String stationName;
    private final RentalStatus rentalStatus;
    private final VehicleStatus vehicleStatus;

    public ReturnRentalDTO(String rentalId, String customerName, String email, String phone,
            String vehicleModel, String licensePlate, Integer batteryLevel, Date startDate,
            Date endDate, BigDecimal totalAmount, String stationName, RentalStatus rentalStatus,
            VehicleStatus vehicleStatus) {
        this.rentalId = rentalId;
        this.customerName = customerName;
        this.email = email;
        this.phone = phone;
        this.vehicleModel = vehicleModel;
        this.licensePlate = licensePlate;
        this.batteryLevel = batteryLevel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.stationName = stationName;
        this.rentalStatus = rentalStatus;
        this.vehicleStatus = vehicleStatus;
    }

    public String getRentalId() { return rentalId; }
    public String getCustomerName() { return customerName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getVehicleModel() { return vehicleModel; }
    public String getLicensePlate() { return licensePlate; }
    public Integer getBatteryLevel() { return batteryLevel; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStationName() { return stationName; }
    public RentalStatus getRentalStatus() { return rentalStatus; }
    public VehicleStatus getVehicleStatus() { return vehicleStatus; }
}
