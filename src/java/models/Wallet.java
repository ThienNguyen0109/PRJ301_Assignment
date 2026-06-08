package models;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Entity class representing a Wallet
 */
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String walletId;
    private String accountId;
    private Double balance;
    private Timestamp updatedAt;

    // Constructors
    public Wallet() {
    }

    public Wallet(String accountId) {
        this.accountId = accountId;
        this.balance = 0.0;
    }

    public Wallet(String walletId, String accountId, Double balance, Timestamp updatedAt) {
        this.walletId = walletId;
        this.accountId = accountId;
        this.balance = balance;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId='" + walletId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", balance=" + balance +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

