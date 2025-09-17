package com.xfer.service;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InitializationService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Autowired
    private AuthService authService;

    @PostConstruct
    public void initialize() {
        System.out.println("=== Xfer FTP Web Service Starting ===");
        
        // Create data directory for H2 database
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("✓ Created data directory: " + dataDir.getAbsolutePath());
            } else {
                System.err.println("✗ Failed to create data directory: " + dataDir.getAbsolutePath());
            }
        } else {
            System.out.println("✓ Data directory already exists: " + dataDir.getAbsolutePath());
        }

        // Create uploads directory
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            boolean created = uploadDirFile.mkdirs();
            if (created) {
                System.out.println("✓ Created uploads directory: " + uploadDirFile.getAbsolutePath());
            } else {
                System.err.println("✗ Failed to create uploads directory: " + uploadDirFile.getAbsolutePath());
            }
        } else {
            System.out.println("✓ Uploads directory already exists: " + uploadDirFile.getAbsolutePath());
        }
        
        // Initialize admin user
        try {
            authService.initializeAdminUser();
            System.out.println("✓ Admin user initialized");
        } catch (Exception e) {
            System.err.println("✗ Failed to initialize admin user: " + e.getMessage());
        }
        
        System.out.println("=== Directory Setup Complete ===");
    }
}
