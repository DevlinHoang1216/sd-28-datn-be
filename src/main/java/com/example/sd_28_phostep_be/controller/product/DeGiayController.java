package com.example.sd_28_phostep_be.controller.product;

import com.example.sd_28_phostep_be.modal.product.DeGiay;
import com.example.sd_28_phostep_be.service.product.DeGiayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/de-giay")
@CrossOrigin(origins = "*")
public class DeGiayController {

    @Autowired
    private DeGiayService deGiayService;

    @GetMapping
    public ResponseEntity<Page<DeGiay>> getAllDeGiay(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ngayTao") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Page<DeGiay> deGiayPage = deGiayService.getAllWithPagination(keyword, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(deGiayPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeGiay>> getAllDeGiay() {
        try {
            List<DeGiay> deGiayList = deGiayService.getAll();
            return ResponseEntity.ok(deGiayList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeGiay> getDeGiayById(@PathVariable Integer id) {
        try {
            Optional<DeGiay> deGiay = deGiayService.findById(id);
            return deGiay.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createDeGiay(@RequestBody DeGiay deGiay) {
        try {
            DeGiay savedDeGiay = deGiayService.save(deGiay);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDeGiay);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi tạo đế giày");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDeGiay(@PathVariable Integer id, @RequestBody DeGiay deGiayDetails) {
        try {
            DeGiay updatedDeGiay = deGiayService.update(id, deGiayDetails);
            return ResponseEntity.ok(updatedDeGiay);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật đế giày");
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        try {
            DeGiay updatedDeGiay = deGiayService.toggleStatus(id);
            if (updatedDeGiay != null) {
                String statusText = updatedDeGiay.getDeleted() ? "vô hiệu hóa" : "kích hoạt";
                return ResponseEntity.ok().body("Đã " + statusText + " đế giày thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật trạng thái đế giày: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDeGiay(@PathVariable Integer id) {
        try {
            deGiayService.delete(id);
            return ResponseEntity.ok().body("Xóa đế giày thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi xóa đế giày");
        }
    }
}
