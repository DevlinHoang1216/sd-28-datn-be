package com.example.sd_28_phostep_be.repository.product;

import com.example.sd_28_phostep_be.modal.product.KichCo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KichCoRepository extends JpaRepository<KichCo, Integer> {
    
    @Query("SELECT kc FROM KichCo kc WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(REPLACE(kc.tenKichCo, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :keyword, '%'), ' ', '')) OR " +
           "LOWER(REPLACE(kc.maKichCo, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :keyword, '%'), ' ', '')) OR " +
           "LOWER(kc.tenKichCo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(kc.maKichCo) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<KichCo> findAllWithKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsByMaKichCo(String maKichCo);
    
    boolean existsByMaKichCoAndIdNot(String maKichCo, Integer id);
    
    boolean existsByTenKichCo(String tenKichCo);
    
    boolean existsByTenKichCoAndIdNot(String tenKichCo, Integer id);
}
