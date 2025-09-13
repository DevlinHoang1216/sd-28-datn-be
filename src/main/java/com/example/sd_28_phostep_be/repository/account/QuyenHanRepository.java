package com.example.sd_28_phostep_be.repository.account;

import com.example.sd_28_phostep_be.modal.account.QuyenHan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuyenHanRepository extends JpaRepository<QuyenHan, Integer> {
    
    Optional<QuyenHan> findByMa(String ma);
}
