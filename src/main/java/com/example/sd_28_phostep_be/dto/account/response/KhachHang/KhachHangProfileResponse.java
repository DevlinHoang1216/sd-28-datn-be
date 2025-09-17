package com.example.sd_28_phostep_be.dto.account.response.KhachHang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangProfileResponse {
    private Integer id;
    private String ma;
    private String ten;
    private String email;
    private String soDienThoai;
    private Short gioiTinh;
    private Date ngaySinh;
    private String cccd;
    private Boolean deleted;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Account information
    private Integer taiKhoanId;
    private String tenDangNhap;
    
    // Default address information
    private Integer defaultAddressId;
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
}
