package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.dto.product.request.ProductWithVariantsCreateRequest;
import com.example.sd_28_phostep_be.dto.product.request.SanPhamCreateRequest;
import com.example.sd_28_phostep_be.dto.product.request.SanPhamUpdateRequest;
import com.example.sd_28_phostep_be.dto.product.response.SanPhamResponse;
import com.example.sd_28_phostep_be.dto.product.response.ChiTietSanPhamResponse;
import com.example.sd_28_phostep_be.modal.product.*;
import com.example.sd_28_phostep_be.repository.product.AnhSanPhamRepository;
import com.example.sd_28_phostep_be.repository.product.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for SanPham entity
 */
@Service
public class SanPhamService {
    private final SanPhamRepository sanPhamRepository;
    private final AnhSanPhamRepository anhSanPhamRepository;
    private final ChiTietSanPhamService chiTietSanPhamService;

    @Autowired
    public SanPhamService(SanPhamRepository sanPhamRepository, AnhSanPhamRepository anhSanPhamRepository, ChiTietSanPhamService chiTietSanPhamService) {
        this.sanPhamRepository = sanPhamRepository;
        this.anhSanPhamRepository = anhSanPhamRepository;
        this.chiTietSanPhamService = chiTietSanPhamService;
    }

    public Page<SanPham> getAllWithDetailsPaged(Pageable pageable) {
        return sanPhamRepository.findAllActiveWithDetailsPaged(pageable);
    }

    /**
     * Get all products with details as DTO response (to avoid Hibernate proxy serialization issues)
     */
    public Page<SanPhamResponse> getAllWithDetailsPagedAsDTO(Pageable pageable) {
        Page<SanPham> sanPhamPage = sanPhamRepository.findAllActiveWithDetailsPaged(pageable);
        
        List<SanPhamResponse> sanPhamResponses = sanPhamPage.getContent().stream()
                .map(this::convertToSanPhamResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(sanPhamResponses, pageable, sanPhamPage.getTotalElements());
    }

    /**
     * Convert SanPham entity to SanPhamResponse DTO
     */
    private SanPhamResponse convertToSanPhamResponse(SanPham sanPham) {
        // Determine status based on deleted field (true = active, false = inactive)
        Boolean trangThai = sanPham.getDeleted() == null || !sanPham.getDeleted();
        
        SanPhamResponse.SanPhamResponseBuilder builder = SanPhamResponse.builder()
                .id(sanPham.getId())
                .ma(sanPham.getMa())
                .tenSanPham(sanPham.getTenSanPham())
                .moTa(sanPham.getMoTaSanPham())
                .trangThai(trangThai)
                .deleted(sanPham.getDeleted())
                .ngayTao(sanPham.getNgayTao())
                .ngayCapNhat(sanPham.getNgayCapNhat());

        // Category info
        if (sanPham.getIdDanhMuc() != null) {
            builder.idDanhMuc(sanPham.getIdDanhMuc().getId())
                   .tenDanhMuc(sanPham.getIdDanhMuc().getTenDanhMuc());
        }

        // Brand info
        if (sanPham.getIdThuongHieu() != null) {
            builder.idThuongHieu(sanPham.getIdThuongHieu().getId())
                   .tenThuongHieu(sanPham.getIdThuongHieu().getTenThuongHieu());
        }

        // Material info
        if (sanPham.getIdChatLieu() != null) {
            builder.idChatLieu(sanPham.getIdChatLieu().getId())
                   .tenChatLieu(sanPham.getIdChatLieu().getTenChatLieu());
        }

        // Sole info
        if (sanPham.getIdDeGiay() != null) {
            builder.idDeGiay(sanPham.getIdDeGiay().getId())
                   .tenDeGiay(sanPham.getIdDeGiay().getTenDeGiay());
        }

        // Product variants
        if (sanPham.getChiTietSanPhams() != null) {
            List<ChiTietSanPhamResponse> chiTietResponses = sanPham.getChiTietSanPhams().stream()
                    .map(this::convertToChiTietSanPhamResponse)
                    .collect(Collectors.toList());
            
            builder.chiTietSanPhams(chiTietResponses)
                   .totalVariants((long) chiTietResponses.size())
                   .activeVariants(chiTietResponses.stream()
                           .filter(ct -> ct.getTrangThai() != null && ct.getTrangThai())
                           .count());
        }

        return builder.build();
    }

    /**
     * Convert ChiTietSanPham entity to ChiTietSanPhamResponse DTO
     */
    private ChiTietSanPhamResponse convertToChiTietSanPhamResponse(ChiTietSanPham chiTiet) {
        // Determine status based on deleted field (true = active, false = inactive)
        Boolean trangThai = chiTiet.getDeleted() == null || !chiTiet.getDeleted();
        
        ChiTietSanPhamResponse.ChiTietSanPhamResponseBuilder builder = ChiTietSanPhamResponse.builder()
                .id(chiTiet.getId())
                .ma(chiTiet.getMa())
                .giaBan(chiTiet.getGiaBan())
                .soLuongTonKho(chiTiet.getSoLuongTonKho())
                .trangThai(trangThai)
                .deleted(chiTiet.getDeleted())
                .ngayTao(chiTiet.getNgayTao())
                .ngayCapNhat(chiTiet.getNgayCapNhat());

        // Product info
        if (chiTiet.getIdSanPham() != null) {
            builder.idSanPham(chiTiet.getIdSanPham().getId())
                   .tenSanPham(chiTiet.getIdSanPham().getTenSanPham());
        }

        // Color info
        if (chiTiet.getIdMauSac() != null) {
            builder.idMauSac(chiTiet.getIdMauSac().getId())
                   .tenMauSac(chiTiet.getIdMauSac().getTenMauSac())
                   .hexMauSac(chiTiet.getIdMauSac().getHex());
        }

        // Size info
        if (chiTiet.getIdKichCo() != null) {
            builder.idKichCo(chiTiet.getIdKichCo().getId())
                   .tenKichCo(chiTiet.getIdKichCo().getTenKichCo());
        }

        // Image info - get first image URL if available
        if (chiTiet.getIdSanPham() != null && chiTiet.getIdSanPham().getIdAnhSanPham() != null) {
            builder.urlAnhSanPham(chiTiet.getIdSanPham().getIdAnhSanPham().getUrlAnh());
        }

        return builder.build();
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
        if (request.getIdChatLieu() != null) {
            ChatLieu chatLieu = new ChatLieu();
            chatLieu.setId(request.getIdChatLieu());
            existingSanPham.setIdChatLieu(chatLieu);
        }
        if (request.getIdDeGiay() != null) {
            DeGiay deGiay = new DeGiay();
            deGiay.setId(request.getIdDeGiay());
            existingSanPham.setIdDeGiay(deGiay);
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
        if (request.getIdChatLieu() != null) {
            ChatLieu chatLieu = new ChatLieu();
            chatLieu.setId(request.getIdChatLieu());
            sanPham.setIdChatLieu(chatLieu);
        }
        if (request.getIdDeGiay() != null) {
            DeGiay deGiay = new DeGiay();
            deGiay.setId(request.getIdDeGiay());
            sanPham.setIdDeGiay(deGiay);
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

    @Transactional
    public SanPham createProductWithVariants(ProductWithVariantsCreateRequest request) {
        // First create the main product
        SanPham sanPham = createSanPham(request.getSanPham());
        
        // Generate product code after saving (so we have the ID)
        String productCode = generateProductCode(sanPham.getId());
        sanPham.setMa(productCode);
        sanPham = sanPhamRepository.save(sanPham);
        
        // Then create all the product variants
        List<ChiTietSanPham> createdVariants = chiTietSanPhamService.createProductVariants(sanPham, request.getChiTietSanPhams());
        
        // Set the variants to the product (optional, for response completeness)
        sanPham.setChiTietSanPhams(new java.util.LinkedHashSet<>(createdVariants));
        
        return sanPham;
    }
    
    private String generateProductCode(Integer productId) {
        // Generate a unique product code
        // Format: SP{productId}
        return String.format("SP%d", productId);
    }

    /**
     * Get active products for sales counter with pagination and search
     */
    public Page<SanPham> getActiveProductsForSales(Pageable pageable, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sanPhamRepository.findActiveProductsForSales(pageable);
        } else {
            return sanPhamRepository.findActiveProductsForSalesWithKeyword(pageable, keyword.trim());
        }
    }

    @Transactional
    public SanPham toggleProductStatus(Integer id) {
        Optional<SanPham> optionalSanPham = sanPhamRepository.findById(id);
        
        if (optionalSanPham.isEmpty()) {
            throw new RuntimeException("Sản phẩm không tồn tại với ID: " + id);
        }
        
        SanPham sanPham = optionalSanPham.get();
        
        // Toggle the deleted status
        Boolean currentStatus = sanPham.getDeleted();
        Boolean newStatus = (currentStatus == null || !currentStatus) ? true : false;
        sanPham.setDeleted(newStatus);
        sanPham.setNgayCapNhat(Instant.now());
        
        // Save the updated product
        SanPham updatedProduct = sanPhamRepository.save(sanPham);
        
        // Cascade the status change to all related product details
        chiTietSanPhamService.updateDeletedStatusByProductId(id, newStatus);
        
        return updatedProduct;
    }
}
