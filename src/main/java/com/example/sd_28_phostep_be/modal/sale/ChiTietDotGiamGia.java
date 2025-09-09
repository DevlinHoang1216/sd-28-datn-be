package com.example.sd_28_phostep_be.modal.sale;

import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "chi_tiet_dot_giam_gia")
public class ChiTietDotGiamGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dot_giam_gia", referencedColumnName = "id")
    private DotGiamGia idDotGiamGia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chi_tiet_sp", referencedColumnName = "id")
    private ChiTietSanPham idChiTietSp;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Column(name = "gia_ban_dau", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaBanDau;

    @Column(name = "gia_sau_khi_giam", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaSauKhiGiam;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}