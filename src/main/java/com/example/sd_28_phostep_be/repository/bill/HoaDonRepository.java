package com.example.sd_28_phostep_be.repository.bill;

import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDTOResponse;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    @Query("""
            SELECT new com.example.sd_28_phostep_be.dto.bill.response.HoaDonDTOResponse(
            h.id,
            h.ma,
            h.idNhanVien.id,
            h.idNhanVien.ma,
            h.tenKhachHang,
            h.soDienThoaiKhachHang,
            h.loaiDon,
            h.phiVanChuyen,
            h.ngayTao,
            h.tongTienSauGiam,
            h.trangThai,
            h.deleted
            ) 
            FROM HoaDon h
            WHERE (
                   :keyword IS NULL 
                   OR h.ma LIKE %:keyword%
                   OR h.tenKhachHang LIKE %:keyword%
                   OR h.soDienThoaiKhachHang LIKE %:keyword%
                   OR h.idNhanVien.ma LIKE %:keyword%
                    )
                   AND (:minAmount IS NULL OR h.tongTienSauGiam >= :minAmount)
                   AND (:maxAmount IS NULL OR h.tongTienSauGiam <= :maxAmount)
                   AND (:startDate IS NULL OR h.ngayTao >= :startDate)
                   AND (:endDate IS NULL OR h.ngayTao <= :endDate)
                   AND (:trangThai IS NULL OR h.trangThai = :trangThai)
                   AND (:deleted IS NULL OR h.deleted = :deleted)
                   AND (:loaiDon IS NULL OR h.loaiDon = :loaiDon)
            """)
    Page<HoaDonDTOResponse> getAllHoaDon(@Param("keyword") String keyword,
                                         @Param("minAmount") Long minAmount,
                                         @Param("maxAmount") Long maxAmount,
                                         @Param("startDate") Timestamp startDate,
                                         @Param("endDate") Timestamp endDate,
                                         @Param("trangThai") Short trangThai,
                                         @Param("deleted") Boolean deleted,
                                         @Param("loaiDon") String loaiDon,
                                         Pageable pageable);


    //Detail HDCT...
    @Query("""
            SELECT hd FROM HoaDon hd 
            LEFT JOIN FETCH hd.idKhachHang
            LEFT JOIN FETCH hd.idNhanVien
            LEFT JOIN FETCH hd.idPhieuGiamGia
            LEFT JOIN FETCH hd.chiTietHoaDon cthd
            LEFT JOIN FETCH cthd.idChiTietSp ctsp       
            LEFT JOIN FETCH ctsp.idSanPham
            LEFT JOIN FETCH ctsp.idMauSac
            LEFT JOIN FETCH ctsp.idKichCo
            LEFT JOIN FETCH hd.lichSuHoaDon lshd
            LEFT JOIN FETCH lshd.idNhanVien
            LEFT JOIN FETCH hd.hinhThucThanhToan httt
            LEFT JOIN FETCH httt.idPhuongThucThanhToan
            WHERE hd.id = :id
            """)
    Optional<HoaDon> findHoaDonDetailById(@Param("id") Integer id);

    @Query("SELECT MIN(h.tongTienSauGiam) FROM HoaDon h WHERE h.deleted = false")
    Long findMinPrice();

    @Query("SELECT MAX(h.tongTienSauGiam) FROM HoaDon h WHERE h.deleted = false")
    Long findMaxPrice();

    // Tìm hóa đơn theo mã
    @Query("SELECT hd FROM HoaDon hd WHERE hd.ma = :ma")
    Optional<HoaDon> findByMa(@Param("ma") String ma);
    
    // Get pending invoices for sales counter (status = 0, deleted = true)
    @Query("SELECT hd FROM HoaDon hd LEFT JOIN FETCH hd.idKhachHang WHERE hd.trangThai = 0 AND hd.deleted = true")
    List<HoaDon> findPendingInvoicesForSales();
}
