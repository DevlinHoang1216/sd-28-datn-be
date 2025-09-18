package com.example.sd_28_phostep_be.controller.account.NhanVien;

import com.example.sd_28_phostep_be.dto.account.request.NhanVien.NhanVienCreateRequest;
import com.example.sd_28_phostep_be.dto.account.request.NhanVien.NhanVienUpdateRequest;
import com.example.sd_28_phostep_be.dto.account.response.NhanVien.NhanVienDTOResponse;
import com.example.sd_28_phostep_be.dto.account.response.NhanVien.NhanVienDetailResponse;
import com.example.sd_28_phostep_be.service.account.NhanVien.NhanVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/nhan-vien")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NhanVienController {

    private final NhanVienService nhanVienService;

    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> getAllNhanVien(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean gioiTinh,
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
            Date keywordAsDate = null;
            if (keyword != null && !keyword.trim().isEmpty()) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    keywordAsDate = new Date(dateFormat.parse(keyword).getTime());
                } catch (ParseException e) {
                    // Not a date, keep keywordAsDate as null
                }
            }

            Page<NhanVienDTOResponse> result = nhanVienService.getAllNhanVien(
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
            errorResponse.put("error", "Lỗi khi lấy danh sách nhân viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNhanVienById(@PathVariable Integer id) {
        try {
            NhanVienDetailResponse nhanVien = nhanVienService.getNhanVienById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", nhanVien);
            response.put("message", "Lấy thông tin nhân viên thành công");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi lấy thông tin nhân viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/them-nhan-vien")
    public ResponseEntity<Map<String, Object>> createNhanVien(@RequestBody NhanVienCreateRequest request) {
        try {
            NhanVienDetailResponse nhanVien = nhanVienService.createNhanVien(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", nhanVien);
            response.put("message", "Tạo nhân viên thành công");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi tạo nhân viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNhanVien(
            @PathVariable Integer id, 
            @RequestBody NhanVienUpdateRequest request) {
        try {
            NhanVienDetailResponse nhanVien = nhanVienService.updateNhanVien(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", nhanVien);
            response.put("message", "Cập nhật nhân viên thành công");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi cập nhật nhân viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNhanVien(@PathVariable Integer id) {
        try {
            nhanVienService.deleteNhanVien(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Xóa nhân viên thành công");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi xóa nhân viên: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, Object>> restoreNhanVien(@PathVariable Integer id) {
        try {
            nhanVienService.restoreNhanVien(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Khôi phục nhân viên thành công");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi khôi phục nhân viên: " + e.getMessage());
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
            case "tennhanvien":
            case "ten_nhan_vien":
                return "tenNhanVien";
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
