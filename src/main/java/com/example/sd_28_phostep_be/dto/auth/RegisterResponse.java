package com.example.sd_28_phostep_be.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private boolean success;
    private String message;
    private Integer customerId;
    private String customerCode;
    
    public static RegisterResponse success(String message, Integer customerId, String customerCode) {
        return new RegisterResponse(true, message, customerId, customerCode);
    }
    
    public static RegisterResponse error(String message) {
        return new RegisterResponse(false, message, null, null);
    }
}
