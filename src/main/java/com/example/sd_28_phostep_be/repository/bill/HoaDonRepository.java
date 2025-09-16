package com.example.sd_28_phostep_be.repository.bill;

import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDTOResponse;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
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

    // Statistics queries - Include all non-cancelled orders (not just completed)
    @Query("SELECT COALESCE(SUM(h.tongTienSauGiam), 0) FROM HoaDon h WHERE h.trangThai != 3 AND h.deleted = false AND CAST(h.ngayTao AS DATE) BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangThai != 3 AND h.deleted = false AND CAST(h.ngayTao AS DATE) BETWEEN :startDate AND :endDate")
    Long getTotalOrdersByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(h.tongTienSauGiam), 0) FROM HoaDon h WHERE h.trangThai != 3 AND h.deleted = false AND CAST(h.ngayTao AS DATE) = :date")
    BigDecimal getTotalRevenueByDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.trangThai != 3 AND h.deleted = false AND CAST(h.ngayTao AS DATE) = :date")
    Long getTotalOrdersByDate(@Param("date") LocalDate date);

    @Query("""
        SELECT CAST(h.ngayTao AS DATE), COALESCE(SUM(h.tongTienSauGiam), 0), COUNT(h)
        FROM HoaDon h 
        WHERE h.trangThai != 3 AND h.deleted = false 
        AND CAST(h.ngayTao AS DATE) BETWEEN :startDate AND :endDate
        GROUP BY CAST(h.ngayTao AS DATE)
        ORDER BY CAST(h.ngayTao AS DATE)
        """)
    List<Object[]> getDailyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT DATEPART(YEAR, h.ngayTao), DATEPART(WEEK, h.ngayTao), COALESCE(SUM(h.tongTienSauGiam), 0), COUNT(h)
        FROM HoaDon h 
        WHERE h.trangThai != 3 AND h.deleted = false 
        AND CAST(h.ngayTao AS DATE) BETWEEN :startDate AND :endDate
        GROUP BY DATEPART(YEAR, h.ngayTao), DATEPART(WEEK, h.ngayTao)
        ORDER BY DATEPART(YEAR, h.ngayTao), DATEPART(WEEK, h.ngayTao)
        """)
    List<Object[]> getWeeklyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT DATEPART(YEAR, h.ngayTao), DATEPART(MONTH, h.ngayTao), COALESCE(SUM(h.tongTienSauGiam), 0), COUNT(h)
        FROM HoaDon h 
        WHERE h.trangThai != 3 AND h.deleted = false 
        AND CAST(h.ngayTao AS DATE) BETWEEN :startDate AND :endDate
        GROUP BY DATEPART(YEAR, h.ngayTao), DATEPART(MONTH, h.ngayTao)
        ORDER BY DATEPART(YEAR, h.ngayTao), DATEPART(MONTH, h.ngayTao)
        """)
    List<Object[]> getMonthlyRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT dm.tenDanhMuc, COALESCE(SUM(h.tongTienSauGiam), 0), COUNT(DISTINCT h.id), COUNT(DISTINCT sp.id)
        FROM HoaDon h
        JOIN HoaDonChiTiet hct ON h.id = hct.idHoaDon.id
        JOIN ChiTietSanPham ctsp ON hct.idChiTietSp.id = ctsp.id
        JOIN SanPham sp ON ctsp.idSanPham.id = sp.id
        JOIN DanhMuc dm ON sp.idDanhMuc.id = dm.id
        WHERE h.trangThai != 3 AND h.deleted = false 
        AND CAST(h.ngayTao AS DATE) BETWEEN :startDate AND :endDate
        GROUP BY dm.id, dm.tenDanhMuc
        ORDER BY SUM(h.tongTienSauGiam) DESC
        """)
    List<Object[]> getCategoryPerformance(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT sp.id, sp.tenSanPham, sp.ma, dm.tenDanhMuc, 
        CASE WHEN asp.urlAnh IS NOT NULL THEN asp.urlAnh 
             ELSE 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=100&h=100&fit=crop&crop=center' END,
        SUM(hct.soLuong), SUM(hct.gia * hct.soLuong)
        FROM HoaDon h
        JOIN HoaDonChiTiet hct ON h.id = hct.idHoaDon.id AND hct.deleted = false
        JOIN ChiTietSanPham ctsp ON hct.idChiTietSp.id = ctsp.id AND ctsp.deleted = false
        JOIN SanPham sp ON ctsp.idSanPham.id = sp.id AND sp.deleted = false
        JOIN DanhMuc dm ON sp.idDanhMuc.id = dm.id AND dm.deleted = false
        LEFT JOIN AnhSanPham asp ON ctsp.idAnhSanPham.id = asp.id AND asp.deleted = false
        WHERE h.trangThai != 3 AND h.deleted = false 
        AND CAST(h.ngayTao AS DATE) BETWEEN :startDate AND :endDate
        GROUP BY sp.id, sp.tenSanPham, sp.ma, dm.tenDanhMuc, asp.urlAnh
        ORDER BY SUM(hct.soLuong) DESC
        """)
    List<Object[]> getTopProducts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT h.id, h.ma, h.tenKhachHang, 
        (SELECT COUNT(hct2) FROM HoaDonChiTiet hct2 WHERE hct2.idHoaDon.id = h.id),
        h.tongTienSauGiam, h.trangThai, h.createdAt
        FROM HoaDon h
        WHERE h.deleted = false
        ORDER BY h.createdAt DESC
        """)
    List<Object[]> getRecentOrders();
}
