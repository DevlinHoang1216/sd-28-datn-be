package com.example.sd_28_phostep_be.controller.auth;

import com.example.sd_28_phostep_be.dto.auth.LoginRequest;
import com.example.sd_28_phostep_be.dto.auth.LoginResponse;
import com.example.sd_28_phostep_be.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout(@RequestParam(required = false) Integer userId) {
        LoginResponse response = authService.logout(userId);
        return ResponseEntity.ok(response);
    }
}
