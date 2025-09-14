package com.example.sd_28_phostep_be.dto.account.request.KhachHang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangCreateRequest {
    private String ten;
    private String soDienThoai;
    private Short gioiTinh;
    private Instant ngaySinh;
    private String cccd;
    private String email;
    private String matKhau;
    
    // Address information
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
}
