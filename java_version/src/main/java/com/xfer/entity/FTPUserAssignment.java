package com.xfer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
    
    @Column(name = "can_read", nullable = false)
    private Boolean canRead = true;
    
    @Column(name = "can_write", nullable = false)
    private Boolean canWrite = false;
    
    @Column(name = "can_delete", nullable = false)
    private Boolean canDelete = false;
    
    @Column(name = "can_upload", nullable = false)
    private Boolean canUpload = false;
    
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
    
    public FTPUserAssignment(Long ftpAccountId, Long userId, Long assignedBy, 
                           Boolean canRead, Boolean canWrite, Boolean canDelete, Boolean canUpload) {
        this.ftpAccountId = ftpAccountId;
        this.userId = userId;
        this.assignedBy = assignedBy;
        this.canRead = canRead;
        this.canWrite = canWrite;
        this.canDelete = canDelete;
        this.canUpload = canUpload;
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
    
    public Boolean getCanRead() {
        return canRead;
    }
    
    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }
    
    public Boolean getCanWrite() {
        return canWrite;
    }
    
    public void setCanWrite(Boolean canWrite) {
        this.canWrite = canWrite;
    }
    
    public Boolean getCanDelete() {
        return canDelete;
    }
    
    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }
    
    public Boolean getCanUpload() {
        return canUpload;
    }
    
    public void setCanUpload(Boolean canUpload) {
        this.canUpload = canUpload;
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
