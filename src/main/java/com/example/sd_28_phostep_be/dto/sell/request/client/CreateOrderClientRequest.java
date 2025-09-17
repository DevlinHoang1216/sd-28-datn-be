package com.example.sd_28_phostep_be.dto.sell.request.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderClientRequest {
    
    // Thông tin khách hàng
    private Integer idKhachHang; // null nếu là khách lẻ
    
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String tenKhachHang;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ")
    private String soDienThoai;
    
    private String email;
    
    // Địa chỉ giao hàng
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String diaChiGiaoHang;
    
    @NotBlank(message = "Phường/Xã không được để trống")
    private String phuongXa;
    
    @NotBlank(message = "Quận/Huyện không được để trống")
    private String quanHuyen;
    
    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String tinhThanhPho;
    
    // Thông tin thanh toán
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private String phuongThucThanhToan; // COD, VNPAY, CHUYENKHOAN
    
    private BigDecimal phiVanChuyen;
    
    // Voucher (optional)
    private Integer idPhieuGiamGia;
    private BigDecimal soTienGiamGia;
    
    // Ghi chú
    private String ghiChu;
    
    // Session ID cho khách chưa đăng nhập
    private String sessionId;
}
