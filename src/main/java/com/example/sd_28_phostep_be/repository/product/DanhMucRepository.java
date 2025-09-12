package com.example.sd_28_phostep_be.repository.product;

import com.example.sd_28_phostep_be.modal.product.DanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
    
    @Query("SELECT dm FROM DanhMuc dm WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(REPLACE(dm.tenDanhMuc, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :keyword, '%'), ' ', '')) OR " +
           "LOWER(REPLACE(dm.maDanhMuc, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :keyword, '%'), ' ', '')) OR " +
           "LOWER(dm.tenDanhMuc) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(dm.maDanhMuc) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<DanhMuc> findAllWithKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsByMaDanhMuc(String maDanhMuc);
    
    boolean existsByMaDanhMucAndIdNot(String maDanhMuc, Integer id);
    
    boolean existsByTenDanhMuc(String tenDanhMuc);
    
    boolean existsByTenDanhMucAndIdNot(String tenDanhMuc, Integer id);
}
