package com.example.sd_28_phostep_be.modal.product;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "de_giay")
public class DeGiay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_de_giay")
    private String tenDeGiay;

    @Column(name = "ma_de_giay")
    private String maDeGiay;

    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Column(name = "deleted")
    private Boolean deleted;
}
