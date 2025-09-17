package com.xfer.service;

import com.xfer.entity.FTPAccount;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FTPOperationsService {
    
    public List<String> listFTPFiles(FTPAccount account) throws Exception {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(account.getHost(), account.getPort());
            ftp.login(account.getUsername(), account.getPassword());
            ftp.enterLocalPassiveMode();
            
            List<String> files = new ArrayList<>();
            ftp.listFiles(".", file -> {
                files.add(file.getName());
                return false;
            });
            
            return files;
        } finally {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }
    
    public List<String> listSFTPFiles(FTPAccount account) throws Exception {
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
            
            List<String> files = new ArrayList<>();
            sftpChannel.ls(".", entry -> {
                files.add(entry.getFilename());
                return 0;
            });
            
            return files;
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
    
    public boolean uploadToFTP(FTPAccount account, File localFile, String remoteFilename) throws Exception {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(account.getHost(), account.getPort());
            ftp.login(account.getUsername(), account.getPassword());
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            
            try (FileInputStream inputStream = new FileInputStream(localFile)) {
                return ftp.storeFile(remoteFilename, inputStream);
            }
        } finally {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }
    
    public boolean uploadToSFTP(FTPAccount account, File localFile, String remoteFilename) throws Exception {
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
            
            sftpChannel.put(new FileInputStream(localFile), remoteFilename);
            return true;
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
    
    public byte[] downloadFromFTP(FTPAccount account, String filename) throws Exception {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(account.getHost(), account.getPort());
            ftp.login(account.getUsername(), account.getPassword());
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            boolean success = ftp.retrieveFile(filename, outputStream);
            
            if (success) {
                return outputStream.toByteArray();
            } else {
                throw new Exception("Dosya indirilemedi: " + filename);
            }
        } finally {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }
    
    public byte[] downloadFromSFTP(FTPAccount account, String filename) throws Exception {
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
            
            try (InputStream inputStream = sftpChannel.get(filename);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                return outputStream.toByteArray();
            }
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
    
    public boolean deleteFromFTP(FTPAccount account, String filename) throws Exception {
        FTPClient ftp = new FTPClient();
        try {
            ftp.connect(account.getHost(), account.getPort());
            ftp.login(account.getUsername(), account.getPassword());
            
            return ftp.deleteFile(filename);
        } finally {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        }
    }
    
    public boolean deleteFromSFTP(FTPAccount account, String filename) throws Exception {
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
        } finally {
            if (sftpChannel != null) {
                sftpChannel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
