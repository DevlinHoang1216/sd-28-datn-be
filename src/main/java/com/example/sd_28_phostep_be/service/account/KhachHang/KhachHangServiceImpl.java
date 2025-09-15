package com.example.sd_28_phostep_be.service.account.KhachHang;

import com.example.sd_28_phostep_be.dto.account.request.KhachHang.KhachHangCreateRequest;
import com.example.sd_28_phostep_be.dto.account.request.KhachHang.KhachHangUpdateRequest;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDTOResponse;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDetailResponse;
import com.example.sd_28_phostep_be.modal.account.DiaChiKhachHang;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.account.QuyenHan;
import com.example.sd_28_phostep_be.modal.account.TaiKhoan;
import com.example.sd_28_phostep_be.repository.account.DiaChiKhachHangRepository;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.account.QuyenHanRepository;
import com.example.sd_28_phostep_be.repository.account.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final QuyenHanRepository quyenHanRepository;
    private final DiaChiKhachHangRepository diaChiKhachHangRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<KhachHangDTOResponse> getAllKhachHang(String keyword, Instant keywordAsDate, Short gioiTinh, Boolean trangThai, Pageable pageable) {
        // Convert trangThai (active status) to deleted status for query
        // trangThai = true (active) -> deleted = true
        // trangThai = false (inactive) -> deleted = false
        Boolean deletedStatus = trangThai;
        return khachHangRepository.getAllKH(keyword, keywordAsDate, gioiTinh, deletedStatus, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhachHangDTOResponse> getAllActiveKhachHang() {
        // Get all active customers (deleted = true in this system's logic)
        return khachHangRepository.findAllActiveKhachHang();
    }

    @Override
    @Transactional(readOnly = true)
    public KhachHangDetailResponse getKhachHangById(Integer id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
        
        return mapToDetailResponse(khachHang);
    }

    @Override
    public KhachHangDetailResponse createKhachHang(KhachHangCreateRequest request) {
        // Validate input
        validateCreateRequest(request);
        
        // Debug logging
        System.out.println("=== DEBUG CREATE KHACH HANG ===");
        System.out.println("Email received: " + request.getEmail());
        System.out.println("Ten: " + request.getTen());
        System.out.println("SoDienThoai: " + request.getSoDienThoai());
        
        // Check if email or phone already exists
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (taiKhoanRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email đã tồn tại trong hệ thống");
            }
        }
        
        if (taiKhoanRepository.existsBySoDienThoai(request.getSoDienThoai())) {
            throw new RuntimeException("Số điện thoại đã tồn tại trong hệ thống");
        }
        
        // Create TaiKhoan first - customer role ID is 3
        QuyenHan quyenHan = quyenHanRepository.findById(3)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền hạn khách hàng (ID=3)"));
        
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .ma(generateTaiKhoanMa())
                .tenDangNhap(generateTenDangNhap(request.getTen(), request.getNgaySinh()))
                .email(request.getEmail())
                .soDienThoai(request.getSoDienThoai())
                .matKhau(request.getMatKhau()) // Should be encoded in real application
                .idQuyenHan(quyenHan)
                .deleted(false)
                .build();
        
        System.out.println("TaiKhoan before save - Email: " + taiKhoan.getEmail());
        taiKhoan = taiKhoanRepository.save(taiKhoan);
        System.out.println("TaiKhoan after save - Email: " + taiKhoan.getEmail());
        
        // Create KhachHang
        KhachHang khachHang = KhachHang.builder()
                .taiKhoan(taiKhoan)
                .ma(generateKhachHangMa())
                .ten(request.getTen())
                .gioiTinh(request.getGioiTinh())
                .ngaySinh(request.getNgaySinh())
                .cccd(request.getCccd())
                .deleted(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        khachHang = khachHangRepository.save(khachHang);
        
        // Create default address if any address field is provided
        if (hasAddressInfo(request)) {
            DiaChiKhachHang diaChi = DiaChiKhachHang.builder()
                    .idKhachHang(khachHang)
                    .ma(generateDiaChiMa())
                    .thanhPho(request.getThanhPho() != null ? request.getThanhPho() : "")
                    .quan(request.getQuan() != null ? request.getQuan() : "")
                    .phuong(request.getPhuong() != null ? request.getPhuong() : "")
                    .diaChiCuThe(request.getDiaChiCuThe() != null ? request.getDiaChiCuThe() : "")
                    .macDinh(true)
                    .deleted(false)
                    .build();
            
            // Save the address first
            diaChi = diaChiKhachHangRepository.save(diaChi);
            
            // Then update the customer with the saved address
            khachHang.setIdDiaChiKhachHang(diaChi);
            khachHang = khachHangRepository.save(khachHang);
        }
        
        return mapToDetailResponse(khachHang);
    }
    
    private void validateCreateRequest(KhachHangCreateRequest request) {
        if (request.getTen() == null || request.getTen().trim().isEmpty()) {
            throw new RuntimeException("Tên khách hàng không được để trống");
        }
        
        if (request.getSoDienThoai() == null || request.getSoDienThoai().trim().isEmpty()) {
            throw new RuntimeException("Số điện thoại không được để trống");
        }
        
        if (!request.getSoDienThoai().matches("^0\\d{9}$")) {
            throw new RuntimeException("Số điện thoại phải bắt đầu bằng 0 và có đúng 10 chữ số");
        }
        
        if (request.getGioiTinh() == null) {
            throw new RuntimeException("Giới tính không được để trống");
        }
        
        if (request.getNgaySinh() == null) {
            throw new RuntimeException("Ngày sinh không được để trống");
        }
        
        if (request.getMatKhau() == null || request.getMatKhau().trim().isEmpty()) {
            throw new RuntimeException("Mật khẩu không được để trống");
        }
    }
    
    private boolean hasAddressInfo(KhachHangCreateRequest request) {
        return (request.getThanhPho() != null && !request.getThanhPho().trim().isEmpty()) ||
               (request.getQuan() != null && !request.getQuan().trim().isEmpty()) ||
               (request.getPhuong() != null && !request.getPhuong().trim().isEmpty()) ||
               (request.getDiaChiCuThe() != null && !request.getDiaChiCuThe().trim().isEmpty());
    }

    @Override
    public KhachHangDetailResponse updateKhachHang(Integer id, KhachHangUpdateRequest request) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
        
        // Update KhachHang fields
        if (request.getTen() != null) {
            khachHang.setTen(request.getTen());
        }
        if (request.getGioiTinh() != null) {
            khachHang.setGioiTinh(request.getGioiTinh());
        }
        if (request.getNgaySinh() != null) {
            khachHang.setNgaySinh(request.getNgaySinh());
        }
        if (request.getCccd() != null) {
            khachHang.setCccd(request.getCccd());
        }
        if (request.getDeleted() != null) {
            khachHang.setDeleted(request.getDeleted());
        }
        
        khachHang.setUpdatedAt(Instant.now());
        
        // Update TaiKhoan fields
        TaiKhoan taiKhoan = khachHang.getTaiKhoan();
        if (taiKhoan != null) {
            if (request.getSoDienThoai() != null) {
                taiKhoan.setSoDienThoai(request.getSoDienThoai());
            }
            if (request.getEmail() != null) {
                taiKhoan.setEmail(request.getEmail());
            }
            if (request.getDeleted() != null) {
                taiKhoan.setDeleted(request.getDeleted());
            }
            taiKhoanRepository.save(taiKhoan);
        }
        
        // Update address if provided
        if (request.getThanhPho() != null || request.getQuan() != null || 
            request.getPhuong() != null || request.getDiaChiCuThe() != null) {
            
            DiaChiKhachHang diaChi = khachHang.getIdDiaChiKhachHang();
            if (diaChi != null) {
                // Update existing address
                if (request.getThanhPho() != null) {
                    diaChi.setThanhPho(request.getThanhPho());
                }
                if (request.getQuan() != null) {
                    diaChi.setQuan(request.getQuan());
                }
                if (request.getPhuong() != null) {
                    diaChi.setPhuong(request.getPhuong());
                }
                if (request.getDiaChiCuThe() != null) {
                    diaChi.setDiaChiCuThe(request.getDiaChiCuThe());
                }
                diaChiKhachHangRepository.save(diaChi);
            } else {
                // Create new address if customer doesn't have one
                DiaChiKhachHang newDiaChi = DiaChiKhachHang.builder()
                        .idKhachHang(khachHang)
                        .ma(generateDiaChiMa())
                        .thanhPho(request.getThanhPho())
                        .quan(request.getQuan())
                        .phuong(request.getPhuong())
                        .diaChiCuThe(request.getDiaChiCuThe())
                        .macDinh(true)
                        .deleted(false)
                        .build();
                
                newDiaChi = diaChiKhachHangRepository.save(newDiaChi);
                khachHang.setIdDiaChiKhachHang(newDiaChi);
            }
        }
        
        khachHang = khachHangRepository.save(khachHang);
        
        return mapToDetailResponse(khachHang);
    }

    @Override
    public void deleteKhachHang(Integer id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
        
        khachHang.setDeleted(false);
        khachHang.setUpdatedAt(Instant.now());
        
        // Also soft delete the account
        TaiKhoan taiKhoan = khachHang.getTaiKhoan();
        if (taiKhoan != null) {
            taiKhoan.setDeleted(false);
            taiKhoanRepository.save(taiKhoan);
        }
        
        khachHangRepository.save(khachHang);
    }

    @Override
    public void restoreKhachHang(Integer id) {
        KhachHang khachHang = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
        
        khachHang.setDeleted(true);
        khachHang.setUpdatedAt(Instant.now());
        
        // Also restore the account
        TaiKhoan taiKhoan = khachHang.getTaiKhoan();
        if (taiKhoan != null) {
            taiKhoan.setDeleted(true);
            taiKhoanRepository.save(taiKhoan);
        }
        
        khachHangRepository.save(khachHang);
    }

    private KhachHangDetailResponse mapToDetailResponse(KhachHang khachHang) {
        KhachHangDetailResponse response = new KhachHangDetailResponse();
        response.setId(khachHang.getId());
        response.setMa(khachHang.getMa());
        response.setTen(khachHang.getTen());
        response.setGioiTinh(khachHang.getGioiTinh());
        response.setNgaySinh(khachHang.getNgaySinh());
        response.setCccd(khachHang.getCccd());
        response.setDeleted(khachHang.getDeleted());
        response.setCreatedAt(khachHang.getCreatedAt());
        response.setUpdatedAt(khachHang.getUpdatedAt());
        response.setCreatedBy(khachHang.getCreatedBy());
        response.setUpdatedBy(khachHang.getUpdatedBy());
        
        // Map TaiKhoan information
        TaiKhoan taiKhoan = khachHang.getTaiKhoan();
        if (taiKhoan != null) {
            response.setTaiKhoanId(taiKhoan.getId());
            response.setTenDangNhap(taiKhoan.getTenDangNhap());
            response.setSoDienThoai(taiKhoan.getSoDienThoai());
            response.setEmail(taiKhoan.getEmail());
        }
        
        // Map address information
        DiaChiKhachHang diaChi = khachHang.getIdDiaChiKhachHang();
        if (diaChi != null) {
            response.setThanhPho(diaChi.getThanhPho());
            response.setQuan(diaChi.getQuan());
            response.setPhuong(diaChi.getPhuong());
            response.setDiaChiCuThe(diaChi.getDiaChiCuThe());
        }
        
        return response;
    }

    private String generateKhachHangMa() {
        long count = khachHangRepository.count() + 1;
        return String.format("KH%03d", count);
    }

    private String generateTaiKhoanMa() {
        long count = taiKhoanRepository.count() + 1;
        return String.format("TK%03d", count);
    }

    private String generateDiaChiMa() {
        return "DC" + System.currentTimeMillis();
    }

    private String generateTenDangNhap(String ten, Instant ngaySinh) {
        if (ten == null || ngaySinh == null) {
            return "user" + System.currentTimeMillis();
        }
        
        String[] parts = ten.trim().split("\\s+");
        String lastName = parts[parts.length - 1].toLowerCase();
        String birthYear = String.valueOf(ngaySinh.toString().substring(0, 4));
        
        return lastName + birthYear + System.currentTimeMillis() % 1000;
    }
}
