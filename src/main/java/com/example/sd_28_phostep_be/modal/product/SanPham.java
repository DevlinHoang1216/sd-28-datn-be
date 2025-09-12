package com.example.sd_28_phostep_be.modal.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "san_pham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc", referencedColumnName = "id")
    private DanhMuc idDanhMuc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thuong_hieu", referencedColumnName = "id")
    private ThuongHieu idThuongHieu;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "ten_san_pham", nullable = false)
    private String tenSanPham;

    @Nationalized
    @Lob
    @Column(name = "mo_ta_san_pham")
    private String moTaSanPham;

    @Size(max = 100)
    @Nationalized
    @Column(name = "quoc_gia_san_xuat", length = 100)
    private String quocGiaSanXuat;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Column(name = "deleted")
    private Boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anh_san_pham", referencedColumnName = "id")
    private AnhSanPham idAnhSanPham;

    @OneToMany(mappedBy = "idSanPham")
    private Set<ChiTietSanPham> chiTietSanPhams = new LinkedHashSet<>();

}