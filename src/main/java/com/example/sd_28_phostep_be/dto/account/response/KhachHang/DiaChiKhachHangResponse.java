package com.example.sd_28_phostep_be.dto.account.response.KhachHang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiKhachHangResponse {
    private Integer id;
    private String ma;
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
    private Boolean macDinh;
    private Boolean deleted;
    
    // Customer info for display
    private String tenKhachHang;
    private String soDienThoai;
}
