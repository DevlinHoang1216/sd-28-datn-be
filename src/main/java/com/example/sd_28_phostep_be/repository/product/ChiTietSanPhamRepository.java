package com.example.sd_28_phostep_be.repository.product;

import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {
    
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.deleted = false OR ctsp.deleted IS NULL")
    List<ChiTietSanPham> findAllActive();
    
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
           "LEFT JOIN FETCH ctsp.idSanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo")
    List<ChiTietSanPham> findAllActiveWithDetails();
    
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.idSanPham.id = :productId")
    Page<ChiTietSanPham> findByProductIdPaged(@Param("productId") Integer productId, Pageable pageable);
    
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
           "LEFT JOIN FETCH ctsp.idSanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "WHERE ctsp.idSanPham.id = :productId")
    Page<ChiTietSanPham> findByProductIdWithDetailsPaged(@Param("productId") Integer productId, Pageable pageable);
    
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
           "LEFT JOIN FETCH ctsp.idSanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH ctsp.idMauSac ms " +
           "LEFT JOIN FETCH ctsp.idKichCo kc " +
           "WHERE (:productId IS NULL OR ctsp.idSanPham.id = :productId) " +
           "AND (:search IS NULL OR :search = '' OR " +
           "     LOWER(REPLACE(sp.tenSanPham, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :search, '%'), ' ', '')) OR " +
           "     LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "     LOWER(REPLACE(ctsp.ma, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :search, '%'), ' ', '')) OR " +
           "     LOWER(ctsp.ma) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:sizeId IS NULL OR ctsp.idKichCo.id = :sizeId) " +
           "AND (:colorId IS NULL OR ctsp.idMauSac.id = :colorId) " +
           "AND (:status IS NULL OR :status = '' OR " +
           "     (:status = 'active' AND (ctsp.deleted = false OR ctsp.deleted IS NULL) AND ctsp.soLuongTonKho > 5) OR " +
           "     (:status = 'inactive' AND ctsp.deleted = true) OR " +
           "     (:status = 'out_of_stock' AND (ctsp.deleted = false OR ctsp.deleted IS NULL) AND ctsp.soLuongTonKho = 0) OR " +
           "     (:status = 'low_stock' AND (ctsp.deleted = false OR ctsp.deleted IS NULL) AND ctsp.soLuongTonKho > 0 AND ctsp.soLuongTonKho <= 5)) " +
           "AND (:minImportPrice IS NULL OR ctsp.giaNhap >= :minImportPrice) " +
           "AND (:maxImportPrice IS NULL OR ctsp.giaNhap <= :maxImportPrice) " +
           "AND (:minSellingPrice IS NULL OR ctsp.giaBan >= :minSellingPrice) " +
           "AND (:maxSellingPrice IS NULL OR ctsp.giaBan <= :maxSellingPrice)")
    Page<ChiTietSanPham> findAllWithFilters(@Param("productId") Integer productId, 
                                           @Param("search") String search,
                                           @Param("sizeId") Integer sizeId,
                                           @Param("colorId") Integer colorId,
                                           @Param("status") String status,
                                           @Param("minImportPrice") Double minImportPrice,
                                           @Param("maxImportPrice") Double maxImportPrice,
                                           @Param("minSellingPrice") Double minSellingPrice,
                                           @Param("maxSellingPrice") Double maxSellingPrice,
                                           Pageable pageable);
    
    /**
     * Find active product details by product ID for sales counter
     */
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
           "LEFT JOIN FETCH ctsp.idSanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "WHERE ctsp.idSanPham.id = :productId " +
           "AND (ctsp.deleted = false OR ctsp.deleted IS NULL) " +
           "AND (sp.deleted = false OR sp.deleted IS NULL) " +
           "AND ctsp.soLuongTonKho > 0")
    List<ChiTietSanPham> findActiveByProductIdForSales(@Param("productId") Integer productId);

    /**
     * Find active product details for sales counter with pagination
     */
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
           "LEFT JOIN FETCH ctsp.idSanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "WHERE (ctsp.deleted = false OR ctsp.deleted IS NULL) " +
           "AND (sp.deleted = false OR sp.deleted IS NULL) " +
           "AND ctsp.soLuongTonKho > 0")
    Page<ChiTietSanPham> findActiveProductDetailsForSales(Pageable pageable);

    /**
     * Find active product details for sales counter with keyword search
     */
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
           "LEFT JOIN FETCH ctsp.idSanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "WHERE (ctsp.deleted = false OR ctsp.deleted IS NULL) " +
           "AND (sp.deleted = false OR sp.deleted IS NULL) " +
           "AND ctsp.soLuongTonKho > 0 " +
           "AND (LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(ctsp.ma) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ChiTietSanPham> findActiveProductDetailsForSalesWithKeyword(Pageable pageable, @Param("keyword") String keyword);

    /**
     * Find active product details for sales counter (non-paginated)
     */
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
           "LEFT JOIN FETCH ctsp.idSanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "WHERE (ctsp.deleted = false OR ctsp.deleted IS NULL) " +
           "AND (sp.deleted = false OR sp.deleted IS NULL) " +
           "AND ctsp.soLuongTonKho > 0")
    List<ChiTietSanPham> findActiveProductDetailsForSales();

    @Modifying
    @Query("UPDATE ChiTietSanPham ctsp SET ctsp.deleted = :deletedStatus, ctsp.ngayCapNhat = CURRENT_TIMESTAMP WHERE ctsp.idSanPham.id = :productId")
    void updateDeletedStatusByProductId(@Param("productId") Integer productId, @Param("deletedStatus") Boolean deletedStatus);
}
