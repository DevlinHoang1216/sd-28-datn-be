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
public class KhachHangUpdateRequest {
    private Integer id;
    private String ten;
    private String soDienThoai;
    private Short gioiTinh;
    private Instant ngaySinh;
    private String cccd;
    private String email;
    
    // Address information
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
    
    // Status
    private Boolean deleted;
}
