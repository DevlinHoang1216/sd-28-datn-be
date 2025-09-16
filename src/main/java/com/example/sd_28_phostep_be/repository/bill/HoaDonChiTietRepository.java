package com.example.sd_28_phostep_be.repository.bill;

import com.example.sd_28_phostep_be.modal.bill.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
    
    @Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.idHoaDon.id = :hoaDonId AND hdct.deleted = false")
    List<HoaDonChiTiet> findByHoaDonId(@Param("hoaDonId") Integer hoaDonId);
    
    @Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.idHoaDon.id = :hoaDonId")
    List<HoaDonChiTiet> findAllByHoaDonId(@Param("hoaDonId") Integer hoaDonId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM HoaDonChiTiet hdct WHERE hdct.idHoaDon.id = :hoaDonId")
    void deleteByIdHoaDon_Id(@Param("hoaDonId") Integer hoaDonId);
}
