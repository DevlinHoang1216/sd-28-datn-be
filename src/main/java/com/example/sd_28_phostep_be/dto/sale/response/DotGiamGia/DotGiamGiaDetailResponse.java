package com.example.sd_28_phostep_be.dto.sale.response.DotGiamGia;

import com.example.sd_28_phostep_be.dto.product.response.sanpham.SanPhamDetailResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotGiamGiaDetailResponse {
    private Integer id;
    private String ma;
    private String tenDotGiamGia;
    private String loaiGiamGiaApDung;
    private BigDecimal giaTriGiamGia;
    private BigDecimal soTienGiamToiDa;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Boolean trangThai;

    private List<SanPhamDetailResponse> danhSachSanPham;
}
