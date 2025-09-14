package com.example.sd_28_phostep_be.controller.sales;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ban-hang")
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
        Page<ChiTietSanPham> activeDetails = chiTietSanPhamService.getActiveProductDetailsForSales(pageable, keyword);
        return ResponseEntity.ok(activeDetails);
    }
}
