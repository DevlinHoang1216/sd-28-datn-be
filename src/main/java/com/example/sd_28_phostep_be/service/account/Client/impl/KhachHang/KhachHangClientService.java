package com.example.sd_28_phostep_be.service.account.Client.impl.KhachHang;

import com.example.sd_28_phostep_be.dto.account.request.KhachHang.DiaChiKhachHangRequest;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.DiaChiKhachHangResponse;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangProfileResponse;
import com.example.sd_28_phostep_be.dto.statistics.KhachHangOverviewResponse;
import com.example.sd_28_phostep_be.modal.account.DiaChiKhachHang;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.account.TaiKhoan;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import com.example.sd_28_phostep_be.repository.account.DiaChiKhachHangRepository;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.bill.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class KhachHangClientService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private DiaChiKhachHangRepository diaChiKhachHangRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;


    /**
     * Get customer profile by account
     */
    public KhachHangProfileResponse getCustomerProfile(TaiKhoan taiKhoan) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
        
        // If not found by TaiKhoan object, try to find by taiKhoan ID directly
        if (khachHangOpt.isEmpty()) {
            khachHangOpt = khachHangRepository.findByTaiKhoanId(taiKhoan.getId());
            
            if (khachHangOpt.isEmpty()) {
                throw new RuntimeException("Không tìm thấy thông tin khách hàng với taiKhoan ID: " + taiKhoan.getId());
            }
        }
        
        KhachHang khachHang = khachHangOpt.get();
        
        KhachHangProfileResponse response = new KhachHangProfileResponse();
        
        // Set basic customer info with null safety
        response.setId(khachHang.getId());
        response.setMa(khachHang.getMa() != null ? khachHang.getMa() : "");
        response.setTen(khachHang.getTen() != null ? khachHang.getTen() : "");
        response.setGioiTinh(khachHang.getGioiTinh());
        response.setNgaySinh(khachHang.getNgaySinh());
        response.setCccd(khachHang.getCccd() != null ? khachHang.getCccd() : "");
        response.setDeleted(khachHang.getDeleted() != null ? khachHang.getDeleted() : false);
        response.setCreatedAt(khachHang.getCreatedAt());
        response.setUpdatedAt(khachHang.getUpdatedAt());
        
        // Set account info with null safety
        response.setTaiKhoanId(taiKhoan.getId());
        response.setTenDangNhap(taiKhoan.getTenDangNhap() != null ? taiKhoan.getTenDangNhap() : "");
        response.setEmail(taiKhoan.getEmail() != null ? taiKhoan.getEmail() : "");
        response.setSoDienThoai(taiKhoan.getSoDienThoai() != null ? taiKhoan.getSoDienThoai() : "");
        
        try {
            // Get default address
            Optional<DiaChiKhachHang> defaultAddress = diaChiKhachHangRepository.findDefaultByKhachHangId(khachHang.getId());
            
            // Set default address info
            if (defaultAddress.isPresent()) {
                DiaChiKhachHang address = defaultAddress.get();
                response.setDefaultAddressId(address.getId());
                response.setThanhPho(address.getThanhPho() != null ? address.getThanhPho() : "");
                response.setQuan(address.getQuan() != null ? address.getQuan() : "");
                response.setPhuong(address.getPhuong() != null ? address.getPhuong() : "");
                response.setDiaChiCuThe(address.getDiaChiCuThe() != null ? address.getDiaChiCuThe() : "");
            } else {
                response.setDefaultAddressId(null);
                response.setThanhPho("");
                response.setQuan("");
                response.setPhuong("");
                response.setDiaChiCuThe("");
            }
        } catch (Exception e) {
            // Don't throw exception here, just set empty address values
            response.setDefaultAddressId(null);
            response.setThanhPho("");
            response.setQuan("");
            response.setPhuong("");
            response.setDiaChiCuThe("");
        }
        
        return response;
    }

    /**
     * Get all addresses for a customer
     */
    public List<DiaChiKhachHangResponse> getCustomerAddresses(TaiKhoan taiKhoan) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
        
        if (khachHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy thông tin khách hàng");
        }
        
        KhachHang khachHang = khachHangOpt.get();
        List<DiaChiKhachHang> addresses = diaChiKhachHangRepository.findByKhachHangId(khachHang.getId());
        
        return addresses.stream().map(address -> {
            DiaChiKhachHangResponse response = new DiaChiKhachHangResponse();
            response.setId(address.getId());
            response.setMa(address.getMa());
            response.setThanhPho(address.getThanhPho());
            response.setQuan(address.getQuan());
            response.setPhuong(address.getPhuong());
            response.setDiaChiCuThe(address.getDiaChiCuThe());
            response.setMacDinh(address.getMacDinh());
            response.setDeleted(address.getDeleted());
            response.setTenKhachHang(khachHang.getTen());
            response.setSoDienThoai(khachHang.getTaiKhoan().getSoDienThoai());
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * Add new address for customer
     */
    public DiaChiKhachHangResponse addCustomerAddress(TaiKhoan taiKhoan, DiaChiKhachHangRequest request) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
        
        if (khachHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy thông tin khách hàng");
        }
        
        KhachHang khachHang = khachHangOpt.get();
        
        // If setting as default, remove default from other addresses
        if (request.getMacDinh() != null && request.getMacDinh()) {
            List<DiaChiKhachHang> existingAddresses = diaChiKhachHangRepository.findByKhachHangId(khachHang.getId());
            existingAddresses.forEach(addr -> {
                addr.setMacDinh(false);
                diaChiKhachHangRepository.save(addr);
            });
        }
        
        // Generate address code
        String addressCode = generateAddressCode(khachHang.getId());
        
        DiaChiKhachHang newAddress = DiaChiKhachHang.builder()
                .idKhachHang(khachHang)
                .ma(addressCode)
                .thanhPho(request.getThanhPho())
                .quan(request.getQuan())
                .phuong(request.getPhuong())
                .diaChiCuThe(request.getDiaChiCuThe())
                .macDinh(request.getMacDinh() != null ? request.getMacDinh() : false)
                .deleted(false)
                .build();
        
        DiaChiKhachHang savedAddress = diaChiKhachHangRepository.save(newAddress);
        
        DiaChiKhachHangResponse response = new DiaChiKhachHangResponse();
        response.setId(savedAddress.getId());
        response.setMa(savedAddress.getMa());
        response.setThanhPho(savedAddress.getThanhPho());
        response.setQuan(savedAddress.getQuan());
        response.setPhuong(savedAddress.getPhuong());
        response.setDiaChiCuThe(savedAddress.getDiaChiCuThe());
        response.setMacDinh(savedAddress.getMacDinh());
        response.setDeleted(savedAddress.getDeleted());
        response.setTenKhachHang(request.getTenNguoiNhan() != null ? request.getTenNguoiNhan() : khachHang.getTen());
        response.setSoDienThoai(request.getSoDienThoai() != null ? request.getSoDienThoai() : khachHang.getTaiKhoan().getSoDienThoai());
        
        return response;
    }

    /**
     * Update customer address
     */
    public DiaChiKhachHangResponse updateCustomerAddress(TaiKhoan taiKhoan, Integer addressId, DiaChiKhachHangRequest request) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
        
        if (khachHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy thông tin khách hàng");
        }
        
        KhachHang khachHang = khachHangOpt.get();
        
        Optional<DiaChiKhachHang> addressOpt = diaChiKhachHangRepository.findById(addressId);
        if (addressOpt.isEmpty() || !addressOpt.get().getIdKhachHang().getId().equals(khachHang.getId())) {
            throw new RuntimeException("Không tìm thấy địa chỉ hoặc không có quyền truy cập");
        }
        
        DiaChiKhachHang address = addressOpt.get();
        
        // If setting as default, remove default from other addresses
        if (request.getMacDinh() != null && request.getMacDinh() && !address.getMacDinh()) {
            List<DiaChiKhachHang> existingAddresses = diaChiKhachHangRepository.findByKhachHangId(khachHang.getId());
            existingAddresses.forEach(addr -> {
                if (!addr.getId().equals(addressId)) {
                    addr.setMacDinh(false);
                    diaChiKhachHangRepository.save(addr);
                }
            });
        }
        
        // Update address
        address.setThanhPho(request.getThanhPho());
        address.setQuan(request.getQuan());
        address.setPhuong(request.getPhuong());
        address.setDiaChiCuThe(request.getDiaChiCuThe());
        address.setMacDinh(request.getMacDinh() != null ? request.getMacDinh() : address.getMacDinh());
        
        DiaChiKhachHang savedAddress = diaChiKhachHangRepository.save(address);
        
        DiaChiKhachHangResponse response = new DiaChiKhachHangResponse();
        response.setId(savedAddress.getId());
        response.setMa(savedAddress.getMa());
        response.setThanhPho(savedAddress.getThanhPho());
        response.setQuan(savedAddress.getQuan());
        response.setPhuong(savedAddress.getPhuong());
        response.setDiaChiCuThe(savedAddress.getDiaChiCuThe());
        response.setMacDinh(savedAddress.getMacDinh());
        response.setDeleted(savedAddress.getDeleted());
        response.setTenKhachHang(request.getTenNguoiNhan() != null ? request.getTenNguoiNhan() : khachHang.getTen());
        response.setSoDienThoai(request.getSoDienThoai() != null ? request.getSoDienThoai() : khachHang.getTaiKhoan().getSoDienThoai());
        
        return response;
    }

    /**
     * Delete customer address
     */
    public void deleteCustomerAddress(TaiKhoan taiKhoan, Integer addressId) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
        
        if (khachHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy thông tin khách hàng");
        }
        
        KhachHang khachHang = khachHangOpt.get();
        
        Optional<DiaChiKhachHang> addressOpt = diaChiKhachHangRepository.findById(addressId);
        if (addressOpt.isEmpty() || !addressOpt.get().getIdKhachHang().getId().equals(khachHang.getId())) {
            throw new RuntimeException("Không tìm thấy địa chỉ hoặc không có quyền truy cập");
        }
        
        DiaChiKhachHang address = addressOpt.get();
        
        // Cannot delete default address if it's the only one
        List<DiaChiKhachHang> allAddresses = diaChiKhachHangRepository.findByKhachHangId(khachHang.getId());
        if (address.getMacDinh() && allAddresses.size() > 1) {
            // Set another address as default
            Optional<DiaChiKhachHang> nextAddress = allAddresses.stream()
                    .filter(addr -> !addr.getId().equals(addressId))
                    .findFirst();
            if (nextAddress.isPresent()) {
                nextAddress.get().setMacDinh(true);
                diaChiKhachHangRepository.save(nextAddress.get());
            }
        }
        
        address.setDeleted(true);
        diaChiKhachHangRepository.save(address);
    }

    /**
     * Set address as default
     */
    public void setDefaultAddress(TaiKhoan taiKhoan, Integer addressId) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
        
        if (khachHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy thông tin khách hàng");
        }
        
        KhachHang khachHang = khachHangOpt.get();
        
        Optional<DiaChiKhachHang> addressOpt = diaChiKhachHangRepository.findById(addressId);
        if (addressOpt.isEmpty() || !addressOpt.get().getIdKhachHang().getId().equals(khachHang.getId())) {
            throw new RuntimeException("Không tìm thấy địa chỉ hoặc không có quyền truy cập");
        }
        
        // Remove default from all addresses
        List<DiaChiKhachHang> allAddresses = diaChiKhachHangRepository.findByKhachHangId(khachHang.getId());
        allAddresses.forEach(addr -> {
            addr.setMacDinh(addr.getId().equals(addressId));
            diaChiKhachHangRepository.save(addr);
        });
    }

    /**
     * Get customer order history
     */
    public Page<HoaDon> getCustomerOrderHistory(TaiKhoan taiKhoan, Pageable pageable) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
        
        if (khachHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy thông tin khách hàng");
        }
        
        KhachHang khachHang = khachHangOpt.get();
        return hoaDonRepository.findByIdKhachHangOrderByCreatedAtDesc(khachHang, pageable);
    }

    /**
     * Get customer overview/dashboard statistics
     */
    public KhachHangOverviewResponse getCustomerOverview(TaiKhoan taiKhoan) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);
        
        if (khachHangOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy thông tin khách hàng");
        }
        
        KhachHang khachHang = khachHangOpt.get();
        
        // Get order statistics
        List<HoaDon> allOrders = hoaDonRepository.findByIdKhachHang(khachHang);
        
        long totalOrders = allOrders.size();
        long completedOrders = allOrders.stream().filter(order -> order.getTrangThai() == 5).count();
        long processingOrders = allOrders.stream().filter(order -> order.getTrangThai() >= 1 && order.getTrangThai() <= 4).count();
        long cancelledOrders = allOrders.stream().filter(order -> order.getTrangThai() == 6).count();
        
        // Calculate financial statistics
        BigDecimal totalSpent = allOrders.stream()
                .filter(order -> order.getTrangThai() == 5) // Only completed orders
                .map(order -> order.getTongTienSauGiam() != null ? order.getTongTienSauGiam() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageOrderValue = completedOrders > 0 ? 
                totalSpent.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // Get last order info
        Optional<HoaDon> lastOrder = allOrders.stream()
                .max((o1, o2) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()));
        
        String lastOrderDate = null;
        String lastOrderStatus = null;
        BigDecimal lastOrderAmount = BigDecimal.ZERO;
        
        if (lastOrder.isPresent()) {
            HoaDon order = lastOrder.get();
            lastOrderDate = order.getCreatedAt().toString();
            lastOrderStatus = getStatusText(order.getTrangThai());
            lastOrderAmount = order.getTongTienSauGiam() != null ? order.getTongTienSauGiam() : BigDecimal.ZERO;
        }
        
        KhachHangOverviewResponse response = new KhachHangOverviewResponse();
        response.setTotalOrders(totalOrders);
        response.setCompletedOrders(completedOrders);
        response.setProcessingOrders(processingOrders);
        response.setCancelledOrders(cancelledOrders);
        response.setTotalSpent(totalSpent);
        response.setAverageOrderValue(averageOrderValue);
        response.setCustomerName(khachHang.getTen());
        response.setCustomerCode(khachHang.getMa());
        response.setMembershipLevel("Thành viên"); // Default membership level
        response.setLoyaltyPoints(0); // Default loyalty points
        response.setLastOrderDate(lastOrderDate);
        response.setLastOrderStatus(lastOrderStatus);
        response.setLastOrderAmount(lastOrderAmount);
        
        return response;
    }

    private String generateAddressCode(Integer khachHangId) {
        List<DiaChiKhachHang> existingAddresses = diaChiKhachHangRepository.findByKhachHangId(khachHangId);
        int nextNumber = existingAddresses.size() + 1;
        return "DC" + String.format("%06d", khachHangId) + String.format("%03d", nextNumber);
    }

    private String getStatusText(Short status) {
        switch (status) {
            case 0: return "Hóa đơn chờ";
            case 1: return "Chờ xác nhận";
            case 2: return "Chờ xử lý";
            case 3: return "Chờ vận chuyển";
            case 4: return "Đang vận chuyển";
            case 5: return "Đã hoàn thành";
            case 6: return "Đã hủy";
            default: return "Không xác định";
        }
    }
}
