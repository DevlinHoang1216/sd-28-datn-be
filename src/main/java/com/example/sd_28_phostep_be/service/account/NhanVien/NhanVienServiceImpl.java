package com.example.sd_28_phostep_be.service.account.NhanVien;

import com.example.sd_28_phostep_be.dto.account.request.NhanVien.NhanVienCreateRequest;
import com.example.sd_28_phostep_be.dto.account.request.NhanVien.NhanVienUpdateRequest;
import com.example.sd_28_phostep_be.dto.account.response.NhanVien.NhanVienDTOResponse;
import com.example.sd_28_phostep_be.dto.account.response.NhanVien.NhanVienDetailResponse;
import com.example.sd_28_phostep_be.modal.account.NhanVien;
import com.example.sd_28_phostep_be.modal.account.QuyenHan;
import com.example.sd_28_phostep_be.modal.account.TaiKhoan;
import com.example.sd_28_phostep_be.repository.account.NhanVien.NhanVienRepository;
import com.example.sd_28_phostep_be.repository.account.QuyenHanRepository;
import com.example.sd_28_phostep_be.repository.account.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final QuyenHanRepository quyenHanRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<NhanVienDTOResponse> getAllNhanVien(String keyword, Date keywordAsDate, Boolean gioiTinh, Boolean trangThai, Pageable pageable) {
        return nhanVienRepository.getAllNV(keyword, keywordAsDate, gioiTinh, trangThai, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public NhanVienDetailResponse getNhanVienById(Integer id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + id));
        
        return mapToDetailResponse(nhanVien);
    }

    @Override
    public NhanVienDetailResponse createNhanVien(NhanVienCreateRequest request) {
        // Create TaiKhoan first
        QuyenHan quyenHan = quyenHanRepository.findById(request.getIdQuyenHan())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền hạn với ID: " + request.getIdQuyenHan()));
        
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .ma(generateTaiKhoanMa())
                .tenDangNhap(request.getTenDangNhap() != null ? request.getTenDangNhap() : generateTenDangNhap(request.getTenNhanVien(), request.getNgaySinh()))
                .email(request.getEmail())
                .soDienThoai(request.getSoDienThoai())
                .matKhau(request.getMatKhau()) // Should be encoded in real application
                .idQuyenHan(quyenHan)
                .deleted(true)
                .build();
        
        taiKhoan = taiKhoanRepository.save(taiKhoan);
        
        // Create NhanVien
        NhanVien nhanVien = NhanVien.builder()
                .idTaiKhoan(taiKhoan)
                .ma(request.getMa() != null ? request.getMa() : generateNhanVienMa(request.getTenNhanVien(), request.getNgaySinh()))
                .tenNhanVien(request.getTenNhanVien())
                .ngaySinh(request.getNgaySinh())
                .anhNhanVien(request.getAnhNhanVien())
                .ghiChu(request.getGhiChu())
                .thanhPho(request.getThanhPho())
                .quan(request.getQuan())
                .phuong(request.getPhuong())
                .diaChiCuThe(request.getDiaChiCuThe())
                .cccd(request.getCccd())
                .gioiTinh(request.getGioiTinh())
                .deleted(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        
        nhanVien = nhanVienRepository.save(nhanVien);
        
        return mapToDetailResponse(nhanVien);
    }

    @Override
    public NhanVienDetailResponse updateNhanVien(Integer id, NhanVienUpdateRequest request) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + id));
        
        // Update NhanVien fields
        if (request.getTenNhanVien() != null) {
            nhanVien.setTenNhanVien(request.getTenNhanVien());
        }
        if (request.getNgaySinh() != null) {
            nhanVien.setNgaySinh(request.getNgaySinh());
        }
        if (request.getAnhNhanVien() != null) {
            nhanVien.setAnhNhanVien(request.getAnhNhanVien());
        }
        if (request.getGhiChu() != null) {
            nhanVien.setGhiChu(request.getGhiChu());
        }
        if (request.getThanhPho() != null) {
            nhanVien.setThanhPho(request.getThanhPho());
        }
        if (request.getQuan() != null) {
            nhanVien.setQuan(request.getQuan());
        }
        if (request.getPhuong() != null) {
            nhanVien.setPhuong(request.getPhuong());
        }
        if (request.getDiaChiCuThe() != null) {
            nhanVien.setDiaChiCuThe(request.getDiaChiCuThe());
        }
        if (request.getCccd() != null) {
            nhanVien.setCccd(request.getCccd());
        }
        if (request.getGioiTinh() != null) {
            nhanVien.setGioiTinh(request.getGioiTinh());
        }
        
        nhanVien.setUpdatedAt(OffsetDateTime.now());
        
        // Update TaiKhoan fields if provided
        TaiKhoan taiKhoan = nhanVien.getIdTaiKhoan();
        if (request.getEmail() != null) {
            taiKhoan.setEmail(request.getEmail());
        }
        if (request.getSoDienThoai() != null) {
            taiKhoan.setSoDienThoai(request.getSoDienThoai());
        }
        if (request.getDeleted() != null) {
            taiKhoan.setDeleted(request.getDeleted());
        }
        
        taiKhoanRepository.save(taiKhoan);
        nhanVien = nhanVienRepository.save(nhanVien);
        
        return mapToDetailResponse(nhanVien);
    }

    @Override
    public void deleteNhanVien(Integer id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + id));
        
        // Soft delete by setting deleted = false for both NhanVien and TaiKhoan (inactive)
        nhanVien.setDeleted(false);
        nhanVien.setUpdatedAt(OffsetDateTime.now());
        
        TaiKhoan taiKhoan = nhanVien.getIdTaiKhoan();
        taiKhoan.setDeleted(false);
        
        taiKhoanRepository.save(taiKhoan);
        nhanVienRepository.save(nhanVien);
    }

    @Override
    public void restoreNhanVien(Integer id) {
        NhanVien nhanVien = nhanVienRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID: " + id));
        
        // Restore by setting deleted = true for both NhanVien and TaiKhoan (active)
        nhanVien.setDeleted(true);
        nhanVien.setUpdatedAt(OffsetDateTime.now());
        
        TaiKhoan taiKhoan = nhanVien.getIdTaiKhoan();
        taiKhoan.setDeleted(true);
        
        taiKhoanRepository.save(taiKhoan);
        nhanVienRepository.save(nhanVien);
    }

    private NhanVienDetailResponse mapToDetailResponse(NhanVien nhanVien) {
        TaiKhoan taiKhoan = nhanVien.getIdTaiKhoan();
        
        return new NhanVienDetailResponse(
                nhanVien.getId(),
                nhanVien.getMa(),
                nhanVien.getTenNhanVien(),
                nhanVien.getNgaySinh(),
                nhanVien.getAnhNhanVien(),
                nhanVien.getGhiChu(),
                nhanVien.getThanhPho(),
                nhanVien.getQuan(),
                nhanVien.getPhuong(),
                nhanVien.getDiaChiCuThe(),
                nhanVien.getCccd(),
                nhanVien.getGioiTinh(),
                nhanVien.getDeleted(),
                nhanVien.getCreatedAt(),
                nhanVien.getUpdatedAt(),
                taiKhoan.getId(),
                taiKhoan.getMa(),
                taiKhoan.getTenDangNhap(),
                taiKhoan.getEmail(),
                taiKhoan.getSoDienThoai(),
                taiKhoan.getIdQuyenHan() != null ? taiKhoan.getIdQuyenHan().getMa() : null,
                taiKhoan.getDeleted()
        );
    }

    private String generateNhanVienMa(String tenNhanVien, Date ngaySinh) {
        String normalizedName = normalizeVietnameseName(tenNhanVien);
        String[] nameParts = normalizedName.split(" ");
        
        if (nameParts.length < 2) {
            return "NV" + System.currentTimeMillis();
        }
        
        // Get first name and last name initials
        String firstName = nameParts[nameParts.length - 1].toLowerCase(); // Last word is first name
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < nameParts.length - 1; i++) {
            if (!nameParts[i].isEmpty()) {
                initials.append(nameParts[i].charAt(0));
            }
        }
        
        // Get year from birth date
        String year = String.valueOf(ngaySinh.toLocalDate().getYear()).substring(2);
        
        return firstName + initials.toString().toLowerCase() + year;
    }
    
    private String generateTenDangNhap(String tenNhanVien, Date ngaySinh) {
        String normalizedName = normalizeVietnameseName(tenNhanVien);
        String[] nameParts = normalizedName.split(" ");
        
        if (nameParts.length < 2) {
            return "user" + System.currentTimeMillis();
        }
        
        // Get first name and last name initials
        String firstName = nameParts[nameParts.length - 1].toLowerCase(); // Last word is first name
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < nameParts.length - 1; i++) {
            if (!nameParts[i].isEmpty()) {
                initials.append(nameParts[i].charAt(0));
            }
        }
        
        // Get day and month from birth date
        String dayMonth = String.format("%02d%02d", 
            ngaySinh.toLocalDate().getDayOfMonth(),
            ngaySinh.toLocalDate().getMonthValue());
        
        return firstName + initials.toString().toLowerCase() + dayMonth;
    }

    private String generateTaiKhoanMa() {
        return "TK" + System.currentTimeMillis();
    }
    
    private String normalizeVietnameseName(String name) {
        if (name == null) return "";
        
        // Remove Vietnamese accents
        String normalized = name.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                               .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                               .replaceAll("[ìíịỉĩ]", "i")
                               .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                               .replaceAll("[ùúụủũưừứựửữ]", "u")
                               .replaceAll("[ỳýỵỷỹ]", "y")
                               .replaceAll("[đ]", "d")
                               .replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A")
                               .replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E")
                               .replaceAll("[ÌÍỊỈĨ]", "I")
                               .replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O")
                               .replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U")
                               .replaceAll("[ỲÝỴỶỸ]", "Y")
                               .replaceAll("[Đ]", "D");
        
        // Remove extra spaces and trim
        return normalized.replaceAll("\\s+", " ").trim();
    }
}
