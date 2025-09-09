package com.example.sd_28_phostep_be.modal.account;

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
@Table(name = "quyen_han")
public class QuyenHan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "ma")
    private String ma;

    @Column(name = "cap_quyen_han")
    private Integer capQuyenHan;

    @Column(name = "deleted")
    private Boolean deleted;

}