package com.example.sd_28_phostep_be.controller.account.KhachHang;

import com.example.sd_28_phostep_be.dto.account.request.KhachHang.KhachHangCreateRequest;
import com.example.sd_28_phostep_be.dto.account.request.KhachHang.KhachHangUpdateRequest;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDTOResponse;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDetailResponse;
import com.example.sd_28_phostep_be.service.account.KhachHang.KhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/khach-hang")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KhachHangController {

    private final KhachHangService khachHangService;

    @GetMapping
    public ResponseEntity<List<KhachHangDTOResponse>> getAllActiveKhachHang() {
        try {
            List<KhachHangDTOResponse> customers = khachHangService.getAllActiveKhachHang();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getAllKhachHang(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Short gioiTinh,
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            // Validate and map sortBy field to actual entity fields
            String validSortBy = mapSortField(sortBy);
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(validSortBy).descending() : Sort.by(validSortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // Try to parse keyword as date if it looks like a date
            Instant keywordAsDate = null;
            if (keyword != null && !keyword.trim().isEmpty()) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    keywordAsDate = dateFormat.parse(keyword).toInstant();
                } catch (ParseException e) {
                    // Not a date, keep keywordAsDate as null
                }
            }

            Page<KhachHangDTOResponse> result = khachHangService.getAllKhachHang(
                keyword, keywordAsDate, gioiTinh, trangThai, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", result.getContent());
            response.put("currentPage", result.getNumber());
            response.put("totalItems", result.getTotalElements());
            response.put("totalPages", result.getTotalPages());
            response.put("size", result.getSize());
            response.put("hasNext", result.hasNext());
            response.put("hasPrevious", result.hasPrevious());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi lấy danh sách khách hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getKhachHangById(@PathVariable Integer id) {
        try {
            KhachHangDetailResponse khachHang = khachHangService.getKhachHangById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", khachHang);
            response.put("message", "Lấy thông tin khách hàng thành công");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi lấy thông tin khách hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/them-khach-hang")
    public ResponseEntity<Map<String, Object>> createKhachHang(@RequestBody KhachHangCreateRequest request) {
        try {
            KhachHangDetailResponse khachHang = khachHangService.createKhachHang(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", khachHang);
            response.put("message", "Tạo khách hàng thành công");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi tạo khách hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateKhachHang(
            @PathVariable Integer id, 
            @RequestBody KhachHangUpdateRequest request) {
        try {
            KhachHangDetailResponse khachHang = khachHangService.updateKhachHang(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", khachHang);
            response.put("message", "Cập nhật khách hàng thành công");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi cập nhật khách hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteKhachHang(@PathVariable Integer id) {
        try {
            khachHangService.deleteKhachHang(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Xóa khách hàng thành công");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi xóa khách hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, Object>> restoreKhachHang(@PathVariable Integer id) {
        try {
            khachHangService.restoreKhachHang(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Khôi phục khách hàng thành công");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi khôi phục khách hàng: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Map frontend sort field names to actual entity field names
     * This prevents errors when frontend sends invalid field names
     */
    private String mapSortField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "id"; // Default sort field
        }
        
        switch (sortBy.toLowerCase()) {
            case "ngaytao":
            case "ngay_tao":
                return "createdAt"; // Map to correct field name
            case "createdat":
            case "created_at":
                return "createdAt";
            case "updatedat":
            case "updated_at":
                return "updatedAt";
            case "ten":
            case "tenkhachhang":
            case "ten_khach_hang":
                return "ten";
            case "ngaysinh":
            case "ngay_sinh":
                return "ngaySinh";
            case "gioitinh":
            case "gioi_tinh":
                return "gioiTinh";
            case "cccd":
                return "cccd";
            case "ma":
                return "ma";
            case "id":
                return "id";
            default:
                // If field name is not recognized, default to id to prevent errors
                return "id";
        }
    }
}
