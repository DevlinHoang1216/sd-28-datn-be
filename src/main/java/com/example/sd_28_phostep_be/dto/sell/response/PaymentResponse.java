package com.example.sd_28_phostep_be.dto.sell.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    
    private boolean success;
    private String message;
    private Integer hoaDonId;
    private String maHoaDon;
    private BigDecimal tongTien;
    private BigDecimal tienThua; // Change amount for cash payments
    private String phuongThucThanhToan;
    
    // For VNPay payments (future implementation)
    private String vnpayUrl;
    private String transactionId;
    
    public static PaymentResponse success(Integer hoaDonId, String maHoaDon, BigDecimal tongTien, 
                                        BigDecimal tienThua, String phuongThucThanhToan) {
        return PaymentResponse.builder()
                .success(true)
                .message("Thanh toán thành công")
                .hoaDonId(hoaDonId)
                .maHoaDon(maHoaDon)
                .tongTien(tongTien)
                .tienThua(tienThua)
                .phuongThucThanhToan(phuongThucThanhToan)
                .build();
    }
    
    public static PaymentResponse error(String message) {
        return PaymentResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
