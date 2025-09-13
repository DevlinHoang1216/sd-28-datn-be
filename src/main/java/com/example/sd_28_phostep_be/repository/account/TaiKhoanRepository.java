package com.example.sd_28_phostep_be.repository.account;

import com.example.sd_28_phostep_be.modal.account.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
    
    Optional<TaiKhoan> findByEmail(String email);
    
    Optional<TaiKhoan> findBySoDienThoai(String soDienThoai);
    
    @Query("SELECT tk FROM TaiKhoan tk WHERE tk.tenDangNhap = :tenDangNhap AND tk.deleted = false")
    Optional<TaiKhoan> findActiveByTenDangNhap(@Param("tenDangNhap") String tenDangNhap);
    
    boolean existsByTenDangNhap(String tenDangNhap);
    
    boolean existsByEmail(String email);
    
    boolean existsBySoDienThoai(String soDienThoai);
}
