package com.example.sd_28_phostep_be.repository.account;

import com.example.sd_28_phostep_be.modal.account.DiaChiKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaChiKhachHangRepository extends JpaRepository<DiaChiKhachHang, Integer> {
    
    @Query("SELECT dc FROM DiaChiKhachHang dc WHERE dc.idKhachHang.id = :khachHangId AND dc.deleted = false")
    List<DiaChiKhachHang> findByKhachHangId(@Param("khachHangId") Integer khachHangId);
    
    @Query("SELECT dc FROM DiaChiKhachHang dc WHERE dc.idKhachHang.id = :khachHangId AND dc.macDinh = true AND dc.deleted = false")
    Optional<DiaChiKhachHang> findDefaultByKhachHangId(@Param("khachHangId") Integer khachHangId);
}
