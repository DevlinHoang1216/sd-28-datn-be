package com.example.sd_28_phostep_be.controller.product;

import com.example.sd_28_phostep_be.dto.product.request.ChiTietSanPhamCreateRequest;
import com.example.sd_28_phostep_be.dto.product.request.ProductWithVariantsCreateRequest;
import com.example.sd_28_phostep_be.dto.product.request.SanPhamUpdateRequest;
import com.example.sd_28_phostep_be.dto.product.request.SanPhamCreateRequest;
import com.example.sd_28_phostep_be.dto.product.response.SanPhamResponse;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.modal.product.SanPham;
import com.example.sd_28_phostep_be.service.product.ChiTietSanPhamService;
import com.example.sd_28_phostep_be.service.product.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/san-pham")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class SanPhamController {
    private final SanPhamService sanPhamService;
    private final ChiTietSanPhamService chiTietSanPhamService;

    public SanPhamController(SanPhamService sanPhamService, ChiTietSanPhamService chiTietSanPhamService) {
        this.sanPhamService = sanPhamService;
        this.chiTietSanPhamService = chiTietSanPhamService;
    }

    @GetMapping
    public Page<SanPhamResponse> getAllWithDetailsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
            
        Pageable pageable = PageRequest.of(page, size, sort);
        return sanPhamService.getAllWithDetailsPagedAsDTO(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPham> getById(@PathVariable Integer id) {
        Optional<SanPham> sanPham = sanPhamService.findById(id);
        return sanPham.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createSanPham(@RequestBody @Valid SanPhamCreateRequest request) {
        try {
            SanPham newSanPham = sanPhamService.createSanPham(request);
            return ResponseEntity.ok(newSanPham);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSanPham(@PathVariable Integer id, @RequestBody @Valid SanPhamUpdateRequest request) {
        try {
            SanPham updatedSanPham = sanPhamService.updateSanPham(id, request);
            return ResponseEntity.ok(updatedSanPham);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/with-variants")
    public ResponseEntity<?> createProductWithVariants(@RequestBody @Valid ProductWithVariantsCreateRequest request) {
        try {
            SanPham newProduct = sanPhamService.createProductWithVariants(request);
            return ResponseEntity.ok(newProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/{productId}/variants")
    public ResponseEntity<?> addVariantsToProduct(@PathVariable Integer productId, @RequestBody @Valid List<ChiTietSanPhamCreateRequest> variants) {
        try {
            Optional<SanPham> productOpt = sanPhamService.findById(productId);
            if (productOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm với ID: " + productId);
            }
            SanPham product = productOpt.get();
            List<ChiTietSanPham> createdVariants = chiTietSanPhamService.createProductVariants(product, variants);
            return ResponseEntity.ok(createdVariants);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleProductStatus(@PathVariable Integer id) {
        try {
            SanPham updatedProduct = sanPhamService.toggleProductStatus(id);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/by-thuong-hieu")
    public ResponseEntity<Page<SanPham>> getProductsByBrandIds(
            @RequestParam List<Integer> brandIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SanPham> products = sanPhamService.getProductsByBrandIds(brandIds, pageable);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-danh-muc")
    public ResponseEntity<Page<SanPham>> getProductsByCategoryName(
            @RequestParam String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SanPham> products = sanPhamService.getProductsByCategoryName(categoryName, pageable);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
