package com.example.sd_28_phostep_be.service.sell;

import com.example.sd_28_phostep_be.dto.sell.request.client.AddToCartClientRequest;
import com.example.sd_28_phostep_be.dto.sell.response.client.CartClientResponse;

public interface GioHangClientService {
    
    /**
     * Lấy hoặc tạo giỏ hàng cho khách hàng
     * @param idKhachHang ID khách hàng (null nếu là khách lẻ)
     * @param sessionId Session ID cho khách chưa đăng nhập
     * @return Thông tin giỏ hàng
     */
    CartClientResponse getOrCreateCart(Integer idKhachHang, String sessionId);
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     * @param request Thông tin sản phẩm cần thêm
     * @return Thông tin giỏ hàng sau khi thêm
     */
    CartClientResponse addToCart(AddToCartClientRequest request);
    
    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     * @param cartItemId ID item trong giỏ
     * @param soLuong Số lượng mới
     * @param idKhachHang ID khách hàng (null nếu là khách lẻ)
     * @param sessionId Session ID cho khách chưa đăng nhập
     * @return Thông tin giỏ hàng sau khi cập nhật
     */
    CartClientResponse updateCartItem(Integer cartItemId, Integer soLuong, Integer idKhachHang, String sessionId);
    
    /**
     * Xóa sản phẩm khỏi giỏ hàng
     * @param cartItemId ID item trong giỏ
     * @param idKhachHang ID khách hàng (null nếu là khách lẻ)
     * @param sessionId Session ID cho khách chưa đăng nhập
     * @return Thông tin giỏ hàng sau khi xóa
     */
    CartClientResponse removeCartItem(Integer cartItemId, Integer idKhachHang, String sessionId);
    
    /**
     * Xóa toàn bộ giỏ hàng
     * @param idKhachHang ID khách hàng (null nếu là khách lẻ)
     * @param sessionId Session ID cho khách chưa đăng nhập
     */
    void clearCart(Integer idKhachHang, String sessionId);
}
