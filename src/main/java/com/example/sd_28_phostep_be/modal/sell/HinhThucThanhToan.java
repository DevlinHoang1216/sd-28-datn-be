package com.example.sd_28_phostep_be.modal.sell;

import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "hinh_thuc_thanh_toan")
public class HinhThucThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoa_don", referencedColumnName = "id")
    private HoaDon idHoaDon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_phuong_thuc_thanh_toan", referencedColumnName = "id")
    private PhuongThucThanhToan idPhuongThucThanhToan;

    @ColumnDefault("0")
    @Column(name = "tien_chuyen_khoan", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienChuyenKhoan;

    @ColumnDefault("0")
    @Column(name = "tien_mat", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienMat;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

}