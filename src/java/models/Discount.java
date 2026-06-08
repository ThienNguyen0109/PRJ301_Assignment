package models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class representing a Discount
 */
public class Discount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String discountId;
    private String code;
    private Integer discountPercent;
    private Timestamp expiredAt;
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

