package com.example.sd_28_phostep_be.repository.account.KhachHang;

import com.example.sd_28_phostep_be.modal.account.KhachHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    @Query("SELECT kh FROM KhachHang kh WHERE kh.ma <> 'KH001'")
    List<KhachHang> findAllExceptKH001();
    
    // Load active customers for sales with phone numbers from account table
    @Query("SELECT kh FROM KhachHang kh " +
           "LEFT JOIN FETCH kh.taiKhoan tk " +
           "WHERE (kh.deleted = false OR kh.deleted IS NULL) " +
           "AND kh.ma <> 'KH001' " +
           "ORDER BY kh.createdAt DESC")
    Page<KhachHang> findActiveCustomersForSales(Pageable pageable);
    
    @Query("SELECT kh FROM KhachHang kh " +
           "LEFT JOIN FETCH kh.taiKhoan tk " +
           "WHERE (kh.deleted = false OR kh.deleted IS NULL) " +
           "AND kh.ma <> 'KH001' " +
           "AND (LOWER(kh.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(kh.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(tk.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY kh.createdAt DESC")
    Page<KhachHang> findActiveCustomersForSalesWithKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT kh FROM KhachHang kh " +
           "LEFT JOIN FETCH kh.taiKhoan tk " +
           "WHERE (kh.deleted = false OR kh.deleted IS NULL) " +
           "AND kh.ma <> 'KH001' " +
           "ORDER BY kh.createdAt DESC")
    List<KhachHang> findAllActiveCustomersForSales();
}
