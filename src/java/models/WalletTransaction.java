package models;

import enums.TransactionType;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a Wallet Transaction
 */
@Entity
@Table(name = "Wallet_Transaction")
public class WalletTransaction implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "transaction_id", columnDefinition = "uniqueidentifier")
    private String transactionId;
    @Column(name = "wallet_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String walletId;
    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private Double amount;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;
    @Lob
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", referencedColumnName = "wallet_id", insertable = false, updatable = false)
    private Wallet wallet;

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

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
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


