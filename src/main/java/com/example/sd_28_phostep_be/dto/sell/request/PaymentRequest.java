package com.example.sd_28_phostep_be.dto.sell.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    
    @NotNull(message = "ID hóa đơn không được để trống")
    private Integer hoaDonId;
    
    @NotNull(message = "ID nhân viên không được để trống")
    private Integer nhanVienId;
    
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private String phuongThucThanhToan; // "TIEN_MAT", "VNPAY", "KET_HOP"
    
    @NotNull(message = "Tổng tiền không được để trống")
    @Positive(message = "Tổng tiền phải lớn hơn 0")
    private BigDecimal tongTien;
    
    @PositiveOrZero(message = "Tiền mặt phải >= 0")
    private BigDecimal tienMat = BigDecimal.ZERO;
    
    @PositiveOrZero(message = "Tiền chuyển khoản phải >= 0")
    private BigDecimal tienChuyenKhoan = BigDecimal.ZERO;
    
    @PositiveOrZero(message = "Phí vận chuyển phải >= 0")
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;
    
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<CartItem> cartItems;
    
    private Integer phieuGiamGiaId; // Optional voucher
    
    private String ghiChu; // Optional note
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItem {
        @NotNull(message = "ID chi tiết sản phẩm không được để trống")
        private Integer idChiTietSanPham;
        
        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng phải lớn hơn 0")
        private Integer soLuong;
        
        @NotNull(message = "Giá không được để trống")
        @Positive(message = "Giá phải lớn hơn 0")
        private BigDecimal gia;
    }
}
