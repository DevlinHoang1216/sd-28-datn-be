package com.example.sd_28_phostep_be.modal.product;

import com.example.sd_28_phostep_be.modal.sale.ChiTietDotGiamGia;
import com.example.sd_28_phostep_be.modal.sell.GioHangChiTiet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
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
@Table(name = "chi_tiet_san_pham")
public class ChiTietSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ChatLieu idChatLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MauSac idMauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_co", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private KichCo idKichCo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "chiTietSanPhams"})
    private SanPham idSanPham;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ma")
    private String ma;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "so_luong_ton_kho", nullable = false)
    private Integer soLuongTonKho;

    @Size(max = 500)
    @Nationalized
    @Column(name = "mo_ta_chi_tiet", length = 500)
    private String moTaChiTiet;

    @NotNull
    @Column(name = "gia_nhap", nullable = false, precision = 19, scale = 2)
    private BigDecimal giaNhap;

    @NotNull
    @Column(name = "gia_ban", nullable = false, precision = 19, scale = 2)
    private BigDecimal giaBan;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_nhap")
    private Instant ngayNhap;

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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "chiTietSanPhams", "sanPhams"})
    private AnhSanPham idAnhSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_de_giay", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DeGiay idDeGiay;

    @OneToMany(mappedBy = "idChiTietSp")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "idChiTietSp"})
    @Builder.Default
    private Set<ChiTietDotGiamGia> chiTietDotGiamGias = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idChiTietSp")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "idChiTietSp"})
    @Builder.Default
    private Set<GioHangChiTiet> gioHangChiTiets = new LinkedHashSet<>();

}