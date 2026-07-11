package dto;

import enums.TransactionType;
import java.sql.Timestamp;

public class AdminWalletTransactionRow {
    private final String transactionId;
    private final String walletId;
    private final String customerName;
    private final String customerEmail;
    private final Double amount;
    private final TransactionType type;
    private final String description;
    private final Timestamp createdAt;

    public AdminWalletTransactionRow(String transactionId, String walletId, String customerName, String customerEmail,
            Double amount, TransactionType type, String description, Timestamp createdAt) {
        this.transactionId = transactionId;
        this.walletId = walletId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.createdAt = createdAt;
    }

    public String getTransactionId() { return transactionId; }
    public String getWalletId() { return walletId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public Double getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public String getDescription() { return description; }
    public Timestamp getCreatedAt() { return createdAt; }
}
