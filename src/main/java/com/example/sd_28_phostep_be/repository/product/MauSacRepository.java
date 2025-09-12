package com.example.sd_28_phostep_be.repository.product;

import com.example.sd_28_phostep_be.modal.product.MauSac;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MauSacRepository extends JpaRepository<MauSac, Integer> {
    
    @Query("SELECT ms FROM MauSac ms WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(REPLACE(ms.tenMauSac, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :keyword, '%'), ' ', '')) OR " +
           "LOWER(REPLACE(ms.maMauSac, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :keyword, '%'), ' ', '')) OR " +
           "LOWER(REPLACE(ms.hex, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :keyword, '%'), ' ', '')) OR " +
           "LOWER(ms.tenMauSac) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ms.maMauSac) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ms.hex) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<MauSac> findAllWithKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsByMaMauSac(String maMauSac);
    
    boolean existsByMaMauSacAndIdNot(String maMauSac, Integer id);
    
    boolean existsByTenMauSac(String tenMauSac);
    
    boolean existsByTenMauSacAndIdNot(String tenMauSac, Integer id);
    
    boolean existsByTenMauSacAndHex(String tenMauSac, String hex);
    
    boolean existsByTenMauSacAndHexAndIdNot(String tenMauSac, String hex, Integer id);
}
