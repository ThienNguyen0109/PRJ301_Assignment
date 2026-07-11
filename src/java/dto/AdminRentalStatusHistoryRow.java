package dto;

import enums.RentalStatus;
import java.sql.Timestamp;

public class AdminRentalStatusHistoryRow {
    private final String historyId;
    private final String rentalId;
    private final String customerName;
    private final String customerEmail;
    private final String vehicleName;
    private final String licensePlate;
    private final RentalStatus status;
    private final Timestamp changedAt;

    public AdminRentalStatusHistoryRow(String historyId, String rentalId, String customerName, String customerEmail,
            String vehicleName, String licensePlate, RentalStatus status, Timestamp changedAt) {
        this.historyId = historyId;
        this.rentalId = rentalId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.vehicleName = vehicleName;
        this.licensePlate = licensePlate;
        this.status = status;
        this.changedAt = changedAt;
    }

    public String getHistoryId() { return historyId; }
    public String getRentalId() { return rentalId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getVehicleName() { return vehicleName; }
    public String getLicensePlate() { return licensePlate; }
    public RentalStatus getStatus() { return status; }
    public Timestamp getChangedAt() { return changedAt; }
}
