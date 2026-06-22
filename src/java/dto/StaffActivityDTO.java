package dto;

import java.sql.Timestamp;

public class StaffActivityDTO {
    private final String rentalId;
    private final String customer;
    private final String vehicle;
    private final String action;
    private final Timestamp time;

    public StaffActivityDTO(String rentalId, String customer, String vehicle, String action, Timestamp time) {
        this.rentalId = rentalId;
        this.customer = customer;
        this.vehicle = vehicle;
        this.action = action;
        this.time = time;
    }

    public String getRentalId() { return rentalId; }
    public String getCustomer() { return customer; }
    public String getVehicle() { return vehicle; }
    public String getAction() { return action; }
    public Timestamp getTime() { return time; }
}
