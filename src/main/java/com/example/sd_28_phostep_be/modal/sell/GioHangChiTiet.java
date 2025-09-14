package com.example.sd_28_phostep_be.modal.sell;

import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "gio_hang_chi_tiet")
public class GioHangChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_gio_hang", referencedColumnName = "id")
    private GioHang idGioHang;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_chi_tiet_sp", referencedColumnName = "id")
    private ChiTietSanPham idChiTietSp;

    @ColumnDefault("1")
    @Column(name = "so_luong", nullable = false)
    @Builder.Default
    private Integer soLuong = 1;

    @Column(name = "gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal gia;

    @Column(name = "thanh_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (soLuong == null) {
            soLuong = 1;
        }
        // Calculate thanh_tien automatically
        if (gia != null && soLuong != null) {
            thanhTien = gia.multiply(BigDecimal.valueOf(soLuong));
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Recalculate thanh_tien when updating
        if (gia != null && soLuong != null) {
            thanhTien = gia.multiply(BigDecimal.valueOf(soLuong));
        }
    }
}