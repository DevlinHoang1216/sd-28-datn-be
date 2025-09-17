package com.example.sd_28_phostep_be.modal.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "dia_chi_khach_hang")
public class DiaChiKhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id")
    @JsonIgnore
    private KhachHang idKhachHang;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Nationalized
    @Column(name = "thanh_pho", nullable = false)
    private String thanhPho;

    @Nationalized
    @Column(name = "quan", nullable = false)
    private String quan;

    @Nationalized
    @Column(name = "phuong", nullable = false)
    private String phuong;

    @Nationalized
    @Column(name = "dia_chi_cu_the", nullable = false)
    private String diaChiCuThe;

    @ColumnDefault("0")
    @Column(name = "mac_dinh", nullable = false)
    private Boolean macDinh = false;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}