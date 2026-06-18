package models;

import enums.Role;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class representing an Account
 */
@Entity
@Table(name = "Account")
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "account_id", columnDefinition = "uniqueidentifier")
    private String accountId;
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    @Column(name = "full_name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String fullName;
    @Column(name = "phone", length = 20)
    private String phone;
    @Column(name = "is_verified")
    private Boolean isVerified;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;
    @Column(name = "status", length = 20)
    private String status; // ACTIVE, INACTIVE
    @Column(name = "created_at")
    private Timestamp createdAt;

    // Constructors
    public Account() {
    }

    public Account(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Account(String accountId, String email, String password, String fullName, 
                   String phone, Boolean isVerified, Role role, String status, Timestamp createdAt) {
        this.accountId = accountId;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.isVerified = isVerified;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId='" + accountId + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}


