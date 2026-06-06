package models;

import java.io.Serializable;

/**
 * Entity class representing a Rental_Discount
 */
public class RentalDiscount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rentalDiscountId;
    private String rentalId;
    private String discountId;

    public RentalDiscount() {
    }

    public RentalDiscount(String rentalId, String discountId) {
        this.rentalId = rentalId;
        this.discountId = discountId;
    }

    public RentalDiscount(String rentalDiscountId, String rentalId, String discountId) {
        this.rentalDiscountId = rentalDiscountId;
        this.rentalId = rentalId;
        this.discountId = discountId;
    }

    public String getRentalDiscountId() {
        return rentalDiscountId;
    }

    public void setRentalDiscountId(String rentalDiscountId) {
        this.rentalDiscountId = rentalDiscountId;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    @Override
    public String toString() {
        return "RentalDiscount{" +
                "rentalDiscountId='" + rentalDiscountId + '\'' +
                ", rentalId='" + rentalId + '\'' +
                ", discountId='" + discountId + '\'' +
                '}';
    }
}
