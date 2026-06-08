package dto;

import enums.PaymentMethod;
import enums.PaymentStatus;
import java.io.Serializable;

/**
 * Booking result displayed after payment succeeds.
 */
public class BookingDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rentalId;
    private String paymentId;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionCode;
    private BookingQuote quote;

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public BookingQuote getQuote() {
        return quote;
    }

    public void setQuote(BookingQuote quote) {
        this.quote = quote;
    }
}

