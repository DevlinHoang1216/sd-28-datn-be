package com.example.sd_28_phostep_be.controller.product;

import com.example.sd_28_phostep_be.modal.product.KichCo;
import com.example.sd_28_phostep_be.service.product.KichCoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/kich-co")
@CrossOrigin(origins = "*")
public class KichCoController {

    @Autowired
    private KichCoService kichCoService;

    @GetMapping
    public ResponseEntity<Page<KichCo>> getAllWithPagination(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Page<KichCo> kichCoPage = kichCoService.getAllWithPagination(keyword, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(kichCoPage);
    }

    @GetMapping("/all")
    public ResponseEntity<List<KichCo>> getAll() {
        List<KichCo> kichCoList = kichCoService.getAll();
        return ResponseEntity.ok(kichCoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KichCo> getById(@PathVariable Integer id) {
        Optional<KichCo> kichCo = kichCoService.findById(id);
        return kichCo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody KichCo kichCo) {
        try {
            // Validate required fields
            if (kichCo.getTenKichCo() == null || kichCo.getTenKichCo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên kích cỡ không được để trống");
            }

            KichCo savedKichCo = kichCoService.save(kichCo);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedKichCo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi tạo kích cỡ: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody KichCo kichCo) {
        try {
            // Validate required fields
            if (kichCo.getTenKichCo() == null || kichCo.getTenKichCo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên kích cỡ không được để trống");
            }

            KichCo updatedKichCo = kichCoService.update(id, kichCo);
            if (updatedKichCo != null) {
                return ResponseEntity.ok(updatedKichCo);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật kích cỡ: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        try {
            KichCo updatedKichCo = kichCoService.toggleStatus(id);
            if (updatedKichCo != null) {
                String statusText = updatedKichCo.getDeleted() ? "vô hiệu hóa" : "kích hoạt";
                return ResponseEntity.ok().body("Đã " + statusText + " kích cỡ thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật trạng thái kích cỡ: " + e.getMessage());
        }
    }

    @GetMapping("/check-name-exists")
    public ResponseEntity<?> checkNameExists(
            @RequestParam String name,
            @RequestParam(required = false) Integer excludeId) {
        try {
            boolean exists = kichCoService.checkNameExists(name.trim(), excludeId);
            return ResponseEntity.ok().body(new CheckExistsResponse(exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi kiểm tra tên kích cỡ: " + e.getMessage());
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
