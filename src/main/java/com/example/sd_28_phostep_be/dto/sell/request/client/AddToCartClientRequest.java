package com.example.sd_28_phostep_be.dto.sell.request.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartClientRequest {
    
    @NotNull(message = "ID chi tiết sản phẩm không được để trống")
    private Integer idChiTietSanPham;
    
    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer soLuong;
    
    // ID khách hàng (nullable - nếu null thì là khách lẻ)
    private Integer idKhachHang;
    
    // Session ID để track giỏ hàng của khách chưa đăng nhập
    private String sessionId;
}
