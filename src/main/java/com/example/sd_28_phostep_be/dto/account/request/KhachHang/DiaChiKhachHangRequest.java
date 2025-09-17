package com.example.sd_28_phostep_be.dto.account.request.KhachHang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiKhachHangRequest {
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
    private Boolean macDinh = false;
    
    // Customer info for address book
    private String tenNguoiNhan;
    private String soDienThoai;
}
