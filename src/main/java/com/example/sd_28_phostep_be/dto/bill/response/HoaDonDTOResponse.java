package com.example.sd_28_phostep_be.dto.bill.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDTOResponse {
    private Integer id;
    private String ma;
    private Integer idNhanVien;
    private String maNhanVien;
    private String tenKhachHang;
    private String soDienThoaiKhachHang;
    private String loaiDon;
    private BigDecimal phiVanChuyen;
    private Date ngayTao;
    private BigDecimal tongTienSauGiam;
    private Short trangThai;
    private Boolean deleted;
}
