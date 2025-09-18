package com.xfer.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DatabaseController {
    
    @GetMapping("/database")
    @PreAuthorize("hasRole('ADMIN')")
    public String databasePage(Model model) {
        // H2 Console bilgilerini model'e ekle
        model.addAttribute("h2ConsoleUrl", "/xfer-ftp-web-service/h2-console");
        model.addAttribute("jdbcUrl", "jdbc:h2:file:./data/ftp_manager");
        model.addAttribute("username", "sa");
        model.addAttribute("password", "admin123");
        
        return "admin/database";
    }
}
