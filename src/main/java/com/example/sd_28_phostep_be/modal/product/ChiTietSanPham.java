package com.example.sd_28_phostep_be.modal.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private SanPham idSanPham;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @ColumnDefault("0")
    @Column(name = "so_luong_ton_kho", nullable = false)
    private Integer soLuongTonKho;

    @Nationalized
    @Column(name = "mo_ta_chi_tiet", length = 500)
    private String moTaChiTiet;

    @Column(name = "gia_nhap", nullable = false, precision = 19, scale = 2)
    private BigDecimal giaNhap;

    @Column(name = "gia_ban", nullable = false, precision = 19, scale = 2)
    private BigDecimal giaBan;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_nhap")
    private Instant ngayNhap;

    @Nationalized
    @ColumnDefault("'Còn hàng'")
    @Column(name = "trang_thai_san_pham_rieng", length = 50)
    private String trangThaiSanPhamRieng;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Column(name = "deleted")
    private Boolean deleted;

}