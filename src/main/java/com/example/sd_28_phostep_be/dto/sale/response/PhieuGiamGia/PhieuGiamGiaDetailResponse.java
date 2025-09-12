package com.example.sd_28_phostep_be.dto.sale.response.PhieuGiamGia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhieuGiamGiaDetailResponse {
    private Integer id;
    private String ma;
    private String tenPhieuGiamGia;
    private String loaiPhieuGiamGia;
    private Double phanTramGiamGia;
    private Double soTienGiamToiDa;
    private Double hoaDonToiThieu;
    private Integer soLuongDung;
    private Instant ngayBatDau;
    private Instant ngayKetThuc;
    private Boolean trangThai;
    private Boolean riengTu;
    private String moTa;

    // danh sách khách hàng
    private List<CustomerDetail> customers;

    @Data
    @AllArgsConstructor
    public static class CustomerDetail {
        private Integer idKhachHang;
        private String email;
        private String soDienThoai;
    }
}
