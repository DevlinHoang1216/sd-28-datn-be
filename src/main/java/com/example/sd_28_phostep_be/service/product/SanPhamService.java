package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.dto.product.request.SanPhamCreateRequest;
import com.example.sd_28_phostep_be.dto.product.request.SanPhamUpdateRequest;
import com.example.sd_28_phostep_be.modal.product.*;
import com.example.sd_28_phostep_be.repository.product.AnhSanPhamRepository;
import com.example.sd_28_phostep_be.repository.product.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Service class for SanPham entity
 */
@Service
public class SanPhamService {
    private final SanPhamRepository sanPhamRepository;
    private final AnhSanPhamRepository anhSanPhamRepository;

    @Autowired
    public SanPhamService(SanPhamRepository sanPhamRepository, AnhSanPhamRepository anhSanPhamRepository) {
        this.sanPhamRepository = sanPhamRepository;
        this.anhSanPhamRepository = anhSanPhamRepository;
    }

    public Page<SanPham> getAllWithDetailsPaged(Pageable pageable) {
        return sanPhamRepository.findAllActiveWithDetailsPaged(pageable);
    }

    public Optional<SanPham> findById(Integer id) {
        return sanPhamRepository.findById(id)
                .filter(sanPham -> sanPham.getDeleted() == null || !sanPham.getDeleted());
    }

    public SanPham updateSanPham(Integer id, SanPhamUpdateRequest request) {
        Optional<SanPham> existingSanPhamOpt = findById(id);
        
        if (existingSanPhamOpt.isEmpty()) {
            throw new RuntimeException("Sản phẩm không tồn tại với ID: " + id);
        }

        SanPham existingSanPham = existingSanPhamOpt.get();
        
        // Update fields
        if (request.getTenSanPham() != null && !request.getTenSanPham().trim().isEmpty()) {
            existingSanPham.setTenSanPham(request.getTenSanPham());
        }
        // Remove ma field update - code should not be updatable
        if (request.getMoTaSanPham() != null) {
            existingSanPham.setMoTaSanPham(request.getMoTaSanPham());
        }
        if (request.getQuocGiaSanXuat() != null) {
            existingSanPham.setQuocGiaSanXuat(request.getQuocGiaSanXuat());
        }
        if (request.getIdDanhMuc() != null) {
            DanhMuc danhMuc = new DanhMuc();
            danhMuc.setId(request.getIdDanhMuc());
            existingSanPham.setIdDanhMuc(danhMuc);
        }
        if (request.getIdThuongHieu() != null) {
            ThuongHieu thuongHieu = new ThuongHieu();
            thuongHieu.setId(request.getIdThuongHieu());
            existingSanPham.setIdThuongHieu(thuongHieu);
        }
        if (request.getUrlAnhDaiDien() != null) {
            // Create or update AnhSanPham record
            AnhSanPham anhSanPham = new AnhSanPham();
            anhSanPham.setUrlAnh(request.getUrlAnhDaiDien());
            anhSanPham.setLaAnhDaiDien(true);
            anhSanPham.setNgayTao(Instant.now());
            anhSanPham.setNgayCapNhat(Instant.now());
            anhSanPham.setDeleted(false);
            
            // Save the image first
            AnhSanPham savedAnhSanPham = anhSanPhamRepository.save(anhSanPham);
            existingSanPham.setIdAnhSanPham(savedAnhSanPham);
        }
        
        // Update timestamp
        existingSanPham.setNgayCapNhat(Instant.now());
        
        return sanPhamRepository.save(existingSanPham);
    }

    public SanPham createSanPham(SanPhamCreateRequest request) {
        SanPham sanPham = new SanPham();
        
        // Update basic fields
        sanPham.setTenSanPham(request.getTenSanPham());
        sanPham.setMoTaSanPham(request.getMoTaSanPham());
        sanPham.setQuocGiaSanXuat(request.getQuocGiaSanXuat());
        
        // Set relationships
        if (request.getIdDanhMuc() != null) {
            DanhMuc danhMuc = new DanhMuc();
            danhMuc.setId(request.getIdDanhMuc());
            sanPham.setIdDanhMuc(danhMuc);
        }
        if (request.getIdThuongHieu() != null) {
            ThuongHieu thuongHieu = new ThuongHieu();
            thuongHieu.setId(request.getIdThuongHieu());
            sanPham.setIdThuongHieu(thuongHieu);
        }
        if (request.getUrlAnhDaiDien() != null) {
            // Create AnhSanPham record for new product
            AnhSanPham anhSanPham = new AnhSanPham();
            anhSanPham.setUrlAnh(request.getUrlAnhDaiDien());
            anhSanPham.setLaAnhDaiDien(true);
            anhSanPham.setNgayTao(Instant.now());
            anhSanPham.setNgayCapNhat(Instant.now());
            anhSanPham.setDeleted(false);
            
            // Save the image first
            AnhSanPham savedAnhSanPham = anhSanPhamRepository.save(anhSanPham);
            sanPham.setIdAnhSanPham(savedAnhSanPham);
        }
        
        // Set timestamps and defaults
        sanPham.setNgayTao(Instant.now());
        sanPham.setNgayCapNhat(Instant.now());
        sanPham.setDeleted(false);
        
        return sanPhamRepository.save(sanPham);
    }
}
