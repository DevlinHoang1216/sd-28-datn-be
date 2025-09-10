package com.example.sd_28_phostep_be.service.sale.impl;

import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import com.example.sd_28_phostep_be.repository.sale.PhieuGiamGiaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PhieuGiamGiaServices {
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;

    public PhieuGiamGiaServices(PhieuGiamGiaRepository phieuGiamGiaRepository) {
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
    }

    public List<PhieuGiamGia> getall() {
        return phieuGiamGiaRepository.findAll();
    }
}
