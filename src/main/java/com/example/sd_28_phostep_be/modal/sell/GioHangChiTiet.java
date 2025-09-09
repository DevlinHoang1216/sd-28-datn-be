package com.example.sd_28_phostep_be.modal.sell;

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
@ToString
@Table(name = "gio_hang_chi_tiet")
public class GioHangChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_gio_hang", referencedColumnName = "id")
    private GioHang idGioHang;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chi_tiet_sp", referencedColumnName = "id")
    private ChiTietSanPham idChiTietSp;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @ColumnDefault("0")
    @Column(name = "trang_thai", columnDefinition = "tinyint not null")
    private Short trangThai;

    @Column(name = "tong_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}