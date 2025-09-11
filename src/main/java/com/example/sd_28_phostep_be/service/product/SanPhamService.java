package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.modal.product.SanPham;
import com.example.sd_28_phostep_be.repository.product.SanPhamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class SanPhamService {
    private final SanPhamRepository sanPhamRepository;

    public SanPhamService(SanPhamRepository sanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
    }

    public Page<SanPham> getAllWithDetailsPaged(Pageable pageable) {
        return sanPhamRepository.findAllActiveWithDetailsPaged(pageable);
    }
}
