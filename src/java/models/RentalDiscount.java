package models;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a Rental_Discount
 */
@Entity
@Table(name = "Rental_Discount")
public class RentalDiscount implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "rental_discount_id", columnDefinition = "uniqueidentifier")
    private String rentalDiscountId;
    @Column(name = "rental_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String rentalId;
    @Column(name = "discount_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String discountId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", referencedColumnName = "rental_id", insertable = false, updatable = false)
    private Rental rental;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", referencedColumnName = "discount_id", insertable = false, updatable = false)
    private Discount discount;

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

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
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

