package com.xfer.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ftp_user_assignments")
public class FTPUserAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ftp_account_id", nullable = false)
    private Long ftpAccountId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "assigned_by", nullable = false)
    private Long assignedBy;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ftp_account_id", insertable = false, updatable = false)
    private FTPAccount ftpAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", insertable = false, updatable = false)
    private User assignedByUser;
    
    // Constructors
    public FTPUserAssignment() {}
    
    public FTPUserAssignment(Long ftpAccountId, Long userId, Long assignedBy) {
        this.ftpAccountId = ftpAccountId;
        this.userId = userId;
        this.assignedBy = assignedBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getFtpAccountId() {
        return ftpAccountId;
    }
    
    public void setFtpAccountId(Long ftpAccountId) {
        this.ftpAccountId = ftpAccountId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getAssignedBy() {
        return assignedBy;
    }
    
    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }
    
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public FTPAccount getFtpAccount() {
        return ftpAccount;
    }
    
    public void setFtpAccount(FTPAccount ftpAccount) {
        this.ftpAccount = ftpAccount;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public User getAssignedByUser() {
        return assignedByUser;
    }
    
    public void setAssignedByUser(User assignedByUser) {
        this.assignedByUser = assignedByUser;
    }
    
    @Override
    public String toString() {
        return "FTPUserAssignment{" +
                "id=" + id +
                ", ftpAccountId=" + ftpAccountId +
                ", userId=" + userId +
                ", assignedBy=" + assignedBy +
                ", assignedAt=" + assignedAt +
                '}';
    }
}
