package com.example.sd_28_phostep_be.controller.bill;

import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDTOResponse;
import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDetailResponse;
import com.example.sd_28_phostep_be.service.bill.HoaDonService;
import com.example.sd_28_phostep_be.service.bill.InHoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/hoa-don")
public class HoaDonController {
    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private InHoaDonService inHoaDonService;

    @GetMapping("/home")
    public ResponseEntity<?> getAllHoaDon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,  // Default size nhỏ hơn để phân trang thực
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long minAmount,
            @RequestParam(required = false) Long maxAmount,
            @RequestParam(required = false) Long startDate,  // Changed to Long to receive timestamp
            @RequestParam(required = false) Long endDate,    // Changed to Long to receive timestamp
            @RequestParam(required = false) Short trangThai,
            @RequestParam(required = false) String loaiDon,
            @RequestParam(defaultValue = "id") String sortBy,  // Mới: Field sắp xếp
            @RequestParam(defaultValue = "DESC") String sortDir) {  // Mới: Hướng sắp xếp
        if (loaiDon != null) {
            // Normalize the input to match database values
            if (loaiDon.equalsIgnoreCase("Tại quầy") || loaiDon.equalsIgnoreCase("tai quay")) {
                loaiDon = "Tại quầy";
            } else if (loaiDon.equalsIgnoreCase("Online")) {
                loaiDon = "Online";
            } else {
                loaiDon = null;
            }
        }
        if (minAmount != null && maxAmount != null && minAmount > maxAmount) {
            return ResponseEntity.badRequest().body("minAmount phải nhỏ hơn hoặc bằng maxAmount");
        }

        // Convert Long timestamps to Timestamp objects
        Timestamp startTimestamp = startDate != null ? new Timestamp(startDate) : null;
        Timestamp endTimestamp = endDate != null ? new Timestamp(endDate) : null;

        if (startTimestamp != null && endTimestamp != null && startTimestamp.after(endTimestamp)) {
            return ResponseEntity.badRequest().body("startDate phải trước hoặc bằng endDate");
        }
        // Validate and map sortBy field to actual entity fields
        String validSortBy = mapSortField(sortBy);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), validSortBy);  // Xây sort động
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HoaDonDTOResponse> response = hoaDonService.getHoaDonAndFilters(keyword, minAmount, maxAmount, startTimestamp, endTimestamp, trangThai, loaiDon, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<HoaDonDetailResponse> getHoaDonDetail(@PathVariable Integer id) {
        // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
        HoaDonDetailResponse response = hoaDonService.getHoaDonDetail(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status-counts")
    public ResponseEntity<Map<String, Long>> getStatusCounts() {
        try {
            Map<String, Long> statusCounts = hoaDonService.getStatusCounts();
            return ResponseEntity.ok(statusCounts);
        } catch (Exception e) {
            Map<String, Long> error = new HashMap<>();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/price-range")
    public ResponseEntity<Map<String, Long>> getPriceRange() {
        try {
            Map<String, Long> priceRange = hoaDonService.getPriceRange();
            return ResponseEntity.ok(priceRange);
        } catch (Exception e) {
            Map<String, Long> error = new HashMap<>();
            error.put("minPrice", 0L);
            error.put("maxPrice", 0L);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<HoaDonDTOResponse> updateHoaDonStatus(
            @PathVariable Integer id,
            @RequestParam Short trangThai,
            @RequestParam(required = false) Integer idNhanVien,
            @RequestParam(required = false) String ghiChu) {
        try {
            // Gọi service với ghi chú để lưu vào lịch sử
            HoaDonDTOResponse response = hoaDonService.updateHoaDonStatusWithNote(id, trangThai, idNhanVien, ghiChu);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/QR-by-ma/{ma}")
    public ResponseEntity<HoaDonDTOResponse> getHoaDonByMa(@PathVariable("ma") String maHD) {
        try {
            // Gọi service, thông báo WebSocket đã được tích hợp trong HoaDonServiceImpl
            return ResponseEntity.ok(hoaDonService.getHoaDonByMa(maHD));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> inHoaDon(@PathVariable Integer id) throws Exception {
        HoaDonDetailResponse hoaDon = hoaDonService.getHoaDonDetail(id);
        byte[] pdfBytes = inHoaDonService.generateHoaDonPdf(hoaDon);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "hoa_don_" + hoaDon.getMaHoaDon() + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
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
            case "tongtiensaugiam":
            case "tong_tien_sau_giam":
            case "tongtienthanhtoan": // Map invalid field to correct one
            case "tong_tien_thanh_toan": // Map invalid field to correct one
                return "tongTienSauGiam";
            case "tongtien":
            case "tong_tien":
                return "tongTien";
            case "tiensanpham":
            case "tien_san_pham":
                return "tienSanPham";
            case "ngaytao":
            case "ngay_tao":
                return "ngayTao";
            case "ngaythanhtoan":
            case "ngay_thanh_toan":
                return "ngayThanhToan";
            case "trangthai":
            case "trang_thai":
                return "trangThai";
            case "ma":
                return "ma";
            case "tenkhachhang":
            case "ten_khach_hang":
                return "tenKhachHang";
            case "sodienthoaikhachhang":
            case "so_dien_thoai_khach_hang":
                return "soDienThoaiKhachHang";
            case "loaidon":
            case "loai_don":
                return "loaiDon";
            case "phivanchuyen":
            case "phi_van_chuyen":
                return "phiVanChuyen";
            case "createdat":
            case "created_at":
                return "createdAt";
            case "updatedat":
            case "updated_at":
                return "updatedAt";
            case "id":
                return "id";
            default:
                // If field name is not recognized, default to id to prevent errors
                return "id";
        }
    }

}
