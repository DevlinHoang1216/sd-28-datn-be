package com.example.sd_28_phostep_be.repository.product;

import com.example.sd_28_phostep_be.modal.product.ChatLieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {
    
    @Query("SELECT cl FROM ChatLieu cl WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(cl.tenChatLieu) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(cl.maChatLieu) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ChatLieu> findAllWithKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsByMaChatLieu(String maChatLieu);
    
    boolean existsByMaChatLieuAndIdNot(String maChatLieu, Integer id);
}
