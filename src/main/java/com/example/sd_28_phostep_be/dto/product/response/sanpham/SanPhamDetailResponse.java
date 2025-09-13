package com.example.sd_28_phostep_be.dto.product.response.sanpham;

import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamDetailResponse {
    private Integer idSanPham;
    private String tenSanPham;
    private String maSanPham;
    private BigDecimal giaBanDau;
    private BigDecimal giaSauKhiGiam;


}
