package com.xfer.controller;

import com.xfer.entity.FTPAccount;
import com.xfer.entity.User;
import com.xfer.service.FTPService;
import com.xfer.service.AuthService;
import com.xfer.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private FTPService ftpService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private TransferService transferService;
    
    @GetMapping
    public String adminPanel(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Get all FTP accounts
        List<FTPAccount> allAccounts = ftpService.getAllAccounts();
        
        // Get all users
        List<User> allUsers = authService.getAllUsers();
        
        model.addAttribute("ftpAccounts", allAccounts);
        model.addAttribute("users", allUsers);
        model.addAttribute("currentUser", currentUser);
        
        return "admin";
    }
    
    @GetMapping("/add-ftp")
    public String addFTPForm(Model model) {
        List<User> users = authService.getAllUsers();
        model.addAttribute("users", users);
        return "admin_add_ftp";
    }
    
    @PostMapping("/add-ftp")
    public String addFTP(@RequestParam Map<String, String> params, 
                        @RequestParam(value = "userIds", required = false) List<Long> userIds,
                        Authentication authentication, RedirectAttributes redirectAttributes) {
        
        User currentUser = (User) authentication.getPrincipal();
        
        try {
            String name = params.get("name");
            String protocol = params.get("protocol");
            String host = params.get("host");
            Integer port = Integer.parseInt(params.get("port"));
            String username = params.get("username");
            String password = params.get("password");
            
            FTPAccount account = ftpService.createAccount(name, protocol, host, port, 
                    username, password, currentUser.getId(), userIds);
            
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
        List<User> assignedUsers = ftpService.getAccountAssignments(id);
        List<Long> assignedUserIds = assignedUsers.stream()
                .map(User::getId)
                .collect(Collectors.toList());
        
        model.addAttribute("account", account);
        model.addAttribute("users", users);
        model.addAttribute("assignedUserIds", assignedUserIds);
        
        return "admin_edit_ftp";
    }
    
    @PostMapping("/edit-ftp/{id}")
    public String editFTP(@PathVariable Long id, @RequestParam Map<String, String> params,
                         @RequestParam(value = "userIds", required = false) List<Long> userIds,
                         RedirectAttributes redirectAttributes) {
        
        try {
            String name = params.get("name");
            String protocol = params.get("protocol");
            String host = params.get("host");
            Integer port = Integer.parseInt(params.get("port"));
            String username = params.get("username");
            String password = params.get("password");
            
            ftpService.updateAccount(id, name, protocol, host, port, username, password, userIds);
            
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
            boolean success = ftpService.deleteAccount(id);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "FTP hesabı başarıyla silindi");
            } else {
                redirectAttributes.addFlashAttribute("error", "FTP hesabı silinirken hata oluştu");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "FTP hesabı silinirken hata: " + e.getMessage());
        }
        
        return "redirect:/admin";
    }
    
    @GetMapping("/add-user")
    public String addUserForm() {
        return "admin_add_user";
    }
    
    @PostMapping("/add-user")
    public String addUser(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
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
        return "admin_edit_user";
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
}
