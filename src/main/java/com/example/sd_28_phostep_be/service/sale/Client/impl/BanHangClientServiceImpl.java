package com.example.sd_28_phostep_be.service.sale.Client.impl;


import com.example.sd_28_phostep_be.dto.sell.request.PaymentRequest;
import com.example.sd_28_phostep_be.service.StockUpdateService;
import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDetailResponse;
import com.example.sd_28_phostep_be.dto.sell.request.AddToCartRequest;
import com.example.sd_28_phostep_be.dto.sell.request.ClientPaymentRequest;
import com.example.sd_28_phostep_be.dto.sell.request.UpdateCartItemRequest;
import com.example.sd_28_phostep_be.dto.sell.response.CartItemResponse;
import com.example.sd_28_phostep_be.dto.sell.response.CartResponse;
import com.example.sd_28_phostep_be.exception.ResourceNotFoundException;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.account.NhanVien;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import com.example.sd_28_phostep_be.modal.bill.HoaDonChiTiet;
import com.example.sd_28_phostep_be.modal.bill.LichSuHoaDon;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.modal.sell.GioHang;
import com.example.sd_28_phostep_be.modal.sell.GioHangChiTiet;
import com.example.sd_28_phostep_be.modal.sell.HinhThucThanhToan;
import com.example.sd_28_phostep_be.modal.sell.PhuongThucThanhToan;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.account.NhanVien.NhanVienRepository;
import com.example.sd_28_phostep_be.repository.bill.HoaDonChiTietRepository;
import com.example.sd_28_phostep_be.repository.bill.HoaDonRepository;
import com.example.sd_28_phostep_be.repository.bill.LichSuHoaDonRepository;
import com.example.sd_28_phostep_be.repository.product.ChiTietSanPhamRepository;
import com.example.sd_28_phostep_be.repository.sell.GioHangChiTietRepository;
import com.example.sd_28_phostep_be.repository.sell.GioHangRepository;
import com.example.sd_28_phostep_be.repository.sell.HinhThucThanhToanRepository;
import com.example.sd_28_phostep_be.repository.sell.PhuongThucThanhToanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BanHangClientServiceImpl implements BanHangClientService{

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;
    
    @Autowired
    private StockUpdateService stockUpdateService;
    
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;
    
    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    private String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return "HD_CLIENT_" + code;
    }

    @Override
    public HoaDonDetailResponse taoHoaDonCho(Integer khachHangId) {
        System.out.println("=== Creating pending invoice ===");
        System.out.println("Received khachHangId: " + khachHangId);
        
        // Get customer or use default guest customer
        KhachHang khachHang;
        if (khachHangId != null) {
            khachHang = khachHangRepository.findById(khachHangId)
                    .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại với ID: " + khachHangId));
            System.out.println("Found customer: " + khachHang.getTen() + " (ID: " + khachHang.getId() + ")");
        } else {
            // Use default guest customer with ID = 1
            khachHang = khachHangRepository.findById(1)
                    .orElseThrow(() -> new ResourceNotFoundException("Khách lẻ không tồn tại với ID: 1"));
            System.out.println("Using default guest customer: " + khachHang.getTen());
        }

        // Check for existing pending invoice
        List<HoaDon> existingPendingInvoices = hoaDonRepository.findByIdKhachHangAndTrangThai(khachHang.getId(), (short) 0);
        if (!existingPendingInvoices.isEmpty()) {
            return mapToHoaDonDetailResponse(existingPendingInvoices.get(0));
        }

        // Get default employee (ID = 1)
        NhanVien nhanVien = nhanVienRepository.findById(1)
                .orElseThrow(() -> new ResourceNotFoundException("Nhân viên mặc định không tồn tại"));

        // Create new pending invoice
        HoaDon hoaDon = HoaDon.builder()
                .idKhachHang(khachHang)
                .idNhanVien(nhanVien)
                .ma(generateRandomCode())
                .tienSanPham(BigDecimal.ZERO)
                .loaiDon("Online")
                .phiVanChuyen(BigDecimal.ZERO)
                .tongTien(BigDecimal.ZERO)
                .tongTienSauGiam(BigDecimal.ZERO)
                .ghiChu("Hóa đơn chờ từ client")
                .tenKhachHang(khachHang.getTen() != null ? khachHang.getTen() : "Khách lẻ")
                .diaChiKhachHang("N/A")
                .soDienThoaiKhachHang(khachHang.getTaiKhoan() != null ? khachHang.getTaiKhoan().getSoDienThoai() : "N/A")
                .email(khachHang.getTaiKhoan() != null ? khachHang.getTaiKhoan().getEmail() : "N/A")
                .ngayTao(new java.sql.Date(System.currentTimeMillis()))
                .trangThai((short) 0) // Pending status
                .deleted(false)
                .createdAt(new java.sql.Date(System.currentTimeMillis()))
                .createdBy(1)
                .build();

        hoaDon = hoaDonRepository.save(hoaDon);

        // Create cart in database
        GioHang gioHang = GioHang.builder()
                .idKhachHang(khachHang)
                .idHoaDon(hoaDon)
                .tongTien(BigDecimal.ZERO)
                .build();
        gioHangRepository.save(gioHang);

        return mapToHoaDonDetailResponse(hoaDon);
    }

    @Override
    public List<HoaDonDetailResponse> getPendingInvoicesByCustomer(Integer khachHangId) {
        KhachHang khachHang = khachHangRepository.findById(khachHangId)
                .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại với ID: " + khachHangId));

        List<HoaDon> pendingHoaDons = hoaDonRepository.findByIdKhachHangAndTrangThai(khachHang.getId(), (short) 0);

        return pendingHoaDons.stream()
                .map(this::mapToHoaDonDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CartResponse themSanPhamVaoGioHang(Integer hoaDonId, AddToCartRequest request) {
        // Validate invoice
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn có id: " + hoaDonId));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn này không phải hóa đơn chờ! Trạng thái hiện tại: " + hoaDon.getTrangThai());
        }

        // Validate product detail
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(request.getIdChiTietSanPham())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết sản phẩm!"));

        // Check stock availability
        if (chiTietSanPham.getSoLuongTonKho() < request.getSoLuong()) {
            throw new RuntimeException("Không đủ số lượng tồn kho! Còn lại: " + chiTietSanPham.getSoLuongTonKho());
        }

        // Get or create cart
        GioHang gioHang = gioHangRepository.findByHoaDonId(hoaDonId)
                .orElseGet(() -> {
                    GioHang newCart = GioHang.builder()
                            .idKhachHang(hoaDon.getIdKhachHang())
                            .idHoaDon(hoaDon)
                            .tongTien(BigDecimal.ZERO)
                            .build();
                    return gioHangRepository.save(newCart);
                });

        // Check if product already exists in cart
        Optional<GioHangChiTiet> existingItem = gioHangChiTietRepository
                .findByGioHangIdAndChiTietSpId(gioHang.getId(), request.getIdChiTietSanPham());

        if (existingItem.isPresent()) {
            // Update existing item
            GioHangChiTiet item = existingItem.get();
            int newQuantity = item.getSoLuong() + request.getSoLuong();

            // Check total stock availability
            if (chiTietSanPham.getSoLuongTonKho() < newQuantity) {
                throw new RuntimeException("Không đủ số lượng tồn kho! Còn lại: " + chiTietSanPham.getSoLuongTonKho());
            }

            // Calculate quantity difference
            int quantityDiff = request.getSoLuong();
            
            item.setSoLuong(newQuantity);
            item.setGia(request.getGia());
            item.setThanhTien(request.getGia().multiply(BigDecimal.valueOf(newQuantity)));
            gioHangChiTietRepository.save(item);
            
            // Update stock: reduce by the added quantity
            chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() - quantityDiff);
            chiTietSanPhamRepository.save(chiTietSanPham);
            System.out.println("Updated stock for ChiTietSanPham ID " + chiTietSanPham.getId() + 
                             ": reduced by " + quantityDiff + ", new stock: " + chiTietSanPham.getSoLuongTonKho());
            
            // Broadcast stock update
            stockUpdateService.notifyStockUpdate(chiTietSanPham.getId(), chiTietSanPham.getSoLuongTonKho());
        } else {
            // Create new cart item
            GioHangChiTiet newItem = GioHangChiTiet.builder()
                    .idGioHang(gioHang)
                    .idChiTietSp(chiTietSanPham)
                    .soLuong(request.getSoLuong())
                    .gia(request.getGia())
                    .thanhTien(request.getGia().multiply(BigDecimal.valueOf(request.getSoLuong())))
                    .build();
            gioHangChiTietRepository.save(newItem);
            
            // Update stock: reduce by the added quantity
            chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() - request.getSoLuong());
            chiTietSanPhamRepository.save(chiTietSanPham);
            System.out.println("Updated stock for ChiTietSanPham ID " + chiTietSanPham.getId() + 
                             ": reduced by " + request.getSoLuong() + ", new stock: " + chiTietSanPham.getSoLuongTonKho());
            
            // Broadcast stock update
            stockUpdateService.notifyStockUpdate(chiTietSanPham.getId(), chiTietSanPham.getSoLuongTonKho());
        }

        // Update cart total
        updateCartTotal(gioHang.getId());

        return mapToCartResponse(gioHang);
    }

    @Override
    public CartResponse layGioHang(Integer hoaDonId) {
        System.out.println("=== Getting cart for invoice ID: " + hoaDonId + " ===");
        
        GioHang gioHang = gioHangRepository.findByHoaDonId(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho hóa đơn ID: " + hoaDonId));
        
        System.out.println("Found GioHang ID: " + gioHang.getId() + " for HoaDon ID: " + hoaDonId);

        return mapToCartResponse(gioHang);
    }

    @Override
    public CartResponse capNhatSoLuongSanPham(Integer hoaDonId, Integer cartItemId, UpdateCartItemRequest request) {
        // Validate invoice
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn có id: " + hoaDonId));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn này không phải hóa đơn chờ!");
        }

        // Get cart item
        GioHangChiTiet cartItem = gioHangChiTietRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng!"));

        // Get current and new quantities
        ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
        int oldQuantity = cartItem.getSoLuong();
        int newQuantity = request.getSoLuong();
        int quantityDiff = newQuantity - oldQuantity;
        
        // Check stock availability for the difference
        if (quantityDiff > 0 && chiTietSanPham.getSoLuongTonKho() < quantityDiff) {
            throw new RuntimeException("Không đủ số lượng tồn kho! Còn lại: " + chiTietSanPham.getSoLuongTonKho());
        }

        // Update cart item
        cartItem.setSoLuong(newQuantity);
        cartItem.setThanhTien(cartItem.getGia().multiply(BigDecimal.valueOf(newQuantity)));
        gioHangChiTietRepository.save(cartItem);
        
        // Update stock: adjust by the quantity difference
        chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() - quantityDiff);
        chiTietSanPhamRepository.save(chiTietSanPham);
        System.out.println("Updated stock for ChiTietSanPham ID " + chiTietSanPham.getId() + 
                         ": changed by " + (-quantityDiff) + ", new stock: " + chiTietSanPham.getSoLuongTonKho());
        
        // Broadcast stock update
        stockUpdateService.notifyStockUpdate(chiTietSanPham.getId(), chiTietSanPham.getSoLuongTonKho());

        // Update cart total
        updateCartTotal(cartItem.getIdGioHang().getId());

        return mapToCartResponse(cartItem.getIdGioHang());
    }

    @Override
    public CartResponse xoaSanPhamKhoiGioHang(Integer hoaDonId, Integer cartItemId) {
        // Validate invoice
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn có id: " + hoaDonId));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn này không phải hóa đơn chờ!");
        }

        // Get cart item
        GioHangChiTiet cartItem = gioHangChiTietRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng!"));

        GioHang gioHang = cartItem.getIdGioHang();
        
        // Restore stock before deleting
        ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
        int quantityToRestore = cartItem.getSoLuong();
        chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() + quantityToRestore);
        chiTietSanPhamRepository.save(chiTietSanPham);
        System.out.println("Restored stock for ChiTietSanPham ID " + chiTietSanPham.getId() + 
                         ": restored " + quantityToRestore + ", new stock: " + chiTietSanPham.getSoLuongTonKho());
        
        // Broadcast stock update
        stockUpdateService.notifyStockUpdate(chiTietSanPham.getId(), chiTietSanPham.getSoLuongTonKho());
        
        // Delete cart item
        gioHangChiTietRepository.delete(cartItem);

        // Update cart total
        updateCartTotal(gioHang.getId());

        return mapToCartResponse(gioHang);
    }

    @Override
    public HoaDonDetailResponse thanhToan(Integer hoaDonId, ClientPaymentRequest request) {
        // Validate invoice
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn có id: " + hoaDonId));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn này không phải hóa đơn chờ!");
        }

        // Get cart
        GioHang gioHang = gioHangRepository.findByHoaDonId(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho hóa đơn ID: " + hoaDonId));

        // Get cart items
        List<GioHangChiTiet> cartItems = gioHangChiTietRepository.findByGioHangId(gioHang.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể thanh toán!");
        }

        // Update invoice information - only update if values are provided
        if (request.getTenKhachHang() != null && !request.getTenKhachHang().trim().isEmpty()) {
            hoaDon.setTenKhachHang(request.getTenKhachHang());
        }
        if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
            hoaDon.setSoDienThoaiKhachHang(request.getSoDienThoai());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            hoaDon.setEmail(request.getEmail());
        }
        if (request.getDiaChi() != null && !request.getDiaChi().trim().isEmpty()) {
            hoaDon.setDiaChiKhachHang(request.getDiaChi());
        }
        if (request.getGhiChu() != null && !request.getGhiChu().trim().isEmpty()) {
            hoaDon.setGhiChu(request.getGhiChu());
        }
        
        // Set voucher if provided
        if (request.getVoucherId() != null) {
            // TODO: Set voucher - hoaDon.setIdPhieuGiamGia(voucher);
        }
        
        // Calculate totals
        BigDecimal tienSanPham = cartItems.stream()
                .map(GioHangChiTiet::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Only update shipping fee and discount if provided, otherwise keep existing values
        BigDecimal phiVanChuyen = request.getPhiVanChuyen() != null ? 
            request.getPhiVanChuyen() : 
            (hoaDon.getPhiVanChuyen() != null ? hoaDon.getPhiVanChuyen() : BigDecimal.ZERO);
            
        BigDecimal tienGiam = request.getTienGiam() != null ? 
            request.getTienGiam() : 
            (hoaDon.getTongTien() != null && hoaDon.getTongTienSauGiam() != null ? 
                hoaDon.getTongTien().subtract(hoaDon.getTongTienSauGiam()) : BigDecimal.ZERO);
        
        BigDecimal tongTien = tienSanPham.add(phiVanChuyen);
        BigDecimal tongTienSauGiam = tongTien.subtract(tienGiam);

        hoaDon.setTienSanPham(tienSanPham);
        hoaDon.setPhiVanChuyen(phiVanChuyen);
        hoaDon.setTongTien(tongTien);
        hoaDon.setTongTienSauGiam(tongTienSauGiam);
        
        // Update status to confirmed (1) and payment date
        hoaDon.setTrangThai((short) 1);
        hoaDon.setNgayThanhToan(new java.sql.Date(System.currentTimeMillis()));
        hoaDon.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));
        
        hoaDonRepository.save(hoaDon);

        // Create payment method record
        createPaymentMethod(hoaDon, request.getPhuongThucThanhToan(), tongTienSauGiam);

        // Create invoice details from cart items
        for (GioHangChiTiet cartItem : cartItems) {
            HoaDonChiTiet hoaDonChiTiet = HoaDonChiTiet.builder()
                    .idHoaDon(hoaDon)
                    .idChiTietSp(cartItem.getIdChiTietSp())
                    .soLuong(cartItem.getSoLuong())
                    .gia(cartItem.getGia())
                    .trangThai((short) 1) // Active status
                    .deleted(false)
                    .build();
            hoaDonChiTietRepository.save(hoaDonChiTiet);
        }

        // Create invoice history
        LichSuHoaDon lichSu = LichSuHoaDon.builder()
                .idHoaDon(hoaDon)
                .hanhDong("Thanh toán đơn hàng")
                .thoiGian(new java.sql.Date(System.currentTimeMillis()))
                .deleted((short) 0) // 0 = not deleted
                .build();
        lichSuHoaDonRepository.save(lichSu);

        // Clear cart after successful payment
        gioHangChiTietRepository.deleteAll(cartItems);
        gioHangRepository.delete(gioHang);

        System.out.println("Payment completed for invoice ID: " + hoaDonId + 
                         ", Amount: " + tongTienSauGiam + ", Method: " + request.getPhuongThucThanhToan());

        return mapToHoaDonDetailResponse(hoaDon);
    }

    // Get cart total for VNPay payment
    public BigDecimal getCartTotal(Integer hoaDonId) {
        try {
            GioHang gioHang = gioHangRepository.findByHoaDonId(hoaDonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho hóa đơn ID: " + hoaDonId));
            
            return gioHang.getTongTien() != null ? gioHang.getTongTien() : BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Error getting cart total: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    // Create payment method record
    private void createPaymentMethod(HoaDon hoaDon, String paymentMethodType, BigDecimal amount) {
        try {
            // Find or create payment method type
            PhuongThucThanhToan phuongThuc = findOrCreatePaymentMethodType(paymentMethodType);
            
            // Create payment record
            HinhThucThanhToan hinhThuc = HinhThucThanhToan.builder()
                    .idHoaDon(hoaDon)
                    .idPhuongThucThanhToan(phuongThuc)
                    .ma(generatePaymentCode())
                    .deleted(false)
                    .build();

            // Set payment amounts based on method type
            if ("COD".equalsIgnoreCase(paymentMethodType) || "TIEN_MAT".equalsIgnoreCase(paymentMethodType)) {
                hinhThuc.setTienMat(amount);
                hinhThuc.setTienChuyenKhoan(BigDecimal.ZERO);
            } else if ("VNPAY".equalsIgnoreCase(paymentMethodType) || "CHUYEN_KHOAN".equalsIgnoreCase(paymentMethodType)) {
                hinhThuc.setTienChuyenKhoan(amount);
                hinhThuc.setTienMat(BigDecimal.ZERO);
            } else {
                // Default to cash
                hinhThuc.setTienMat(amount);
                hinhThuc.setTienChuyenKhoan(BigDecimal.ZERO);
            }

            hinhThucThanhToanRepository.save(hinhThuc);
            
            System.out.println("Created payment method: " + paymentMethodType + 
                             ", Amount: " + amount + 
                             ", Cash: " + hinhThuc.getTienMat() + 
                             ", Transfer: " + hinhThuc.getTienChuyenKhoan());
        } catch (Exception e) {
            System.err.println("Error creating payment method: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Find or create payment method type
    private PhuongThucThanhToan findOrCreatePaymentMethodType(String paymentMethodType) {
        // Try to find existing payment method
        Optional<PhuongThucThanhToan> existingMethod = phuongThucThanhToanRepository.findByKieuThanhToan(paymentMethodType);
        
        if (existingMethod.isPresent()) {
            return existingMethod.get();
        }
        
        // Create new payment method type if not found
        PhuongThucThanhToan newMethod = PhuongThucThanhToan.builder()
                .ma(generatePaymentMethodCode(paymentMethodType))
                .kieuThanhToan(paymentMethodType)
                .deleted(false)
                .build();
                
        return phuongThucThanhToanRepository.save(newMethod);
    }

    // Generate payment code
    private String generatePaymentCode() {
        return "PAY_" + System.currentTimeMillis();
    }

    // Generate payment method code
    private String generatePaymentMethodCode(String type) {
        return "PM_" + type.toUpperCase() + "_" + System.currentTimeMillis();
    }

    @Override
    public CartResponse xoaToanBoGioHang(Integer hoaDonId) {
        GioHang gioHang = gioHangRepository.findByHoaDonId(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng cho hóa đơn ID: " + hoaDonId));

        // Delete all cart items
        gioHangChiTietRepository.deleteByGioHangId(gioHang.getId());

        // Update cart total to zero
        gioHang.setTongTien(BigDecimal.ZERO);
        gioHangRepository.save(gioHang);
        
        // Return updated cart response
        return CartResponse.builder()
                .id(gioHang.getId())
                .idHoaDon(hoaDonId)
                .tongTien(BigDecimal.ZERO)
                .totalItems(0)
                .items(new ArrayList<>())
                .build();
    }

    @Override
    public void updateCustomerInfo(Integer hoaDonId, ClientPaymentRequest request) {
        // Validate invoice
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn có id: " + hoaDonId));

        System.out.println("Updating customer info for invoice: " + hoaDonId);
        System.out.println("Current customer info: " + hoaDon.getTenKhachHang() + ", " + hoaDon.getDiaChiKhachHang());
        
        // Update customer information if provided
        if (request.getTenKhachHang() != null && !request.getTenKhachHang().trim().isEmpty()) {
            hoaDon.setTenKhachHang(request.getTenKhachHang());
            System.out.println("Updated customer name: " + request.getTenKhachHang());
        }
        if (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty()) {
            hoaDon.setSoDienThoaiKhachHang(request.getSoDienThoai());
            System.out.println("Updated customer phone: " + request.getSoDienThoai());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            hoaDon.setEmail(request.getEmail());
            System.out.println("Updated customer email: " + request.getEmail());
        }
        if (request.getDiaChi() != null && !request.getDiaChi().trim().isEmpty()) {
            hoaDon.setDiaChiKhachHang(request.getDiaChi());
            System.out.println("Updated customer address: " + request.getDiaChi());
        }
        
        // Update shipping fee and discount if provided
        if (request.getPhiVanChuyen() != null) {
            hoaDon.setPhiVanChuyen(request.getPhiVanChuyen());
            System.out.println("Updated shipping fee: " + request.getPhiVanChuyen());
        }
        
        // Calculate discount from voucher if provided
        if (request.getTienGiam() != null) {
            // Update totals with discount
            BigDecimal tongTien = hoaDon.getTienSanPham() != null ? hoaDon.getTienSanPham() : BigDecimal.ZERO;
            BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() != null ? hoaDon.getPhiVanChuyen() : BigDecimal.ZERO;
            BigDecimal tongTienTruocGiam = tongTien.add(phiVanChuyen);
            BigDecimal tongTienSauGiam = tongTienTruocGiam.subtract(request.getTienGiam());
            
            hoaDon.setTongTien(tongTienTruocGiam);
            hoaDon.setTongTienSauGiam(tongTienSauGiam);
            System.out.println("Updated discount: " + request.getTienGiam() + ", Final total: " + tongTienSauGiam);
        }
        
        hoaDon.setUpdatedAt(new java.sql.Date(System.currentTimeMillis()));
        hoaDonRepository.save(hoaDon);
        
        System.out.println("Customer info updated successfully for invoice: " + hoaDonId);
    }

    @Override
    public HoaDonDetailResponse thanhToan(Integer hoaDonId, PaymentRequest paymentRequest) {
        // Validate invoice
        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại!"));

        if (hoaDon.getTrangThai() != 0) {
            throw new RuntimeException("Hóa đơn không ở trạng thái chờ thanh toán!");
        }

        // Get cart
        GioHang gioHang = gioHangRepository.findByHoaDonId(hoaDonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng!"));

        List<GioHangChiTiet> cartItems = gioHangChiTietRepository.findByGioHangId(gioHang.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống, không thể thanh toán!");
        }

        // Validate stock availability for all items
        for (GioHangChiTiet item : cartItems) {
            ChiTietSanPham chiTietSanPham = item.getIdChiTietSp();
            if (chiTietSanPham.getSoLuongTonKho() < item.getSoLuong()) {
                throw new RuntimeException("Sản phẩm " + chiTietSanPham.getIdSanPham().getTenSanPham() +
                        " không đủ số lượng! Còn lại: " + chiTietSanPham.getSoLuongTonKho());
            }
        }

        // Calculate totals
        BigDecimal tienSanPham = cartItems.stream()
                .map(GioHangChiTiet::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal phiVanChuyen = "Online".equals(hoaDon.getLoaiDon()) ? new BigDecimal("30000") : BigDecimal.ZERO;
        BigDecimal tongTienSauGiam = tienSanPham.add(phiVanChuyen);

        // Update invoice
        hoaDon.setTienSanPham(tienSanPham);
        hoaDon.setPhiVanChuyen(phiVanChuyen);
        hoaDon.setTongTien(tongTienSauGiam);
        hoaDon.setTongTienSauGiam(tongTienSauGiam);
        hoaDon.setTrangThai((short) 1); // Confirmed status
        hoaDon.setNgayThanhToan(new java.sql.Date(System.currentTimeMillis()));
        hoaDonRepository.save(hoaDon);

        // Create invoice details and update stock
        for (GioHangChiTiet cartItem : cartItems) {
            // Create invoice detail for each quantity
            for (int i = 0; i < cartItem.getSoLuong(); i++) {
                HoaDonChiTiet hoaDonChiTiet = HoaDonChiTiet.builder()
                        .idHoaDon(hoaDon)
                        .idChiTietSp(cartItem.getIdChiTietSp())
                        .gia(cartItem.getGia())
                        .soLuong(1)
                        .trangThai((short) 1)
                        .deleted(false)
                        .build();
                hoaDonChiTietRepository.save(hoaDonChiTiet);
            }

            // Update stock
            ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
            chiTietSanPham.setSoLuongTonKho(chiTietSanPham.getSoLuongTonKho() - cartItem.getSoLuong());
            chiTietSanPhamRepository.save(chiTietSanPham);
        }

        // Create invoice history
        LichSuHoaDon lichSu = LichSuHoaDon.builder()
                .idHoaDon(hoaDon)
                .idNhanVien(hoaDon.getIdNhanVien())
                .ma("LSHD_" + UUID.randomUUID().toString().substring(0, 8))
                .hanhDong("Thanh toán hóa đơn qua client (" + paymentRequest.getPhuongThucThanhToan() + ")")
                .thoiGian(new java.sql.Date(System.currentTimeMillis()))
                .deleted((short) 0)
                .build();
        lichSuHoaDonRepository.save(lichSu);

        // Clear cart
        gioHangChiTietRepository.deleteByGioHangId(gioHang.getId());
        gioHangRepository.delete(gioHang);

        return mapToHoaDonDetailResponse(hoaDon);
    }

    private void updateCartTotal(Integer cartId) {
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(cartId);
        BigDecimal total = items.stream()
                .map(GioHangChiTiet::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        GioHang cart = gioHangRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giỏ hàng với ID: " + cartId));

        cart.setTongTien(total);
        gioHangRepository.save(cart);
    }

    private CartResponse mapToCartResponse(GioHang cart) {
        System.out.println("=== Mapping cart to response ===");
        System.out.println("Cart ID: " + cart.getId());
        
        List<GioHangChiTiet> items = gioHangChiTietRepository.findByGioHangId(cart.getId());
        System.out.println("Found " + items.size() + " items in cart");
        
        // Also check with simple query
        List<GioHangChiTiet> allItems = gioHangChiTietRepository.findAll();
        System.out.println("Total items in database: " + allItems.size());
        
        // Check items for this specific cart ID
        long itemsForThisCart = allItems.stream()
            .filter(item -> item.getIdGioHang().getId().equals(cart.getId()))
            .count();
        System.out.println("Items for cart ID " + cart.getId() + ": " + itemsForThisCart);
        
        for (GioHangChiTiet item : items) {
            System.out.println("Item: " + item.getId() + " - " + item.getIdChiTietSp().getIdSanPham().getTenSanPham() + " x" + item.getSoLuong());
        }

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
        ChiTietSanPham chiTietSp = item.getIdChiTietSp();
        return CartItemResponse.builder()
                .id(item.getId())
                .idChiTietSanPham(chiTietSp.getId())
                .maSanPham(chiTietSp.getIdSanPham().getMa())
                .tenSanPham(chiTietSp.getIdSanPham().getTenSanPham())
                .tenMauSac(chiTietSp.getIdMauSac().getTenMauSac())
                .hexMauSac(chiTietSp.getIdMauSac().getHex())
                .tenKichCo(chiTietSp.getIdKichCo().getTenKichCo())
                .tenThuongHieu(chiTietSp.getIdSanPham().getIdThuongHieu().getTenThuongHieu())
                .tenDanhMuc(chiTietSp.getIdSanPham().getIdDanhMuc().getTenDanhMuc())
                .urlAnhSanPham(chiTietSp.getIdAnhSanPham() != null ? chiTietSp.getIdAnhSanPham().getUrlAnh() : null)
                .soLuong(item.getSoLuong())
                .gia(item.getGia())
                .thanhTien(item.getThanhTien())
                .soLuongTonKho(chiTietSp.getSoLuongTonKho())
                .createdAt(item.getCreatedAt())
                .build();
    }

    private HoaDonDetailResponse mapToHoaDonDetailResponse(HoaDon hoaDon) {
        HoaDonDetailResponse response = new HoaDonDetailResponse();
        response.setId(hoaDon.getId());
        response.setMaHoaDon(hoaDon.getMa());
        response.setLoaiDon(hoaDon.getLoaiDon());
        response.setTenKhachHang(hoaDon.getTenKhachHang());
        response.setSoDienThoaiKhachHang(hoaDon.getSoDienThoaiKhachHang());
        response.setDiaChiKhachHang(hoaDon.getDiaChiKhachHang());
        response.setEmail(hoaDon.getEmail());
        response.setGhiChu(hoaDon.getGhiChu());
        response.setTienSanPham(hoaDon.getTienSanPham());
        response.setTongTien(hoaDon.getTongTien());
        response.setTongTienSauGiam(hoaDon.getTongTienSauGiam());
        response.setPhiVanChuyen(hoaDon.getPhiVanChuyen());
        response.setTrangThai(hoaDon.getTrangThai());
        response.setNgayTao(hoaDon.getNgayTao());
        response.setNgayThanhToan(hoaDon.getNgayThanhToan());
        
        // Thông tin giảm giá
        if (hoaDon.getIdPhieuGiamGia() != null) {
            response.setMaGiamGia(hoaDon.getIdPhieuGiamGia().getMa());
        }
        
        // Thông tin nhân viên
        if (hoaDon.getIdNhanVien() != null) {
            response.setMaNhanVien(hoaDon.getIdNhanVien().getMa());
        }
        
        return response;
    }

    @Override
    public HoaDonDetailResponse getOrderDetails(Integer hoaDonId) {
        try {
            // Find the invoice
            HoaDon hoaDon = hoaDonRepository.findById(hoaDonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng có ID: " + hoaDonId));

            // Use existing mapping method
            return mapToHoaDonDetailResponse(hoaDon);
        } catch (Exception e) {
            System.err.println("Error getting order details: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể lấy thông tin đơn hàng: " + e.getMessage());
        }
    }

    @Override
    public HoaDonDetailResponse searchOrderByCode(String orderCode) {
        try {
            // Find the invoice by order code
            HoaDon hoaDon = hoaDonRepository.findByMa(orderCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng có mã: " + orderCode));

            // Only return orders that are confirmed (status >= 1) for security
            if (hoaDon.getTrangThai() < 1) {
                throw new ResourceNotFoundException("Đơn hàng chưa được xác nhận");
            }

            // Get basic invoice info
            HoaDonDetailResponse response = mapToHoaDonDetailResponse(hoaDon);
            
            // Get product details from HoaDonChiTiet with eager loading
            List<com.example.sd_28_phostep_be.modal.bill.HoaDonChiTiet> hoaDonChiTiets = 
                hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());
            
            System.out.println("Found " + hoaDonChiTiets.size() + " order items");
            
            if (!hoaDonChiTiets.isEmpty()) {
                List<HoaDonDetailResponse.SanPhamChiTietInfo> sanPhamInfos = new java.util.ArrayList<>();
                
                for (com.example.sd_28_phostep_be.modal.bill.HoaDonChiTiet hdct : hoaDonChiTiets) {
                    HoaDonDetailResponse.SanPhamChiTietInfo info = new HoaDonDetailResponse.SanPhamChiTietInfo();
                    
                    // Basic info
                    info.setHoaDonChiTietId(hdct.getId());
                    info.setIdHoaDon(hdct.getIdHoaDon().getId());
                    info.setSoLuong(hdct.getSoLuong());
                    info.setGiaBan(hdct.getGia());
                    info.setGhiChu(hdct.getGhiChu());
                    
                    // Product details from ChiTietSanPham
                    if (hdct.getIdChiTietSp() != null) {
                        com.example.sd_28_phostep_be.modal.product.ChiTietSanPham ctsp = hdct.getIdChiTietSp();
                        info.setChiTietSanPhamId(ctsp.getId());
                        info.setMaChiTietSanPham(ctsp.getMa());
                        
                        // Product info
                        if (ctsp.getIdSanPham() != null) {
                            info.setIdSanPham(ctsp.getIdSanPham().getId());
                            info.setMaSanPham(ctsp.getIdSanPham().getMa());
                            info.setTenSanPham(ctsp.getIdSanPham().getTenSanPham());
                            info.setMoTaChiTiet(ctsp.getIdSanPham().getMoTaSanPham());
                            
                            // Get image from AnhSanPham relationship
                            if (ctsp.getIdSanPham().getIdAnhSanPham() != null) {
                                String imageUrl = ctsp.getIdSanPham().getIdAnhSanPham().getUrlAnh();
                                System.out.println("Product: " + ctsp.getIdSanPham().getTenSanPham() + " - Image URL: " + imageUrl);
                                info.setDuongDan(imageUrl);
                            } else {
                                System.out.println("Product: " + ctsp.getIdSanPham().getTenSanPham() + " - No image found");
                            }
                        }
                        
                        // Color info
                        if (ctsp.getIdMauSac() != null) {
                            info.setMauSac(ctsp.getIdMauSac().getTenMauSac());
                        }
                        
                        // Size info
                        if (ctsp.getIdKichCo() != null) {
                            info.setKichCo(ctsp.getIdKichCo().getTenKichCo());
                        }
                        
                        // Material info from SanPham
                        if (ctsp.getIdSanPham() != null && ctsp.getIdSanPham().getIdChatLieu() != null) {
                            info.setChatLieu(ctsp.getIdSanPham().getIdChatLieu().getTenChatLieu());
                        }
                    }
                    
                    sanPhamInfos.add(info);
                }
                
                response.setSanPhamChiTietInfos(sanPhamInfos);
            }

            return response;
        } catch (Exception e) {
            System.err.println("Error searching order by code: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể tìm kiếm đơn hàng: " + e.getMessage());
        }
    }
}
