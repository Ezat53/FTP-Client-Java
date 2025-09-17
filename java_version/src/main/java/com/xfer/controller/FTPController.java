package com.xfer.controller;

import com.xfer.entity.FTPAccount;
import com.xfer.entity.User;
import com.xfer.service.FTPService;
import com.xfer.service.FTPOperationsService;
import com.xfer.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class FTPController {
    
    @Autowired
    private FTPService ftpService;
    
    @Autowired
    private FTPOperationsService ftpOperationsService;
    
    @Autowired
    private TransferService transferService;
    
    private static final String UPLOAD_DIR = "uploads";
    
    @PostMapping("/upload/{accountId}")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @PathVariable Long accountId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            // Check if user has access to this account
            if (!ftpService.hasUserAccess(accountId, currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Bu hesaba erişim yetkiniz yok");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<FTPAccount> accountOpt = ftpService.getAccountById(accountId);
            if (accountOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "FTP hesabı bulunamadı");
                return ResponseEntity.badRequest().body(response);
            }
            
            FTPAccount account = accountOpt.get();
            
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Dosya seçilmedi");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Validate file type
            if (!isValidFileType(file.getOriginalFilename())) {
                response.put("success", false);
                response.put("message", "Desteklenmeyen dosya türü");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Save file temporarily
            String filename = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());
            
            try {
                // Upload to FTP/SFTP server
                boolean success;
                if ("ftp".equals(account.getProtocol())) {
                    success = ftpOperationsService.uploadToFTP(account, filePath.toFile(), filename);
                } else if ("sftp".equals(account.getProtocol())) {
                    success = ftpOperationsService.uploadToSFTP(account, filePath.toFile(), filename);
                } else {
                    response.put("success", false);
                    response.put("message", "Desteklenmeyen protokol");
                    return ResponseEntity.badRequest().body(response);
                }
                
                if (success) {
                    // Log the transfer
                    transferService.logTransfer(currentUser.getId(), accountId, "upload", 
                            filename, file.getSize(), "success", null);
                    
                    response.put("success", true);
                    response.put("message", "Dosya başarıyla yüklendi");
                } else {
                    transferService.logTransfer(currentUser.getId(), accountId, "upload", 
                            filename, 0L, "error", "Yükleme başarısız");
                    
                    response.put("success", false);
                    response.put("message", "Dosya yüklenirken hata oluştu");
                }
                
            } finally {
                // Clean up local file
                Files.deleteIfExists(filePath);
            }
            
        } catch (Exception e) {
            transferService.logTransfer(currentUser.getId(), accountId, "upload", 
                    file.getOriginalFilename(), 0L, "error", e.getMessage());
            
            response.put("success", false);
            response.put("message", "Hata: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/download/{accountId}/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long accountId,
            @PathVariable String filename,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            // Check if user has access to this account
            if (!ftpService.hasUserAccess(accountId, currentUser.getId())) {
                return ResponseEntity.badRequest().build();
            }
            
            Optional<FTPAccount> accountOpt = ftpService.getAccountById(accountId);
            if (accountOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            FTPAccount account = accountOpt.get();
            
            byte[] fileData;
            if ("ftp".equals(account.getProtocol())) {
                fileData = ftpOperationsService.downloadFromFTP(account, filename);
            } else if ("sftp".equals(account.getProtocol())) {
                fileData = ftpOperationsService.downloadFromSFTP(account, filename);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            // Log the transfer
            transferService.logTransfer(currentUser.getId(), accountId, "download", 
                    filename, (long) fileData.length, "success", null);
            
            ByteArrayResource resource = new ByteArrayResource(fileData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileData.length)
                    .body(resource);
                    
        } catch (Exception e) {
            transferService.logTransfer(currentUser.getId(), accountId, "download", 
                    filename, 0L, "error", e.getMessage());
            
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/delete/{accountId}/{filename}")
    public ResponseEntity<Map<String, Object>> deleteFile(
            @PathVariable Long accountId,
            @PathVariable String filename,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();
        
        // Only admin users can delete files
        if (!"admin".equals(currentUser.getRole())) {
            response.put("success", false);
            response.put("message", "Dosya silme yetkiniz yok. Sadece admin kullanıcıları dosya silebilir.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            Optional<FTPAccount> accountOpt = ftpService.getAccountById(accountId);
            if (accountOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "FTP hesabı bulunamadı");
                return ResponseEntity.badRequest().body(response);
            }
            
            FTPAccount account = accountOpt.get();
            
            // Check if admin has access to this account
            if (!account.getOwnerId().equals(currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Bu hesaptan dosya silme yetkiniz yok");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean success;
            if ("ftp".equals(account.getProtocol())) {
                success = ftpOperationsService.deleteFromFTP(account, filename);
            } else if ("sftp".equals(account.getProtocol())) {
                success = ftpOperationsService.deleteFromSFTP(account, filename);
            } else {
                response.put("success", false);
                response.put("message", "Desteklenmeyen protokol");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (success) {
                transferService.logTransfer(currentUser.getId(), accountId, "delete", 
                        filename, 0L, "success", null);
                
                response.put("success", true);
                response.put("message", "Dosya başarıyla silindi");
            } else {
                transferService.logTransfer(currentUser.getId(), accountId, "delete", 
                        filename, 0L, "error", "Silme başarısız");
                
                response.put("success", false);
                response.put("message", "Dosya silinirken hata oluştu");
            }
            
        } catch (Exception e) {
            transferService.logTransfer(currentUser.getId(), accountId, "delete", 
                    filename, 0L, "error", e.getMessage());
            
            response.put("success", false);
            response.put("message", "Hata: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/list/{accountId}")
    public ResponseEntity<Map<String, Object>> listFiles(
            @PathVariable Long accountId,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            // Check if user has access to this account
            if (!ftpService.hasUserAccess(accountId, currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Bu hesaba erişim yetkiniz yok");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<FTPAccount> accountOpt = ftpService.getAccountById(accountId);
            if (accountOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "FTP hesabı bulunamadı");
                return ResponseEntity.badRequest().body(response);
            }
            
            FTPAccount account = accountOpt.get();
            
            List<String> files;
            if ("ftp".equals(account.getProtocol())) {
                files = ftpOperationsService.listFTPFiles(account);
            } else if ("sftp".equals(account.getProtocol())) {
                files = ftpOperationsService.listSFTPFiles(account);
            } else {
                response.put("success", false);
                response.put("message", "Desteklenmeyen protokol");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Log the transfer
            transferService.logTransfer(currentUser.getId(), accountId, "list", 
                    null, 0L, "success", null);
            
            response.put("success", true);
            response.put("files", files);
            
        } catch (Exception e) {
            transferService.logTransfer(currentUser.getId(), accountId, "list", 
                    null, 0L, "error", e.getMessage());
            
            response.put("success", false);
            response.put("message", "Hata: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    private boolean isValidFileType(String filename) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        String[] allowedExtensions = {
            "xls", "xlsx","csv", "json", "xml","sql"
        };
        
        for (String allowedExt : allowedExtensions) {
            if (allowedExt.equals(extension)) {
                return true;
            }
        }
        
        return false;
    }
}
