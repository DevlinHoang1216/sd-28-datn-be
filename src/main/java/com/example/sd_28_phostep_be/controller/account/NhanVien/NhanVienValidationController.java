package com.example.sd_28_phostep_be.controller.account.NhanVien;

import com.example.sd_28_phostep_be.dto.account.request.NhanVien.NhanVienCreateRequest;
import com.example.sd_28_phostep_be.dto.account.request.NhanVien.NhanVienUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/nhan-vien/validate")
@CrossOrigin(origins = "*")
public class NhanVienValidationController {

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[^\\d]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern CCCD_PATTERN = Pattern.compile("^\\d{12}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> validateCreateEmployee(@RequestBody NhanVienCreateRequest request) {
        List<String> errors = new ArrayList<>();
        
        // Validate required fields
        if (request.getTenNhanVien() == null || request.getTenNhanVien().trim().isEmpty()) {
            errors.add("Tên nhân viên không được để trống");
        } else if (!NAME_PATTERN.matcher(request.getTenNhanVien().trim()).matches()) {
            errors.add("Tên nhân viên không được chứa số");
        }
        
        if (request.getNgaySinh() == null) {
            errors.add("Ngày sinh không được để trống");
        }
        
        if (request.getSoDienThoai() == null || request.getSoDienThoai().trim().isEmpty()) {
            errors.add("Số điện thoại không được để trống");
        } else if (!PHONE_PATTERN.matcher(request.getSoDienThoai()).matches()) {
            errors.add("Số điện thoại phải gồm đúng 10 chữ số");
        }
        
        if (request.getCccd() == null || request.getCccd().trim().isEmpty()) {
            errors.add("CCCD không được để trống");
        } else if (!CCCD_PATTERN.matcher(request.getCccd()).matches()) {
            errors.add("CCCD phải gồm đúng 12 chữ số");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.add("Email không được để trống");
        } else if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            errors.add("Email không đúng định dạng");
        }
        
        if (request.getDiaChiCuThe() == null || request.getDiaChiCuThe().trim().isEmpty()) {
            errors.add("Địa chỉ cụ thể không được để trống");
        }
        
        if (request.getThanhPho() == null || request.getThanhPho().trim().isEmpty()) {
            errors.add("Tỉnh/Thành phố không được để trống");
        }
        
        if (request.getQuan() == null || request.getQuan().trim().isEmpty()) {
            errors.add("Quận/Huyện không được để trống");
        }
        
        if (request.getPhuong() == null || request.getPhuong().trim().isEmpty()) {
            errors.add("Phường/Xã không được để trống");
        }
        
        Map<String, Object> response = new HashMap<>();
        
        if (errors.isEmpty()) {
            response.put("valid", true);
            response.put("message", "Dữ liệu hợp lệ");
        } else {
            response.put("valid", false);
            response.put("errors", errors);
            response.put("message", "Dữ liệu không hợp lệ");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> validateUpdateEmployee(@RequestBody NhanVienUpdateRequest request) {
        List<String> errors = new ArrayList<>();
        
        // Validate required fields
        if (request.getTenNhanVien() == null || request.getTenNhanVien().trim().isEmpty()) {
            errors.add("Tên nhân viên không được để trống");
        } else if (!NAME_PATTERN.matcher(request.getTenNhanVien().trim()).matches()) {
            errors.add("Tên nhân viên không được chứa số");
        }
        
        if (request.getNgaySinh() == null) {
            errors.add("Ngày sinh không được để trống");
        }
        
        if (request.getSoDienThoai() == null || request.getSoDienThoai().trim().isEmpty()) {
            errors.add("Số điện thoại không được để trống");
        } else if (!PHONE_PATTERN.matcher(request.getSoDienThoai()).matches()) {
            errors.add("Số điện thoại phải gồm đúng 10 chữ số");
        }
        
        if (request.getCccd() == null || request.getCccd().trim().isEmpty()) {
            errors.add("CCCD không được để trống");
        } else if (!CCCD_PATTERN.matcher(request.getCccd()).matches()) {
            errors.add("CCCD phải gồm đúng 12 chữ số");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            errors.add("Email không được để trống");
        } else if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            errors.add("Email không đúng định dạng");
        }
        
        if (request.getDiaChiCuThe() == null || request.getDiaChiCuThe().trim().isEmpty()) {
            errors.add("Địa chỉ cụ thể không được để trống");
        }
        
        if (request.getThanhPho() == null || request.getThanhPho().trim().isEmpty()) {
            errors.add("Tỉnh/Thành phố không được để trống");
        }
        
        if (request.getQuan() == null || request.getQuan().trim().isEmpty()) {
            errors.add("Quận/Huyện không được để trống");
        }
        
        if (request.getPhuong() == null || request.getPhuong().trim().isEmpty()) {
            errors.add("Phường/Xã không được để trống");
        }
        
        Map<String, Object> response = new HashMap<>();
        
        if (errors.isEmpty()) {
            response.put("valid", true);
            response.put("message", "Dữ liệu hợp lệ");
        } else {
            response.put("valid", false);
            response.put("errors", errors);
            response.put("message", "Dữ liệu không hợp lệ");
        }
        
        return ResponseEntity.ok(response);
    }
}
