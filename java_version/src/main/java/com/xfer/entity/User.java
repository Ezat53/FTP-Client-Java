package com.xfer.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 80)
    private String username;
    
    @Column(nullable = false, length = 120)
    private String passwordHash;
    
    @Column(length = 20)
    private String role = "user";
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FTPAccount> ownedFtpAccounts = new ArrayList<>();
    
    @ManyToMany(mappedBy = "assignedUsers", fetch = FetchType.LAZY)
    private List<FTPAccount> assignedFtpAccounts = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransferLog> transferLogs = new ArrayList<>();
    
    // Constructors
    public User() {}
    
    public User(String username, String passwordHash, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public List<FTPAccount> getOwnedFtpAccounts() {
        return ownedFtpAccounts;
    }
    
    public void setOwnedFtpAccounts(List<FTPAccount> ownedFtpAccounts) {
        this.ownedFtpAccounts = ownedFtpAccounts;
    }
    
    public List<FTPAccount> getAssignedFtpAccounts() {
        return assignedFtpAccounts;
    }
    
    public void setAssignedFtpAccounts(List<FTPAccount> assignedFtpAccounts) {
        this.assignedFtpAccounts = assignedFtpAccounts;
    }
    
    public List<TransferLog> getTransferLogs() {
        return transferLogs;
    }
    
    public void setTransferLogs(List<TransferLog> transferLogs) {
        this.transferLogs = transferLogs;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
