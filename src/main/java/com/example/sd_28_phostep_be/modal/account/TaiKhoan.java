package com.example.sd_28_phostep_be.modal.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "tai_khoan")
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER) // đổi từ LAZY -> EAGER để load luôn
    @JoinColumn(name = "id_quyen_han", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // tránh lỗi proxy
    private QuyenHan idQuyenHan;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Nationalized
    @Column(name = "ten_dang_nhap")
    private String tenDangNhap;

    @Nationalized
    @Column(name = "email")
    private String email;

    @Nationalized
    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Nationalized
    @Column(name = "mat_khau")
    private String matKhau;

    @Column(name = "deleted")
    private Boolean deleted;

}