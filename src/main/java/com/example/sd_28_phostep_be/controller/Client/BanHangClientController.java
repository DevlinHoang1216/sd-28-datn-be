package com.example.sd_28_phostep_be.controller.Client;

import com.example.sd_28_phostep_be.dto.sell.request.AddToCartRequest;
import com.example.sd_28_phostep_be.dto.sell.request.UpdateCartItemRequest;
import com.example.sd_28_phostep_be.dto.sell.request.ClientPaymentRequest;
import com.example.sd_28_phostep_be.dto.sell.response.CartResponse;
import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDetailResponse;
import com.example.sd_28_phostep_be.service.sale.Client.impl.BanHangClientService;
import com.example.sd_28_phostep_be.modal.product.SanPham;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.service.product.SanPhamService;
import com.example.sd_28_phostep_be.service.product.ChiTietSanPhamService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ban-hang-client")
@CrossOrigin(origins = "*")
public class BanHangClientController {

    @Autowired
    private BanHangClientService banHangClientService;
    
    @Autowired
    private SanPhamService sanPhamService;
    
    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;

    /**
     * Load all active products for sales counter
     */
    @GetMapping("/san-pham")
    public ResponseEntity<Page<SanPham>> getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SanPham> activeProducts = sanPhamService.getActiveProductsForSales(pageable, keyword);
        return ResponseEntity.ok(activeProducts);
    }

    /**
     * Load active product details by product ID for sales counter
     */
    @GetMapping("/chi-tiet-san-pham/{productId}")
    public ResponseEntity<List<ChiTietSanPham>> getActiveProductDetails(@PathVariable Integer productId) {
        List<ChiTietSanPham> activeDetails = chiTietSanPhamService.getActiveByProductIdForSales(productId);
        return ResponseEntity.ok(activeDetails);
    }

    /**
     * Load all active product details for sales counter with pagination
     */
    @GetMapping("/chi-tiet-san-pham")
    public ResponseEntity<Page<ChiTietSanPham>> getAllActiveProductDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ChiTietSanPham> activeDetails;

        if (keyword != null && !keyword.trim().isEmpty()) {
            activeDetails = chiTietSanPhamService.getActiveProductDetailsForSalesWithKeyword(pageable, keyword.trim());
        } else {
            activeDetails = chiTietSanPhamService.getActiveProductDetailsForSales(pageable);
        }

        return ResponseEntity.ok(activeDetails);
    }

    /**
     * Create pending invoice for customer
     */
    @PostMapping("/hoa-don-cho")
    public ResponseEntity<?> taoHoaDonCho(@RequestParam(required = false) Integer khachHangId) {
        try {
            HoaDonDetailResponse response = banHangClientService.taoHoaDonCho(khachHangId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Get pending invoices by customer
     */
    @GetMapping("/hoa-don-cho/khach-hang/{khachHangId}")
    public ResponseEntity<?> getPendingInvoicesByCustomer(@PathVariable Integer khachHangId) {
        try {
            List<HoaDonDetailResponse> responses = banHangClientService.getPendingInvoicesByCustomer(khachHangId);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Add product to cart
     */
    @PostMapping("/gio-hang/{hoaDonId}/them-san-pham")
    public ResponseEntity<?> themSanPhamVaoGioHang(
            @PathVariable Integer hoaDonId,
            @RequestBody AddToCartRequest request) {
        try {
            CartResponse response = banHangClientService.themSanPhamVaoGioHang(hoaDonId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Get cart by invoice ID
     */
    @GetMapping("/gio-hang/{hoaDonId}")
    public ResponseEntity<?> layGioHang(@PathVariable Integer hoaDonId) {
        try {
            CartResponse response = banHangClientService.layGioHang(hoaDonId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Update cart item quantity
     */
    @PutMapping("/gio-hang/{hoaDonId}/san-pham/{cartItemId}")
    public ResponseEntity<?> capNhatSoLuongSanPham(
            @PathVariable Integer hoaDonId,
            @PathVariable Integer cartItemId,
            @RequestBody UpdateCartItemRequest request) {
        try {
            CartResponse response = banHangClientService.capNhatSoLuongSanPham(hoaDonId, cartItemId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Remove product from cart
     */
    @DeleteMapping("/gio-hang/{hoaDonId}/san-pham/{cartItemId}")
    public ResponseEntity<?> xoaSanPhamKhoiGioHang(
            @PathVariable Integer hoaDonId,
            @PathVariable Integer cartItemId) {
        try {
            CartResponse response = banHangClientService.xoaSanPhamKhoiGioHang(hoaDonId, cartItemId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Clear entire cart
     */
    @DeleteMapping("/gio-hang/{hoaDonId}/xoa-tat-ca")
    public ResponseEntity<?> xoaToanBoGioHang(@PathVariable Integer hoaDonId) {
        try {
            banHangClientService.xoaToanBoGioHang(hoaDonId);
            return ResponseEntity.ok("Đã xóa toàn bộ giỏ hàng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Process payment and complete order
     */
    @PostMapping("/thanh-toan/{hoaDonId}")
    public ResponseEntity<?> thanhToan(
            @PathVariable Integer hoaDonId,
            @RequestBody ClientPaymentRequest request) {
        try {
            HoaDonDetailResponse result = banHangClientService.thanhToan(hoaDonId, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get order details after payment
     */
    @GetMapping("/don-hang/{hoaDonId}")
    public ResponseEntity<?> getDonHang(@PathVariable Integer hoaDonId) {
        try {
            HoaDonDetailResponse result = banHangClientService.getOrderDetails(hoaDonId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Search order by order code (for guest users)
     */
    @GetMapping("/tra-cuu-don-hang")
    public ResponseEntity<?> searchOrderByCode(@RequestParam String ma) {
        try {
            HoaDonDetailResponse result = banHangClientService.searchOrderByCode(ma);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
