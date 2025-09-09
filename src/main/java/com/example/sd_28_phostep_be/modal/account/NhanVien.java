package com.example.sd_28_phostep_be.modal.account;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "nhan_vien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tai_khoan", referencedColumnName = "id")
    private TaiKhoan idTaiKhoan;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Nationalized
    @Column(name = "ten_nhan_vien")
    private String tenNhanVien;

    @Column(name = "ngay_sinh")
    private Instant ngaySinh;

    @Nationalized
    @Column(name = "anh_nhan_vien")
    private String anhNhanVien;

    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @Nationalized
    @Column(name = "thanh_pho")
    private String thanhPho;

    @Nationalized
    @Column(name = "quan")
    private String quan;

    @Nationalized
    @Column(name = "phuong")
    private String phuong;

    @Nationalized
    @Column(name = "dia_chi_cu_the")
    private String diaChiCuThe;

    @Nationalized
    @Column(name = "cccd")
    private String cccd;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

}