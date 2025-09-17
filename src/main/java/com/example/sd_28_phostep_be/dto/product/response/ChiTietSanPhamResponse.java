package com.example.sd_28_phostep_be.dto.product.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietSanPhamResponse {
    private Integer id;
    private String ma;
    private BigDecimal giaBan;
    private Integer soLuongTonKho;
    private Boolean trangThai;
    private Boolean deleted;
    private Instant ngayTao;
    private Instant ngayCapNhat;
    
    // Product info
    private Integer idSanPham;
    private String tenSanPham;
    
    // Color info
    private Integer idMauSac;
    private String tenMauSac;
    private String hexMauSac;
    
    // Size info
    private Integer idKichCo;
    private String tenKichCo;
    
    // Image info
    private String urlAnhSanPham;
}
