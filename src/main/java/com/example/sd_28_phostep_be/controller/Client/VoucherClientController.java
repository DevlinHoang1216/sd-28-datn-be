package com.example.sd_28_phostep_be.controller.Client;

import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import com.example.sd_28_phostep_be.service.sale.impl.PhieuGiamGia.PhieuGiamGiaServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "*")
public class VoucherClientController {

    @Autowired
    private PhieuGiamGiaServices phieuGiamGiaService;

    /**
     * Smart endpoint that auto-detects authentication and returns appropriate vouchers
     */
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableVouchers(
            @RequestParam BigDecimal orderAmount,
            @RequestParam(required = false) Integer customerId) {
        try {
            System.out.println("=== GET AVAILABLE VOUCHERS (SMART) ===");
            System.out.println("Order amount: " + orderAmount);
            System.out.println("Customer ID from param: " + customerId);
            
            // If customerId is provided, get personal vouchers
            if (customerId != null) {
                System.out.println("Getting personal vouchers for customer: " + customerId);
                return getPersonalVouchers(customerId, orderAmount);
            } else {
                System.out.println("Getting public vouchers only");
                return getPublicVouchers(orderAmount);
            }
            
        } catch (Exception e) {
            System.err.println("Error getting available vouchers: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("vouchers", new ArrayList<>());
            errorResponse.put("bestVoucher", null);
            errorResponse.put("message", "Không thể tải danh sách voucher");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Test endpoint to check if service is working
     */
    @GetMapping("/test")
    public ResponseEntity<?> testVoucherService() {
        try {
            System.out.println("=== TESTING VOUCHER SERVICE ===");
            
            // Test public vouchers
            List<PhieuGiamGia> publicVouchers = phieuGiamGiaService.getActivePublicVouchers();
            System.out.println("Found " + publicVouchers.size() + " public vouchers");
            
            // Test personal vouchers (with sample customer ID)
            List<PhieuGiamGia> personalVouchers = phieuGiamGiaService.getActiveVouchersForCustomer(1);
            System.out.println("Found " + personalVouchers.size() + " vouchers for customer 1");
            
            Map<String, Object> response = new HashMap<>();
            response.put("publicVouchersCount", publicVouchers.size());
            response.put("personalVouchersCount", personalVouchers.size());
            response.put("message", "Voucher service is working");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error testing voucher service: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("message", "Voucher service test failed");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get available vouchers for guest users (public vouchers only)
     */
    @GetMapping("/public")
    public ResponseEntity<?> getPublicVouchers(@RequestParam BigDecimal orderAmount) {
        try {
            System.out.println("=== GET PUBLIC VOUCHERS ===");
            System.out.println("Order amount: " + orderAmount);
            
            // Debug: Get ALL vouchers first to see what's in database
            List<PhieuGiamGia> allVouchers = phieuGiamGiaService.getall();
            System.out.println("Total vouchers in database: " + allVouchers.size());
            
            for (PhieuGiamGia voucher : allVouchers) {
                System.out.println("Voucher: " + voucher.getMa() + 
                    ", Status: " + voucher.getTrangThai() + 
                    ", Deleted: " + voucher.getDeleted() +
                    ", RiengTu: " + voucher.getRiengTu() +
                    ", Start: " + voucher.getNgayBatDau() +
                    ", End: " + voucher.getNgayKetThuc() +
                    ", MinOrder: " + voucher.getHoaDonToiThieu());
            }
            
            // Get active public vouchers from service
            List<PhieuGiamGia> publicVouchers = phieuGiamGiaService.getActivePublicVouchers();
            System.out.println("Active public vouchers found: " + publicVouchers.size());
            
            // Filter vouchers applicable to order amount and calculate discount
            List<Map<String, Object>> applicableVouchers = publicVouchers.stream()
                .filter(voucher -> {
                    BigDecimal minAmount = voucher.getHoaDonToiThieu() != null ? 
                        BigDecimal.valueOf(voucher.getHoaDonToiThieu()) : BigDecimal.ZERO;
                    return orderAmount.compareTo(minAmount) >= 0;
                })
                .map(voucher -> formatVoucherResponse(voucher, orderAmount, false))
                .sorted((v1, v2) -> {
                    // Sort by discount amount descending (best voucher first)
                    BigDecimal discount1 = (BigDecimal) v1.get("discountAmount");
                    BigDecimal discount2 = (BigDecimal) v2.get("discountAmount");
                    return discount2.compareTo(discount1);
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("vouchers", applicableVouchers);
            response.put("bestVoucher", applicableVouchers.isEmpty() ? null : applicableVouchers.get(0));
            response.put("message", "Danh sách voucher khả dụng");
            
            System.out.println("Found " + applicableVouchers.size() + " applicable public vouchers");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error getting public vouchers: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("vouchers", new ArrayList<>());
            errorResponse.put("bestVoucher", null);
            errorResponse.put("message", "Không thể tải danh sách voucher");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get personal vouchers for authenticated users (personal + public vouchers)
     */
    @GetMapping("/personal/{customerId}")
    public ResponseEntity<?> getPersonalVouchers(@PathVariable Integer customerId, @RequestParam BigDecimal orderAmount) {
        try {
            System.out.println("=== GET PERSONAL VOUCHERS ===");
            System.out.println("Customer ID: " + customerId + ", Order amount: " + orderAmount);
            
            // Get personal vouchers for customer
            List<PhieuGiamGia> personalVouchers = phieuGiamGiaService.getActiveVouchersForCustomer(customerId);
            
            // Filter and format personal vouchers
            List<Map<String, Object>> applicablePersonalVouchers = personalVouchers.stream()
                .filter(voucher -> {
                    BigDecimal minAmount = voucher.getHoaDonToiThieu() != null ? 
                        BigDecimal.valueOf(voucher.getHoaDonToiThieu()) : BigDecimal.ZERO;
                    return orderAmount.compareTo(minAmount) >= 0;
                })
                .map(voucher -> formatVoucherResponse(voucher, orderAmount, true))
                .sorted((v1, v2) -> {
                    BigDecimal discount1 = (BigDecimal) v1.get("discountAmount");
                    BigDecimal discount2 = (BigDecimal) v2.get("discountAmount");
                    return discount2.compareTo(discount1);
                })
                .collect(Collectors.toList());
            
            // Also get public vouchers as fallback
            List<PhieuGiamGia> publicVouchers = phieuGiamGiaService.getActivePublicVouchers();
            List<Map<String, Object>> applicablePublicVouchers = publicVouchers.stream()
                .filter(voucher -> {
                    BigDecimal minAmount = voucher.getHoaDonToiThieu() != null ? 
                        BigDecimal.valueOf(voucher.getHoaDonToiThieu()) : BigDecimal.ZERO;
                    return orderAmount.compareTo(minAmount) >= 0;
                })
                .map(voucher -> formatVoucherResponse(voucher, orderAmount, false))
                .collect(Collectors.toList());
            
            // Combine personal and public vouchers, prioritize personal
            List<Map<String, Object>> allVouchers = new ArrayList<>();
            allVouchers.addAll(applicablePersonalVouchers);
            allVouchers.addAll(applicablePublicVouchers);
            
            // Sort all vouchers by discount amount (best first)
            allVouchers.sort((v1, v2) -> {
                BigDecimal discount1 = (BigDecimal) v1.get("discountAmount");
                BigDecimal discount2 = (BigDecimal) v2.get("discountAmount");
                return discount2.compareTo(discount1);
            });
            
            Map<String, Object> response = new HashMap<>();
            response.put("personalVouchers", applicablePersonalVouchers);
            response.put("publicVouchers", applicablePublicVouchers);
            response.put("allVouchers", allVouchers);
            response.put("bestVoucher", allVouchers.isEmpty() ? null : allVouchers.get(0));
            response.put("message", "Danh sách voucher cá nhân và công khai");
            
            System.out.println("Found " + applicablePersonalVouchers.size() + " personal vouchers, " + 
                             applicablePublicVouchers.size() + " public vouchers");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error getting personal vouchers: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("personalVouchers", new ArrayList<>());
            errorResponse.put("publicVouchers", new ArrayList<>());
            errorResponse.put("allVouchers", new ArrayList<>());
            errorResponse.put("bestVoucher", null);
            errorResponse.put("message", "Không thể tải danh sách voucher");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Validate voucher by code
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateVoucher(
            @RequestParam String code, 
            @RequestParam BigDecimal orderAmount, 
            @RequestParam(required = false) Integer customerId) {
        try {
            System.out.println("=== VALIDATE VOUCHER ===");
            System.out.println("Code: " + code + ", Order amount: " + orderAmount + ", Customer ID: " + customerId);
            
            Map<String, Object> response = new HashMap<>();
            
            // First check personal vouchers if customer is logged in
            if (customerId != null) {
                List<PhieuGiamGia> personalVouchers = phieuGiamGiaService.getActiveVouchersForCustomer(customerId);
                for (PhieuGiamGia voucher : personalVouchers) {
                    if (code.equalsIgnoreCase(voucher.getMa())) {
                        return validateAndFormatVoucher(voucher, orderAmount, true);
                    }
                }
            }
            
            // Check public vouchers
            List<PhieuGiamGia> publicVouchers = phieuGiamGiaService.getActivePublicVouchers();
            for (PhieuGiamGia voucher : publicVouchers) {
                if (code.equalsIgnoreCase(voucher.getMa())) {
                    return validateAndFormatVoucher(voucher, orderAmount, false);
                }
            }
            
            // Voucher not found
            response.put("valid", false);
            response.put("message", "Mã voucher không tồn tại hoặc đã hết hạn");
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

    /**
     * Apply voucher to invoice (for checkout process)
     */
    @PostMapping("/apply/{voucherId}/invoice/{invoiceId}")
    public ResponseEntity<?> applyVoucherToInvoice(
            @PathVariable Integer voucherId,
            @PathVariable Integer invoiceId) {
        try {
            System.out.println("=== APPLY VOUCHER TO INVOICE ===");
            System.out.println("Voucher ID: " + voucherId + ", Invoice ID: " + invoiceId);
            
            phieuGiamGiaService.applyVoucherToInvoice(invoiceId, voucherId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Áp dụng phiếu giảm giá thành công!");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("Error applying voucher: " + e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Remove voucher from invoice
     */
    @DeleteMapping("/remove/invoice/{invoiceId}")
    public ResponseEntity<?> removeVoucherFromInvoice(@PathVariable Integer invoiceId) {
        try {
            System.out.println("=== REMOVE VOUCHER FROM INVOICE ===");
            System.out.println("Invoice ID: " + invoiceId);
            
            phieuGiamGiaService.removeVoucherFromInvoice(invoiceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa phiếu giảm giá thành công!");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("Error removing voucher: " + e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Format voucher data for response
     */
    private Map<String, Object> formatVoucherResponse(PhieuGiamGia voucher, BigDecimal orderAmount, boolean isPersonal) {
        Map<String, Object> voucherInfo = new HashMap<>();
        voucherInfo.put("id", voucher.getId());
        voucherInfo.put("ma", voucher.getMa());
        voucherInfo.put("tenPhieuGiamGia", voucher.getTenPhieuGiamGia());
        voucherInfo.put("loaiPhieuGiamGia", voucher.getLoaiPhieuGiamGia());
        voucherInfo.put("phanTramGiamGia", voucher.getPhanTramGiamGia());
        voucherInfo.put("soTienGiamToiDa", voucher.getSoTienGiamToiDa());
        voucherInfo.put("hoaDonToiThieu", voucher.getHoaDonToiThieu());
        voucherInfo.put("ngayBatDau", voucher.getNgayBatDau());
        voucherInfo.put("ngayKetThuc", voucher.getNgayKetThuc());
        voucherInfo.put("isPersonal", isPersonal);
        
        // Calculate discount amount
        BigDecimal discountAmount = calculateDiscountAmount(voucher, orderAmount);
        voucherInfo.put("discountAmount", discountAmount);
        
        return voucherInfo;
    }

    /**
     * Validate voucher and format response
     */
    private ResponseEntity<?> validateAndFormatVoucher(PhieuGiamGia voucher, BigDecimal orderAmount, boolean isPersonal) {
        Map<String, Object> response = new HashMap<>();
        
        BigDecimal minAmount = voucher.getHoaDonToiThieu() != null ? 
            BigDecimal.valueOf(voucher.getHoaDonToiThieu()) : BigDecimal.ZERO;
        
        if (orderAmount.compareTo(minAmount) >= 0) {
            BigDecimal discountAmount = calculateDiscountAmount(voucher, orderAmount);
            response.put("valid", true);
            response.put("voucherId", voucher.getId());
            response.put("discountAmount", discountAmount);
            response.put("message", isPersonal ? "Áp dụng voucher cá nhân thành công" : "Áp dụng voucher thành công");
            response.put("isPersonal", isPersonal);
            response.put("voucher", formatVoucherResponse(voucher, orderAmount, isPersonal));
            return ResponseEntity.ok(response);
        } else {
            response.put("valid", false);
            response.put("message", "Đơn hàng tối thiểu " + formatCurrency(minAmount) + " để sử dụng voucher này");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Calculate discount amount for a voucher
     */
    private BigDecimal calculateDiscountAmount(PhieuGiamGia voucher, BigDecimal orderAmount) {
        if ("PERCENTAGE".equals(voucher.getLoaiPhieuGiamGia())) {
            // Percentage discount
            BigDecimal discountAmount = orderAmount.multiply(BigDecimal.valueOf(voucher.getPhanTramGiamGia() / 100.0));
            
            // Apply maximum discount limit
            if (voucher.getSoTienGiamToiDa() != null) {
                BigDecimal maxDiscount = BigDecimal.valueOf(voucher.getSoTienGiamToiDa());
                return discountAmount.min(maxDiscount);
            }
            
            return discountAmount;
        } else {
            // Fixed amount discount
            return voucher.getSoTienGiamToiDa() != null ? 
                BigDecimal.valueOf(voucher.getSoTienGiamToiDa()) : BigDecimal.ZERO;
        }
    }

    /**
     * Format currency for display
     */
    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.0fđ", amount);
    }
}
