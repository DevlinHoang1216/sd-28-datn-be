package com.example.sd_28_phostep_be.repository.bill;

import com.example.sd_28_phostep_be.modal.bill.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {
}
