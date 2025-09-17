package com.example.sd_28_phostep_be.service.bill.impl;

import com.example.sd_28_phostep_be.common.bill.HoaDonDetailMapper;
import com.example.sd_28_phostep_be.common.bill.HoaDonMapper;
import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDTOResponse;
import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDetailResponse;
import com.example.sd_28_phostep_be.dto.bill.request.UpdateCustomerRequest;
import com.example.sd_28_phostep_be.dto.sell.request.PaymentRequest;
import com.example.sd_28_phostep_be.dto.sell.response.PaymentResponse;
import com.example.sd_28_phostep_be.exception.ResourceNotFoundException;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import com.example.sd_28_phostep_be.modal.bill.HoaDonChiTiet;
import com.example.sd_28_phostep_be.modal.bill.LichSuHoaDon;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.modal.sell.HinhThucThanhToan;
import com.example.sd_28_phostep_be.modal.sell.PhuongThucThanhToan;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.account.NhanVien.NhanVienRepository;
import com.example.sd_28_phostep_be.repository.bill.HoaDonRepository;
import com.example.sd_28_phostep_be.repository.bill.HoaDonChiTietRepository;
import com.example.sd_28_phostep_be.repository.bill.LichSuHoaDonRepository;
import com.example.sd_28_phostep_be.repository.product.ChiTietSanPhamRepository;
import com.example.sd_28_phostep_be.repository.sell.HinhThucThanhToanRepository;
import com.example.sd_28_phostep_be.repository.sell.PhuongThucThanhToanRepository;
import com.example.sd_28_phostep_be.repository.sale.PhieuGiamGia.PhieuGiamGiaRepository;
import com.example.sd_28_phostep_be.repository.sell.GioHangRepository;
import com.example.sd_28_phostep_be.repository.sell.GioHangChiTietRepository;
import com.example.sd_28_phostep_be.modal.sell.GioHang;
import com.example.sd_28_phostep_be.modal.sell.GioHangChiTiet;
import com.example.sd_28_phostep_be.service.bill.HoaDonService;
import com.example.sd_28_phostep_be.service.sell.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HoaDonServiceImpl implements HoaDonService {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonDetailMapper hoaDonDetailMapper;

    @Autowired
    private HoaDonMapper hoaDonMapper;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private GioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private GioHangService gioHangService;

    @Override
    public Page<HoaDonDTOResponse> getHoaDonAndFilters(String keyword, Long minAmount, Long maxAmount, Timestamp startDate, Timestamp endDate, Short trangThai, String loaiDon, Pageable pageable) {
        return hoaDonRepository.getAllHoaDon(keyword, minAmount, maxAmount, startDate, endDate, trangThai, false, loaiDon, pageable);
    }

    @Override
    public HoaDonDetailResponse getHoaDonDetail(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findHoaDonDetailById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại hoặc đã bị xóa"));

        List<HoaDonDetailResponse.SanPhamChiTietInfo> sanPhamChiTietInfos = hoaDon.getChiTietHoaDon()
                .stream()
                .map(hoaDonDetailMapper::mapToSanPhamChiTietInfo)
                .collect(Collectors.toList());

        List<HoaDonDetailResponse.ThanhToanInfo> thanhToanInfos = hoaDon.getHinhThucThanhToan()
                .stream()
                .map(hoaDonDetailMapper::mapToThanhToanInfo)
                .collect(Collectors.toList());

        List<HoaDonDetailResponse.LichSuHoaDonInfo> lichSuHoaDonInfos = hoaDon.getLichSuHoaDon()
                .stream()
                .map(hoaDonDetailMapper::mapToLichSuHoaDonInfo)
                .collect(Collectors.toList());

        return new HoaDonDetailResponse.Builder()
                .withHoaDonInfo(hoaDon, hoaDon.getIdPhieuGiamGia())
                .withNhanVienInfo(hoaDon.getIdNhanVien())
                .withThanhToanInfos(thanhToanInfos)
                .withSanPhamChiTietInfos(sanPhamChiTietInfos)
                .withLichSuHoaDonInfos(lichSuHoaDonInfos)
                .build();
    }

    @Override
    public Map<String, Long> getStatusCounts() {
        List<HoaDon> allInvoices = hoaDonRepository.findAll().stream()
                .filter(hoaDon -> hoaDon.getDeleted() == null || !hoaDon.getDeleted())
                .collect(Collectors.toList());

        Map<String, Long> statusCounts = new HashMap<>();
        // Initialize with numeric keys as strings to match frontend expectations
        statusCounts.put("0", 0L); // Hóa đơn chờ
        statusCounts.put("1", 0L); // Chờ xác nhận
        statusCounts.put("2", 0L); // Chờ xử lý
        statusCounts.put("3", 0L); // Chờ vận chuyển
        statusCounts.put("4", 0L); // Đang vận chuyển
        statusCounts.put("5", 0L); // Đã hoàn thành
        statusCounts.put("6", 0L); // Đã hủy

        for (HoaDon hoaDon : allInvoices) {
            String statusKey = String.valueOf(hoaDon.getTrangThai());
            if (statusCounts.containsKey(statusKey)) {
                statusCounts.put(statusKey, statusCounts.get(statusKey) + 1);
            }
        }

        return statusCounts;
    }

    @Override
    public Map<String, Long> getPriceRange() {
        Long minPrice = hoaDonRepository.findMinPrice();
        Long maxPrice = hoaDonRepository.findMaxPrice();
        
        Map<String, Long> priceRange = new HashMap<>();
        priceRange.put("minPrice", minPrice != null ? minPrice : 0L);
        priceRange.put("maxPrice", maxPrice != null ? maxPrice : 0L);
        
        return priceRange;
    }

    private boolean isValidTrangThai(Short trangThai) {
        return trangThai >= 0 && trangThai <= 6;
    }


    @Override
    public synchronized HoaDonDTOResponse updateHoaDonStatus(Integer id, Short trangThai, Integer idNhanVien) {
        return updateHoaDonStatusWithNote(id, trangThai, idNhanVien, null);
    }

    @Override
    public synchronized HoaDonDTOResponse updateHoaDonStatusWithNote(Integer id, Short trangThai, Integer idNhanVien, String ghiChu) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại hoặc đã bị xóa"));

        // If the invoice was in a pending state (deleted = true), mark it as active.
        if (hoaDon.getDeleted()) {
            hoaDon.setDeleted(false);
        }

        if (!isValidTrangThai(trangThai)) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        hoaDon.setTrangThai(trangThai);

        LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
        lichSuHoaDon.setMa("LSHD_" + System.currentTimeMillis());
        
        // Create action description with note if provided
        String hanhDong = "Cập nhật trạng thái: " + mapStatusToString(trangThai);
        if (ghiChu != null && !ghiChu.trim().isEmpty()) {
            hanhDong += " - Ghi chú: " + ghiChu.trim();
        }
        lichSuHoaDon.setHanhDong(hanhDong);
        
        lichSuHoaDon.setThoiGian(Instant.now());
        lichSuHoaDon.setIdNhanVien(idNhanVien != null ?
                nhanVienRepository.findById(idNhanVien)
                        .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"))
                : null);
        lichSuHoaDon.setIdHoaDon(hoaDon);
        // Lưu trạng thái mới sau khi chuyển để timeline có thể filter chính xác
        lichSuHoaDon.setDeleted(trangThai);

        lichSuHoaDonRepository.save(lichSuHoaDon);
        hoaDonRepository.save(hoaDon);

        return hoaDonMapper.mapToDto(hoaDon);
    }

    @Override
    public HoaDonDTOResponse getHoaDonByMa(String maHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findByMa(maHoaDon)
                .orElseThrow(() -> new ResourceNotFoundException("Hóa đơn không tồn tại với mã: " + maHoaDon));
        
        return hoaDonMapper.mapToDto(hoaDon);
    }

    private String mapStatusToString(Short trangThai) {
        switch (trangThai) {
            case 0: return "Hóa đơn chờ";
            case 1: return "Chờ xác nhận";
            case 2: return "Chờ xử lý";
            case 3: return "Chờ vận chuyển";
            case 4: return "Đang vận chuyển";
            case 5: return "Đã hoàn thành";
            case 6: return "Đã hủy";
            default: return "N/A";
        }
    }

    // Sales counter specific methods implementation
    @Override
    public List<HoaDon> getPendingInvoicesForSales() {
        return hoaDonRepository.findPendingInvoicesForSales();
    }

    @Override
    public HoaDon createPendingInvoice() {
        HoaDon newInvoice = new HoaDon();
        
        // Don't set ma - let database auto-generate it
        // newInvoice.setMa() is not called - database will handle auto-increment
        
        // Set default values for pending invoice
        newInvoice.setTrangThai((short) 0); // Status = 0 (pending for sales counter)
        newInvoice.setDeleted(true); // Set deleted = 1 for pending invoices
        newInvoice.setCreatedAt(Instant.now());
        newInvoice.setNgayTao(Date.valueOf(LocalDate.now()));
        
        // Set default amounts
        newInvoice.setTienSanPham(BigDecimal.ZERO);
        newInvoice.setPhiVanChuyen(BigDecimal.ZERO);
        newInvoice.setTongTien(BigDecimal.ZERO);
        newInvoice.setTongTienSauGiam(BigDecimal.ZERO);
        
        // Set default customer info (guest customer)
        newInvoice.setTenKhachHang("Khách lẻ");
        newInvoice.setDiaChiKhachHang("");
        newInvoice.setSoDienThoaiKhachHang("");
        newInvoice.setLoaiDon("Tại quầy");
        
        // Set default customer ID = 1 (required by database)
        // Get the default customer entity with ID = 1
        newInvoice.setIdKhachHang(khachHangRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Default customer with ID 1 not found")));
        
        // Set default employee ID = 1 (required by database)
        // Get the default employee entity with ID = 1
        newInvoice.setIdNhanVien(nhanVienRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Default employee with ID 1 not found")));
        
        // Save the invoice first
        HoaDon savedInvoice = hoaDonRepository.save(newInvoice);
        
        // Force flush to ensure database operations are completed
        hoaDonRepository.flush();
        
        // Reload from database to get the auto-generated ma field
        HoaDon reloadedInvoice = hoaDonRepository.findById(savedInvoice.getId())
                .orElseThrow(() -> new RuntimeException("Failed to reload saved invoice"));
        
        // If ma is still null, generate it manually based on ID
        if (reloadedInvoice.getMa() == null || reloadedInvoice.getMa().isEmpty()) {
            String generatedMa = "HD" + String.format("%06d", reloadedInvoice.getId());
            reloadedInvoice.setMa(generatedMa);
            reloadedInvoice = hoaDonRepository.save(reloadedInvoice);
        }
        
        return reloadedInvoice;
    }

    @Override
    @Transactional
    public HoaDon updatePendingInvoiceCustomer(Integer id, UpdateCustomerRequest request) {
        HoaDon invoice = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        
        // Update customer information
        if (request.getKhachHangId() != null) {
            invoice.setIdKhachHang(khachHangRepository.findById(request.getKhachHangId())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại")));
        }
        
        if (request.getTenKhachHang() != null) {
            invoice.setTenKhachHang(request.getTenKhachHang());
        }
        
        if (request.getSoDienThoaiKhachHang() != null) {
            invoice.setSoDienThoaiKhachHang(request.getSoDienThoaiKhachHang());
        }
        
        if (request.getDiaChiKhachHang() != null) {
            invoice.setDiaChiKhachHang(request.getDiaChiKhachHang());
        }
        
        if (request.getEmail() != null) {
            invoice.setEmail(request.getEmail());
        }
        
        // Update timestamp
        invoice.setUpdatedAt(Instant.now());
        
        // Save invoice first
        HoaDon savedInvoice = hoaDonRepository.save(invoice);
        
        // Synchronize cart customer ID with invoice customer ID
        gioHangService.updateCartCustomer(id, request.getKhachHangId());
        
        return savedInvoice;
    }

    @Override
    @Transactional
    public void deletePendingInvoice(Integer id) {
        try {
            HoaDon invoice = hoaDonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
            
            // Only allow deletion of pending invoices (status = 0)
            if (invoice.getTrangThai() != 0) {
                throw new RuntimeException("Chỉ có thể xóa hóa đơn đang chờ");
            }
            
            // Get all cart items for this invoice
            List<HoaDonChiTiet> cartItems = hoaDonChiTietRepository.findAllByHoaDonId(id);
            
            // Restore product quantities for each cart item before deletion
            for (HoaDonChiTiet cartItem : cartItems) {
                ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
                if (chiTietSanPham != null) {
                    // Restore the quantity back to inventory
                    Integer currentStock = chiTietSanPham.getSoLuongTonKho();
                    Integer quantityToRestore = cartItem.getSoLuong();
                    chiTietSanPham.setSoLuongTonKho(currentStock + quantityToRestore);
                    
                    // Save the updated product detail
                    chiTietSanPhamRepository.save(chiTietSanPham);
                }
            }
            
            // Delete cart items after restoring quantities
            if (!cartItems.isEmpty()) {
                hoaDonChiTietRepository.deleteAll(cartItems);
            }
            
            // Delete any GioHang records that reference this invoice
            gioHangRepository.findByHoaDonId(id).ifPresent(gioHang -> {
                // First, get all cart details for this cart and restore product quantities
                List<GioHangChiTiet> gioHangChiTietList = gioHangChiTietRepository.findByGioHangId(gioHang.getId());
                
                // Restore product quantities from cart details
                for (GioHangChiTiet gioHangChiTiet : gioHangChiTietList) {
                    ChiTietSanPham chiTietSanPham = gioHangChiTiet.getIdChiTietSp();
                    if (chiTietSanPham != null) {
                        // Restore the quantity back to inventory
                        Integer currentStock = chiTietSanPham.getSoLuongTonKho();
                        Integer quantityToRestore = gioHangChiTiet.getSoLuong();
                        chiTietSanPham.setSoLuongTonKho(currentStock + quantityToRestore);
                        
                        // Save the updated product detail
                        chiTietSanPhamRepository.save(chiTietSanPham);
                    }
                }
                
                // Delete cart details first
                if (!gioHangChiTietList.isEmpty()) {
                    gioHangChiTietRepository.deleteAll(gioHangChiTietList);
                }
                
                // Then delete the cart
                gioHangRepository.delete(gioHang);
            });
            
            // Finally delete the invoice
            hoaDonRepository.delete(invoice);
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa hóa đơn: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        try {
            // 1. Validate and get the pending invoice
            HoaDon hoaDon = hoaDonRepository.findById(paymentRequest.getHoaDonId())
                    .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

            if (hoaDon.getTrangThai() != 0) {
                return PaymentResponse.error("Hóa đơn đã được thanh toán hoặc không ở trạng thái chờ");
            }

            // 2. Validate payment amounts
            BigDecimal tongTienSauGiam = paymentRequest.getTongTienSauGiam() != null ? 
                paymentRequest.getTongTienSauGiam() : paymentRequest.getTongTien();
            BigDecimal totalPayment = paymentRequest.getTienMat().add(paymentRequest.getTienChuyenKhoan());
            if (totalPayment.compareTo(tongTienSauGiam) < 0) {
                return PaymentResponse.error("Số tiền thanh toán không đủ");
            }

            // 3. Calculate change for cash payments
            BigDecimal tienThua = BigDecimal.ZERO;
            if ("Tiền mặt".equals(paymentRequest.getPhuongThucThanhToan())) {
                tienThua = paymentRequest.getTienMat().subtract(tongTienSauGiam);
                if (tienThua.compareTo(BigDecimal.ZERO) < 0) {
                    return PaymentResponse.error("Số tiền mặt không đủ để thanh toán");
                }
            }

            // 4. Update invoice information
            hoaDon.setTrangThai((short) 5); // Đã hoàn thành
            hoaDon.setDeleted(false); // Mark as active invoice
            hoaDon.setTienSanPham(paymentRequest.getTongTien()); // Original product total
            hoaDon.setPhiVanChuyen(paymentRequest.getPhiVanChuyen());
            hoaDon.setTongTien(paymentRequest.getTongTien()); // Original total before discount
            hoaDon.setTongTienSauGiam(tongTienSauGiam); // Total after discount
            hoaDon.setUpdatedAt(Instant.now());

            // Set employee
            hoaDon.setIdNhanVien(nhanVienRepository.findById(paymentRequest.getNhanVienId())
                    .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại")));

            // Apply voucher if provided
            if (paymentRequest.getPhieuGiamGiaId() != null) {
                PhieuGiamGia voucher = phieuGiamGiaRepository.findById(paymentRequest.getPhieuGiamGiaId())
                        .orElseThrow(() -> new RuntimeException("Phiếu giảm giá không tồn tại"));
                hoaDon.setIdPhieuGiamGia(voucher);
            }

            // 5. Create invoice details (HoaDonChiTiet) from actual cart data
            // First, get the actual cart data to ensure consistency
            Optional<GioHang> gioHangOpt = gioHangRepository.findByHoaDonId(hoaDon.getId());
            if (gioHangOpt.isPresent()) {
                List<GioHangChiTiet> gioHangChiTietList = gioHangChiTietRepository.findByGioHangId(gioHangOpt.get().getId());
                
                for (GioHangChiTiet cartItem : gioHangChiTietList) {
                    ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
                    
                    // Create invoice detail using cart data (preserves original price and quantity)
                    // Stock was already reduced when items were added to cart, so no need to reduce again
                    HoaDonChiTiet hoaDonChiTiet = HoaDonChiTiet.builder()
                            .idHoaDon(hoaDon)
                            .idChiTietSp(chiTietSanPham)
                            .gia(cartItem.getGia()) // Use price from cart, not current product price
                            .soLuong(cartItem.getSoLuong()) // Use quantity from cart
                            .trangThai((short) 1)
                            .build();

                    hoaDonChiTietRepository.save(hoaDonChiTiet);
                    
                    // NOTE: Do NOT reduce stock here as it was already reduced when adding to cart
                    // Stock reduction happens when items are added to cart, not during payment
                }
                
                // Clean up cart data immediately after creating invoice details
                // This ensures all data is transferred to HoaDonChiTiet before deletion
                gioHangChiTietRepository.deleteAll(gioHangChiTietList);
                gioHangRepository.delete(gioHangOpt.get());
                
            } else {
                return PaymentResponse.error("Không tìm thấy giỏ hàng cho hóa đơn này");
            }

            // 6. Get existing payment method by type
            PhuongThucThanhToan phuongThuc = phuongThucThanhToanRepository
                    .findByKieuThanhToan(paymentRequest.getPhuongThucThanhToan())
                    .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không tồn tại: " + paymentRequest.getPhuongThucThanhToan()));

            HinhThucThanhToan hinhThucThanhToan = HinhThucThanhToan.builder()
                    .idHoaDon(hoaDon)
                    .idPhuongThucThanhToan(phuongThuc)
                    .tienMat(paymentRequest.getTienMat())
                    .tienChuyenKhoan(paymentRequest.getTienChuyenKhoan())
                    .deleted(false)
                    .build();

            hinhThucThanhToanRepository.save(hinhThucThanhToan);

            // 7. Create payment history record (LichSuHoaDon)
            LichSuHoaDon lichSuHoaDon = new LichSuHoaDon();
            lichSuHoaDon.setHanhDong("Thanh toán thành công - " + paymentRequest.getPhuongThucThanhToan());
            lichSuHoaDon.setThoiGian(Instant.now());
            lichSuHoaDon.setIdNhanVien(nhanVienRepository.findById(paymentRequest.getNhanVienId()).orElse(null));
            lichSuHoaDon.setIdHoaDon(hoaDon);
            lichSuHoaDon.setDeleted((short) 5); // Status = 5 (completed)

            if (paymentRequest.getGhiChu() != null && !paymentRequest.getGhiChu().trim().isEmpty()) {
                lichSuHoaDon.setHanhDong(lichSuHoaDon.getHanhDong() + " - Ghi chú: " + paymentRequest.getGhiChu().trim());
            }

            lichSuHoaDonRepository.save(lichSuHoaDon);

            // 8. Cart cleanup already handled in step 5 after creating HoaDonChiTiet
            // This ensures data consistency and prevents issues with missing cart data

            // 9. Save the updated invoice
            HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

            // 10. Return success response
            return PaymentResponse.success(
                    savedHoaDon.getId(),
                    savedHoaDon.getMa(),
                    paymentRequest.getTongTien(),
                    tienThua,
                    paymentRequest.getPhuongThucThanhToan()
            );

        } catch (Exception e) {
            return PaymentResponse.error("Lỗi xử lý thanh toán: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateInvoiceStatusAfterPayment(Integer invoiceId, Integer status) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(invoiceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + invoiceId));

            if (status == 1) {
                // Payment successful - process like cash payment
                // 1. Update invoice status to completed
                hoaDon.setTrangThai((short) 5); // Đã hoàn thành
                hoaDon.setDeleted(false); // Mark as active invoice
                hoaDon.setUpdatedAt(Instant.now());

                // 2. Create invoice details (HoaDonChiTiet) from cart data
                Optional<GioHang> gioHangOpt = gioHangRepository.findByHoaDonId(hoaDon.getId());
                if (gioHangOpt.isPresent()) {
                    List<GioHangChiTiet> gioHangChiTietList = gioHangChiTietRepository.findByGioHangId(gioHangOpt.get().getId());
                    
                    BigDecimal tongTien = BigDecimal.ZERO;
                    
                    for (GioHangChiTiet cartItem : gioHangChiTietList) {
                        ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
                        
                        // Create invoice detail using cart data
                        HoaDonChiTiet hoaDonChiTiet = HoaDonChiTiet.builder()
                                .idHoaDon(hoaDon)
                                .idChiTietSp(chiTietSanPham)
                                .gia(cartItem.getGia())
                                .soLuong(cartItem.getSoLuong())
                                .trangThai((short) 1)
                                .build();

                        hoaDonChiTietRepository.save(hoaDonChiTiet);
                        
                        // Calculate total
                        tongTien = tongTien.add(cartItem.getGia().multiply(BigDecimal.valueOf(cartItem.getSoLuong())));
                    }
                    
                    // Update invoice totals
                    hoaDon.setTienSanPham(tongTien);
                    hoaDon.setTongTien(tongTien);
                    hoaDon.setTongTienSauGiam(tongTien);
                    
                    // 3. Create VNPay payment record
                    PhuongThucThanhToan vnpayMethod = phuongThucThanhToanRepository
                            .findByKieuThanhToan("VnPay")
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán VnPay"));

                    HinhThucThanhToan hinhThucThanhToan = HinhThucThanhToan.builder()
                            .idHoaDon(hoaDon)
                            .idPhuongThucThanhToan(vnpayMethod)
                            .tienMat(BigDecimal.ZERO)
                            .tienChuyenKhoan(tongTien)
                            .deleted(false)
                            .build();

                    hinhThucThanhToanRepository.save(hinhThucThanhToan);
                    
                    // 4. Clean up cart data after successful payment
                    gioHangChiTietRepository.deleteAll(gioHangChiTietList);
                    gioHangRepository.delete(gioHangOpt.get());
                    
                } else {
                    throw new RuntimeException("Không tìm thấy giỏ hàng cho hóa đơn này");
                }
            } else {
                // Payment failed - update status to cancelled
                hoaDon.setTrangThai((short) 4); // Đã hủy
                hoaDon.setUpdatedAt(Instant.now());
            }

            hoaDonRepository.save(hoaDon);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật trạng thái hóa đơn: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void updateInvoiceStatusAfterVNPayPayment(Integer invoiceId, Integer status, Long vnpayAmount) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(invoiceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + invoiceId));

            if (status == 1) {
                // Payment successful - process like cash payment
                // 1. Update invoice status to completed
                hoaDon.setTrangThai((short) 5); // Đã hoàn thành
                hoaDon.setDeleted(false); // Mark as active invoice
                hoaDon.setUpdatedAt(Instant.now());

                // 2. Create invoice details (HoaDonChiTiet) from cart data
                Optional<GioHang> gioHangOpt = gioHangRepository.findByHoaDonId(hoaDon.getId());
                if (gioHangOpt.isPresent()) {
                    List<GioHangChiTiet> gioHangChiTietList = gioHangChiTietRepository.findByGioHangId(gioHangOpt.get().getId());
                    
                    for (GioHangChiTiet cartItem : gioHangChiTietList) {
                        ChiTietSanPham chiTietSanPham = cartItem.getIdChiTietSp();
                        
                        // Create invoice detail using cart data
                        HoaDonChiTiet hoaDonChiTiet = HoaDonChiTiet.builder()
                                .idHoaDon(hoaDon)
                                .idChiTietSp(chiTietSanPham)
                                .gia(cartItem.getGia())
                                .soLuong(cartItem.getSoLuong())
                                .trangThai((short) 1)
                                .build();

                        hoaDonChiTietRepository.save(hoaDonChiTiet);
                    }
                    
                    // Update invoice totals with VNPay amount
                    BigDecimal vnpayAmountBD = BigDecimal.valueOf(vnpayAmount);
                    hoaDon.setTienSanPham(vnpayAmountBD);
                    hoaDon.setTongTien(vnpayAmountBD);
                    hoaDon.setTongTienSauGiam(vnpayAmountBD);
                    
                    // 3. Create VNPay payment record with actual VNPay amount
                    PhuongThucThanhToan vnpayMethod = phuongThucThanhToanRepository
                            .findByKieuThanhToan("VnPay")
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán VnPay"));

                    HinhThucThanhToan hinhThucThanhToan = HinhThucThanhToan.builder()
                            .idHoaDon(hoaDon)
                            .idPhuongThucThanhToan(vnpayMethod)
                            .tienMat(BigDecimal.ZERO)
                            .tienChuyenKhoan(vnpayAmountBD) // Use actual VNPay amount
                            .deleted(false)
                            .build();

                    hinhThucThanhToanRepository.save(hinhThucThanhToan);
                    
                    // 4. Clean up cart data after successful payment
                    gioHangChiTietRepository.deleteAll(gioHangChiTietList);
                    gioHangRepository.delete(gioHangOpt.get());
                    
                } else {
                    throw new RuntimeException("Không tìm thấy giỏ hàng cho hóa đơn này");
                }
            } else {
                // Payment failed - update status to cancelled
                hoaDon.setTrangThai((short) 4); // Đã hủy
                hoaDon.setUpdatedAt(Instant.now());
            }

            hoaDonRepository.save(hoaDon);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật trạng thái hóa đơn VNPay: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void processCashPayment(Integer invoiceId, BigDecimal cashAmount) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(invoiceId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + invoiceId));

            // Find cash payment method
            PhuongThucThanhToan cashPaymentMethod = phuongThucThanhToanRepository.findByKieuThanhToan("Tiền mặt")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán tiền mặt"));

            // Create cash payment record
            HinhThucThanhToan cashPayment = new HinhThucThanhToan();
            cashPayment.setIdHoaDon(hoaDon);
            cashPayment.setIdPhuongThucThanhToan(cashPaymentMethod);
            cashPayment.setTienMat(cashAmount);
            cashPayment.setTienChuyenKhoan(BigDecimal.ZERO);
            cashPayment.setDeleted(false);

            hinhThucThanhToanRepository.save(cashPayment);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xử lý thanh toán tiền mặt: " + e.getMessage(), e);
        }
    }

}
