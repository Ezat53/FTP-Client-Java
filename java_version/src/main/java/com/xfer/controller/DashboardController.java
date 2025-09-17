package com.xfer.controller;

import com.xfer.entity.User;
import com.xfer.service.FTPService;
import com.xfer.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private FTPService ftpService;
    
    @Autowired
    private TransferService transferService;
    
    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        
        // Get user's assigned FTP accounts
        List<com.xfer.entity.FTPAccount> ftpAccounts = ftpService.getUserAccounts(currentUser.getId());
        
        // Get recent transfer logs
        List<com.xfer.entity.TransferLog> recentLogs = transferService.getRecentUserTransferLogs(currentUser.getId(), 10);
        
        model.addAttribute("ftpAccounts", ftpAccounts);
        model.addAttribute("recentLogs", recentLogs);
        model.addAttribute("currentUser", currentUser);
        
        return "dashboard";
    }
}
