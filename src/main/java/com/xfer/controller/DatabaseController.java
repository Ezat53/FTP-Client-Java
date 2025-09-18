package com.xfer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class DatabaseController {
    
    @Value("${spring.datasource.url:jdbc:h2:file:./data/ftp_manager}")
    private String jdbcUrl;
    
    @Value("${spring.datasource.username:sa}")
    private String dbUsername;
    
    @Value("${spring.datasource.password:admin123}")
    private String dbPassword;
    
    @GetMapping("/database")
    @PreAuthorize("hasRole('ADMIN')")
    public String databasePage(Model model) {
        // H2 Console bilgilerini model'e ekle
        model.addAttribute("h2ConsoleUrl", "/xfer-ftp-web-service/h2-console");
        model.addAttribute("jdbcUrl", jdbcUrl);
        model.addAttribute("username", dbUsername);
        model.addAttribute("password", dbPassword);
        
        return "admin/database";
    }
}
