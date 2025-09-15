package com.example.sd_28_phostep_be.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String tenDangNhap;
    private String matKhau;
    private Boolean rememberMe;
}
