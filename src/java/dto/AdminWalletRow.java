package dto;

import java.sql.Timestamp;

public class AdminWalletRow {
    private final String walletId, accountId, customerName, customerEmail, customerPhone;
    private final Double balance;
    private final Timestamp updatedAt;
    public AdminWalletRow(String walletId, String accountId, String customerName, String customerEmail,
            String customerPhone, Double balance, Timestamp updatedAt) {
        this.walletId = walletId; this.accountId = accountId; this.customerName = customerName;
        this.customerEmail = customerEmail; this.customerPhone = customerPhone; this.balance = balance; this.updatedAt = updatedAt;
    }
    public String getWalletId() { return walletId; } public String getAccountId() { return accountId; }
    public String getCustomerName() { return customerName; } public String getCustomerEmail() { return customerEmail; }
    public String getCustomerPhone() { return customerPhone; } public Double getBalance() { return balance; }
    public Timestamp getUpdatedAt() { return updatedAt; }
}
