package com.example.sd_28_phostep_be.config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {
    
    // Client application runs on port 3000
    @Value("${client.frontend.url:http://localhost:3000}")
    private String clientFrontendUrl;
    
    // Backend runs on port 8080
    private static final String BACKEND_URL = "http://localhost:8080";
    
    // Getters
    public String getClientFrontendUrl() {
        return clientFrontendUrl;
    }
    
    public String getBackendUrl() {
        return BACKEND_URL;
    }
}
