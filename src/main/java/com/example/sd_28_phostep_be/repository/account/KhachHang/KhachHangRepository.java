package com.example.sd_28_phostep_be.repository.account.KhachHang;

import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDTOResponse;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.account.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    
    @Query("SELECT kh FROM KhachHang kh WHERE kh.ma <> 'KH001'")
    List<KhachHang> findAllExceptKH001();
    
    @Query("""
            SELECT new com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDTOResponse(
            kh.id,
            kh.ma,
            kh.ten,
            kh.taiKhoan.email,
            kh.taiKhoan.soDienThoai,
            kh.gioiTinh,
            kh.ngaySinh,
            kh.createdAt,
            kh.updatedAt,
            kh.deleted
            ) 
            FROM KhachHang kh
            WHERE (
            :keyword IS NULL
            OR kh.ma LIKE %:keyword%
            OR kh.ten LIKE %:keyword%
            OR kh.taiKhoan.soDienThoai LIKE %:keyword%
            OR kh.taiKhoan.email LIKE %:keyword%
            OR kh.cccd LIKE %:keyword%
            OR (:keywordAsDate IS NOT NULL AND kh.ngaySinh = :keywordAsDate)
            )
            AND (:gioiTinh IS NULL OR kh.gioiTinh = :gioiTinh)
            AND (:trangThai IS NULL OR kh.deleted = :trangThai)
            """)
    Page<KhachHangDTOResponse> getAllKH(
            @Param("keyword") String keyWord,
            @Param("keywordAsDate") Instant keywordAsDate,
            @Param("gioiTinh") Short gioiTinh,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable);
    
    /**
     * Get active customers for sales counter with phone numbers from account table
     */
    @Query("""
            SELECT new com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDTOResponse(
            kh.id,
            kh.ma,
            kh.ten,
            kh.taiKhoan.email,
            kh.taiKhoan.soDienThoai,
            kh.gioiTinh,
            kh.ngaySinh,
            kh.createdAt,
            kh.updatedAt,
            kh.taiKhoan.deleted
            ) 
            FROM KhachHang kh
            JOIN kh.taiKhoan tk
            WHERE (kh.deleted = true OR kh.deleted IS NULL)
            AND (tk.deleted = true OR tk.deleted IS NULL)
            AND (:keyword IS NULL OR :keyword = '' OR
                 LOWER(kh.ten) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 LOWER(kh.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 LOWER(tk.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                 LOWER(tk.soDienThoai) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<KhachHangDTOResponse> findActiveCustomersForSales(Pageable pageable, @Param("keyword") String keyword);

    /**
     * Get all active customers without pagination for dropdown lists
     */
    @Query("""
            SELECT new com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDTOResponse(
            kh.id,
            kh.ma,
            kh.ten,
            kh.taiKhoan.email,
            kh.taiKhoan.soDienThoai,
            kh.gioiTinh,
            kh.ngaySinh,
            kh.createdAt,
            kh.updatedAt,
            kh.taiKhoan.deleted
            ) 
            FROM KhachHang kh
            JOIN kh.taiKhoan tk
            WHERE (kh.deleted = true OR kh.deleted IS NULL)
            AND (tk.deleted = true OR tk.deleted IS NULL)
            ORDER BY kh.ten ASC
            """)
    List<KhachHangDTOResponse> findAllActiveKhachHang();

    // Statistics query
    @Query("SELECT COUNT(kh) FROM KhachHang kh WHERE kh.deleted = false OR kh.deleted IS NULL")
    Long getTotalActiveCustomers();

    // Find customer by TaiKhoan for authentication
    Optional<KhachHang> findByTaiKhoan(TaiKhoan taiKhoan);
    
    // Find customer by TaiKhoan ID directly
    @Query("SELECT kh FROM KhachHang kh WHERE kh.taiKhoan.id = :taiKhoanId")
    Optional<KhachHang> findByTaiKhoanId(@Param("taiKhoanId") Integer taiKhoanId);
    
    // Find customer by phone number (from TaiKhoan)
    @Query("SELECT kh FROM KhachHang kh WHERE kh.taiKhoan.soDienThoai = :soDienThoai")
    Optional<KhachHang> findBySoDienThoai(@Param("soDienThoai") String soDienThoai);
}