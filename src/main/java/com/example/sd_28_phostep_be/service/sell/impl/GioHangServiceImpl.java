package com.example.sd_28_phostep_be.service.sell.impl;

import com.example.sd_28_phostep_be.dto.sell.request.AddToCartRequest;
import com.example.sd_28_phostep_be.dto.sell.request.UpdateCartItemRequest;
import com.example.sd_28_phostep_be.dto.sell.response.CartResponse;
import com.example.sd_28_phostep_be.dto.sell.response.CartItemResponse;
import com.example.sd_28_phostep_be.exception.ResourceNotFoundException;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.modal.sell.GioHang;
import com.example.sd_28_phostep_be.modal.sell.GioHangChiTiet;
import com.example.sd_28_phostep_be.repository.bill.HoaDonRepository;
import com.example.sd_28_phostep_be.repository.product.ChiTietSanPhamRepository;
import com.example.sd_28_phostep_be.repository.sell.GioHangRepository;
import com.example.sd_28_phostep_be.repository.sell.GioHangChiTietRepository;
import com.example.sd_28_phostep_be.service.sell.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GioHangServiceImpl implements GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Override
    public CartResponse getOrCreateCartByInvoiceId(Integer hoaDonId) {
        // Find existing cart or create new one
        Optional<GioHang> existingCart = gioHangRepository.findByHoaDonId(hoaDonId);
        
        if (existingCart.isPresent()) {
            return mapToCartResponse(existingCart.get());
        }
        
        // Create new cart
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + hoaDonId));
        
        GioHang newCart = GioHang.builder()
            .idHoaDon(hoaDon)
            .idKhachHang(hoaDon.getIdKhachHang())
            .tongTien(BigDecimal.ZERO)
            .build();
        
        GioHang savedCart = gioHangRepository.save(newCart);
        return mapToCartResponse(savedCart);
    }

    @Override
    public CartItemResponse addProductToCart(Integer hoaDonId, AddToCartRequest request) {
        // Get or create cart
        GioHang cart = gioHangRepository.findByHoaDonId(hoaDonId)
            .orElseGet(() -> {
                HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + hoaDonId));
                
                GioHang newCart = GioHang.builder()
                    .idHoaDon(hoaDon)
                    .idKhachHang(hoaDon.getIdKhachHang())
                    .tongTien(BigDecimal.ZERO)
                    .build();
                
                return gioHangRepository.save(newCart);
            });

        // Get product details
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(request.getIdChiTietSanPham())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết sản phẩm với ID: " + request.getIdChiTietSanPham()));

        // Check stock availability
        if (chiTietSanPham.getSoLuongTonKho() < request.getSoLuong()) {
            throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + chiTietSanPham.getSoLuongTonKho());
        }

        // Check if product already exists in cart
        Optional<GioHangChiTiet> existingItem = gioHangChiTietRepository
            .findByGioHangIdAndChiTietSpId(cart.getId(), request.getIdChiTietSanPham());

        GioHangChiTiet cartItem;
        if (existingItem.isPresent()) {
            // Update existing item
            cartItem = existingItem.get();
            int newQuantity = cartItem.getSoLuong() + request.getSoLuong();
            
            // Check total quantity against stock
            if (chiTietSanPham.getSoLuongTonKho() < newQuantity) {
                throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + chiTietSanPham.getSoLuongTonKho());
            }
            
            cartItem.setSoLuong(newQuantity);
            cartItem.setGia(request.getGia());
        } else {
            // Create new cart item
            cartItem = GioHangChiTiet.builder()
                .idGioHang(cart)
                .idChiTietSp(chiTietSanPham)
                .soLuong(request.getSoLuong())
                .gia(request.getGia())
                .build();
        }

        // Reduce stock quantity
        chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() - request.getSoLuong());
        chiTietSanPhamRepository.save(chiTietSanPham);

        GioHangChiTiet savedItem = gioHangChiTietRepository.save(cartItem);
        
        // Update cart total
        updateCartTotal(cart.getId());
        
        return mapToCartItemResponse(savedItem);
    }

    @Override
    public CartItemResponse updateCartItem(Integer hoaDonId, Integer cartItemId, UpdateCartItemRequest request) {
        // Verify cart exists for this invoice
        GioHang cart = gioHangRepository.findByHoaDonId(hoaDonId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho hóa đơn ID: " + hoaDonId));

        // Get cart item
        GioHangChiTiet cartItem = gioHangChiTietRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng với ID: " + cartItemId));

        // Verify item belongs to this cart
        if (!cartItem.getIdGioHang().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Sản phẩm không thuộc giỏ hàng này");
        }

        // Get product details
        ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
        int currentQuantity = cartItem.getSoLuong();
        int newQuantity = request.getSoLuong();
        int quantityDifference = newQuantity - currentQuantity;

        // Check stock availability if increasing quantity
        if (quantityDifference > 0) {
            if (chiTietSanPham.getSoLuongTonKho() < quantityDifference) {
                throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + chiTietSanPham.getSoLuongTonKho());
            }
            // Reduce stock
            chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() - quantityDifference);
        } else if (quantityDifference < 0) {
            // Restore stock when reducing quantity
            chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() - quantityDifference); // quantityDifference is negative
        }

        // Save updated stock
        if (quantityDifference != 0) {
            chiTietSanPhamRepository.save(chiTietSanPham);
        }

        // Update quantity
        cartItem.setSoLuong(newQuantity);
        GioHangChiTiet savedItem = gioHangChiTietRepository.save(cartItem);
        
        // Update cart total
        updateCartTotal(cart.getId());
        
        return mapToCartItemResponse(savedItem);
    }

    @Override
    public void removeCartItem(Integer hoaDonId, Integer cartItemId) {
        // Verify cart exists for this invoice
        GioHang cart = gioHangRepository.findByHoaDonId(hoaDonId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho hóa đơn ID: " + hoaDonId));

        // Get cart item
        GioHangChiTiet cartItem = gioHangChiTietRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng với ID: " + cartItemId));

        // Verify item belongs to this cart
        if (!cartItem.getIdGioHang().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Sản phẩm không thuộc giỏ hàng này");
        }

        // Restore stock quantity when removing item
        ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
        chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() + cartItem.getSoLuong());
        chiTietSanPhamRepository.save(chiTietSanPham);

        // Remove item
        gioHangChiTietRepository.delete(cartItem);
        
        // Update cart total
        updateCartTotal(cart.getId());
    }

    @Override
    public void clearCart(Integer hoaDonId) {
        GioHang cart = gioHangRepository.findByHoaDonId(hoaDonId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho hóa đơn ID: " + hoaDonId));

        // Restore stock for all items before clearing
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(cart.getId());
        for (GioHangChiTiet item : items) {
            ChiTietSanPham chiTietSanPham = item.getIdChiTietSp();
            chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() + item.getSoLuong());
            chiTietSanPhamRepository.save(chiTietSanPham);
        }

        // Delete all cart items
        gioHangChiTietRepository.deleteByGioHangId(cart.getId());
        
        // Reset cart total
        cart.setTongTien(BigDecimal.ZERO);
        gioHangRepository.save(cart);
    }

    @Override
    public CartResponse getCartByInvoiceId(Integer hoaDonId) {
        GioHang cart = gioHangRepository.findByHoaDonId(hoaDonId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho hóa đơn ID: " + hoaDonId));
        
        return mapToCartResponse(cart);
    }

    private void updateCartTotal(Integer cartId) {
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(cartId);
        BigDecimal total = items.stream()
            .map(item -> item.getThanhTien())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        GioHang cart = gioHangRepository.findById(cartId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với ID: " + cartId));
        
        cart.setTongTien(total);
        gioHangRepository.save(cart);
    }

    private CartResponse mapToCartResponse(GioHang cart) {
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(cart.getId());
        
        return CartResponse.builder()
            .id(cart.getId())
            .idKhachHang(cart.getIdKhachHang() != null ? cart.getIdKhachHang().getId() : null)
            .tenKhachHang(cart.getIdKhachHang() != null ? cart.getIdKhachHang().getTen() : null)
            .idHoaDon(cart.getIdHoaDon().getId())
            .maHoaDon(cart.getIdHoaDon().getMa())
            .tongTien(cart.getTongTien())
            .createdAt(cart.getCreatedAt())
            .updatedAt(cart.getUpdatedAt())
            .items(items.stream().map(this::mapToCartItemResponse).collect(Collectors.toList()))
            .totalItems(items.size())
            .build();
    }

    private CartItemResponse mapToCartItemResponse(GioHangChiTiet item) {
        ChiTietSanPham chiTiet = item.getIdChiTietSp();
        
        return CartItemResponse.builder()
            .id(item.getId())
            .idChiTietSanPham(chiTiet.getId())
            .maSanPham(chiTiet.getMa())
            .tenSanPham(chiTiet.getIdSanPham().getTenSanPham())
            .tenMauSac(chiTiet.getIdMauSac().getTenMauSac())
            .hexMauSac(chiTiet.getIdMauSac().getMaMauSac())
            .tenKichCo(chiTiet.getIdKichCo().getTenKichCo())
            .tenThuongHieu(chiTiet.getIdSanPham().getIdThuongHieu().getTenThuongHieu())
            .tenDanhMuc(chiTiet.getIdSanPham().getIdDanhMuc().getTenDanhMuc())
            .urlAnhSanPham(chiTiet.getIdAnhSanPham().getUrlAnh())
            .soLuong(item.getSoLuong())
            .gia(item.getGia())
            .thanhTien(item.getThanhTien())
            .soLuongTonKho(chiTiet.getSoLuongTonKho())
            .createdAt(item.getCreatedAt())
            .build();
    }

    @Override
    public void updateCartCustomer(Integer hoaDonId, Integer customerId) {
        // Find cart by invoice ID
        Optional<GioHang> cartOptional = gioHangRepository.findByHoaDonId(hoaDonId);
        
        if (cartOptional.isPresent()) {
            GioHang cart = cartOptional.get();
            
            // Get customer from invoice
            HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + hoaDonId));
            
            // Update cart customer to match invoice customer
            cart.setIdKhachHang(hoaDon.getIdKhachHang());
            gioHangRepository.save(cart);
        }
    }
}
