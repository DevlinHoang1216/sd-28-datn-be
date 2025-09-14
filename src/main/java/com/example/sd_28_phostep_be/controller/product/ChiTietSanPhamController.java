package com.example.sd_28_phostep_be.controller.product;

import com.example.sd_28_phostep_be.dto.product.request.ChiTietSanPhamUpdateRequest;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.service.product.ChiTietSanPhamService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/chi-tiet-san-pham")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ChiTietSanPhamController {
    private final ChiTietSanPhamService chiTietSanPhamService;

    public ChiTietSanPhamController(ChiTietSanPhamService chiTietSanPhamService) {
        this.chiTietSanPhamService = chiTietSanPhamService;
    }

    @GetMapping("/product/{productId}")
    public Page<ChiTietSanPham> getByProductId(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer sizeId,
            @RequestParam(required = false) Integer colorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minImportPrice,
            @RequestParam(required = false) Double maxImportPrice,
            @RequestParam(required = false) Double minSellingPrice,
            @RequestParam(required = false) Double maxSellingPrice) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
            
        Pageable pageable = PageRequest.of(page, size, sort);
        return chiTietSanPhamService.getAllWithFilters(productId, search, sizeId, colorId, status, 
                                                      minImportPrice, maxImportPrice, minSellingPrice, maxSellingPrice, pageable);
    }
    
    @GetMapping
    public Page<ChiTietSanPham> getAllWithFilters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer sizeId,
            @RequestParam(required = false) Integer colorId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minImportPrice,
            @RequestParam(required = false) Double maxImportPrice,
            @RequestParam(required = false) Double minSellingPrice,
            @RequestParam(required = false) Double maxSellingPrice) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
            
        Pageable pageable = PageRequest.of(page, size, sort);
        return chiTietSanPhamService.getAllWithFilters(null, search, sizeId, colorId, status, 
                                                      minImportPrice, maxImportPrice, minSellingPrice, maxSellingPrice, pageable);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ChiTietSanPham> getById(@PathVariable Integer id) {
        Optional<ChiTietSanPham> chiTietSanPham = chiTietSanPhamService.findById(id);
        if (chiTietSanPham.isPresent()) {
            return ResponseEntity.ok(chiTietSanPham.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ChiTietSanPham> updateChiTietSanPham(
            @PathVariable Integer id,
            @Valid @RequestBody ChiTietSanPhamUpdateRequest request) {
        try {
            ChiTietSanPham updatedChiTietSanPham = chiTietSanPhamService.updateChiTietSanPham(id, request);
            return ResponseEntity.ok(updatedChiTietSanPham);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Sales counter endpoints
    @GetMapping("/sales/product/{productId}")
    public List<ChiTietSanPham> getActiveByProductIdForSales(@PathVariable Integer productId) {
        return chiTietSanPhamService.getActiveByProductIdForSales(productId);
    }
    
    @GetMapping("/sales")
    public Page<ChiTietSanPham> getActiveProductDetailsForSales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
            
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            return chiTietSanPhamService.getActiveProductDetailsForSalesWithKeyword(pageable, keyword.trim());
        } else {
            return chiTietSanPhamService.getActiveProductDetailsForSales(pageable);
        }
    }
}
