package com.example.sd_28_phostep_be.controller.Client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "*")
public class VoucherClientController {

    // Constructor để debug component loading
    public VoucherClientController() {
        System.out.println("=== VoucherClientController LOADED ===");
    }

    /**
     * Test endpoint to verify controller is loaded
     */
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "VoucherClientController is working!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Get available vouchers for client
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableVouchers() {
        try {
            // Mock voucher data for now (can be replaced with real service later)
            List<Map<String, Object>> vouchers = new ArrayList<>();
            
            // Sample vouchers
            Map<String, Object> voucher1 = new HashMap<>();
            voucher1.put("id", 1);
            voucher1.put("code", "WELCOME10");
            voucher1.put("name", "Chào mừng khách hàng mới");
            voucher1.put("description", "Giảm 10% cho đơn hàng đầu tiên");
            voucher1.put("discountType", "PERCENTAGE");
            voucher1.put("discountValue", 10);
            voucher1.put("minOrderAmount", 500000);
            voucher1.put("maxDiscountAmount", 100000);
            voucher1.put("isActive", true);
            
            Map<String, Object> voucher2 = new HashMap<>();
            voucher2.put("id", 2);
            voucher2.put("code", "FREESHIP");
            voucher2.put("name", "Miễn phí vận chuyển");
            voucher2.put("description", "Miễn phí ship cho đơn từ 300k");
            voucher2.put("discountType", "SHIPPING");
            voucher2.put("discountValue", 30000);
            voucher2.put("minOrderAmount", 300000);
            voucher2.put("maxDiscountAmount", 30000);
            voucher2.put("isActive", true);
            
            Map<String, Object> voucher3 = new HashMap<>();
            voucher3.put("id", 3);
            voucher3.put("code", "SAVE50K");
            voucher3.put("name", "Giảm 50K");
            voucher3.put("description", "Giảm 50,000đ cho đơn từ 1 triệu");
            voucher3.put("discountType", "FIXED");
            voucher3.put("discountValue", 50000);
            voucher3.put("minOrderAmount", 1000000);
            voucher3.put("maxDiscountAmount", 50000);
            voucher3.put("isActive", true);
            
            vouchers.add(voucher1);
            vouchers.add(voucher2);
            vouchers.add(voucher3);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", vouchers);
            response.put("message", "Lấy danh sách voucher thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting available vouchers: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Không thể lấy danh sách voucher");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Validate voucher code
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateVoucher(@RequestBody Map<String, Object> request) {
        try {
            String code = (String) request.get("code");
            BigDecimal orderAmount = new BigDecimal(request.get("orderAmount").toString());
            
            System.out.println("Validating voucher: " + code + " for order amount: " + orderAmount);
            
            Map<String, Object> response = new HashMap<>();
            
            // Mock validation logic
            if ("WELCOME10".equals(code)) {
                if (orderAmount.compareTo(new BigDecimal("500000")) >= 0) {
                    BigDecimal discount = orderAmount.multiply(new BigDecimal("0.1"));
                    if (discount.compareTo(new BigDecimal("100000")) > 0) {
                        discount = new BigDecimal("100000");
                    }
                    
                    response.put("valid", true);
                    response.put("discountAmount", discount);
                    response.put("message", "Áp dụng voucher thành công");
                } else {
                    response.put("valid", false);
                    response.put("message", "Đơn hàng tối thiểu 500,000đ để sử dụng voucher này");
                }
            } else if ("FREESHIP".equals(code)) {
                if (orderAmount.compareTo(new BigDecimal("300000")) >= 0) {
                    response.put("valid", true);
                    response.put("discountAmount", new BigDecimal("30000"));
                    response.put("message", "Miễn phí vận chuyển");
                } else {
                    response.put("valid", false);
                    response.put("message", "Đơn hàng tối thiểu 300,000đ để miễn phí ship");
                }
            } else if ("SAVE50K".equals(code)) {
                if (orderAmount.compareTo(new BigDecimal("1000000")) >= 0) {
                    response.put("valid", true);
                    response.put("discountAmount", new BigDecimal("50000"));
                    response.put("message", "Giảm 50,000đ thành công");
                } else {
                    response.put("valid", false);
                    response.put("message", "Đơn hàng tối thiểu 1,000,000đ để sử dụng voucher này");
                }
            } else {
                response.put("valid", false);
                response.put("message", "Mã voucher không tồn tại hoặc đã hết hạn");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error validating voucher: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("message", "Không thể xác thực voucher");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
