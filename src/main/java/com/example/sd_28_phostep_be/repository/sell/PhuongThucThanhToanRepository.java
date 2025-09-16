package com.example.sd_28_phostep_be.repository.sell;

import com.example.sd_28_phostep_be.modal.sell.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, Integer> {
    
    @Query("SELECT ptt FROM PhuongThucThanhToan ptt WHERE ptt.kieuThanhToan = :kieuThanhToan AND (ptt.deleted = false OR ptt.deleted IS NULL)")
    Optional<PhuongThucThanhToan> findByKieuThanhToan(@Param("kieuThanhToan") String kieuThanhToan);
}
