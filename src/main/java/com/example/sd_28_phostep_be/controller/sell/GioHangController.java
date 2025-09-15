package com.example.sd_28_phostep_be.controller.sell;

import com.example.sd_28_phostep_be.dto.sell.request.AddToCartRequest;
import com.example.sd_28_phostep_be.dto.sell.request.UpdateCartItemRequest;
import com.example.sd_28_phostep_be.dto.sell.response.CartResponse;
import com.example.sd_28_phostep_be.dto.sell.response.CartItemResponse;
import com.example.sd_28_phostep_be.service.sell.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/gio-hang")
@CrossOrigin(origins = "*")
public class GioHangController {

    @Autowired
    private GioHangService gioHangService;

    /**
     * Get cart by invoice ID
     */
    @GetMapping("/hoa-don/{hoaDonId}")
    public ResponseEntity<CartResponse> getCartByInvoiceId(@PathVariable Integer hoaDonId) {
        try {
            CartResponse cart = gioHangService.getOrCreateCartByInvoiceId(hoaDonId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add product to cart
     */
    @PostMapping("/hoa-don/{hoaDonId}/them-san-pham")
    public ResponseEntity<CartItemResponse> addProductToCart(
            @PathVariable Integer hoaDonId,
            @Valid @RequestBody AddToCartRequest request) {
        try {
            CartItemResponse cartItem = gioHangService.addProductToCart(hoaDonId, request);
            return ResponseEntity.ok(cartItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update cart item quantity
     */
    @PutMapping("/hoa-don/{hoaDonId}/cap-nhat/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Integer hoaDonId,
            @PathVariable Integer cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        try {
            CartItemResponse cartItem = gioHangService.updateCartItem(hoaDonId, cartItemId, request);
            return ResponseEntity.ok(cartItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/hoa-don/{hoaDonId}/xoa/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Integer hoaDonId,
            @PathVariable Integer cartItemId) {
        try {
            gioHangService.removeCartItem(hoaDonId, cartItemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Clear all items from cart
     */
    @DeleteMapping("/hoa-don/{hoaDonId}/xoa-tat-ca")
    public ResponseEntity<Void> clearCart(@PathVariable Integer hoaDonId) {
        try {
            gioHangService.clearCart(hoaDonId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
