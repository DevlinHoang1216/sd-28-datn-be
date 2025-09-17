package com.example.sd_28_phostep_be.dto.sell.response.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartClientResponse {
    private Integer id;
    private Integer idHoaDon;
    private String maHoaDon;
    private Integer idKhachHang;
    private String tenKhachHang;
    private BigDecimal tongTien;
    private Integer tongSoLuong;
    private String sessionId;
    private List<CartItemClientResponse> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemClientResponse {
        private Integer id;
        private Integer idChiTietSanPham;
        private String maSanPham;
        private String tenSanPham;
        private String urlAnh;
        private String tenMauSac;
        private String tenKichCo;
        private Integer soLuong;
        private Integer soLuongTonKho;
        private BigDecimal gia;
        private BigDecimal thanhTien;
    }
}
