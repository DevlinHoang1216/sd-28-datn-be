package com.example.sd_28_phostep_be.repository.sell;

import com.example.sd_28_phostep_be.modal.sell.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {
    
    /**
     * Find cart by invoice ID
     */
    @Query("SELECT gh FROM GioHang gh WHERE gh.idHoaDon.id = :hoaDonId")
    Optional<GioHang> findByHoaDonId(@Param("hoaDonId") Integer hoaDonId);
    
    /**
     * Find cart by customer ID and invoice ID
     */
    @Query("SELECT gh FROM GioHang gh WHERE gh.idKhachHang.id = :khachHangId AND gh.idHoaDon.id = :hoaDonId")
    Optional<GioHang> findByKhachHangIdAndHoaDonId(@Param("khachHangId") Integer khachHangId, @Param("hoaDonId") Integer hoaDonId);
    
    /**
     * Check if cart exists for invoice
     */
    @Query("SELECT COUNT(gh) > 0 FROM GioHang gh WHERE gh.idHoaDon.id = :hoaDonId")
    boolean existsByHoaDonId(@Param("hoaDonId") Integer hoaDonId);
}
