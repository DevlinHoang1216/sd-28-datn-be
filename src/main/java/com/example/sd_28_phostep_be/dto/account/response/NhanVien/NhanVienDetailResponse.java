package com.example.sd_28_phostep_be.dto.account.response.NhanVien;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienDetailResponse {
    private Integer id;
    private String ma;
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
    private Boolean deleted;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    
    // TaiKhoan info
    private Integer idTaiKhoan;
    private String maTaiKhoan;
    private String tenDangNhap;
    private String email;
    private String soDienThoai;
    private String tenQuyenHan;
    private Boolean deletedTaiKhoan;
}
