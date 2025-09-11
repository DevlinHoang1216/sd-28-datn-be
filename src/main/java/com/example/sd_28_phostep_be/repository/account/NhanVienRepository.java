package com.example.sd_28_phostep_be.repository.account;

import com.example.sd_28_phostep_be.modal.account.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
}
