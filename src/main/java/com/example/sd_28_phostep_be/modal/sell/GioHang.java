package com.example.sd_28_phostep_be.modal.sell;

import com.example.sd_28_phostep_be.modal.account.KhachHang;
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
@Table(name = "gio_hang")
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id")
    private KhachHang idKhachHang;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "id_hoa_don")
    private Integer idHoaDon;

}