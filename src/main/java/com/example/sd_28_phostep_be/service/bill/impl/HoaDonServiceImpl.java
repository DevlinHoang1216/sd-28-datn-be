package com.example.sd_28_phostep_be.service.bill.impl;

import com.example.sd_28_phostep_be.common.bill.HoaDonDetailMapper;
import com.example.sd_28_phostep_be.common.bill.HoaDonMapper;
import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDTOResponse;
import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDetailResponse;
import com.example.sd_28_phostep_be.dto.bill.request.UpdateCustomerRequest;
import com.example.sd_28_phostep_be.exception.ResourceNotFoundException;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import com.example.sd_28_phostep_be.modal.bill.LichSuHoaDon;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.account.NhanVien.NhanVienRepository;
import com.example.sd_28_phostep_be.repository.bill.HoaDonRepository;
import com.example.sd_28_phostep_be.repository.bill.LichSuHoaDonRepository;
import com.example.sd_28_phostep_be.service.bill.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        newInvoice.setLoaiDon("Tại Quầy");
        
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
        
        return hoaDonRepository.save(invoice);
    }

    @Override
    public void deletePendingInvoice(Integer id) {
        HoaDon invoice = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        
        // Hard delete - remove from database completely
        hoaDonRepository.delete(invoice);
    }


}
