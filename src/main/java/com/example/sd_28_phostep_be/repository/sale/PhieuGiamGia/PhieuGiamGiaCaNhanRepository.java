package com.example.sd_28_phostep_be.repository.sale.PhieuGiamGia;

import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGiaCaNhan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PhieuGiamGiaCaNhanRepository extends JpaRepository<PhieuGiamGiaCaNhan, Integer> {
    Optional<PhieuGiamGiaCaNhan> findByIdPhieuGiamGiaId(Integer idPhieuGiamGiaId);

    List<PhieuGiamGiaCaNhan> findAllByIdPhieuGiamGiaId(Integer id);

    Optional<PhieuGiamGiaCaNhan> findByIdPhieuGiamGiaAndIdKhachHang(PhieuGiamGia pgg, KhachHang kh);

    void deleteByIdPhieuGiamGia(PhieuGiamGia existingPgg);


    @Transactional
    @Modifying
    @Query("DELETE FROM PhieuGiamGiaCaNhan p WHERE p.idPhieuGiamGia.id = :pggId")
    void deleteAllByPhieuGiamGiaId(@Param("pggId") Integer pggId);
}
