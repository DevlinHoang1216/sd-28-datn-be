package com.example.sd_28_phostep_be.controller.Client;

import com.example.sd_28_phostep_be.dto.account.request.KhachHang.KhachHangQuickCreateRequest;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDTOResponse;
import com.example.sd_28_phostep_be.dto.bill.request.UpdateCustomerRequest;
import com.example.sd_28_phostep_be.dto.sell.request.PaymentRequest;
import com.example.sd_28_phostep_be.dto.sell.request.client.AddToCartClientRequest;
import com.example.sd_28_phostep_be.dto.sell.response.PaymentResponse;
import com.example.sd_28_phostep_be.dto.sell.response.client.CartClientResponse;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.modal.product.SanPham;
import com.example.sd_28_phostep_be.service.account.Client.impl.KhachHang.KhachHangServices;
import com.example.sd_28_phostep_be.service.bill.HoaDonService;
import com.example.sd_28_phostep_be.service.product.ChiTietSanPhamService;
import com.example.sd_28_phostep_be.service.product.SanPhamService;
import com.example.sd_28_phostep_be.service.sell.GioHangClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ban-hang-client")
@CrossOrigin(origins = "*")
public class BanHangClientController {

    private final SanPhamService sanPhamService;
    private final ChiTietSanPhamService chiTietSanPhamService;
    private final KhachHangServices khachHangServices;
    private final HoaDonService hoaDonService;
    private final GioHangClientService gioHangClientService;

    @Autowired
    public BanHangClientController(SanPhamService sanPhamService, ChiTietSanPhamService chiTietSanPhamService,
                             KhachHangServices khachHangServices, HoaDonService hoaDonService,
                             GioHangClientService gioHangClientService) {
        this.sanPhamService = sanPhamService;
        this.chiTietSanPhamService = chiTietSanPhamService;
        this.khachHangServices = khachHangServices;
        this.hoaDonService = hoaDonService;
        this.gioHangClientService = gioHangClientService;
    }

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
     * Get active customers for sales counter with phone numbers from account table
     */
    @GetMapping("/khach-hang")
    public ResponseEntity<Page<KhachHangDTOResponse>> getActiveCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String keyword) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<KhachHangDTOResponse> activeCustomers = khachHangServices.getActiveCustomersForSales(pageable, keyword.trim());
        return ResponseEntity.ok(activeCustomers);
    }

    /**
     * Quick create customer with only name and phone number for sales counter
     */
    @PostMapping("/khach-hang/quick-create")
    public ResponseEntity<KhachHang> quickCreateCustomer(@RequestBody KhachHangQuickCreateRequest request) {
        KhachHang newCustomer = khachHangServices.quickCreateCustomer(request);
        return ResponseEntity.ok(newCustomer);
    }

    /**
     * Get all pending invoices for sales counter (status = 1)
     */
    @GetMapping("/hoa-don-cho")
    public ResponseEntity<List<HoaDon>> getPendingInvoices() {
        List<HoaDon> pendingInvoices = hoaDonService.getPendingInvoicesForSales();
        return ResponseEntity.ok(pendingInvoices);
    }

    /**
     * Create new pending invoice for sales counter
     */
    @PostMapping("/hoa-don-cho")
    public ResponseEntity<HoaDon> createPendingInvoice() {
        HoaDon newInvoice = hoaDonService.createPendingInvoice();
        return ResponseEntity.ok(newInvoice);
    }

    /**
     * Update pending invoice customer information
     */
    @PutMapping("/hoa-don-cho/{id}/khach-hang")
    public ResponseEntity<HoaDon> updatePendingInvoiceCustomer(
            @PathVariable Integer id,
            @RequestBody UpdateCustomerRequest request) {
        HoaDon updatedInvoice = hoaDonService.updatePendingInvoiceCustomer(id, request);
        return ResponseEntity.ok(updatedInvoice);
    }

    /**
     * Delete pending invoice (soft delete)
     */
    @DeleteMapping("/hoa-don-cho/{id}")
    public ResponseEntity<Void> deletePendingInvoice(@PathVariable Integer id) {
        hoaDonService.deletePendingInvoice(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Process payment for cash transactions
     */
    @PostMapping("/thanh-toan")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = hoaDonService.processPayment(paymentRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ================ GIỎ HÀNG CLIENT ENDPOINTS ================
    
    /**
     * Lấy giỏ hàng của khách hàng (đăng nhập hoặc khách lẻ)
     */
    @GetMapping("/gio-hang")
    public ResponseEntity<CartClientResponse> getCart(
            @RequestParam(required = false) Integer idKhachHang,
            @RequestParam(required = false) String sessionId) {
        try {
            CartClientResponse cart = gioHangClientService.getOrCreateCart(idKhachHang, sessionId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @PostMapping("/gio-hang/them-san-pham")
    public ResponseEntity<CartClientResponse> addToCart(@RequestBody AddToCartClientRequest request) {
        try {
            CartClientResponse cart = gioHangClientService.addToCart(request);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            // Trả về lỗi với message cụ thể (vd: không đủ hàng)
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    @PutMapping("/gio-hang/cap-nhat/{cartItemId}")
    public ResponseEntity<CartClientResponse> updateCartItem(
            @PathVariable Integer cartItemId,
            @RequestParam Integer soLuong,
            @RequestParam(required = false) Integer idKhachHang,
            @RequestParam(required = false) String sessionId) {
        try {
            CartClientResponse cart = gioHangClientService.updateCartItem(cartItemId, soLuong, idKhachHang, sessionId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/gio-hang/xoa/{cartItemId}")
    public ResponseEntity<CartClientResponse> removeFromCart(
            @PathVariable Integer cartItemId,
            @RequestParam(required = false) Integer idKhachHang,
            @RequestParam(required = false) String sessionId) {
        try {
            CartClientResponse cart = gioHangClientService.removeCartItem(cartItemId, idKhachHang, sessionId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Xóa toàn bộ giỏ hàng
     */
    @DeleteMapping("/gio-hang/xoa-tat-ca")
    public ResponseEntity<Void> clearCart(
            @RequestParam(required = false) Integer idKhachHang,
            @RequestParam(required = false) String sessionId) {
        try {
            gioHangClientService.clearCart(idKhachHang, sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
