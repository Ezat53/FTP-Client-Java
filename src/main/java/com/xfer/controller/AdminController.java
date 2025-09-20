package com.xfer.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.xfer.entity.FTPAccount;
import com.xfer.entity.FTPUserAssignment;
import com.xfer.entity.User;
import com.xfer.repository.FTPUserAssignmentRepository;
import com.xfer.service.AuthService;
import com.xfer.service.FTPService;
import com.xfer.service.TransferService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private FTPService ftpService;
    
    @Autowired
    private FTPUserAssignmentRepository ftpUserAssignmentRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private TransferService transferService;
    
    @GetMapping
    public String adminPanel(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Check if user is admin
        if (!"admin".equals(currentUser.getRole())) {
            return "redirect:/dashboard?error=access_denied";
        }
        
        // Get all FTP accounts
        List<FTPAccount> allAccounts = ftpService.getAllAccounts();
        
        // Get all users
        List<User> allUsers = authService.getAllUsers();
        
        // Get recent transfer logs
        List<com.xfer.entity.TransferLog> recentLogs = transferService.getRecentTransferLogs(20);
        
        // Get transfer statistics
        long totalTransfers = transferService.getTotalTransferCount();
        long todayTransfers = transferService.getTodayTransferCount();
        long totalSize = transferService.getTotalTransferSize();
        
        model.addAttribute("ftpAccounts", allAccounts);
        model.addAttribute("users", allUsers);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("recentLogs", recentLogs);
        model.addAttribute("totalTransfers", totalTransfers);
        model.addAttribute("todayTransfers", todayTransfers);
        model.addAttribute("totalSize", totalSize);
        
        return "admin/admin";
    }
    
    @GetMapping("/add-ftp")
    public String addFTPForm(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Check if user is admin
        if (!"admin".equals(currentUser.getRole())) {
            return "redirect:/dashboard?error=access_denied";
        }
        
        List<User> users = authService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/admin_add_ftp";
    }
    
    @PostMapping("/add-ftp")
    public String addFTP(@RequestParam Map<String, String> params, 
                        @RequestParam(value = "userIds", required = false) List<Long> userIds,
                        Authentication authentication, RedirectAttributes redirectAttributes) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        // Check if user is admin
        if (!"admin".equals(currentUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Bu işlem için admin yetkisi gerekli");
            return "redirect:/dashboard";
        }
        
        try {
            String name = params.get("name");
            String protocol = params.get("protocol");
            String host = params.get("host");
            Integer port = Integer.parseInt(params.get("port"));
            String username = params.get("username");
            String password = params.get("password");
            String remotePath = params.get("remote_path");
            
            // Create account first
            FTPAccount account = ftpService.createAccount(name, protocol, host, port, 
                    username, password, remotePath, currentUser.getId(), null);
            
            // Process user assignments with permissions
            if (userIds != null && !userIds.isEmpty()) {
                for (Long userId : userIds) {
                    // Get permissions for this user
                    Boolean canRead = params.get("permissions_" + userId + "_read") != null;
                    Boolean canWrite = params.get("permissions_" + userId + "_write") != null;
                    Boolean canDelete = params.get("permissions_" + userId + "_delete") != null;
                    Boolean canUpload = params.get("permissions_" + userId + "_upload") != null;
                    
                    // Create assignment with permissions
                    FTPUserAssignment assignment = new FTPUserAssignment(
                        account.getId(), userId, currentUser.getId(),
                        canRead, canWrite, canDelete, canUpload
                    );
                    ftpUserAssignmentRepository.save(assignment);
                }
            }
            
            redirectAttributes.addFlashAttribute("success", "FTP hesabı başarıyla oluşturuldu");
            return "redirect:/admin";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "FTP hesabı oluşturulurken hata: " + e.getMessage());
            return "redirect:/admin/add-ftp";
        }
    }
    
    @GetMapping("/edit-ftp/{id}")
    public String editFTPForm(@PathVariable Long id, Model model) {
        FTPAccount account = ftpService.getAccountById(id)
                .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
        
        List<User> users = authService.getAllUsers();
        List<FTPUserAssignment> assignments = ftpUserAssignmentRepository.findByFtpAccountIdWithUser(id);
        
        model.addAttribute("account", account);
        model.addAttribute("users", users);
        model.addAttribute("assignments", assignments);
        
        return "admin/admin_edit_ftp";
    }
    
    @PostMapping("/edit-ftp/{id}")
    @Transactional
    public String editFTP(@PathVariable Long id, @RequestParam Map<String, String> params,
                         @RequestParam(value = "userIds", required = false) List<Long> userIds,
                         Authentication authentication, RedirectAttributes redirectAttributes) {
        
        try {
            User currentUser = (User) authentication.getPrincipal();
            String name = params.get("name");
            String protocol = params.get("protocol");
            String host = params.get("host");
            Integer port = Integer.parseInt(params.get("port"));
            String username = params.get("username");
            String password = params.get("password");
            
            // Update account basic info
            ftpService.updateAccount(id, name, protocol, host, port, username, password, null);
            
            // Update user assignments with permissions
            System.out.println("Updating assignments for FTP account " + id);
            System.out.println("Selected user IDs: " + (userIds != null ? userIds.toString() : "null"));
            
            if (userIds != null && !userIds.isEmpty()) {
                // Delete existing assignments
                System.out.println("Deleting existing assignments...");
                ftpUserAssignmentRepository.deleteByFtpAccountId(id);
                
                // Create new assignments with permissions
                for (Long userId : userIds) {
                    // Get permissions for this user
                    Boolean canRead = params.get("permissions_" + userId + "_read") != null;
                    Boolean canWrite = params.get("permissions_" + userId + "_write") != null;
                    Boolean canDelete = params.get("permissions_" + userId + "_delete") != null;
                    Boolean canUpload = params.get("permissions_" + userId + "_upload") != null;
                    
                    System.out.println("Creating assignment for user " + userId + 
                                     " - Read: " + canRead + ", Write: " + canWrite + 
                                     ", Delete: " + canDelete + ", Upload: " + canUpload);
                    
                    // Create assignment with permissions
                    FTPUserAssignment assignment = new FTPUserAssignment(
                        id, userId, currentUser.getId(),
                        canRead, canWrite, canDelete, canUpload
                    );
                    ftpUserAssignmentRepository.save(assignment);
                    System.out.println("Assignment saved for user " + userId);
                }
            } else {
                // Remove all assignments if no users selected
                System.out.println("No users selected, removing all assignments...");
                ftpUserAssignmentRepository.deleteByFtpAccountId(id);
            }
            
            redirectAttributes.addFlashAttribute("success", "FTP hesabı başarıyla güncellendi");
            return "redirect:/admin";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "FTP hesabı güncellenirken hata: " + e.getMessage());
            return "redirect:/admin/edit-ftp/" + id;
        }
    }
    
    @PostMapping("/delete-ftp/{id}")
    public String deleteFTP(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Deleting FTP account with ID: " + id);
            boolean success = ftpService.deleteAccount(id);
            if (success) {
                System.out.println("FTP account deleted successfully");
                redirectAttributes.addFlashAttribute("success", "FTP hesabı başarıyla silindi");
            } else {
                System.out.println("Failed to delete FTP account");
                redirectAttributes.addFlashAttribute("error", "FTP hesabı silinirken hata oluştu");
            }
        } catch (Exception e) {
            System.out.println("Exception while deleting FTP account: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "FTP hesabı silinirken hata: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }
    
    @GetMapping("/add-user")
    public String addUserForm(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Check if user is admin
        if (!"admin".equals(currentUser.getRole())) {
            return "redirect:/dashboard?error=access_denied";
        }
        
        return "admin/add_user";
    }
    
    @PostMapping("/add-user")
    public String addUser(@RequestParam Map<String, String> params, 
                         Authentication authentication, RedirectAttributes redirectAttributes) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Check if user is admin
        if (!"admin".equals(currentUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Bu işlem için admin yetkisi gerekli");
            return "redirect:/dashboard";
        }
        
        try {
            String username = params.get("username");
            String password = params.get("password");
            String role = params.get("role");
            
            authService.createUser(username, password, role);
            
            redirectAttributes.addFlashAttribute("success", "Kullanıcı başarıyla oluşturuldu");
            return "redirect:/admin";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Kullanıcı oluşturulurken hata: " + e.getMessage());
            return "redirect:/admin/add-user";
        }
    }
    
    @GetMapping("/edit-user/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = authService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/admin_edit_user";
    }
    
    @PostMapping("/edit-user/{id}")
    public String editUser(@PathVariable Long id, @RequestParam Map<String, String> params,
                          RedirectAttributes redirectAttributes) {
        try {
            String password = params.get("password");
            String role = params.get("role");
            
            User user = authService.getUserById(id);
            user.setRole(role);
            if (password != null && !password.trim().isEmpty()) {
                authService.updateUserPassword(id, password);
            }
            
            redirectAttributes.addFlashAttribute("success", "Kullanıcı başarıyla güncellendi");
            return "redirect:/admin";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Kullanıcı güncellenirken hata: " + e.getMessage());
            return "redirect:/admin/edit-user/" + id;
        }
    }
    
    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Don't allow deleting admin users
            User user = authService.getUserById(id);
            if ("admin".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("error", "Admin kullanıcıları silinemez");
                return "redirect:/admin";
            }
            
            authService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Kullanıcı başarıyla silindi");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Kullanıcı silinirken hata: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }
    
    @GetMapping("/debug-account/{id}")
    public String debugAccount(@PathVariable Long id, Model model) {
        try {
            FTPAccount account = ftpService.getAccountById(id)
                    .orElseThrow(() -> new RuntimeException("FTP hesabı bulunamadı"));
            
            model.addAttribute("account", account);
            model.addAttribute("protocolRaw", account.getProtocol());
            model.addAttribute("protocolLength", account.getProtocol() != null ? account.getProtocol().length() : 0);
            model.addAttribute("protocolBytes", account.getProtocol() != null ? java.util.Arrays.toString(account.getProtocol().getBytes()) : "null");
            
            return "admin/debug_account";
            
        } catch (Exception e) {
            model.addAttribute("error", "Debug hatası: " + e.getMessage());
            return "admin/debug_account";
        }
    }
}
