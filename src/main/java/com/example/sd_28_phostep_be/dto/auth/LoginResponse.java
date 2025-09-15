package com.example.sd_28_phostep_be.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer id;
    private String ma;
    private String tenDangNhap;
    private String email;
    private String soDienThoai;
    private String tenQuyen;
    private Integer capQuyenHan;
    private boolean success;
    private String message;
    
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
