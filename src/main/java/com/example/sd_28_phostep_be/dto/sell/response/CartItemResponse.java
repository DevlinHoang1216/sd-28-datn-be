package com.example.sd_28_phostep_be.dto.sell.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    
    private Integer id;
    private Integer idChiTietSanPham;
    private String maSanPham;
    private String tenSanPham;
    private String tenMauSac;
    private String hexMauSac;
    private String tenKichCo;
    private String tenThuongHieu;
    private String tenDanhMuc;
    private String urlAnhSanPham;
    private Integer soLuong;
    private BigDecimal gia;
    private BigDecimal thanhTien;
    private Integer soLuongTonKho;
    private LocalDateTime createdAt;
}
