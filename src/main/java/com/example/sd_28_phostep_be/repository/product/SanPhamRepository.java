package com.example.sd_28_phostep_be.repository.product;

import com.example.sd_28_phostep_be.modal.product.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    
    @Query("SELECT sp FROM SanPham sp WHERE sp.deleted = false OR sp.deleted IS NULL")
    List<SanPham> findAllActive();
    
    @Query("SELECT sp FROM SanPham sp LEFT JOIN FETCH sp.idDanhMuc LEFT JOIN FETCH sp.idThuongHieu LEFT JOIN FETCH sp.idChatLieu LEFT JOIN FETCH sp.idDeGiay WHERE sp.deleted = false OR sp.deleted IS NULL")
    List<SanPham> findAllActiveWithDetails();
    
    @Query("SELECT sp FROM SanPham sp WHERE sp.deleted = false OR sp.deleted IS NULL")
    Page<SanPham> findAllActivePaged(Pageable pageable);
    
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH sp.idAnhSanPham " +
           "LEFT JOIN FETCH sp.chiTietSanPhams ctsp " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "LEFT JOIN FETCH ctsp.idAnhSanPham")
    Page<SanPham> findAllActiveWithDetailsPaged(Pageable pageable);
    
    /**
     * Find active products for sales counter with all necessary relationships
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH sp.idAnhSanPham " +
           "LEFT JOIN FETCH sp.chiTietSanPhams ctsp " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "LEFT JOIN FETCH ctsp.idAnhSanPham " +
           "WHERE (sp.deleted = false OR sp.deleted IS NULL)")
    Page<SanPham> findActiveProductsForSales(Pageable pageable);
    
    /**
     * Find active products for sales counter with keyword search and all necessary relationships
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH sp.idAnhSanPham " +
           "LEFT JOIN FETCH sp.chiTietSanPhams ctsp " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "LEFT JOIN FETCH ctsp.idAnhSanPham " +
           "WHERE (sp.deleted = false OR sp.deleted IS NULL) " +
           "AND (LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(sp.ma) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SanPham> findActiveProductsForSalesWithKeyword(Pageable pageable, String keyword);

    /**
     * Find products by brand IDs with all necessary relationships
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH sp.idAnhSanPham " +
           "LEFT JOIN FETCH sp.chiTietSanPhams ctsp " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "LEFT JOIN FETCH ctsp.idAnhSanPham " +
           "WHERE (sp.deleted = false OR sp.deleted IS NULL) " +
           "AND sp.idThuongHieu.id IN :brandIds")
    Page<SanPham> findProductsByBrandIds(List<Integer> brandIds, Pageable pageable);

    /**
     * Find products by category names with all necessary relationships
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH sp.idAnhSanPham " +
           "LEFT JOIN FETCH sp.chiTietSanPhams ctsp " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "LEFT JOIN FETCH ctsp.idAnhSanPham " +
           "WHERE (sp.deleted = false OR sp.deleted IS NULL) " +
           "AND sp.idDanhMuc.tenDanhMuc = :categoryName")
    Page<SanPham> findProductsByCategoryName(String categoryName, Pageable pageable);

    /**
     * Find products available for discount campaigns (only products with at least one variant with stock > 0)
     */
    @Query("SELECT DISTINCT sp FROM SanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH sp.idAnhSanPham " +
           "LEFT JOIN FETCH sp.chiTietSanPhams ctsp " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "LEFT JOIN FETCH ctsp.idAnhSanPham " +
           "WHERE (sp.deleted = false OR sp.deleted IS NULL) " +
           "AND EXISTS (SELECT 1 FROM ChiTietSanPham ctsp2 " +
           "           WHERE ctsp2.idSanPham = sp " +
           "           AND (ctsp2.deleted = false OR ctsp2.deleted IS NULL) " +
           "           AND ctsp2.soLuongTonKho > 0)")
    Page<SanPham> findProductsAvailableForDiscount(Pageable pageable);

    // Statistics query
    @Query("SELECT COUNT(sp) FROM SanPham sp WHERE sp.deleted = false OR sp.deleted IS NULL")
    Long getTotalActiveProducts();
}
