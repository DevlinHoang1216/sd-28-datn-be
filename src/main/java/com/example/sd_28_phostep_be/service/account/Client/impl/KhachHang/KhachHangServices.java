package com.example.sd_28_phostep_be.service.account.Client.impl.KhachHang;

import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    // Load active customers for sales counter
    public Page<KhachHang> getActiveCustomersForSales(Pageable pageable, String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return khachHangRepository.findActiveCustomersForSalesWithKeyword(keyword.trim(), pageable);
        }
        return khachHangRepository.findActiveCustomersForSales(pageable);
    }
    
    public List<KhachHang> getAllActiveCustomersForSales() {
        return khachHangRepository.findAllActiveCustomersForSales();
    }
}
