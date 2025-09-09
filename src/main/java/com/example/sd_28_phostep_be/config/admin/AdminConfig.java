package com.example.sd_28_phostep_be.config.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminConfig {
    
    // Admin application runs on port 5173
    @Value("${admin.frontend.url:http://localhost:5173}")
    private String adminFrontendUrl;
    
    // Backend runs on port 8080
    private static final String BACKEND_URL = "http://localhost:8080";
    
    // Getters
    public String getAdminFrontendUrl() {
        return adminFrontendUrl;
    }
    
    public String getBackendUrl() {
        return BACKEND_URL;
    }
}
