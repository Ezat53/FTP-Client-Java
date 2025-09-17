package com.xfer.config;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xfer.service.AuthService;

@Component
public class ApplicationStartupListener implements ServletContextListener {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Autowired
    private AuthService authService;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== Xfer FTP Web Service Starting ===");
        
        // Create data directory for SQLite database
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

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("=== Xfer FTP Web Service Shutting Down ===");
    }
}
