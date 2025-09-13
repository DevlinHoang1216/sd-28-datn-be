package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.dto.product.request.ChiTietSanPhamCreateRequest;
import com.example.sd_28_phostep_be.dto.product.request.ChiTietSanPhamUpdateRequest;
import com.example.sd_28_phostep_be.modal.product.*;
import com.example.sd_28_phostep_be.repository.product.AnhSanPhamRepository;
import com.example.sd_28_phostep_be.repository.product.ChiTietSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChiTietSanPhamService {
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final AnhSanPhamRepository anhSanPhamRepository;

    @Autowired
    public ChiTietSanPhamService(ChiTietSanPhamRepository chiTietSanPhamRepository, AnhSanPhamRepository anhSanPhamRepository) {
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
        this.anhSanPhamRepository = anhSanPhamRepository;
    }

    public Page<ChiTietSanPham> getByProductIdPaged(Integer productId, Pageable pageable) {
        return chiTietSanPhamRepository.findByProductIdPaged(productId, pageable);
    }

    public List<ChiTietSanPham> createProductVariants(SanPham sanPham, List<ChiTietSanPhamCreateRequest> variantRequests) {
        List<ChiTietSanPham> createdVariants = new ArrayList<>();
        
        for (ChiTietSanPhamCreateRequest request : variantRequests) {
            ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
            
            // Set product relationship
            chiTietSanPham.setIdSanPham(sanPham);
            
            // Set color relationship
            if (request.getIdMauSac() != null) {
                MauSac mauSac = new MauSac();
                mauSac.setId(request.getIdMauSac());
                chiTietSanPham.setIdMauSac(mauSac);
            }
            
            // Set size relationship
            if (request.getIdKichCo() != null) {
                KichCo kichCo = new KichCo();
                kichCo.setId(request.getIdKichCo());
                chiTietSanPham.setIdKichCo(kichCo);
            }
            
            // Set basic fields
            chiTietSanPham.setSoLuongTonKho(request.getSoLuongTonKho());
            chiTietSanPham.setGiaNhap(request.getGiaNhap());
            chiTietSanPham.setGiaBan(request.getGiaBan());
            chiTietSanPham.setMoTaChiTiet(request.getMoTaChiTiet());
            
            // Handle variant image if provided
            if (request.getUrlAnhSanPham() != null && !request.getUrlAnhSanPham().trim().isEmpty()) {
                AnhSanPham anhSanPham = new AnhSanPham();
                anhSanPham.setUrlAnh(request.getUrlAnhSanPham());
                anhSanPham.setLaAnhDaiDien(false); // Variant images are not main images
                anhSanPham.setNgayTao(Instant.now());
                anhSanPham.setNgayCapNhat(Instant.now());
                anhSanPham.setDeleted(false);
                
                // Save the image first
                AnhSanPham savedAnhSanPham = anhSanPhamRepository.save(anhSanPham);
                chiTietSanPham.setIdAnhSanPham(savedAnhSanPham);
            }
            
            // Set timestamps and defaults
            chiTietSanPham.setNgayNhap(Instant.now());
            chiTietSanPham.setNgayTao(Instant.now());
            chiTietSanPham.setNgayCapNhat(Instant.now());
            chiTietSanPham.setDeleted(false);
            
            // Generate product variant code
            String variantCode = generateVariantCode(sanPham, request.getIdMauSac(), request.getIdKichCo());
            chiTietSanPham.setMa(variantCode);
            
            ChiTietSanPham savedVariant = chiTietSanPhamRepository.save(chiTietSanPham);
            createdVariants.add(savedVariant);
        }
        
        return createdVariants;
    }
    
    private String generateVariantCode(SanPham sanPham, Integer colorId, Integer sizeId) {
        // Generate a unique code for the product variant
        // Format: SP{productId}_{colorId}_{sizeId}
        return String.format("SP%d_%d_%d", sanPham.getId(), colorId, sizeId);
    }
    
    public Optional<ChiTietSanPham> findById(Integer id) {
        return chiTietSanPhamRepository.findById(id);
    }
    
    public ChiTietSanPham updateChiTietSanPham(Integer id, ChiTietSanPhamUpdateRequest request) {
        Optional<ChiTietSanPham> optionalChiTietSanPham = chiTietSanPhamRepository.findById(id);
        
        if (optionalChiTietSanPham.isEmpty()) {
            throw new RuntimeException("Chi tiết sản phẩm không tồn tại với ID: " + id);
        }
        
        ChiTietSanPham chiTietSanPham = optionalChiTietSanPham.get();
        
        // Update color relationship
        if (request.getIdMauSac() != null) {
            MauSac mauSac = new MauSac();
            mauSac.setId(request.getIdMauSac());
            chiTietSanPham.setIdMauSac(mauSac);
        }
        
        // Update size relationship
        if (request.getIdKichCo() != null) {
            KichCo kichCo = new KichCo();
            kichCo.setId(request.getIdKichCo());
            chiTietSanPham.setIdKichCo(kichCo);
        }
        
        // Update basic fields
        chiTietSanPham.setSoLuongTonKho(request.getSoLuongTonKho());
        chiTietSanPham.setGiaNhap(request.getGiaNhap());
        chiTietSanPham.setGiaBan(request.getGiaBan());
        chiTietSanPham.setMoTaChiTiet(request.getMoTaChiTiet());
        
        // Handle variant image update
        if (request.getUrlAnhSanPham() != null && !request.getUrlAnhSanPham().trim().isEmpty()) {
            // If there's an existing image, update it; otherwise create new
            AnhSanPham anhSanPham = chiTietSanPham.getIdAnhSanPham();
            if (anhSanPham == null) {
                anhSanPham = new AnhSanPham();
                anhSanPham.setLaAnhDaiDien(false);
                anhSanPham.setNgayTao(Instant.now());
                anhSanPham.setDeleted(false);
            }
            
            anhSanPham.setUrlAnh(request.getUrlAnhSanPham());
            anhSanPham.setNgayCapNhat(Instant.now());
            
            // Save the image
            AnhSanPham savedAnhSanPham = anhSanPhamRepository.save(anhSanPham);
            chiTietSanPham.setIdAnhSanPham(savedAnhSanPham);
        }
        
        // Update timestamp
        chiTietSanPham.setNgayCapNhat(Instant.now());
        
        // Generate new variant code if color or size changed
        String newVariantCode = generateVariantCode(
            chiTietSanPham.getIdSanPham(), 
            request.getIdMauSac(), 
            request.getIdKichCo()
        );
        chiTietSanPham.setMa(newVariantCode);
        
        return chiTietSanPhamRepository.save(chiTietSanPham);
    }

    public void updateDeletedStatusByProductId(Integer productId, Boolean deletedStatus) {
        chiTietSanPhamRepository.updateDeletedStatusByProductId(productId, deletedStatus);
    }
}
