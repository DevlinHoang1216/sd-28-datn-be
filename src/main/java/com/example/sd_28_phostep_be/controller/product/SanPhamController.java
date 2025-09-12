package com.example.sd_28_phostep_be.controller.product;

import com.example.sd_28_phostep_be.dto.product.request.SanPhamUpdateRequest;
import com.example.sd_28_phostep_be.dto.product.request.SanPhamCreateRequest;
import com.example.sd_28_phostep_be.modal.product.SanPham;
import com.example.sd_28_phostep_be.service.product.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/san-pham")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class SanPhamController {
    private final SanPhamService sanPhamService;

    public SanPhamController(SanPhamService sanPhamService) {
        this.sanPhamService = sanPhamService;
    }

    @GetMapping
    public Page<SanPham> getAllWithDetailsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : 
            Sort.by(sortBy).ascending();
            
        Pageable pageable = PageRequest.of(page, size, sort);
        return sanPhamService.getAllWithDetailsPaged(pageable);
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
}
