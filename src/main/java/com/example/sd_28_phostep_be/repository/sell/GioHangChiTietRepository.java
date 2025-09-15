package com.example.sd_28_phostep_be.repository.sell;

import com.example.sd_28_phostep_be.modal.sell.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {
    
    /**
     * Find all cart details by cart ID
     */
    @Query("SELECT ghct FROM GioHangChiTiet ghct " +
           "JOIN FETCH ghct.idChiTietSp ctsp " +
           "JOIN FETCH ctsp.idSanPham sp " +
           "JOIN FETCH ctsp.idMauSac ms " +
           "JOIN FETCH ctsp.idKichCo kc " +
           "WHERE ghct.idGioHang.id = :gioHangId")
    List<GioHangChiTiet> findByGioHangId(@Param("gioHangId") Integer gioHangId);
    
    /**
     * Find cart detail by cart ID and product detail ID
     */
    @Query("SELECT ghct FROM GioHangChiTiet ghct " +
           "WHERE ghct.idGioHang.id = :gioHangId AND ghct.idChiTietSp.id = :chiTietSpId")
    Optional<GioHangChiTiet> findByGioHangIdAndChiTietSpId(@Param("gioHangId") Integer gioHangId, 
                                                           @Param("chiTietSpId") Integer chiTietSpId);
    
    /**
     * Delete all cart details by cart ID
     */
    @Query("DELETE FROM GioHangChiTiet ghct WHERE ghct.idGioHang.id = :gioHangId")
    void deleteByGioHangId(@Param("gioHangId") Integer gioHangId);
    
    /**
     * Count items in cart
     */
    @Query("SELECT COUNT(ghct) FROM GioHangChiTiet ghct WHERE ghct.idGioHang.id = :gioHangId")
    Long countByGioHangId(@Param("gioHangId") Integer gioHangId);
}
