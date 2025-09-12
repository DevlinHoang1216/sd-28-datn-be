package com.example.sd_28_phostep_be.modal.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "anh_san_pham")
public class AnhSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Nationalized
    @Lob
    @Column(name = "url_anh")
    private String urlAnh;

    @ColumnDefault("0")
    @Column(name = "la_anh_dai_dien")
    private Boolean laAnhDaiDien;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @OneToMany(mappedBy = "idAnhSanPham")
    private Set<ChiTietSanPham> chiTietSanPhams = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idAnhSanPham")
    private Set<SanPham> sanPhams = new LinkedHashSet<>();

}