package com.example.sd_28_phostep_be.repository.account.KhachHang;

import com.example.sd_28_phostep_be.modal.account.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    @Query("SELECT kh FROM KhachHang kh WHERE kh.ma <> 'KH001'")
    List<KhachHang> findAllExceptKH001();
}
