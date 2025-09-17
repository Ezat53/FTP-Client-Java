package com.xfer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xfer.entity.FTPAccount;
import com.xfer.entity.User;
import com.xfer.service.FTPService;
import com.xfer.service.TransferService;

@RestController
@RequestMapping("/api")
public class FTPController {
    
    @Autowired
    private FTPService ftpService;
    
    
    @Autowired
    private TransferService transferService;
    
    @PostMapping("/upload/{accountId}")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @PathVariable Long accountId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "path", required = false) String path,
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
            
            // Check if user has upload permission
            if (!"admin".equals(currentUser.getRole()) && !ftpService.hasUserPermission(accountId, currentUser.getId(), "upload")) {
                response.put("success", false);
                response.put("message", "Dosya yükleme yetkiniz yok.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Optional<FTPAccount> accountOpt = ftpService.getAccountById(accountId);
            if (!accountOpt.isPresent()) {
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
            
            // Upload directly to FTP/SFTP server
            String filename = file.getOriginalFilename();
            System.out.println("Starting upload for file: " + filename + " to account: " + accountId + " at path: " + (path != null ? path : "default"));
            
            boolean success = ftpService.uploadFile(accountId, file, path);
            
            if (success) {
                System.out.println("Upload successful for file: " + filename);
                // Log the transfer
                transferService.logTransfer(currentUser.getId(), accountId, "upload", 
                        filename, file.getSize(), "success", null);
                
                response.put("success", true);
                response.put("message", "Dosya başarıyla yüklendi");
            } else {
                System.out.println("Upload failed for file: " + filename);
                transferService.logTransfer(currentUser.getId(), accountId, "upload", 
                        filename, 0L, "error", "Yükleme başarısız");
                
                response.put("success", false);
                response.put("message", "Dosya yüklenirken hata oluştu");
            }
            
        } catch (Exception e) {
            System.out.println("Upload exception for file: " + file.getOriginalFilename() + " - " + e.getMessage());
            e.printStackTrace();
            
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
            @RequestParam(value = "path", required = false) String path,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            // Check if user has access to this account
            if (!ftpService.hasUserAccess(accountId, currentUser.getId())) {
                return ResponseEntity.badRequest().build();
            }
            
            Optional<FTPAccount> accountOpt = ftpService.getAccountById(accountId);
            if (!accountOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            FTPAccount account = accountOpt.get();
            
            byte[] fileData = ftpService.downloadFile(accountId, filename, path);
            
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
            @RequestParam(value = "path", required = false) String path,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();
        
        // Check if user has delete permission
        if (!"admin".equals(currentUser.getRole()) && !ftpService.hasUserPermission(accountId, currentUser.getId(), "delete")) {
            response.put("success", false);
            response.put("message", "Dosya silme yetkiniz yok.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            Optional<FTPAccount> accountOpt = ftpService.getAccountById(accountId);
            if (!accountOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "FTP hesabı bulunamadı");
                return ResponseEntity.badRequest().body(response);
            }
            
            FTPAccount account = accountOpt.get();
            
            // Check if user has access to this account (admin or assigned user)
            if (!"admin".equals(currentUser.getRole()) && !account.getOwnerId().equals(currentUser.getId()) && !ftpService.hasUserAccess(accountId, currentUser.getId())) {
                response.put("success", false);
                response.put("message", "Bu hesaptan dosya silme yetkiniz yok");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean success = ftpService.deleteFile(accountId, filename, path);
            
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
            if (!accountOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "FTP hesabı bulunamadı");
                return ResponseEntity.badRequest().body(response);
            }
            
            FTPAccount account = accountOpt.get();
            
            List<FTPService.FileInfo> files = ftpService.listFiles(accountId);
            
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
