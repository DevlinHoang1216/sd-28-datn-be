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
    
    @Query("SELECT sp FROM SanPham sp LEFT JOIN FETCH sp.idDanhMuc LEFT JOIN FETCH sp.idThuongHieu WHERE sp.deleted = false OR sp.deleted IS NULL")
    List<SanPham> findAllActiveWithDetails();
    
    @Query("SELECT sp FROM SanPham sp WHERE sp.deleted = false OR sp.deleted IS NULL")
    Page<SanPham> findAllActivePaged(Pageable pageable);
    
    @Query("SELECT sp FROM SanPham sp LEFT JOIN FETCH sp.idDanhMuc LEFT JOIN FETCH sp.idThuongHieu WHERE sp.deleted = false OR sp.deleted IS NULL")
    Page<SanPham> findAllActiveWithDetailsPaged(Pageable pageable);
}
