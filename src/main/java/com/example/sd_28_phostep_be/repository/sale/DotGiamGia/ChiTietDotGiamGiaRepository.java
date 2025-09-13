package com.example.sd_28_phostep_be.repository.sale.DotGiamGia;

import com.example.sd_28_phostep_be.modal.sale.ChiTietDotGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietDotGiamGiaRepository extends JpaRepository<ChiTietDotGiamGia, Integer> {
    List<ChiTietDotGiamGia> findByIdDotGiamGia_IdAndDeletedFalse(Integer id);
}
