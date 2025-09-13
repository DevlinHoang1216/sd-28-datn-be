package com.example.sd_28_phostep_be.dto.account.response.NhanVien;

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
public class NhanVienDTOResponse {
    private Integer id;
    private String ma;
    private String tenNhanVien;
    private Date ngaySinh;
    private Boolean gioiTinh;
    private String soDienThoai;
    private String cccd;
    private String diaChiCuThe;
    private Boolean deletedTrangThai;

}
