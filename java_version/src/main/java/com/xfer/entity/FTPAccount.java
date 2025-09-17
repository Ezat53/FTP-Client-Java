package com.xfer.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "ftp_accounts")
public class FTPAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 10)
    private String protocol; // ftp, sftp, scp
    
    @Column(nullable = false, length = 255)
    private String host;
    
    @Column(nullable = false)
    private Integer port;
    
    @Column(nullable = false, length = 100)
    private String username;
    
    @Column(length = 255)
    private String password;
    
    @Column(name = "remote_path", length = 500)
    private String remotePath = "/";
    
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private User owner;
    
    @OneToMany(mappedBy = "ftpAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransferLog> transferLogs = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ftp_user_assignments",
        joinColumns = @JoinColumn(name = "ftp_account_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> assignedUsers = new ArrayList<>();
    
    // Constructors
    public FTPAccount() {}
    
    public FTPAccount(String name, String protocol, String host, Integer port, 
                     String username, String password, Long ownerId) {
        this.name = name;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.ownerId = ownerId;
        this.remotePath = "/";
    }
    
    public FTPAccount(String name, String protocol, String host, Integer port, 
                     String username, String password, String remotePath, Long ownerId) {
        this.name = name;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.remotePath = remotePath != null ? remotePath : "/";
        this.ownerId = ownerId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRemotePath() {
        return remotePath;
    }
    
    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath != null ? remotePath : "/";
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public List<TransferLog> getTransferLogs() {
        return transferLogs;
    }
    
    public void setTransferLogs(List<TransferLog> transferLogs) {
        this.transferLogs = transferLogs;
    }
    
    public List<User> getAssignedUsers() {
        return assignedUsers;
    }
    
    public void setAssignedUsers(List<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }
    
    @Override
    public String toString() {
        return "FTPAccount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", protocol='" + protocol + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", ownerId=" + ownerId +
                ", createdAt=" + createdAt +
                '}';
    }
}
