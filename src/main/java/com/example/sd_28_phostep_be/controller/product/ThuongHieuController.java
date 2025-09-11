package com.example.sd_28_phostep_be.controller.product;

import com.example.sd_28_phostep_be.modal.product.ThuongHieu;
import com.example.sd_28_phostep_be.service.product.ThuongHieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/thuong-hieu")
@CrossOrigin(origins = "*")
public class ThuongHieuController {

    @Autowired
    private ThuongHieuService thuongHieuService;

    @GetMapping
    public ResponseEntity<Page<ThuongHieu>> getAllWithPagination(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Page<ThuongHieu> thuongHieuPage = thuongHieuService.getAllWithPagination(keyword, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(thuongHieuPage);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ThuongHieu>> getAll() {
        List<ThuongHieu> thuongHieuList = thuongHieuService.getAll();
        return ResponseEntity.ok(thuongHieuList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThuongHieu> getById(@PathVariable Integer id) {
        Optional<ThuongHieu> thuongHieu = thuongHieuService.findById(id);
        return thuongHieu.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ThuongHieu thuongHieu) {
        try {
            // Validate required fields
            if (thuongHieu.getTenThuongHieu() == null || thuongHieu.getTenThuongHieu().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên thương hiệu không được để trống");
            }
            
            if (thuongHieu.getMaThuongHieu() == null || thuongHieu.getMaThuongHieu().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã thương hiệu không được để trống");
            }

            // Check if code already exists
            if (thuongHieuService.existsByMaThuongHieu(thuongHieu.getMaThuongHieu())) {
                return ResponseEntity.badRequest().body("Mã thương hiệu đã tồn tại");
            }

            ThuongHieu savedThuongHieu = thuongHieuService.save(thuongHieu);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedThuongHieu);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi tạo thương hiệu: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody ThuongHieu thuongHieu) {
        try {
            // Validate required fields
            if (thuongHieu.getTenThuongHieu() == null || thuongHieu.getTenThuongHieu().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên thương hiệu không được để trống");
            }
            
            if (thuongHieu.getMaThuongHieu() == null || thuongHieu.getMaThuongHieu().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã thương hiệu không được để trống");
            }

            // Check if code already exists for other records
            if (thuongHieuService.existsByMaThuongHieuAndIdNot(thuongHieu.getMaThuongHieu(), id)) {
                return ResponseEntity.badRequest().body("Mã thương hiệu đã tồn tại");
            }

            ThuongHieu updatedThuongHieu = thuongHieuService.update(id, thuongHieu);
            if (updatedThuongHieu != null) {
                return ResponseEntity.ok(updatedThuongHieu);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật thương hiệu: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        try {
            ThuongHieu updatedThuongHieu = thuongHieuService.toggleStatus(id);
            if (updatedThuongHieu != null) {
                String statusText = updatedThuongHieu.getDeleted() ? "vô hiệu hóa" : "kích hoạt";
                return ResponseEntity.ok().body("Đã " + statusText + " thương hiệu thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật trạng thái thương hiệu: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            boolean deleted = thuongHieuService.deleteById(id);
            if (deleted) {
                return ResponseEntity.ok().body("Xóa thương hiệu thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi xóa thương hiệu: " + e.getMessage());
        }
    }
}
