package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.dto.product.request.ChiTietSanPhamCreateRequest;
import com.example.sd_28_phostep_be.dto.product.request.ChiTietSanPhamUpdateRequest;
import com.example.sd_28_phostep_be.modal.product.*;
import com.example.sd_28_phostep_be.modal.sale.ChiTietDotGiamGia;
import com.example.sd_28_phostep_be.modal.sale.DotGiamGia;
import com.example.sd_28_phostep_be.repository.product.AnhSanPhamRepository;
import com.example.sd_28_phostep_be.repository.product.ChiTietSanPhamRepository;
import com.example.sd_28_phostep_be.repository.sale.DotGiamGia.ChiTietDotGiamGiaRepository;
import com.example.sd_28_phostep_be.repository.sale.DotGiamGia.DotGiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChiTietSanPhamService {
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final AnhSanPhamRepository anhSanPhamRepository;
    private final DotGiamGiaRepository dotGiamGiaRepository;
    private final ChiTietDotGiamGiaRepository chiTietDotGiamGiaRepository;

    @Autowired
    public ChiTietSanPhamService(ChiTietSanPhamRepository chiTietSanPhamRepository, 
                                AnhSanPhamRepository anhSanPhamRepository,
                                DotGiamGiaRepository dotGiamGiaRepository,
                                ChiTietDotGiamGiaRepository chiTietDotGiamGiaRepository) {
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
        this.anhSanPhamRepository = anhSanPhamRepository;
        this.dotGiamGiaRepository = dotGiamGiaRepository;
        this.chiTietDotGiamGiaRepository = chiTietDotGiamGiaRepository;
    }

    public Page<ChiTietSanPham> getByProductIdPaged(Integer productId, Pageable pageable) {
        return chiTietSanPhamRepository.findByProductIdPaged(productId, pageable);
    }
    
    public Page<ChiTietSanPham> getAllWithFilters(Integer productId, String search, Integer sizeId, Integer colorId, 
                                                 String status, Double minImportPrice, Double maxImportPrice, 
                                                 Double minSellingPrice, Double maxSellingPrice, Pageable pageable) {
        return chiTietSanPhamRepository.findAllWithFilters(productId, search, sizeId, colorId, status, 
                                                          minImportPrice, maxImportPrice, minSellingPrice, maxSellingPrice, pageable);
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

    /**
     * Get active product details by product ID for sales counter
     */
    public List<ChiTietSanPham> getActiveByProductIdForSales(Integer productId) {
        return chiTietSanPhamRepository.findActiveByProductIdForSales(productId);
    }

    /**
     * Get active product details for sales counter with pagination
     */
    public Page<ChiTietSanPham> getActiveProductDetailsForSales(Pageable pageable) {
        return chiTietSanPhamRepository.findActiveProductDetailsForSales(pageable);
    }

    /**
     * Get active product details for sales counter with pagination and search
     */
    public Page<ChiTietSanPham> getActiveProductDetailsForSalesWithKeyword(Pageable pageable, String keyword) {
        return chiTietSanPhamRepository.findActiveProductDetailsForSalesWithKeyword(pageable, keyword);
    }

    /**
     * Get active product details with discount information for sales counter
     */
    public List<ChiTietSanPhamWithDiscount> getActiveProductDetailsWithDiscountPrices() {
        List<ChiTietSanPham> products = chiTietSanPhamRepository.findActiveProductDetailsForSales();
        List<ChiTietSanPhamWithDiscount> result = new ArrayList<>();
        
        for (ChiTietSanPham product : products) {
            DiscountInfo discountInfo = getDiscountInfo(product);
            ChiTietSanPhamWithDiscount productWithDiscount = new ChiTietSanPhamWithDiscount(product, discountInfo);
            result.add(productWithDiscount);
        }
        
        return result;
    }

    // DTO class for product with discount information
    public static class ChiTietSanPhamWithDiscount {
        private final ChiTietSanPham chiTietSanPham;
        private final DiscountInfo discountInfo;
        
        public ChiTietSanPhamWithDiscount(ChiTietSanPham chiTietSanPham, DiscountInfo discountInfo) {
            this.chiTietSanPham = chiTietSanPham;
            this.discountInfo = discountInfo;
        }
        
        public ChiTietSanPham getChiTietSanPham() { return chiTietSanPham; }
        public DiscountInfo getDiscountInfo() { return discountInfo; }
        
        // Convenience methods
        public Integer getId() { return chiTietSanPham.getId(); }
        public String getMa() { return chiTietSanPham.getMa(); }
        public BigDecimal getGiaBan() { return chiTietSanPham.getGiaBan(); }
        public Integer getSoLuongTonKho() { return chiTietSanPham.getSoLuongTonKho(); }
        public SanPham getIdSanPham() { return chiTietSanPham.getIdSanPham(); }
        public AnhSanPham getIdAnhSanPham() { return chiTietSanPham.getIdAnhSanPham(); }
        public KichCo getIdKichCo() { return chiTietSanPham.getIdKichCo(); }
        public MauSac getIdMauSac() { return chiTietSanPham.getIdMauSac(); }
        
        // Discount-related methods
        public BigDecimal getGiaGiamGia() { 
            return discountInfo != null ? discountInfo.getDiscountedPrice() : null; 
        }
        public String getTenDotGiamGia() { 
            return discountInfo != null ? discountInfo.getCampaignName() : null; 
        }
        public boolean hasDiscount() { 
            return discountInfo != null; 
        }
    }

    public void updateDeletedStatusByProductId(Integer productId, Boolean deletedStatus) {
        chiTietSanPhamRepository.updateDeletedStatusByProductId(productId, deletedStatus);
    }

    /**
     * Calculate discounted price for a product detail if discount campaigns are active
     */

    /**
     * Get active product details by product ID with discount campaign prices applied
     */
    public List<ChiTietSanPham> getActiveByProductIdWithDiscountPrices(Integer productId) {
        List<ChiTietSanPham> products = chiTietSanPhamRepository.findActiveByProductIdForSales(productId);
        
        // Apply discount campaign prices to each product
        for (ChiTietSanPham product : products) {
            BigDecimal discountedPrice = calculateDiscountedPrice(product);
            if (discountedPrice != null) {
                product.setGiaBan(discountedPrice);
            }
        }
        
        return products;
    }

    /**
     * Calculate discounted price for a product based on active discount campaigns
     * Combines overlapping discounts by averaging percentages and summing fixed amounts
     */
    private BigDecimal calculateDiscountedPrice(ChiTietSanPham chiTietSanPham) {
        // Find active discount campaigns for this product
        List<ChiTietDotGiamGia> activeDiscounts = chiTietDotGiamGiaRepository.findActiveDiscountsByProductDetail(chiTietSanPham.getId());
        
        if (activeDiscounts.isEmpty()) {
            return null; // No discount, return original price
        }
        
        BigDecimal originalPrice = chiTietSanPham.getGiaBan();
        LocalDate today = LocalDate.now();
        
        // Separate percentage and fixed amount discounts
        List<DotGiamGia> percentageDiscounts = new ArrayList<>();
        List<DotGiamGia> fixedAmountDiscounts = new ArrayList<>();
        
        for (ChiTietDotGiamGia chiTietDiscount : activeDiscounts) {
            DotGiamGia discount = chiTietDiscount.getIdDotGiamGia();
            
            // Check if discount campaign is currently active
            if (discount.getTrangThai() && 
                !discount.getNgayBatDau().isAfter(today) && 
                !discount.getNgayKetThuc().isBefore(today)) {
                
                if ("PHAN_TRAM".equals(discount.getLoaiGiamGiaApDung())) {
                    percentageDiscounts.add(discount);
                } else {
                    fixedAmountDiscounts.add(discount);
                }
            }
        }
        
        // If no valid discounts, return original price
        if (percentageDiscounts.isEmpty() && fixedAmountDiscounts.isEmpty()) {
            return null;
        }
        
        // Calculate combined discount
        BigDecimal finalPrice = originalPrice;
        
        // Apply combined percentage discounts (average the percentages)
        if (!percentageDiscounts.isEmpty()) {
            BigDecimal totalPercentage = BigDecimal.ZERO;
            BigDecimal totalMaxDiscount = BigDecimal.ZERO;
            
            for (DotGiamGia discount : percentageDiscounts) {
                totalPercentage = totalPercentage.add(discount.getGiaTriGiamGia());
                if (discount.getSoTienGiamToiDa() != null) {
                    totalMaxDiscount = totalMaxDiscount.add(discount.getSoTienGiamToiDa());
                }
            }
            
            // Average the percentage (combine discounts)
            BigDecimal averagePercentage = totalPercentage.divide(BigDecimal.valueOf(percentageDiscounts.size()), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal discountAmount = originalPrice.multiply(averagePercentage.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
            
            // Apply combined maximum discount limit
            if (totalMaxDiscount.compareTo(BigDecimal.ZERO) > 0 && 
                discountAmount.compareTo(totalMaxDiscount) > 0) {
                discountAmount = totalMaxDiscount;
            }
            
            finalPrice = finalPrice.subtract(discountAmount);
        }
        
        // Apply combined fixed amount discounts (sum all fixed amounts)
        if (!fixedAmountDiscounts.isEmpty()) {
            BigDecimal totalFixedDiscount = BigDecimal.ZERO;
            
            for (DotGiamGia discount : fixedAmountDiscounts) {
                totalFixedDiscount = totalFixedDiscount.add(discount.getGiaTriGiamGia());
            }
            
            finalPrice = finalPrice.subtract(totalFixedDiscount);
        }
        
        // Ensure price doesn't go below zero
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }
        
        return finalPrice.equals(originalPrice) ? null : finalPrice;
    }

    public DiscountInfo getDiscountInfo(ChiTietSanPham chiTietSanPham) {
        List<ChiTietDotGiamGia> activeDiscounts = chiTietDotGiamGiaRepository.findActiveDiscountsByProductDetail(chiTietSanPham.getId());
        
        if (activeDiscounts.isEmpty()) {
            return null; // No discount
        }
        
        BigDecimal originalPrice = chiTietSanPham.getGiaBan();
        LocalDate today = LocalDate.now();
        
        // Separate percentage and fixed amount discounts
        List<DotGiamGia> percentageDiscounts = new ArrayList<>();
        List<DotGiamGia> fixedAmountDiscounts = new ArrayList<>();
        List<String> campaignNames = new ArrayList<>();
        
        for (ChiTietDotGiamGia chiTietDiscount : activeDiscounts) {
            DotGiamGia discount = chiTietDiscount.getIdDotGiamGia();
            
            // Check if discount campaign is currently active
            if (discount.getTrangThai() && 
                !discount.getNgayBatDau().isAfter(today) && 
                !discount.getNgayKetThuc().isBefore(today)) {
                
                campaignNames.add(discount.getTenDotGiamGia());
                
                if ("PHAN_TRAM".equals(discount.getLoaiGiamGiaApDung())) {
                    percentageDiscounts.add(discount);
                } else {
                    fixedAmountDiscounts.add(discount);
                }
            }
        }
        
        // If no valid discounts, return null
        if (percentageDiscounts.isEmpty() && fixedAmountDiscounts.isEmpty()) {
            return null;
        }
        
        // Calculate combined discount
        BigDecimal finalPrice = originalPrice;
        
        // Apply combined percentage discounts (average the percentages)
        if (!percentageDiscounts.isEmpty()) {
            BigDecimal totalPercentage = BigDecimal.ZERO;
            BigDecimal totalMaxDiscount = BigDecimal.ZERO;
            
            for (DotGiamGia discount : percentageDiscounts) {
                totalPercentage = totalPercentage.add(discount.getGiaTriGiamGia());
                if (discount.getSoTienGiamToiDa() != null) {
                    totalMaxDiscount = totalMaxDiscount.add(discount.getSoTienGiamToiDa());
                }
            }
            
            // Average the percentage (combine discounts)
            BigDecimal averagePercentage = totalPercentage.divide(BigDecimal.valueOf(percentageDiscounts.size()), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal discountAmount = originalPrice.multiply(averagePercentage.divide(BigDecimal.valueOf(100), 4, BigDecimal.ROUND_HALF_UP));
            
            // Apply combined maximum discount limit
            if (totalMaxDiscount.compareTo(BigDecimal.ZERO) > 0 && 
                discountAmount.compareTo(totalMaxDiscount) > 0) {
                discountAmount = totalMaxDiscount;
            }
            
            finalPrice = finalPrice.subtract(discountAmount);
        }
        
        // Apply combined fixed amount discounts (sum all fixed amounts)
        if (!fixedAmountDiscounts.isEmpty()) {
            BigDecimal totalFixedDiscount = BigDecimal.ZERO;
            
            for (DotGiamGia discount : fixedAmountDiscounts) {
                totalFixedDiscount = totalFixedDiscount.add(discount.getGiaTriGiamGia());
            }
            
            finalPrice = finalPrice.subtract(totalFixedDiscount);
        }
        
        // Ensure price doesn't go below zero
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }
        
        if (finalPrice.equals(originalPrice)) {
            return null; // No valid discount found
        }
        
        // Combine campaign names
        String combinedCampaignName = String.join(" + ", campaignNames);
        
        return new DiscountInfo(originalPrice, finalPrice, combinedCampaignName);
    }

    // Inner class for discount information
    public static class DiscountInfo {
        private final BigDecimal originalPrice;
        private final BigDecimal discountedPrice;
        private final String campaignName;
        
        public DiscountInfo(BigDecimal originalPrice, BigDecimal discountedPrice, String campaignName) {
            this.originalPrice = originalPrice;
            this.discountedPrice = discountedPrice;
            this.campaignName = campaignName;
        }
        
        public BigDecimal getOriginalPrice() { return originalPrice; }
        public BigDecimal getDiscountedPrice() { return discountedPrice; }
        public String getCampaignName() { return campaignName; }
    }
}
