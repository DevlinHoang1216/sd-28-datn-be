package com.example.sd_28_phostep_be.controller.product;

import com.example.sd_28_phostep_be.modal.product.DanhMuc;
import com.example.sd_28_phostep_be.service.product.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/danh-muc")
@CrossOrigin(origins = "*")
public class DanhMucController {

    @Autowired
    private DanhMucService danhMucService;

    @GetMapping
    public ResponseEntity<Page<DanhMuc>> getAllWithPagination(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Page<DanhMuc> danhMucPage = danhMucService.getAllWithPagination(keyword, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(danhMucPage);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DanhMuc>> getAll() {
        List<DanhMuc> danhMucList = danhMucService.getAll();
        return ResponseEntity.ok(danhMucList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DanhMuc> getById(@PathVariable Integer id) {
        Optional<DanhMuc> danhMuc = danhMucService.findById(id);
        return danhMuc.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DanhMuc danhMuc) {
        try {
            // Validate required fields
            if (danhMuc.getTenDanhMuc() == null || danhMuc.getTenDanhMuc().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên danh mục không được để trống");
            }
            
            if (danhMuc.getMaDanhMuc() == null || danhMuc.getMaDanhMuc().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã danh mục không được để trống");
            }

            // Check if code already exists
            if (danhMucService.existsByMaDanhMuc(danhMuc.getMaDanhMuc())) {
                return ResponseEntity.badRequest().body("Mã danh mục đã tồn tại");
            }

            DanhMuc savedDanhMuc = danhMucService.save(danhMuc);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDanhMuc);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi tạo danh mục: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody DanhMuc danhMuc) {
        try {
            // Validate required fields
            if (danhMuc.getTenDanhMuc() == null || danhMuc.getTenDanhMuc().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên danh mục không được để trống");
            }
            
            if (danhMuc.getMaDanhMuc() == null || danhMuc.getMaDanhMuc().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã danh mục không được để trống");
            }

            // Check if code already exists for other records
            if (danhMucService.existsByMaDanhMucAndIdNot(danhMuc.getMaDanhMuc(), id)) {
                return ResponseEntity.badRequest().body("Mã danh mục đã tồn tại");
            }

            DanhMuc updatedDanhMuc = danhMucService.update(id, danhMuc);
            if (updatedDanhMuc != null) {
                return ResponseEntity.ok(updatedDanhMuc);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật danh mục: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        try {
            DanhMuc updatedDanhMuc = danhMucService.toggleStatus(id);
            if (updatedDanhMuc != null) {
                String statusText = updatedDanhMuc.getDeleted() ? "vô hiệu hóa" : "kích hoạt";
                return ResponseEntity.ok().body("Đã " + statusText + " danh mục thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật trạng thái danh mục: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            boolean deleted = danhMucService.deleteById(id);
            if (deleted) {
                return ResponseEntity.ok().body("Xóa danh mục thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi xóa danh mục: " + e.getMessage());
        }
    }
}
