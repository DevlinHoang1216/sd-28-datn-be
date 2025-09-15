package com.example.sd_28_phostep_be.dto.sell.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    
    private Integer id;
    private Integer idKhachHang;
    private String tenKhachHang;
    private Integer idHoaDon;
    private String maHoaDon;
    private BigDecimal tongTien;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CartItemResponse> items;
    private Integer totalItems;
}
