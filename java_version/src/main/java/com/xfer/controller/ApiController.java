package com.xfer.controller;

import com.xfer.entity.User;
import com.xfer.service.AuthService;
import com.xfer.service.FTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private FTPService ftpService;
    
    @Autowired
    private AuthService authService;
    
    @DeleteMapping("/account/{accountId}")
    public ResponseEntity<Map<String, Object>> deleteAccount(
            @PathVariable Long accountId,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            // Check if user owns the account or is admin
            if (!ftpService.getAccountById(accountId)
                    .map(account -> account.getOwnerId().equals(currentUser.getId()) || "admin".equals(currentUser.getRole()))
                    .orElse(false)) {
                response.put("success", false);
                response.put("message", "Bu hesaba erişim yetkiniz yok");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean success = ftpService.deleteAccount(accountId);
            if (success) {
                response.put("success", true);
                response.put("message", "Hesap başarıyla silindi");
            } else {
                response.put("success", false);
                response.put("message", "Hesap silinirken hata oluştu");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Hata: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/admin/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(
            @PathVariable Long userId,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        User currentUser = (User) authentication.getPrincipal();
        
        // Only admin users can delete users
        if (!"admin".equals(currentUser.getRole())) {
            response.put("success", false);
            response.put("message", "Bu işlem için admin yetkisi gerekli");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Don't allow deleting own account
        if (userId.equals(currentUser.getId())) {
            response.put("success", false);
            response.put("message", "Kendi hesabınızı silemezsiniz");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            boolean success = authService.deleteUser(userId);
            if (success) {
                response.put("success", true);
                response.put("message", "Kullanıcı başarıyla silindi");
            } else {
                response.put("success", false);
                response.put("message", "Kullanıcı silinirken hata oluştu");
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Hata: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
