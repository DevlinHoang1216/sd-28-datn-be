package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.repository.product.ChiTietSanPhamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ChiTietSanPhamService {
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;

    public ChiTietSanPhamService(ChiTietSanPhamRepository chiTietSanPhamRepository) {
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
    }

    public Page<ChiTietSanPham> getByProductIdPaged(Integer productId, Pageable pageable) {
        return chiTietSanPhamRepository.findByProductIdPaged(productId, pageable);
    }
}
