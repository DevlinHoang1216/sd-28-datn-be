package com.example.sd_28_phostep_be.modal.bill;

import com.example.sd_28_phostep_be.modal.account.NhanVien;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.sql.Date;
import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "lich_su_hoa_don")
public class LichSuHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoa_don", referencedColumnName = "id")
    private HoaDon idHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien", referencedColumnName = "id")
    private NhanVien idNhanVien;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Nationalized
    @Column(name = "hanh_dong")
    private String hanhDong;

    @Column(name = "thoi_gian")
    private Date thoiGian;

    @Column(name = "deleted")
    private Short deleted;

}