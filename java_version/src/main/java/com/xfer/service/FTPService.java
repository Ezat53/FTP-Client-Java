package com.xfer.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.xfer.entity.FTPAccount;
import com.xfer.entity.FTPUserAssignment;
import com.xfer.entity.User;
import com.xfer.repository.FTPAccountRepository;
import com.xfer.repository.FTPUserAssignmentRepository;
import com.xfer.repository.UserRepository;

@Service
@Transactional
public class FTPService {
    
    @Autowired
    private FTPAccountRepository ftpAccountRepository;
    
    @Autowired
    private FTPUserAssignmentRepository ftpUserAssignmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public FTPAccount createAccount(String name, String protocol, String host, Integer port,
                                  String username, String password, Long ownerId, List<Long> userIds) {
        FTPAccount account = new FTPAccount(name, protocol, host, port, username, password, ownerId);
        account = ftpAccountRepository.save(account);
        
        if (userIds != null && !userIds.isEmpty()) {
            for (Long userId : userIds) {
                FTPUserAssignment assignment = new FTPUserAssignment(account.getId(), userId, ownerId);
                ftpUserAssignmentRepository.save(assignment);
            }
        }
        
        return account;
    }
    
    public Optional<FTPAccount> getAccountById(Long accountId) {
        return ftpAccountRepository.findById(accountId);
    }
    
    public List<FTPAccount> getUserAccounts(Long userId) {
        return ftpAccountRepository.findByAssignedUserId(userId);
    }
    
    public List<FTPAccount> getAllAccounts() {
        return ftpAccountRepository.findAllWithOwner();
    }
    
    public List<User> getAccountAssignments(Long accountId) {
        List<FTPUserAssignment> assignments = ftpUserAssignmentRepository.findByFtpAccountIdWithUser(accountId);
        return assignments.stream()
                .map(FTPUserAssignment::getUser)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public boolean assignAccountToUsers(Long accountId, List<Long> userIds, Long assignedBy) {
        try {
            // Remove existing assignments
            ftpUserAssignmentRepository.deleteByFtpAccountId(accountId);
            
            // Add new assignments
            for (Long userId : userIds) {
                FTPUserAssignment assignment = new FTPUserAssignment(accountId, userId, assignedBy);
                ftpUserAssignmentRepository.save(assignment);
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean unassignAccountFromUser(Long accountId, Long userId) {
        try {
            ftpUserAssignmentRepository.deleteByFtpAccountIdAndUserId(accountId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean unassignAccountFromAllUsers(Long accountId) {
        try {
            ftpUserAssignmentRepository.deleteByFtpAccountId(accountId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public FTPAccount updateAccount(Long accountId, String name, String protocol, String host,
                                  Integer port, String username, String password, List<Long> userIds) {
        FTPAccount account = ftpAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
        
        account.setName(name);
        account.setProtocol(protocol);
        account.setHost(host);
        account.setPort(port);
        account.setUsername(username);
        if (password != null && !password.trim().isEmpty()) {
            account.setPassword(password);
        }
        
        // Update user assignments if provided
        if (userIds != null) {
            ftpUserAssignmentRepository.deleteByFtpAccountId(accountId);
            for (Long userId : userIds) {
                FTPUserAssignment assignment = new FTPUserAssignment(accountId, userId, account.getOwnerId());
                ftpUserAssignmentRepository.save(assignment);
            }
        }
        
        return ftpAccountRepository.save(account);
    }
    
    public boolean deleteAccount(Long accountId) {
        try {
            // Check if account exists
            if (!ftpAccountRepository.existsById(accountId)) {
                System.out.println("FTP account with ID " + accountId + " does not exist");
                return false;
            }
            
            // Delete related assignments first (ignore if already deleted)
            try {
                List<FTPUserAssignment> assignments = ftpUserAssignmentRepository.findByFtpAccountId(accountId);
                if (!assignments.isEmpty()) {
                    ftpUserAssignmentRepository.deleteAll(assignments);
                    System.out.println("Deleted " + assignments.size() + " assignments for FTP account " + accountId);
                } else {
                    System.out.println("No assignments found for FTP account " + accountId);
                }
            } catch (Exception e) {
                System.out.println("Warning: Could not delete assignments for FTP account " + accountId + ": " + e.getMessage());
                // Continue with account deletion even if assignments deletion fails
            }
            
            // Delete the account
            ftpAccountRepository.deleteById(accountId);
            System.out.println("Successfully deleted FTP account " + accountId);
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting FTP account " + accountId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean hasUserAccess(Long accountId, Long userId) {
        List<FTPUserAssignment> assignments = ftpUserAssignmentRepository.findByFtpAccountIdWithUser(accountId);
        return assignments.stream()
                .anyMatch(assignment -> assignment.getUserId().equals(userId));
    }
    
    // File Operations
    public List<FileInfo> listFiles(Long accountId) {
        return listFiles(accountId, null);
    }
    
    public List<FileInfo> listFiles(Long accountId, String path) {
        try {
            FTPAccount account = getAccountById(accountId)
                    .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
            
            System.out.println("Listing files for account: " + account.getName() + " (" + account.getProtocol() + ")");
            if (path != null && !path.isEmpty()) {
                System.out.println("Path: " + path);
            }
            
            if ("ftp".equals(account.getProtocol())) {
                return listFTPFiles(account, path);
            } else if ("sftp".equals(account.getProtocol())) {
                return listSFTPFiles(account, path);
            } else {
                throw new RuntimeException("Desteklenmeyen protokol: " + account.getProtocol());
            }
        } catch (Exception e) {
            System.out.println("Error listing files for account " + accountId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Dosya listesi alınırken hata: " + e.getMessage());
        }
    }
    
    private List<FileInfo> listFTPFiles(FTPAccount account) {
        return listFTPFiles(account, null);
    }
    
    private List<FileInfo> listFTPFiles(FTPAccount account, String path) {
        List<FileInfo> files = new ArrayList<>();
        FTPClient ftpClient = new FTPClient();
        
        try {
            System.out.println("Connecting to FTP: " + account.getHost() + ":" + account.getPort());
            ftpClient.connect(account.getHost(), account.getPort());
            
            if (!ftpClient.isConnected()) {
                throw new RuntimeException("FTP sunucusuna bağlanılamadı");
            }
            
            System.out.println("Logging in with username: " + account.getUsername());
            boolean loginSuccess = ftpClient.login(account.getUsername(), account.getPassword());
            if (!loginSuccess) {
                throw new RuntimeException("FTP girişi başarısız. Kullanıcı adı veya şifre hatalı olabilir.");
            }
            
            ftpClient.enterLocalPassiveMode();
            System.out.println("Retrieving file list...");
            
            // Change to specified directory if path is provided
            if (path != null && !path.isEmpty() && !path.equals("/")) {
                boolean changed = ftpClient.changeWorkingDirectory(path);
                if (!changed) {
                    System.out.println("Warning: Could not change to directory: " + path);
                }
            }
            
            FTPFile[] ftpFiles = ftpClient.listFiles();
            if (ftpFiles != null) {
                for (FTPFile ftpFile : ftpFiles) {
                    if (ftpFile != null && ftpFile.getName() != null && !ftpFile.getName().equals(".") && !ftpFile.getName().equals("..")) {
                        FileInfo fileInfo = new FileInfo();
                        fileInfo.setName(ftpFile.getName());
                        fileInfo.setSize(ftpFile.getSize() > 0 ? ftpFile.getSize() : 0);
                        fileInfo.setDirectory(ftpFile.isDirectory());
                        fileInfo.setLastModified(ftpFile.getTimestamp() != null ? ftpFile.getTimestamp().getTimeInMillis() : 0L);
                        files.add(fileInfo);
                    }
                }
            }
            
            System.out.println("Retrieved " + files.size() + " files");
            
        } catch (Exception e) {
            System.out.println("FTP connection error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("FTP dosya listesi alınırken hata: " + e.getMessage());
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                    System.out.println("FTP connection closed");
                }
            } catch (IOException e) {
                System.out.println("Warning: Error closing FTP connection: " + e.getMessage());
            }
        }
        
        return files;
    }
    
    private List<FileInfo> listSFTPFiles(FTPAccount account) {
        return listSFTPFiles(account, null);
    }
    
    private List<FileInfo> listSFTPFiles(FTPAccount account, String path) {
        List<FileInfo> files = new ArrayList<>();
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        
        try {
            session = jsch.getSession(account.getUsername(), account.getHost(), account.getPort());
            session.setPassword(account.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            
            // Use specified path or current directory
            String listPath = (path != null && !path.isEmpty() && !path.equals("/")) ? path : ".";
            List<ChannelSftp.LsEntry> sftpFiles = sftpChannel.ls(listPath);
            for (ChannelSftp.LsEntry entry : sftpFiles) {
                if (!entry.getFilename().equals(".") && !entry.getFilename().equals("..")) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setName(entry.getFilename());
                    fileInfo.setSize(entry.getAttrs().getSize());
                    fileInfo.setDirectory(entry.getAttrs().isDir());
                    fileInfo.setLastModified(entry.getAttrs().getMTime() * 1000L);
                    files.add(fileInfo);
                }
            }
            
        } catch (JSchException | SftpException e) {
            throw new RuntimeException("SFTP dosya listesi alınırken hata: " + e.getMessage());
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
        
        return files;
    }
    
    public boolean uploadFile(Long accountId, MultipartFile file) {
        FTPAccount account = getAccountById(accountId)
                .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
        
        if ("ftp".equals(account.getProtocol())) {
            return uploadToFTP(account, file);
        } else if ("sftp".equals(account.getProtocol())) {
            return uploadToSFTP(account, file);
        } else {
            throw new RuntimeException("Desteklenmeyen protokol: " + account.getProtocol());
        }
    }
    
    private boolean uploadToFTP(FTPAccount account, MultipartFile file) {
        FTPClient ftpClient = new FTPClient();
        
        try {
            ftpClient.connect(account.getHost(), account.getPort());
            ftpClient.login(account.getUsername(), account.getPassword());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            return ftpClient.storeFile(file.getOriginalFilename(), file.getInputStream());
            
        } catch (Exception e) {
            throw new RuntimeException("FTP yükleme hatası: " + e.getMessage());
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
    
    private boolean uploadToSFTP(FTPAccount account, MultipartFile file) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        
        try {
            session = jsch.getSession(account.getUsername(), account.getHost(), account.getPort());
            session.setPassword(account.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            
            sftpChannel.put(file.getInputStream(), file.getOriginalFilename());
            return true;
            
        } catch (JSchException | SftpException | IOException e) {
            throw new RuntimeException("SFTP yükleme hatası: " + e.getMessage());
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
    
    public byte[] downloadFile(Long accountId, String filename) {
        FTPAccount account = getAccountById(accountId)
                .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
        
        if ("ftp".equals(account.getProtocol())) {
            return downloadFromFTP(account, filename);
        } else if ("sftp".equals(account.getProtocol())) {
            return downloadFromSFTP(account, filename);
        } else {
            throw new RuntimeException("Desteklenmeyen protokol: " + account.getProtocol());
        }
    }
    
    private byte[] downloadFromFTP(FTPAccount account, String filename) {
        FTPClient ftpClient = new FTPClient();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            ftpClient.connect(account.getHost(), account.getPort());
            ftpClient.login(account.getUsername(), account.getPassword());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            ftpClient.retrieveFile(filename, outputStream);
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("FTP indirme hatası: " + e.getMessage());
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
    
    private byte[] downloadFromSFTP(FTPAccount account, String filename) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        
        try {
            session = jsch.getSession(account.getUsername(), account.getHost(), account.getPort());
            session.setPassword(account.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            sftpChannel.get(filename, outputStream);
            return outputStream.toByteArray();
            
        } catch (JSchException | SftpException e) {
            throw new RuntimeException("SFTP indirme hatası: " + e.getMessage());
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
    
    public boolean deleteFile(Long accountId, String filename) {
        FTPAccount account = getAccountById(accountId)
                .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
        
        if ("ftp".equals(account.getProtocol())) {
            return deleteFromFTP(account, filename);
        } else if ("sftp".equals(account.getProtocol())) {
            return deleteFromSFTP(account, filename);
        } else {
            throw new RuntimeException("Desteklenmeyen protokol: " + account.getProtocol());
        }
    }
    
    private boolean deleteFromFTP(FTPAccount account, String filename) {
        FTPClient ftpClient = new FTPClient();
        
        try {
            ftpClient.connect(account.getHost(), account.getPort());
            ftpClient.login(account.getUsername(), account.getPassword());
            
            return ftpClient.deleteFile(filename);
            
        } catch (Exception e) {
            throw new RuntimeException("FTP silme hatası: " + e.getMessage());
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
    
    private boolean deleteFromSFTP(FTPAccount account, String filename) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp sftpChannel = null;
        
        try {
            session = jsch.getSession(account.getUsername(), account.getHost(), account.getPort());
            session.setPassword(account.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
            
            sftpChannel.rm(filename);
            return true;
            
        } catch (JSchException | SftpException e) {
            throw new RuntimeException("SFTP silme hatası: " + e.getMessage());
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
    
    // File Info class
    public static class FileInfo {
        private String name;
        private long size;
        private boolean isDirectory;
        private long lastModified;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        
        public boolean isDirectory() { return isDirectory; }
        public void setDirectory(boolean directory) { isDirectory = directory; }
        
        public long getLastModified() { return lastModified; }
        public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    }
}
