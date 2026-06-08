package models;

import enums.TransactionType;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class representing a Wallet Transaction
 */
public class WalletTransaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String transactionId;
    private String walletId;
    private Double amount;
    private TransactionType type;
    private String description;
    private Timestamp createdAt;

    // Constructors
    public WalletTransaction() {
    }

    public WalletTransaction(String walletId, Double amount, TransactionType type, String description) {
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

    public WalletTransaction(String transactionId, String walletId, Double amount, TransactionType type, 
                           String description, Timestamp createdAt) {
        this.transactionId = transactionId;
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "WalletTransaction{" +
                "transactionId='" + transactionId + '\'' +
                ", walletId='" + walletId + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}


