package com.example.sd_28_phostep_be.service.sell.impl;

import com.example.sd_28_phostep_be.dto.sell.request.client.AddToCartClientRequest;
import com.example.sd_28_phostep_be.dto.sell.response.client.CartClientResponse;
import com.example.sd_28_phostep_be.dto.sell.response.client.CartClientResponse.CartItemClientResponse;
import com.example.sd_28_phostep_be.exception.ResourceNotFoundException;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.modal.sell.GioHang;
import com.example.sd_28_phostep_be.modal.sell.GioHangChiTiet;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.bill.HoaDonRepository;
import com.example.sd_28_phostep_be.repository.product.ChiTietSanPhamRepository;
import com.example.sd_28_phostep_be.repository.sell.GioHangRepository;
import com.example.sd_28_phostep_be.repository.sell.GioHangChiTietRepository;
import com.example.sd_28_phostep_be.service.sell.GioHangClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GioHangClientServiceImpl implements GioHangClientService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Override
    public CartClientResponse getOrCreateCart(Integer idKhachHang, String sessionId) {
        // Tìm hóa đơn chờ online cho khách hàng hoặc session
        HoaDon hoaDon = findOrCreateOnlinePendingInvoice(idKhachHang, sessionId);
        
        // Tìm giỏ hàng hoặc tạo mới
        GioHang gioHang = gioHangRepository.findByHoaDonId(hoaDon.getId())
                .orElseGet(() -> createNewCart(hoaDon));
        
        return mapToCartClientResponse(gioHang);
    }

    @Override
    public CartClientResponse addToCart(AddToCartClientRequest request) {
        // Tìm hoặc tạo hóa đơn chờ
        HoaDon hoaDon = findOrCreateOnlinePendingInvoice(request.getIdKhachHang(), request.getSessionId());
        
        // Tìm hoặc tạo giỏ hàng
        GioHang gioHang = gioHangRepository.findByHoaDonId(hoaDon.getId())
                .orElseGet(() -> createNewCart(hoaDon));
        
        // Lấy chi tiết sản phẩm
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(request.getIdChiTietSanPham())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết sản phẩm"));
        
        // Kiểm tra tồn kho
        if (chiTietSanPham.getSoLuongTonKho() < request.getSoLuong()) {
            throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + chiTietSanPham.getSoLuongTonKho());
        }
        
        // Kiểm tra sản phẩm đã có trong giỏ chưa
        Optional<GioHangChiTiet> existingItem = gioHangChiTietRepository
                .findByGioHangIdAndChiTietSpId(gioHang.getId(), request.getIdChiTietSanPham());
        
        GioHangChiTiet cartItem;
        if (existingItem.isPresent()) {
            // Cập nhật số lượng nếu đã tồn tại
            cartItem = existingItem.get();
            int newQuantity = cartItem.getSoLuong() + request.getSoLuong();
            
            // Kiểm tra tổng số lượng với tồn kho
            if (chiTietSanPham.getSoLuongTonKho() < newQuantity) {
                throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + chiTietSanPham.getSoLuongTonKho());
            }
            
            cartItem.setSoLuong(newQuantity);
            cartItem.setGia(chiTietSanPham.getGiaBan());
        } else {
            // Tạo mới item trong giỏ
            cartItem = GioHangChiTiet.builder()
                    .idGioHang(gioHang)
                    .idChiTietSp(chiTietSanPham)
                    .soLuong(request.getSoLuong())
                    .gia(chiTietSanPham.getGiaBan())
                    .build();
        }
        
        // Trừ tồn kho
        chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() - request.getSoLuong());
        chiTietSanPhamRepository.save(chiTietSanPham);
        
        gioHangChiTietRepository.save(cartItem);
        
        // Cập nhật tổng tiền giỏ hàng
        updateCartTotal(gioHang);
        
        return mapToCartClientResponse(gioHang);
    }

    @Override
    public CartClientResponse updateCartItem(Integer cartItemId, Integer soLuong, Integer idKhachHang, String sessionId) {
        // Lấy cart item
        GioHangChiTiet cartItem = gioHangChiTietRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));
        
        // Xác minh giỏ hàng thuộc về người dùng
        GioHang gioHang = cartItem.getIdGioHang();
        validateCartOwnership(gioHang, idKhachHang, sessionId);
        
        ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
        int currentQuantity = cartItem.getSoLuong();
        int quantityDifference = soLuong - currentQuantity;
        
        // Kiểm tra tồn kho nếu tăng số lượng
        if (quantityDifference > 0) {
            if (chiTietSanPham.getSoLuongTonKho() < quantityDifference) {
                throw new IllegalArgumentException("Không đủ hàng trong kho. Còn lại: " + chiTietSanPham.getSoLuongTonKho());
            }
        }
        
        // Cập nhật tồn kho
        chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() - quantityDifference);
        chiTietSanPhamRepository.save(chiTietSanPham);
        
        // Cập nhật số lượng trong giỏ
        cartItem.setSoLuong(soLuong);
        gioHangChiTietRepository.save(cartItem);
        
        // Cập nhật tổng tiền
        updateCartTotal(gioHang);
        
        return mapToCartClientResponse(gioHang);
    }

    @Override
    public CartClientResponse removeCartItem(Integer cartItemId, Integer idKhachHang, String sessionId) {
        // Lấy cart item
        GioHangChiTiet cartItem = gioHangChiTietRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng"));
        
        // Xác minh giỏ hàng thuộc về người dùng
        GioHang gioHang = cartItem.getIdGioHang();
        validateCartOwnership(gioHang, idKhachHang, sessionId);
        
        // Hoàn lại tồn kho
        ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
        chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() + cartItem.getSoLuong());
        chiTietSanPhamRepository.save(chiTietSanPham);
        
        // Xóa item khỏi giỏ
        gioHangChiTietRepository.delete(cartItem);
        
        // Cập nhật tổng tiền
        updateCartTotal(gioHang);
        
        return mapToCartClientResponse(gioHang);
    }

    @Override
    public void clearCart(Integer idKhachHang, String sessionId) {
        // Tìm hóa đơn chờ
        HoaDon hoaDon = findOnlinePendingInvoice(idKhachHang, sessionId);
        if (hoaDon == null) {
            return; // Không có giỏ hàng để xóa
        }
        
        Optional<GioHang> gioHangOpt = gioHangRepository.findByHoaDonId(hoaDon.getId());
        if (gioHangOpt.isEmpty()) {
            return;
        }
        
        GioHang gioHang = gioHangOpt.get();
        List<GioHangChiTiet> cartItems = gioHangChiTietRepository.findByGioHangId(gioHang.getId());
        
        // Hoàn lại tồn kho cho tất cả sản phẩm
        for (GioHangChiTiet item : cartItems) {
            ChiTietSanPham chiTietSanPham = item.getIdChiTietSp();
            chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() + item.getSoLuong());
            chiTietSanPhamRepository.save(chiTietSanPham);
        }
        
        // Xóa tất cả items
        gioHangChiTietRepository.deleteAll(cartItems);
        
        // Cập nhật tổng tiền về 0
        gioHang.setTongTien(BigDecimal.ZERO);
        gioHangRepository.save(gioHang);
    }

    // Tìm hoặc tạo hóa đơn chờ Online
    private HoaDon findOrCreateOnlinePendingInvoice(Integer idKhachHang, String sessionId) {
        HoaDon hoaDon = findOnlinePendingInvoice(idKhachHang, sessionId);
        
        if (hoaDon == null) {
            // Tạo hóa đơn mới
            hoaDon = createOnlinePendingInvoice(idKhachHang, sessionId);
        }
        
        return hoaDon;
    }

    // Tìm hóa đơn chờ Online
    private HoaDon findOnlinePendingInvoice(Integer idKhachHang, String sessionId) {
        if (idKhachHang != null) {
            // Tìm theo khách hàng đăng nhập
            return hoaDonRepository.findPendingOnlineInvoiceByCustomer(idKhachHang);
        } else if (sessionId != null && !sessionId.isEmpty()) {
            // Tìm theo session cho khách chưa đăng nhập
            return hoaDonRepository.findPendingOnlineInvoiceBySession(sessionId);
        }
        return null;
    }

    // Tạo hóa đơn chờ Online mới
    private HoaDon createOnlinePendingInvoice(Integer idKhachHang, String sessionId) {
        HoaDon newInvoice = new HoaDon();
        newInvoice.setTrangThai((short) 0); // Hóa đơn chờ
        newInvoice.setDeleted(false);
        newInvoice.setCreatedAt(Instant.now());
        newInvoice.setNgayTao(Date.valueOf(LocalDate.now()));
        newInvoice.setLoaiDon("Online");
        
        // Set default amounts
        newInvoice.setTienSanPham(BigDecimal.ZERO);
        newInvoice.setPhiVanChuyen(BigDecimal.ZERO);
        newInvoice.setTongTien(BigDecimal.ZERO);
        newInvoice.setTongTienSauGiam(BigDecimal.ZERO);
        
        // Set customer info
        if (idKhachHang != null) {
            KhachHang khachHang = khachHangRepository.findById(idKhachHang)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
            newInvoice.setIdKhachHang(khachHang);
            newInvoice.setTenKhachHang(khachHang.getTen());
            newInvoice.setSoDienThoaiKhachHang(khachHang.getTaiKhoan() != null ? khachHang.getTaiKhoan().getSoDienThoai() : "");
            newInvoice.setEmail(khachHang.getTaiKhoan() != null ? khachHang.getTaiKhoan().getEmail() : "");
        } else {
            // Khách lẻ
            newInvoice.setIdKhachHang(khachHangRepository.findById(1).orElse(null)); // ID 1 = Khách lẻ
            newInvoice.setTenKhachHang("Khách lẻ");
            newInvoice.setGhiChu(sessionId); // Lưu session vào ghi chú tạm
        }
        
        // Set default employee (null for online orders)
        newInvoice.setIdNhanVien(null);
        
        HoaDon savedInvoice = hoaDonRepository.save(newInvoice);
        
        // Generate ma if needed
        if (savedInvoice.getMa() == null || savedInvoice.getMa().isEmpty()) {
            String generatedMa = "HD" + String.format("%06d", savedInvoice.getId());
            savedInvoice.setMa(generatedMa);
            savedInvoice = hoaDonRepository.save(savedInvoice);
        }
        
        return savedInvoice;
    }

    // Tạo giỏ hàng mới
    private GioHang createNewCart(HoaDon hoaDon) {
        GioHang newCart = GioHang.builder()
                .idHoaDon(hoaDon)
                .idKhachHang(hoaDon.getIdKhachHang())
                .tongTien(BigDecimal.ZERO)
                .build();
        return gioHangRepository.save(newCart);
    }

    // Cập nhật tổng tiền giỏ hàng
    private void updateCartTotal(GioHang gioHang) {
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(gioHang.getId());
        
        BigDecimal total = items.stream()
                .map(item -> item.getGia().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        gioHang.setTongTien(total);
        gioHangRepository.save(gioHang);
        
        // Cập nhật hóa đơn
        HoaDon hoaDon = gioHang.getIdHoaDon();
        hoaDon.setTienSanPham(total);
        hoaDon.setTongTien(total);
        hoaDon.setTongTienSauGiam(total);
        hoaDon.setUpdatedAt(Instant.now());
        hoaDonRepository.save(hoaDon);
    }

    // Xác minh quyền sở hữu giỏ hàng
    private void validateCartOwnership(GioHang gioHang, Integer idKhachHang, String sessionId) {
        HoaDon hoaDon = gioHang.getIdHoaDon();
        
        if (idKhachHang != null) {
            // Kiểm tra khách hàng
            if (!hoaDon.getIdKhachHang().getId().equals(idKhachHang)) {
                throw new IllegalArgumentException("Giỏ hàng không thuộc về khách hàng này");
            }
        } else if (sessionId != null) {
            // Kiểm tra session
            if (hoaDon.getGhiChu() == null || !hoaDon.getGhiChu().equals(sessionId)) {
                throw new IllegalArgumentException("Giỏ hàng không thuộc về session này");
            }
        }
    }

    // Map to response
    private CartClientResponse mapToCartClientResponse(GioHang gioHang) {
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(gioHang.getId());
        
        List<CartItemClientResponse> itemResponses = items.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());
        
        int tongSoLuong = items.stream()
                .mapToInt(GioHangChiTiet::getSoLuong)
                .sum();
        
        HoaDon hoaDon = gioHang.getIdHoaDon();
        
        return CartClientResponse.builder()
                .id(gioHang.getId())
                .idHoaDon(hoaDon.getId())
                .maHoaDon(hoaDon.getMa())
                .idKhachHang(hoaDon.getIdKhachHang() != null ? hoaDon.getIdKhachHang().getId() : null)
                .tenKhachHang(hoaDon.getTenKhachHang())
                .tongTien(gioHang.getTongTien())
                .tongSoLuong(tongSoLuong)
                .sessionId(hoaDon.getGhiChu())
                .items(itemResponses)
                .build();
    }

    private CartItemClientResponse mapToCartItemResponse(GioHangChiTiet item) {
        ChiTietSanPham chiTietSanPham = item.getIdChiTietSp();
        
        return CartItemClientResponse.builder()
                .id(item.getId())
                .idChiTietSanPham(chiTietSanPham.getId())
                .maSanPham(chiTietSanPham.getIdSanPham().getMa())
                .tenSanPham(chiTietSanPham.getIdSanPham().getTenSanPham())
                .urlAnh(chiTietSanPham.getIdAnhSanPham() != null ? chiTietSanPham.getIdAnhSanPham().getUrlAnh() : null)
                .tenMauSac(chiTietSanPham.getIdMauSac() != null ? chiTietSanPham.getIdMauSac().getTenMauSac() : null)
                .tenKichCo(chiTietSanPham.getIdKichCo() != null ? chiTietSanPham.getIdKichCo().getTenKichCo() : null)
                .soLuong(item.getSoLuong())
                .soLuongTonKho(chiTietSanPham.getSoLuongTonKho())
                .gia(item.getGia())
                .thanhTien(item.getThanhTien())
                .build();
    }
}
