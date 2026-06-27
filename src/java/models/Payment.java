package models;

import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.PaymentType;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a Payment
 */
@Entity
@Table(name = "Payment")
public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "payment_id", columnDefinition = "uniqueidentifier")
    private String paymentId;
    @Column(name = "rental_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String rentalId;
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private Double amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    private PaymentType paymentType = PaymentType.BOOKING;
    @Column(name = "charge_id", columnDefinition = "uniqueidentifier")
    private String chargeId;
    @Column(name = "transaction_code", length = 255)
    private String transactionCode;
    @Column(name = "payment_date")
    private Timestamp paymentDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id", referencedColumnName = "rental_id", insertable = false, updatable = false)
    private Rental rental;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_id", referencedColumnName = "charge_id", insertable = false, updatable = false)
    private ExtraCharge extraCharge;

    public Payment() {
    }

    public Payment(String rentalId, Double amount, PaymentMethod paymentMethod, PaymentStatus status, String transactionCode) {
        this.rentalId = rentalId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentType = PaymentType.BOOKING;
        this.transactionCode = transactionCode;
    }

    public Payment(String paymentId, String rentalId, Double amount, PaymentMethod paymentMethod,
                   PaymentStatus status, String transactionCode, Timestamp paymentDate) {
        this.paymentId = paymentId;
        this.rentalId = rentalId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentType = PaymentType.BOOKING;
        this.transactionCode = transactionCode;
        this.paymentDate = paymentDate;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public ExtraCharge getExtraCharge() {
        return extraCharge;
    }

    public void setExtraCharge(ExtraCharge extraCharge) {
        this.extraCharge = extraCharge;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", rentalId='" + rentalId + '\'' +
                ", amount=" + amount +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", transactionCode='" + transactionCode + '\'' +
                '}';
    }
}


