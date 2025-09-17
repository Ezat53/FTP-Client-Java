package com.xfer.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_logs")
public class TransferLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "ftp_account_id")
    private Long ftpAccountId;
    
    @Column(nullable = false, length = 20)
    private String action; // upload, download, delete, list
    
    @Column(length = 255)
    private String filename;
    
    @Column
    private Long fileSize;
    
    @Column(nullable = false, length = 20)
    private String status; // success, error
    
    @Column(length = 1000)
    private String errorMessage;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ftp_account_id", insertable = false, updatable = false)
    private FTPAccount ftpAccount;
    
    // Constructors
    public TransferLog() {}
    
    public TransferLog(Long userId, Long ftpAccountId, String action, String filename, 
                      Long fileSize, String status, String errorMessage) {
        this.userId = userId;
        this.ftpAccountId = ftpAccountId;
        this.action = action;
        this.filename = filename;
        this.fileSize = fileSize;
        this.status = status;
        this.errorMessage = errorMessage;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getFtpAccountId() {
        return ftpAccountId;
    }
    
    public void setFtpAccountId(Long ftpAccountId) {
        this.ftpAccountId = ftpAccountId;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public FTPAccount getFtpAccount() {
        return ftpAccount;
    }
    
    public void setFtpAccount(FTPAccount ftpAccount) {
        this.ftpAccount = ftpAccount;
    }
    
    @Override
    public String toString() {
        return "TransferLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", ftpAccountId=" + ftpAccountId +
                ", action='" + action + '\'' +
                ", filename='" + filename + '\'' +
                ", fileSize=" + fileSize +
                ", status='" + status + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
