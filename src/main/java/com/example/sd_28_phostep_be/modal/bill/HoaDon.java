package com.example.sd_28_phostep_be.modal.bill;

import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.account.NhanVien;
import com.example.sd_28_phostep_be.modal.sell.HinhThucThanhToan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "hoa_don")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id")
    private KhachHang idKhachHang;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nhan_vien", referencedColumnName = "id")
    private NhanVien idNhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia", referencedColumnName = "id")
    private PhieuGiamGia idPhieuGiamGia;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Column(name = "tien_san_pham", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienSanPham;

    @Nationalized
    @Column(name = "loai_don", nullable = false)
    private String loaiDon;

    @ColumnDefault("0")
    @Column(name = "phi_van_chuyen", nullable = false, precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "tong_tien", precision = 38, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "tong_tien_sau_giam", precision = 38, scale = 2)
    private BigDecimal tongTienSauGiam;

    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @Nationalized
    @Column(name = "ten_khach_hang", nullable = false)
    private String tenKhachHang;

    @Nationalized
    @Column(name = "dia_chi_khach_hang", nullable = false)
    private String diaChiKhachHang;

    @Column(name = "so_dien_thoai_khach_hang")
    private String soDienThoaiKhachHang;

    @Nationalized
    @Column(name = "email")
    private String email;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao", nullable = false)
    private Date ngayTao;

    @Column(name = "ngay_thanh_toan")
    private OffsetDateTime ngayThanhToan;

    @ColumnDefault("1")
    @Column(name = "trang_thai", columnDefinition = "tinyint not null")
    private Short trangThai;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @ColumnDefault("getdate()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @OneToMany(mappedBy = "idHoaDon")
    private List<LichSuHoaDon> lichSuHoaDon;

    @OneToMany(mappedBy = "idHoaDon", fetch = FetchType.LAZY)
    private Set<HoaDonChiTiet> chiTietHoaDon = new HashSet<>();

    @OneToMany(mappedBy = "idHoaDon", fetch = FetchType.LAZY)
    private Set<HinhThucThanhToan> hinhThucThanhToan = new HashSet<>();

    @Column(name = "giao_ca_id")
    private Integer giaoCaId;

}