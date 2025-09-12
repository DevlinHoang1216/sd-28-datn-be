package com.example.sd_28_phostep_be.repository.product;

import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "WHERE ctsp.deleted = false OR ctsp.deleted IS NULL")
    List<ChiTietSanPham> findAllActiveWithDetails();
    
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp WHERE ctsp.idSanPham.id = :productId AND (ctsp.deleted = false OR ctsp.deleted IS NULL)")
    Page<ChiTietSanPham> findByProductIdPaged(@Param("productId") Integer productId, Pageable pageable);
    
    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
           "LEFT JOIN FETCH ctsp.idSanPham sp " +
           "LEFT JOIN FETCH sp.idDanhMuc " +
           "LEFT JOIN FETCH sp.idThuongHieu " +
           "LEFT JOIN FETCH sp.idChatLieu " +
           "LEFT JOIN FETCH sp.idDeGiay " +
           "LEFT JOIN FETCH ctsp.idMauSac " +
           "LEFT JOIN FETCH ctsp.idKichCo " +
           "WHERE ctsp.idSanPham.id = :productId AND (ctsp.deleted = false OR ctsp.deleted IS NULL)")
    Page<ChiTietSanPham> findByProductIdWithDetailsPaged(@Param("productId") Integer productId, Pageable pageable);
}
