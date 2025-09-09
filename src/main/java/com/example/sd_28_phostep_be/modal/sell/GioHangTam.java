package com.example.sd_28_phostep_be.modal.sell;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "gio_hang_tam")
public class GioHangTam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_hoa_don", nullable = false)
    private Integer idHoaDon;

    @Column(name = "imei", nullable = false, length = 50)
    private String imei;

    @Column(name = "chi_tiet_san_pham_id", nullable = false)
    private Integer chiTietSanPhamId;

    @Column(name = "id_phieu_giam_gia")
    private Integer idPhieuGiamGia;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}