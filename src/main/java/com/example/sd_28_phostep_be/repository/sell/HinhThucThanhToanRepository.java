package com.example.sd_28_phostep_be.repository.sell;

import com.example.sd_28_phostep_be.modal.sell.HinhThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhThucThanhToanRepository extends JpaRepository<HinhThucThanhToan, Integer> {
    
    @Query("SELECT htt FROM HinhThucThanhToan htt WHERE htt.idHoaDon.id = :hoaDonId AND htt.deleted = false")
    List<HinhThucThanhToan> findByHoaDonId(@Param("hoaDonId") Integer hoaDonId);
}
