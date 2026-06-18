package models;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class representing a Wallet
 */
@Entity
@Table(name = "Wallet")
public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "wallet_id", columnDefinition = "uniqueidentifier")
    private String walletId;
    @Column(name = "account_id", nullable = false, columnDefinition = "uniqueidentifier")
    private String accountId;
    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private Double balance;
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "account_id", insertable = false, updatable = false)
    private Account account;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

