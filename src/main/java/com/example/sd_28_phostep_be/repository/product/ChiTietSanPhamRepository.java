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
    
    @Modifying
    @Query("UPDATE ChiTietSanPham ctsp SET ctsp.deleted = :deletedStatus, ctsp.ngayCapNhat = CURRENT_TIMESTAMP WHERE ctsp.idSanPham.id = :productId")
    void updateDeletedStatusByProductId(@Param("productId") Integer productId, @Param("deletedStatus") Boolean deletedStatus);
}
