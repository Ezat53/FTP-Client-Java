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
                                  String username, String password, String remotePath, Long ownerId, List<Long> userIds) {
        FTPAccount account = new FTPAccount(name, protocol, host, port, username, password, remotePath, ownerId);
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
            
            // Delete related assignments first using native query to avoid Hibernate issues
            try {
                ftpUserAssignmentRepository.deleteByFtpAccountId(accountId);
                System.out.println("Deleted assignments for FTP account " + accountId);
            } catch (Exception e) {
                System.out.println("Warning: Could not delete assignments for FTP account " + accountId + ": " + e.getMessage());
                // Try individual deletion as fallback
                try {
                    List<FTPUserAssignment> assignments = ftpUserAssignmentRepository.findByFtpAccountId(accountId);
                    for (FTPUserAssignment assignment : assignments) {
                        try {
                            ftpUserAssignmentRepository.delete(assignment);
                        } catch (Exception ex) {
                            System.out.println("Could not delete assignment " + assignment.getId() + ": " + ex.getMessage());
                        }
                    }
                } catch (Exception ex2) {
                    System.out.println("Fallback assignment deletion also failed: " + ex2.getMessage());
                }
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
    
    public boolean hasUserPermission(Long accountId, Long userId, String permission) {
        List<FTPUserAssignment> assignments = ftpUserAssignmentRepository.findByFtpAccountIdWithUser(accountId);
        return assignments.stream()
                .filter(assignment -> assignment.getUserId().equals(userId))
                .anyMatch(assignment -> {
                    switch (permission.toLowerCase()) {
                        case "read":
                            return assignment.getCanRead() != null && assignment.getCanRead();
                        case "write":
                            return assignment.getCanWrite() != null && assignment.getCanWrite();
                        case "delete":
                            return assignment.getCanDelete() != null && assignment.getCanDelete();
                        case "upload":
                            return assignment.getCanUpload() != null && assignment.getCanUpload();
                        default:
                            return false;
                    }
                });
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
        return uploadFile(accountId, file, null);
    }
    
    public boolean uploadFile(Long accountId, MultipartFile file, String path) {
        try {
            FTPAccount account = getAccountById(accountId)
                    .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
            
            System.out.println("Uploading file: " + file.getOriginalFilename() + " (" + file.getSize() + " bytes) to " + account.getProtocol() + "://" + account.getHost() + " at path: " + (path != null ? path : "default"));
            
            if ("ftp".equals(account.getProtocol())) {
                return uploadToFTP(account, file, path);
            } else if ("sftp".equals(account.getProtocol())) {
                return uploadToSFTP(account, file, path);
            } else {
                throw new RuntimeException("Desteklenmeyen protokol: " + account.getProtocol());
            }
        } catch (Exception e) {
            System.out.println("Error in uploadFile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean uploadToFTP(FTPAccount account, MultipartFile file) {
        return uploadToFTP(account, file, null);
    }
    
    private boolean uploadToFTP(FTPAccount account, MultipartFile file, String path) {
        FTPClient ftpClient = new FTPClient();
        
        try {
            System.out.println("Connecting to FTP: " + account.getHost() + ":" + account.getPort());
            ftpClient.connect(account.getHost(), account.getPort());
            
            if (!ftpClient.isConnected()) {
                System.out.println("Failed to connect to FTP server");
                return false;
            }
            
            System.out.println("Logging in with username: " + account.getUsername());
            boolean loginSuccess = ftpClient.login(account.getUsername(), account.getPassword());
            if (!loginSuccess) {
                System.out.println("FTP login failed");
                return false;
            }
            
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            // Check if we can write to the directory
            System.out.println("Current working directory: " + ftpClient.printWorkingDirectory());
            System.out.println("FTP server type: " + ftpClient.getSystemType());
            
            // Determine target directory
            String targetPath = null;
            if (path != null && !path.trim().isEmpty()) {
                targetPath = path.trim();
                System.out.println("Using specified path: " + targetPath);
            } else if (account.getRemotePath() != null && !account.getRemotePath().trim().isEmpty()) {
                targetPath = account.getRemotePath().trim();
                System.out.println("Using account remote path: " + targetPath);
            } else {
                targetPath = "/GarantiAsset";
                System.out.println("Using default path: " + targetPath);
            }
            
            // Change to the target directory
            if (targetPath != null && !targetPath.equals("/")) {
                System.out.println("Changing to target directory: " + targetPath);
                boolean changed = ftpClient.changeWorkingDirectory(targetPath);
                if (changed) {
                    System.out.println("Successfully changed to: " + ftpClient.printWorkingDirectory());
                } else {
                    System.out.println("Failed to change to target directory: " + targetPath);
                    System.out.println("FTP reply: " + ftpClient.getReplyString());
                    // Try to create the directory
                    System.out.println("Attempting to create directory: " + targetPath);
                    boolean created = ftpClient.makeDirectory(targetPath);
                    if (created) {
                        System.out.println("Directory created successfully");
                        ftpClient.changeWorkingDirectory(targetPath);
                    } else {
                        System.out.println("Failed to create directory: " + ftpClient.getReplyString());
                    }
                }
            } else {
                System.out.println("Target path is root (/), staying in current directory");
            }
            
            
            // Clean filename for FTP
            String filename = file.getOriginalFilename();
            if (filename == null || filename.trim().isEmpty()) {
                filename = "uploaded_file_" + System.currentTimeMillis();
            }
            
            // Remove any problematic characters
            filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
            
            // If filename starts with a number, prefix with 'file_'
            if (filename.matches("^[0-9].*")) {
                filename = "file_" + filename;
            }
            
            // Ensure filename doesn't start with special characters
            if (filename.startsWith(".") || filename.startsWith("-")) {
                filename = "file_" + filename;
            }
            
            System.out.println("Uploading file: " + filename + " (original: " + file.getOriginalFilename() + ")");
            
            // Try different filename variations if the first one fails
            String[] filenameVariations = {
                filename,
                "upload_" + filename,
                "file_" + filename,
                filename.replace(".", "_") + ".xls",
                "document_" + System.currentTimeMillis() + ".xls"
            };
            
            boolean uploadSuccess = false;
            String successfulFilename = null;
            
            for (String testFilename : filenameVariations) {
                System.out.println("Trying filename: " + testFilename);
                uploadSuccess = ftpClient.storeFile(testFilename, file.getInputStream());
                System.out.println("Upload result for '" + testFilename + "': " + uploadSuccess);
                
                if (uploadSuccess) {
                    successfulFilename = testFilename;
                    break;
                } else {
                    // Get FTP reply details
                    int replyCode = ftpClient.getReplyCode();
                    String replyString = ftpClient.getReplyString();
                    System.out.println("FTP storeFile failed. Reply code: " + replyCode);
                    System.out.println("FTP reply string: " + replyString);
                    
                    // Reset input stream for next attempt
                    try {
                        file.getInputStream().reset();
                    } catch (Exception e) {
                        System.out.println("Could not reset input stream, trying with new stream");
                    }
                }
            }
            
            if (uploadSuccess) {
                System.out.println("Successfully uploaded as: " + successfulFilename);
            } else {
                System.out.println("All filename variations failed");
            }
            
            if (!uploadSuccess) {
                System.out.println("FTP storeFile failed. Reply code: " + ftpClient.getReplyCode());
                System.out.println("FTP reply string: " + ftpClient.getReplyString());
                
                // Check if we can write to the directory
                System.out.println("Current working directory: " + ftpClient.printWorkingDirectory());
                System.out.println("Directory listing:");
                FTPFile[] files = ftpClient.listFiles();
                for (FTPFile f : files) {
                    System.out.println("  " + f.getName() + " (" + f.getSize() + " bytes)");
                }
            }
            
            return uploadSuccess;
            
        } catch (Exception e) {
            System.out.println("FTP upload error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.disconnect();
                    System.out.println("FTP connection closed");
                }
            } catch (IOException e) {
                System.out.println("Error closing FTP connection: " + e.getMessage());
            }
        }
    }
    
    private boolean uploadToSFTP(FTPAccount account, MultipartFile file) {
        return uploadToSFTP(account, file, null);
    }
    
    private boolean uploadToSFTP(FTPAccount account, MultipartFile file, String path) {
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
            
            // Determine target directory
            String targetPath = null;
            if (path != null && !path.trim().isEmpty()) {
                targetPath = path.trim();
                System.out.println("Using specified path: " + targetPath);
            } else if (account.getRemotePath() != null && !account.getRemotePath().trim().isEmpty()) {
                targetPath = account.getRemotePath().trim();
                System.out.println("Using account remote path: " + targetPath);
            } else {
                targetPath = "/GarantiAsset";
                System.out.println("Using default path: " + targetPath);
            }
            
            // Change to the target directory
            if (targetPath != null && !targetPath.equals("/")) {
                System.out.println("Changing to target directory: " + targetPath);
                try {
                    sftpChannel.cd(targetPath);
                    System.out.println("Successfully changed to: " + sftpChannel.pwd());
                } catch (SftpException e) {
                    System.out.println("Failed to change to target directory: " + targetPath + " - " + e.getMessage());
                    // Try to create the directory
                    try {
                        System.out.println("Attempting to create directory: " + targetPath);
                        sftpChannel.mkdir(targetPath);
                        sftpChannel.cd(targetPath);
                        System.out.println("Directory created and changed successfully");
                    } catch (SftpException e2) {
                        System.out.println("Failed to create directory: " + e2.getMessage());
                    }
                }
            } else {
                System.out.println("Target path is root (/), staying in current directory");
            }
            
            // Clean filename for SFTP
            String filename = file.getOriginalFilename();
            if (filename == null || filename.trim().isEmpty()) {
                filename = "uploaded_file_" + System.currentTimeMillis();
            }
            
            // Remove any problematic characters
            filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");
            
            // If filename starts with a number, prefix with 'file_'
            if (filename.matches("^[0-9].*")) {
                filename = "file_" + filename;
            }
            
            // Ensure filename doesn't start with special characters
            if (filename.startsWith(".") || filename.startsWith("-")) {
                filename = "file_" + filename;
            }
            
            System.out.println("Uploading file: " + filename + " (original: " + file.getOriginalFilename() + ")");
            sftpChannel.put(file.getInputStream(), filename);
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
        return downloadFile(accountId, filename, null);
    }
    
    public byte[] downloadFile(Long accountId, String filename, String path) {
        FTPAccount account = getAccountById(accountId)
                .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
        
        if ("ftp".equals(account.getProtocol())) {
            return downloadFromFTP(account, filename, path);
        } else if ("sftp".equals(account.getProtocol())) {
            return downloadFromSFTP(account, filename, path);
        } else {
            throw new RuntimeException("Desteklenmeyen protokol: " + account.getProtocol());
        }
    }
    
    private byte[] downloadFromFTP(FTPAccount account, String filename) {
        return downloadFromFTP(account, filename, null);
    }
    
    private byte[] downloadFromFTP(FTPAccount account, String filename, String path) {
        FTPClient ftpClient = new FTPClient();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try {
            ftpClient.connect(account.getHost(), account.getPort());
            ftpClient.login(account.getUsername(), account.getPassword());
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            // Change to the target directory if specified
            if (path != null && !path.trim().isEmpty() && !path.equals("/")) {
                System.out.println("Changing to download path: " + path);
                boolean changed = ftpClient.changeWorkingDirectory(path);
                if (changed) {
                    System.out.println("Successfully changed to: " + ftpClient.printWorkingDirectory());
                } else {
                    System.out.println("Failed to change to download path: " + path);
                }
            } else if (account.getRemotePath() != null && !account.getRemotePath().trim().isEmpty() && !account.getRemotePath().equals("/")) {
                System.out.println("Changing to account remote path: " + account.getRemotePath());
                boolean changed = ftpClient.changeWorkingDirectory(account.getRemotePath());
                if (changed) {
                    System.out.println("Successfully changed to: " + ftpClient.printWorkingDirectory());
                } else {
                    System.out.println("Failed to change to account remote path: " + account.getRemotePath());
                }
            }
            
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
        return downloadFromSFTP(account, filename, null);
    }
    
    private byte[] downloadFromSFTP(FTPAccount account, String filename, String path) {
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
            
            // Change to the target directory if specified
            if (path != null && !path.trim().isEmpty() && !path.equals("/")) {
                System.out.println("Changing to download path: " + path);
                try {
                    sftpChannel.cd(path);
                    System.out.println("Successfully changed to: " + sftpChannel.pwd());
                } catch (SftpException e) {
                    System.out.println("Failed to change to download path: " + path + " - " + e.getMessage());
                }
            } else if (account.getRemotePath() != null && !account.getRemotePath().trim().isEmpty() && !account.getRemotePath().equals("/")) {
                System.out.println("Changing to account remote path: " + account.getRemotePath());
                try {
                    sftpChannel.cd(account.getRemotePath());
                    System.out.println("Successfully changed to: " + sftpChannel.pwd());
                } catch (SftpException e) {
                    System.out.println("Failed to change to account remote path: " + account.getRemotePath() + " - " + e.getMessage());
                }
            }
            
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
        return deleteFile(accountId, filename, null);
    }
    
    public boolean deleteFile(Long accountId, String filename, String path) {
        FTPAccount account = getAccountById(accountId)
                .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
        
        if ("ftp".equals(account.getProtocol())) {
            return deleteFromFTP(account, filename, path);
        } else if ("sftp".equals(account.getProtocol())) {
            return deleteFromSFTP(account, filename, path);
        } else {
            throw new RuntimeException("Desteklenmeyen protokol: " + account.getProtocol());
        }
    }
    
    private boolean deleteFromFTP(FTPAccount account, String filename) {
        return deleteFromFTP(account, filename, null);
    }
    
    private boolean deleteFromFTP(FTPAccount account, String filename, String path) {
        FTPClient ftpClient = new FTPClient();
        
        try {
            ftpClient.connect(account.getHost(), account.getPort());
            ftpClient.login(account.getUsername(), account.getPassword());
            
            // Change to the target directory if specified
            if (path != null && !path.trim().isEmpty() && !path.equals("/")) {
                System.out.println("Changing to delete path: " + path);
                boolean changed = ftpClient.changeWorkingDirectory(path);
                if (changed) {
                    System.out.println("Successfully changed to: " + ftpClient.printWorkingDirectory());
                } else {
                    System.out.println("Failed to change to delete path: " + path);
                }
            } else if (account.getRemotePath() != null && !account.getRemotePath().trim().isEmpty() && !account.getRemotePath().equals("/")) {
                System.out.println("Changing to account remote path: " + account.getRemotePath());
                boolean changed = ftpClient.changeWorkingDirectory(account.getRemotePath());
                if (changed) {
                    System.out.println("Successfully changed to: " + ftpClient.printWorkingDirectory());
                } else {
                    System.out.println("Failed to change to account remote path: " + account.getRemotePath());
                }
            }
            
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
        return deleteFromSFTP(account, filename, null);
    }
    
    private boolean deleteFromSFTP(FTPAccount account, String filename, String path) {
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
            
            // Change to the target directory if specified
            if (path != null && !path.trim().isEmpty() && !path.equals("/")) {
                System.out.println("Changing to delete path: " + path);
                try {
                    sftpChannel.cd(path);
                    System.out.println("Successfully changed to: " + sftpChannel.pwd());
                } catch (SftpException e) {
                    System.out.println("Failed to change to delete path: " + path + " - " + e.getMessage());
                }
            } else if (account.getRemotePath() != null && !account.getRemotePath().trim().isEmpty() && !account.getRemotePath().equals("/")) {
                System.out.println("Changing to account remote path: " + account.getRemotePath());
                try {
                    sftpChannel.cd(account.getRemotePath());
                    System.out.println("Successfully changed to: " + sftpChannel.pwd());
                } catch (SftpException e) {
                    System.out.println("Failed to change to account remote path: " + account.getRemotePath() + " - " + e.getMessage());
                }
            }
            
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
