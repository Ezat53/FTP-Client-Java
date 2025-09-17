package com.xfer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xfer.entity.FTPUserAssignment;
import com.xfer.entity.User;
import com.xfer.repository.FTPUserAssignmentRepository;
import com.xfer.service.FTPService;
import com.xfer.service.TransferService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private FTPService ftpService;
    
    @Autowired
    private FTPUserAssignmentRepository ftpUserAssignmentRepository;
    
    @Autowired
    private TransferService transferService;
    
    @GetMapping
    public String dashboard(Model model, Authentication authentication, 
                          @RequestParam(value = "error", required = false) String error) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Get user's assigned FTP accounts
        List<com.xfer.entity.FTPAccount> ftpAccounts = ftpService.getUserAccounts(currentUser.getId());
        
        // Get recent transfer logs
        List<com.xfer.entity.TransferLog> recentLogs = transferService.getRecentUserTransferLogs(currentUser.getId(), 10);
        
        model.addAttribute("ftpAccounts", ftpAccounts);
        model.addAttribute("recentLogs", recentLogs);
        model.addAttribute("currentUser", currentUser);
        
        // Handle error messages
        if ("access_denied".equals(error)) {
            model.addAttribute("error", "Bu sayfaya erişim yetkiniz yok. Sadece admin kullanıcıları erişebilir.");
        }
        
        return "dashboard";
    }
    
    @GetMapping("/browse/{accountId}")
    public String browseFiles(@PathVariable Long accountId, 
                             @RequestParam(required = false) String path, 
                             Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Check if user has access to this account
        if (!ftpService.hasUserAccess(accountId, currentUser.getId())) {
            model.addAttribute("error", "Bu hesaba erişim yetkiniz yok");
            return "redirect:/dashboard";
        }
        
        try {
            // Get account details
            com.xfer.entity.FTPAccount account = ftpService.getAccountById(accountId)
                    .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
            
            System.out.println("Browse request - Account ID: " + accountId + ", Path: " + path);
            
            // Get files list
            List<FTPService.FileInfo> files = ftpService.listFiles(accountId, path);
            
            // Get user's assignment for this account
            FTPUserAssignment userAssignment = null;
            if (!"admin".equals(currentUser.getRole())) {
                List<FTPUserAssignment> assignments = ftpUserAssignmentRepository.findByFtpAccountIdWithUser(accountId);
                userAssignment = assignments.stream()
                        .filter(assignment -> assignment.getUserId().equals(currentUser.getId()))
                        .findFirst()
                        .orElse(null);
            }
            
        model.addAttribute("account", account);
        model.addAttribute("files", files);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("assignment", userAssignment);
        model.addAttribute("currentPath", path != null ? path : "/");
            
            return "browse";
            
        } catch (Exception e) {
            model.addAttribute("error", "Dosya listesi alınırken hata: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }
}
