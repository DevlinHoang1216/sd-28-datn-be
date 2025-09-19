package com.example.sd_28_phostep_be.dto.sell.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientPaymentRequest {
    
    @NotEmpty(message = "Tên khách hàng không được để trống")
    private String tenKhachHang;
    
    @NotEmpty(message = "Số điện thoại không được để trống")
    private String soDienThoai;
    
    @Email(message = "Email không đúng định dạng")
    private String email;
    
    @NotEmpty(message = "Địa chỉ không được để trống")
    private String diaChi;
    
    @NotNull(message = "Phương thức thanh toán không được để trống")
    private String phuongThucThanhToan; // "COD", "VNPAY"
    
    @PositiveOrZero(message = "Phí vận chuyển phải >= 0")
    private BigDecimal phiVanChuyen = BigDecimal.ZERO;
    
    @PositiveOrZero(message = "Tiền giảm phải >= 0")
    private BigDecimal tienGiam = BigDecimal.ZERO;
    
    private String ghiChu; // Optional note
    
    private Integer voucherId; // Optional voucher ID
}
