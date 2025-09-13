package com.example.sd_28_phostep_be.dto.account.request.NhanVien;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienUpdateRequest {
    private Integer id;
    private String tenNhanVien;
    private Date ngaySinh;
    private String anhNhanVien;
    private String ghiChu;
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
    private String cccd;
    private Boolean gioiTinh;
    
    // TaiKhoan fields
    private String email;
    private String soDienThoai;
    private Boolean deleted;
}
