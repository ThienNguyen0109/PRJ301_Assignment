package dto;

import enums.ExtraChargeStatus;
import enums.ExtraChargeType;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class AdminExtraChargeRow {
    private final String chargeId;
    private final String rentalId;
    private final String incidentId;
    private final String customerName;
    private final String customerEmail;
    private final String vehicleName;
    private final String licensePlate;
    private final ExtraChargeType chargeType;
    private final BigDecimal amount;
    private final String description;
    private final ExtraChargeStatus status;
    private final Timestamp createdAt;
    private final Timestamp paidAt;

    public AdminExtraChargeRow(String chargeId, String rentalId, String incidentId, String customerName,
            String customerEmail, String vehicleName, String licensePlate, ExtraChargeType chargeType,
            BigDecimal amount, String description, ExtraChargeStatus status, Timestamp createdAt, Timestamp paidAt) {
        this.chargeId = chargeId;
        this.rentalId = rentalId;
        this.incidentId = incidentId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.vehicleName = vehicleName;
        this.licensePlate = licensePlate;
        this.chargeType = chargeType;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public String getChargeId() { return chargeId; }
    public String getRentalId() { return rentalId; }
    public String getIncidentId() { return incidentId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getVehicleName() { return vehicleName; }
    public String getLicensePlate() { return licensePlate; }
    public ExtraChargeType getChargeType() { return chargeType; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public ExtraChargeStatus getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getPaidAt() { return paidAt; }
    public boolean isCanEdit() { return status == ExtraChargeStatus.UNPAID; }
    public boolean isCanCancel() { return status == ExtraChargeStatus.UNPAID || status == ExtraChargeStatus.PENDING; }
}
