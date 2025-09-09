package com.example.sd_28_phostep_be.modal.sale;

import com.example.sd_28_phostep_be.modal.account.KhachHang;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "phieu_giam_gia_ca_nhan")
public class PhieuGiamGiaCaNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia", referencedColumnName = "id")
    private PhieuGiamGia idPhieuGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id")
    private KhachHang idKhachHang;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Column(name = "ngay_nhan")
    private Instant ngayNhan;

    @Column(name = "ngay_het_han")
    private Instant ngayHetHan;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @Column(name = "deleted")
    private Boolean deleted;

}