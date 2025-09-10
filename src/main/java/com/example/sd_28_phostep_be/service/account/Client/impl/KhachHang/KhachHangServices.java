package com.example.sd_28_phostep_be.service.account.Client.impl.KhachHang;

import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhachHangServices {
    private final KhachHangRepository khachHangRepository;

    public KhachHangServices(KhachHangRepository khachHangRepository) {
        this.khachHangRepository = khachHangRepository;
    }

    public List<KhachHang> getall() {
        return khachHangRepository.findAllExceptKH001();
    }
}
