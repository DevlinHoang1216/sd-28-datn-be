package com.example.sd_28_phostep_be.controller.sales;

import com.example.sd_28_phostep_be.dto.account.request.KhachHang.KhachHangQuickCreateRequest;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDTOResponse;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.modal.product.SanPham;
import com.example.sd_28_phostep_be.service.account.Client.impl.KhachHang.KhachHangServices;
import com.example.sd_28_phostep_be.service.product.ChiTietSanPhamService;
import com.example.sd_28_phostep_be.service.product.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ban-hang")
@CrossOrigin(origins = "*")
public class BanHangController {

    private final SanPhamService sanPhamService;
    private final ChiTietSanPhamService chiTietSanPhamService;
    private final KhachHangServices khachHangServices;

    @Autowired
    public BanHangController(SanPhamService sanPhamService, ChiTietSanPhamService chiTietSanPhamService, KhachHangServices khachHangServices) {
        this.sanPhamService = sanPhamService;
        this.chiTietSanPhamService = chiTietSanPhamService;
        this.khachHangServices = khachHangServices;
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
}
