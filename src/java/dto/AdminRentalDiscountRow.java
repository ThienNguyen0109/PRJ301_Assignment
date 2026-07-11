package dto;

import java.sql.Date;

public class AdminRentalDiscountRow {
    private final String rentalDiscountId;
    private final String rentalId;
    private final String discountId;
    private final String discountCode;
    private final Integer discountPercent;
    private final String customerName;
    private final String customerEmail;
    private final Date startDate;
    private final Date endDate;

    public AdminRentalDiscountRow(String rentalDiscountId, String rentalId, String discountId, String discountCode,
            Integer discountPercent, String customerName, String customerEmail, Date startDate, Date endDate) {
        this.rentalDiscountId = rentalDiscountId;
        this.rentalId = rentalId;
        this.discountId = discountId;
        this.discountCode = discountCode;
        this.discountPercent = discountPercent;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getRentalDiscountId() { return rentalDiscountId; }
    public String getRentalId() { return rentalId; }
    public String getDiscountId() { return discountId; }
    public String getDiscountCode() { return discountCode; }
    public Integer getDiscountPercent() { return discountPercent; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
}
