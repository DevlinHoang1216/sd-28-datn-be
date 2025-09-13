package com.example.sd_28_phostep_be.dto.sale.request.DotGiamGia;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotGiamGiaDTO {
    private String ma;
    private String tenDotGiamGia;
    private String loaiGiamGiaApDung; // "PHAN_TRAM" hoặc "TIEN_MAT"
    private BigDecimal giaTriGiamGia;
    private BigDecimal soTienGiamToiDa;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private List<Integer> listSanPhamId; // id chi tiết sản phẩm
}
