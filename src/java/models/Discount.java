package models;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class representing a Discount
 */
@Entity
@Table(name = "Discount")
public class Discount implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "discount_id", columnDefinition = "uniqueidentifier")
    private String discountId;
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;
    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;
    @Column(name = "expired_at")
    private Timestamp expiredAt;
    @Column(name = "quantity")
    private Integer quantity;

    public Discount() {
    }

    public Discount(String code, Integer discountPercent, Timestamp expiredAt, Integer quantity) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.expiredAt = expiredAt;
        this.quantity = quantity;
    }

    public Discount(String discountId, String code, Integer discountPercent, Timestamp expiredAt, Integer quantity) {
        this.discountId = discountId;
        this.code = code;
        this.discountPercent = discountPercent;
        this.expiredAt = expiredAt;
        this.quantity = quantity;
    }

    public String getDiscountId() {
        return discountId;
    }

    public void setDiscountId(String discountId) {
        this.discountId = discountId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Timestamp getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Timestamp expiredAt) {
        this.expiredAt = expiredAt;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Discount{" +
                "discountId='" + discountId + '\'' +
                ", code='" + code + '\'' +
                ", discountPercent=" + discountPercent +
                ", expiredAt=" + expiredAt +
                ", quantity=" + quantity +
                '}';
    }
}

