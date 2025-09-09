package com.example.sd_28_phostep_be.modal.sale;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "dot_giam_gia")
public class DotGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Nationalized
    @Column(name = "ten_dot_giam_gia", nullable = false)
    private String tenDotGiamGia;

    @Nationalized
    @Column(name = "loai_giam_gia_ap_dung", nullable = false)
    private String loaiGiamGiaApDung;

    @Column(name = "gia_tri_giam_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTriGiamGia;

    @Column(name = "so_tien_giam_toi_da", nullable = false, precision = 18, scale = 2)
    private BigDecimal soTienGiamToiDa;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @ColumnDefault("1")
    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = false;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}