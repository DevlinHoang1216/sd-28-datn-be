package com.example.sd_28_phostep_be.dto.product.response;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamResponse {
    private Integer id;
    private String ma;
    private String tenSanPham;
    private String moTa;
    private Boolean trangThai;
    private Boolean deleted;
    private Instant ngayTao;
    private Instant ngayCapNhat;
    
    // Category info
    private Integer idDanhMuc;
    private String tenDanhMuc;
    
    // Brand info  
    private Integer idThuongHieu;
    private String tenThuongHieu;
    
    // Material info
    private Integer idChatLieu;
    private String tenChatLieu;
    
    // Sole info
    private Integer idDeGiay;
    private String tenDeGiay;
    
    // Product variants
    private List<ChiTietSanPhamResponse> chiTietSanPhams;
    
    // Additional info
    private Long totalVariants;
    private Long activeVariants;
}
