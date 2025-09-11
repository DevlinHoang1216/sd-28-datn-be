package com.example.sd_28_phostep_be.repository.product;

import com.example.sd_28_phostep_be.modal.product.ThuongHieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThuongHieuRepository extends JpaRepository<ThuongHieu, Integer> {
    
    @Query("SELECT th FROM ThuongHieu th WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(th.tenThuongHieu) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(th.maThuongHieu) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ThuongHieu> findAllWithKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsByMaThuongHieu(String maThuongHieu);
    
    boolean existsByMaThuongHieuAndIdNot(String maThuongHieu, Integer id);
}
