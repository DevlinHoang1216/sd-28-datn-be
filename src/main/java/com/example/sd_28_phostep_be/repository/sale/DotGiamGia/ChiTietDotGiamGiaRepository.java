package com.example.sd_28_phostep_be.repository.sale.DotGiamGia;

import com.example.sd_28_phostep_be.modal.sale.ChiTietDotGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChiTietDotGiamGiaRepository extends JpaRepository<ChiTietDotGiamGia, Integer> {
    List<ChiTietDotGiamGia> findByIdDotGiamGia_IdAndDeletedFalse(Integer id);
    
    @Query("SELECT ctdgg FROM ChiTietDotGiamGia ctdgg " +
           "JOIN ctdgg.idDotGiamGia dgg " +
           "WHERE ctdgg.idChiTietSp.id = :productDetailId " +
           "AND ctdgg.deleted = false " +
           "AND dgg.deleted = false " +
           "AND dgg.trangThai = true " +
           "AND dgg.ngayBatDau <= CURRENT_TIMESTAMP " +
           "AND dgg.ngayKetThuc >= CURRENT_TIMESTAMP")
    List<ChiTietDotGiamGia> findActiveDiscountsByProductDetail(@Param("productDetailId") Integer productDetailId);
}
