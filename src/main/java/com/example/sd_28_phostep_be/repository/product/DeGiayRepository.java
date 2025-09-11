package com.example.sd_28_phostep_be.repository.product;

import com.example.sd_28_phostep_be.modal.product.DeGiay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeGiayRepository extends JpaRepository<DeGiay, Integer> {
    
    @Query("SELECT dg FROM DeGiay dg WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(dg.tenDeGiay) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(dg.maDeGiay) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<DeGiay> findAllWithKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT dg FROM DeGiay dg")
    Page<DeGiay> findAllActive(Pageable pageable);

    boolean existsByMaDeGiay(String maDeGiay);

    boolean existsByMaDeGiayAndIdNot(String maDeGiay, Integer id);

    @Query("SELECT dg FROM DeGiay dg WHERE dg.deleted = false AND dg.id = :id")
    DeGiay findByIdAndNotDeleted(@Param("id") Integer id);
}
