package dto;

import enums.PaymentMethod;
import enums.PaymentStatus;
import enums.PaymentType;
import java.sql.Timestamp;

public class AdminPaymentRow {
    private final String paymentId, rentalId, customerName, customerEmail, transactionCode;
    private final Double amount;
    private final PaymentMethod paymentMethod;
    private final PaymentType paymentType;
    private final PaymentStatus status;
    private final Timestamp paymentDate;
    public AdminPaymentRow(String paymentId, String rentalId, String customerName, String customerEmail,
            Double amount, PaymentMethod paymentMethod, PaymentType paymentType, PaymentStatus status,
            String transactionCode, Timestamp paymentDate) {
        this.paymentId = paymentId; this.rentalId = rentalId; this.customerName = customerName; this.customerEmail = customerEmail;
        this.amount = amount; this.paymentMethod = paymentMethod; this.paymentType = paymentType; this.status = status;
        this.transactionCode = transactionCode; this.paymentDate = paymentDate;
    }
    public String getPaymentId() { return paymentId; } public String getRentalId() { return rentalId; }
    public String getCustomerName() { return customerName; } public String getCustomerEmail() { return customerEmail; }
    public Double getAmount() { return amount; } public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentType getPaymentType() { return paymentType; } public PaymentStatus getStatus() { return status; }
    public String getTransactionCode() { return transactionCode; } public Timestamp getPaymentDate() { return paymentDate; }
    public boolean isCanMarkFailed() { return status == PaymentStatus.PENDING; }
    public boolean isCanConfirmCash() { return status == PaymentStatus.PENDING && paymentMethod == PaymentMethod.CASH; }
}
