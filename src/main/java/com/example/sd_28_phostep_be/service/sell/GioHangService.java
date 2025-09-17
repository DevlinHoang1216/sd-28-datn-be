package com.example.sd_28_phostep_be.service.sell;

import com.example.sd_28_phostep_be.dto.sell.request.AddToCartRequest;
import com.example.sd_28_phostep_be.dto.sell.request.UpdateCartItemRequest;
import com.example.sd_28_phostep_be.dto.sell.response.CartResponse;
import com.example.sd_28_phostep_be.dto.sell.response.CartItemResponse;

public interface GioHangService {
    
    /**
     * Get or create cart for invoice
     */
    CartResponse getOrCreateCartByInvoiceId(Integer hoaDonId);
    
    /**
     * Add product to cart
     */
    CartItemResponse addProductToCart(Integer hoaDonId, AddToCartRequest request);
    
    /**
     * Update cart item quantity
     */
    CartItemResponse updateCartItem(Integer hoaDonId, Integer cartItemId, UpdateCartItemRequest request);
    
    /**
     * Remove item from cart
     */
    void removeCartItem(Integer hoaDonId, Integer cartItemId);
    
    /**
     * Clear all items from cart
     */
    void clearCart(Integer hoaDonId);
    
    /**
     * Get cart by invoice ID
     */
    CartResponse getCartByInvoiceId(Integer hoaDonId);
    
    /**
     * Update cart customer to match invoice customer
     */
    void updateCartCustomer(Integer hoaDonId, Integer customerId);
}
