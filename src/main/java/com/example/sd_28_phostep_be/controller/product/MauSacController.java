package com.example.sd_28_phostep_be.controller.product;

import com.example.sd_28_phostep_be.modal.product.MauSac;
import com.example.sd_28_phostep_be.service.product.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mau-sac")
@CrossOrigin(origins = "*")
public class MauSacController {

    @Autowired
    private MauSacService mauSacService;

    @GetMapping
    public ResponseEntity<Page<MauSac>> getAllWithPagination(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Page<MauSac> mauSacPage = mauSacService.getAllWithPagination(keyword, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(mauSacPage);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MauSac>> getAll() {
        List<MauSac> mauSacList = mauSacService.getAll();
        return ResponseEntity.ok(mauSacList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MauSac> getById(@PathVariable Integer id) {
        Optional<MauSac> mauSac = mauSacService.findById(id);
        return mauSac.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MauSac mauSac) {
        try {
            // Validate required fields
            if (mauSac.getTenMauSac() == null || mauSac.getTenMauSac().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên màu sắc không được để trống");
            }

            MauSac savedMauSac = mauSacService.save(mauSac);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMauSac);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi tạo màu sắc: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody MauSac mauSac) {
        try {
            // Validate required fields
            if (mauSac.getTenMauSac() == null || mauSac.getTenMauSac().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên màu sắc không được để trống");
            }

            MauSac updatedMauSac = mauSacService.update(id, mauSac);
            if (updatedMauSac != null) {
                return ResponseEntity.ok(updatedMauSac);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật màu sắc: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        try {
            MauSac updatedMauSac = mauSacService.toggleStatus(id);
            if (updatedMauSac != null) {
                String statusText = updatedMauSac.getDeleted() ? "vô hiệu hóa" : "kích hoạt";
                return ResponseEntity.ok().body("Đã " + statusText + " màu sắc thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật trạng thái màu sắc: " + e.getMessage());
        }
    }

    @GetMapping("/check-name-exists")
    public ResponseEntity<?> checkNameExists(
            @RequestParam String name,
            @RequestParam(required = false) Integer excludeId) {
        try {
            boolean exists = mauSacService.checkNameExists(name.trim(), excludeId);
            return ResponseEntity.ok().body(new CheckExistsResponse(exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi kiểm tra tên màu sắc: " + e.getMessage());
        }
    }

    @GetMapping("/check-name-and-hex-exists")
    public ResponseEntity<?> checkNameAndHexExists(
            @RequestParam String name,
            @RequestParam String hex,
            @RequestParam(required = false) Integer excludeId) {
        try {
            boolean exists = mauSacService.checkNameAndHexExists(name.trim(), hex.trim(), excludeId);
            return ResponseEntity.ok().body(new CheckExistsResponse(exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi kiểm tra màu sắc: " + e.getMessage());
        }
    }

    // Inner class for response
    public static class CheckExistsResponse {
        private boolean exists;

        public CheckExistsResponse(boolean exists) {
            this.exists = exists;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }

}
