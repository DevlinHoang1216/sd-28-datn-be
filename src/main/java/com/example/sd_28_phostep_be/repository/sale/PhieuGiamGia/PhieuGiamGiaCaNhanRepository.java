package com.example.sd_28_phostep_be.repository.sale.PhieuGiamGia;

import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGiaCaNhan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhieuGiamGiaCaNhanRepository extends JpaRepository<PhieuGiamGiaCaNhan, Integer> {
    Optional<PhieuGiamGiaCaNhan> findByIdPhieuGiamGiaId(Integer idPhieuGiamGiaId);

    List<PhieuGiamGiaCaNhan> findAllByIdPhieuGiamGiaId(Integer id);
}
