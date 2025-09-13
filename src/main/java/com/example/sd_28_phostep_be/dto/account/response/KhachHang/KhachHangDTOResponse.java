package com.example.sd_28_phostep_be.dto.account.response.KhachHang;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTOResponse {
    private Integer id;
    private String ma;
    private String ten;
    private String soDienThoai;
    private Short gioiTinh;
    private Instant ngaySinh;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean deleted;
}
