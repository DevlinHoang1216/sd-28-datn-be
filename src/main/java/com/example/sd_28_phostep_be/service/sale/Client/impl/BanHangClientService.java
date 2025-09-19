package com.example.sd_28_phostep_be.service.sale.Client.impl;

import com.example.sd_28_phostep_be.dto.sell.request.AddToCartRequest;
import com.example.sd_28_phostep_be.dto.sell.request.ClientPaymentRequest;
import com.example.sd_28_phostep_be.dto.sell.request.UpdateCartItemRequest;
import com.example.sd_28_phostep_be.dto.sell.request.PaymentRequest;
import com.example.sd_28_phostep_be.dto.sell.response.CartResponse;
import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDetailResponse;

import java.util.List;

public interface BanHangClientService {
    
    /**
     * Create a pending invoice for customer
     */
    HoaDonDetailResponse taoHoaDonCho(Integer khachHangId);
    
    /**
     * Get pending invoices by customer
     */
    List<HoaDonDetailResponse> getPendingInvoicesByCustomer(Integer khachHangId);
    
    /**
     * Add product to cart (database-based)
     */
    CartResponse themSanPhamVaoGioHang(Integer hoaDonId, AddToCartRequest request);
    
    /**
     * Get cart by invoice ID
     */
    CartResponse layGioHang(Integer hoaDonId);
    
    /**
     * Update cart item quantity
     */
    CartResponse capNhatSoLuongSanPham(Integer hoaDonId, Integer cartItemId, UpdateCartItemRequest request);
    
    /**
     * Remove product from cart
     */
    CartResponse xoaSanPhamKhoiGioHang(Integer hoaDonId, Integer cartItemId);

    HoaDonDetailResponse thanhToan(Integer hoaDonId, ClientPaymentRequest request);

    /**
     * Update customer information in invoice (for pre-saving before VNPay)
     */
    void updateCustomerInfo(Integer hoaDonId, ClientPaymentRequest request);

    /**
     * Clear entire cart
     */
    CartResponse xoaToanBoGioHang(Integer hoaDonId);
    
    /**
     * Process payment and complete order
     */
    HoaDonDetailResponse thanhToan(Integer hoaDonId, PaymentRequest paymentRequest);
    
    /**
     * Get order details after payment
     */
    HoaDonDetailResponse getOrderDetails(Integer hoaDonId);
    
    /**
     * Search order by order code (for guest users)
     */
    HoaDonDetailResponse searchOrderByCode(String orderCode);
}
