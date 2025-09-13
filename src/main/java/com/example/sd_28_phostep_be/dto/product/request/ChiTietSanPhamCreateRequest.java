package com.example.sd_28_phostep_be.dto.product.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietSanPhamCreateRequest {
    @NotNull(message = "ID màu sắc không được để trống")
    private Integer idMauSac;
    
    @NotNull(message = "ID kích cỡ không được để trống")
    private Integer idKichCo;
    
    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải >= 0")
    private Integer soLuongTonKho;
    
    @NotNull(message = "Giá nhập không được để trống")
    @DecimalMin(value = "0.0", message = "Giá nhập phải >= 0")
    private BigDecimal giaNhap;
    
    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", message = "Giá bán phải >= 0")
    private BigDecimal giaBan;
    
    private String moTaChiTiet;
    private String urlAnhSanPham;
}
