package com.example.sd_28_phostep_be.modal.bill;

import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "hoa_don_chi_tiet")
public class HoaDonChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoa_don", referencedColumnName = "id")
    private HoaDon idHoaDon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chi_tiet_sp", referencedColumnName = "id")
    private ChiTietSanPham idChiTietSp;

    @Column(name = "id_imel_da_ban")
    private Integer idImelDaBan;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Column(name = "gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal gia;

    @ColumnDefault("1")
    @Column(name = "trang_thai", columnDefinition = "tinyint not null")
    private Short trangThai;

    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}