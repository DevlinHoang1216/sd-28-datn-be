package com.example.sd_28_phostep_be.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamUpdateRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
    private String tenSanPham;
    
    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String moTaSanPham;
    
    @Size(max = 100, message = "Quốc gia sản xuất không được vượt quá 100 ký tự")
    private String quocGiaSanXuat;
    
    private Integer idDanhMuc;
    private Integer idThuongHieu;
    private String urlAnhDaiDien;
    private String trangThai;
}
