package com.example.sd_28_phostep_be.repository.account.Client;

import com.example.sd_28_phostep_be.modal.account.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachHangClientRepository extends JpaRepository<KhachHang, Integer> {

}
