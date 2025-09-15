package com.example.sd_28_phostep_be.service.sale.impl.PhieuGiamGia;

import com.example.sd_28_phostep_be.dto.sale.request.PhieuGiamGia.PhieuGiamGiaDTO;
import com.example.sd_28_phostep_be.dto.sale.response.PhieuGiamGia.PhieuGiamGiaDetailResponse;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGiaCaNhan;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.bill.HoaDonRepository;
import com.example.sd_28_phostep_be.repository.sale.PhieuGiamGia.PhieuGiamGiaCaNhanRepository;
import com.example.sd_28_phostep_be.repository.sale.PhieuGiamGia.PhieuGiamGiaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhieuGiamGiaServices {
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final KhachHangRepository khachHangRepository;
    private final PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;
    private final HoaDonRepository hoaDonRepository;

    public PhieuGiamGiaServices(PhieuGiamGiaRepository phieuGiamGiaRepository, KhachHangRepository khachHangRepository, PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository, HoaDonRepository hoaDonRepository) {
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
        this.khachHangRepository = khachHangRepository;
        this.phieuGiamGiaCaNhanRepository = phieuGiamGiaCaNhanRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    /**
     * Convert date string (YYYY-MM-DD) to Instant at start of day
     */
    private Instant convertDateStringToInstant(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDate localDate = LocalDate.parse(dateString);
            return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected YYYY-MM-DD, got: " + dateString);
        }
    }

    public List<PhieuGiamGia> getall() {
        Instant now = Instant.now();
        List<PhieuGiamGia> list = phieuGiamGiaRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        list.forEach(pgg -> {
            if (pgg.getNgayKetThuc() != null && pgg.getNgayKetThuc().isBefore(now)) {
                if (Boolean.TRUE.equals(pgg.getTrangThai())) {
                    pgg.setTrangThai(false);
                    phieuGiamGiaRepository.save(pgg);
                }
            }
        });
        return list;
    }


    public PhieuGiamGia add(PhieuGiamGiaDTO dto) {
        Instant now = Instant.now();
        Instant todayStart = now.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        // Convert date strings to Instant
        Instant ngayBatDauInstant = convertDateStringToInstant(dto.getNgayBatDau());
        Instant ngayKetThucInstant = convertDateStringToInstant(dto.getNgayKetThuc());

        // validate ngày - allow today's date
        if (ngayBatDauInstant != null && ngayBatDauInstant.isBefore(todayStart)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được nhỏ hơn ngày hiện tại");
        }

        // 1️⃣ Tạo phiếu giảm giá chung
        PhieuGiamGia pgg = PhieuGiamGia.builder()
                .tenPhieuGiamGia(dto.getTenPhieuGiamGia())
                .loaiPhieuGiamGia(dto.getLoaiPhieuGiamGia())
                .phanTramGiamGia(dto.getPhanTramGiamGia())
                .soTienGiamToiDa(dto.getSoTienGiamToiDa())
                .hoaDonToiThieu(dto.getHoaDonToiThieu())
                .soLuongDung(dto.getSoLuongDung())
                .ngayBatDau(ngayBatDauInstant)
                .ngayKetThuc(ngayKetThucInstant)
                .riengTu(dto.getRiengTu())
                .moTa(dto.getMoTa())
                .deleted(false)
                .trangThai(true)
                .build();

        PhieuGiamGia saved = phieuGiamGiaRepository.save(pgg);

        // 2️⃣ Nếu có danh sách khách hàng → tạo phiếu cá nhân
        if (dto.getKhachHangIds() != null && !dto.getKhachHangIds().isEmpty()) {
            for (Integer khId : dto.getKhachHangIds()) {
                KhachHang khachHang = khachHangRepository.findById(khId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng id = " + khId));

                PhieuGiamGiaCaNhan caNhan = PhieuGiamGiaCaNhan.builder()
                        .idPhieuGiamGia(saved)
                        .idKhachHang(khachHang)
                        .ma("PGG-" + saved.getId() + "-KH" + khachHang.getId()) // tạo mã riêng VD: PGG-1-KH001
                        .ngayNhan(now)
                        .ngayHetHan(saved.getNgayKetThuc())
                        .trangThai(true)
                        .deleted(false)
                        .build();

                phieuGiamGiaCaNhanRepository.save(caNhan);
            }
        }

        return saved;
    }

    // Method to get a single coupon by ID
    public PhieuGiamGia getById(Long id) {
        return phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá với ID: " + id));
    }

    // Method to update an existing coupon
    public PhieuGiamGia update(Long id, PhieuGiamGiaDTO dto) {
        PhieuGiamGia existingPgg = getById(id);
        Instant now = Instant.now();
        Instant todayStart = now.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        // Convert date strings to Instant
        Instant ngayBatDauInstant = convertDateStringToInstant(dto.getNgayBatDau());
        Instant ngayKetThucInstant = convertDateStringToInstant(dto.getNgayKetThuc());

        // Check ngày bắt đầu - allow today's date
        if (ngayBatDauInstant != null && ngayBatDauInstant.isBefore(todayStart)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được nhỏ hơn ngày hiện tại");
        }

        // Update fields
        existingPgg.setTenPhieuGiamGia(dto.getTenPhieuGiamGia());
        existingPgg.setLoaiPhieuGiamGia(dto.getLoaiPhieuGiamGia());
        existingPgg.setPhanTramGiamGia(dto.getPhanTramGiamGia());
        existingPgg.setSoTienGiamToiDa(dto.getSoTienGiamToiDa());
        existingPgg.setHoaDonToiThieu(dto.getHoaDonToiThieu());
        existingPgg.setSoLuongDung(dto.getSoLuongDung());
        existingPgg.setNgayBatDau(ngayBatDauInstant);
        existingPgg.setNgayKetThuc(ngayKetThucInstant);
        existingPgg.setRiengTu(dto.getRiengTu());
        existingPgg.setMoTa(dto.getMoTa());
        existingPgg.setTrangThai(ngayKetThucInstant != null && ngayKetThucInstant.isBefore(now) ? false : true);

        PhieuGiamGia saved = phieuGiamGiaRepository.save(existingPgg);

        // ✅ Nếu riêng tư = false thì xóa hết PGG cá nhân có cùng id
        if (Boolean.FALSE.equals(dto.getRiengTu())) {
            phieuGiamGiaCaNhanRepository.deleteAllByPhieuGiamGiaId(saved.getId());
        }

        return saved;
    }

    public PhieuGiamGia delete(Integer id) {
        PhieuGiamGia existing = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá id = " + id));
        existing.setTrangThai(!Boolean.TRUE.equals(existing.getTrangThai()));
        return phieuGiamGiaRepository.save(existing);
    }

    public PhieuGiamGiaDetailResponse getDetail(Integer id) {
        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy PGG"));

        PhieuGiamGiaDetailResponse dto = PhieuGiamGiaDetailResponse.builder()
                .id(pgg.getId())
                .ma(pgg.getMa())
                .tenPhieuGiamGia(pgg.getTenPhieuGiamGia())
                .loaiPhieuGiamGia(pgg.getLoaiPhieuGiamGia())
                .phanTramGiamGia(pgg.getPhanTramGiamGia())
                .soTienGiamToiDa(pgg.getSoTienGiamToiDa())
                .hoaDonToiThieu(pgg.getHoaDonToiThieu())
                .soLuongDung(pgg.getSoLuongDung())
                .ngayBatDau(pgg.getNgayBatDau() != null
                        ? pgg.getNgayBatDau().atZone(ZoneId.systemDefault()).toLocalDate()
                        : null)
                .ngayKetThuc(pgg.getNgayKetThuc() != null
                        ? pgg.getNgayKetThuc().atZone(ZoneId.systemDefault()).toLocalDate()
                        : null)
                .trangThai(pgg.getTrangThai())
                .riengTu(pgg.getRiengTu())
                .moTa(pgg.getMoTa())
                .build();


        // Nếu là PGG cá nhân (riengTu = true) thì load thêm dữ liệu
        if (Boolean.TRUE.equals(pgg.getRiengTu())) {
            List<PhieuGiamGiaCaNhan> caNhanList =
                    phieuGiamGiaCaNhanRepository.findAllByIdPhieuGiamGiaId(pgg.getId());

            List<PhieuGiamGiaDetailResponse.CustomerDetail> customers = caNhanList.stream()
                    .map(cn -> {
                        KhachHang kh = cn.getIdKhachHang();
                        return new PhieuGiamGiaDetailResponse.CustomerDetail(
                                kh.getId(),
                                kh.getTaiKhoan().getEmail(),
                                kh.getTaiKhoan().getSoDienThoai()
                        );
                    })
                    // loại bỏ trùng theo idKhachHang
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    PhieuGiamGiaDetailResponse.CustomerDetail::getIdKhachHang,
                                    c -> c,
                                    (c1, c2) -> c1 // nếu trùng thì giữ 1 cái
                            ),
                            m -> new ArrayList(m.values())
                    ));
            dto.setCustomers(customers);
        } else {
            dto.setCustomers(Collections.emptyList());
        }
        return dto;
    }

    public void toggleCustomer(Integer pggId, Integer khId) {
        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(pggId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));

        KhachHang kh = khachHangRepository.findById(khId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // Kiểm tra KH đã có trong PGG chưa
        Optional<PhieuGiamGiaCaNhan> existing = phieuGiamGiaCaNhanRepository
                .findByIdPhieuGiamGiaAndIdKhachHang(pgg, kh);

        if (existing.isPresent()) {
            // Nếu đã tồn tại thì xóa
            phieuGiamGiaCaNhanRepository.delete(existing.get());
        } else {
            // Nếu chưa có thì thêm mới
            PhieuGiamGiaCaNhan newPggCn = PhieuGiamGiaCaNhan.builder()
                    .idPhieuGiamGia(pgg)
                    .idKhachHang(kh)
                    .ma("PGG-" + pgg.getId() + "-KH" + kh.getId())
                    .ngayNhan(Instant.now())
                    .ngayHetHan(pgg.getNgayKetThuc())
                    .trangThai(true)
                    .deleted(false)
                    .build();
            phieuGiamGiaCaNhanRepository.save(newPggCn);
        }
    }

    // Lấy tất cả phiếu giảm giá còn hoạt động (công khai và cá nhân cho khách hàng)
    public List<PhieuGiamGia> getActiveVouchersForCustomer(Integer customerId) {
        List<PhieuGiamGia> activeVouchers = new ArrayList<>();
        
        // 1. Lấy tất cả phiếu giảm giá công khai còn hoạt động
        List<PhieuGiamGia> publicVouchers = phieuGiamGiaRepository.findAll().stream()
                .filter(pgg -> pgg.getTrangThai() != null && pgg.getTrangThai()) // Trạng thái true
                .filter(pgg -> pgg.getDeleted() == null || !pgg.getDeleted()) // Deleted false
                .filter(pgg -> pgg.getRiengTu() == null || !pgg.getRiengTu()) // Phiếu công khai
                .collect(Collectors.toList());
        
        activeVouchers.addAll(publicVouchers);
        
        // 2. Nếu có customerId, lấy thêm phiếu giảm giá cá nhân
        if (customerId != null) {
            KhachHang khachHang = khachHangRepository.findById(customerId).orElse(null);
            if (khachHang != null) {
                List<PhieuGiamGiaCaNhan> personalVouchers = phieuGiamGiaCaNhanRepository
                        .findAllByIdKhachHang(khachHang);
                
                for (PhieuGiamGiaCaNhan pggCaNhan : personalVouchers) {
                    PhieuGiamGia pgg = pggCaNhan.getIdPhieuGiamGia();
                    if (pgg.getTrangThai() != null && pgg.getTrangThai() && // Trạng thái true
                        (pgg.getDeleted() == null || !pgg.getDeleted()) && // Deleted false
                        !activeVouchers.contains(pgg)) {
                        activeVouchers.add(pgg);
                    }
                }
            }
        }
        
        return activeVouchers;
    }

    // Lấy phiếu giảm giá công khai còn hoạt động
    public List<PhieuGiamGia> getActivePublicVouchers() {
        return phieuGiamGiaRepository.findAll().stream()
                .filter(pgg -> pgg.getTrangThai() != null && pgg.getTrangThai()) // Trạng thái true
                .filter(pgg -> pgg.getDeleted() == null || !pgg.getDeleted()) // Deleted false
                .collect(Collectors.toList());
    }

    /**
     * Áp dụng phiếu giảm giá cho hóa đơn
     * - Lưu ID phiếu giảm giá vào hóa đơn
     * - Trừ số lượng sử dụng của phiếu giảm giá
     */
    public HoaDon applyVoucherToInvoice(Integer invoiceId, Integer voucherId) {
        // Tìm hóa đơn
        HoaDon hoaDon = hoaDonRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + invoiceId));

        // Tìm phiếu giảm giá
        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá với ID: " + voucherId));

        // Kiểm tra phiếu giảm giá có hợp lệ không
        validateVoucherForApplication(phieuGiamGia);

        // Kiểm tra số lượng sử dụng
        if (phieuGiamGia.getSoLuongDung() != null && phieuGiamGia.getSoLuongDung() <= 0) {
            throw new RuntimeException("Phiếu giảm giá đã hết lượt sử dụng");
        }

        // Nếu hóa đơn đã có phiếu giảm giá khác, khôi phục số lượng của phiếu cũ
        if (hoaDon.getIdPhieuGiamGia() != null) {
            restoreVoucherQuantity(hoaDon.getIdPhieuGiamGia().getId());
        }

        // Áp dụng phiếu giảm giá mới
        hoaDon.setIdPhieuGiamGia(phieuGiamGia);

        // Trừ số lượng sử dụng
        if (phieuGiamGia.getSoLuongDung() != null) {
            phieuGiamGia.setSoLuongDung(phieuGiamGia.getSoLuongDung() - 1);
            phieuGiamGiaRepository.save(phieuGiamGia);
        }

        // Lưu hóa đơn
        return hoaDonRepository.save(hoaDon);
    }

    /**
     * Xóa phiếu giảm giá khỏi hóa đơn
     * - Xóa ID phiếu giảm giá khỏi hóa đơn
     * - Khôi phục số lượng sử dụng của phiếu giảm giá
     */
    public HoaDon removeVoucherFromInvoice(Integer invoiceId) {
        // Tìm hóa đơn
        HoaDon hoaDon = hoaDonRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + invoiceId));

        // Kiểm tra hóa đơn có phiếu giảm giá không
        if (hoaDon.getIdPhieuGiamGia() == null) {
            throw new RuntimeException("Hóa đơn không có phiếu giảm giá để xóa");
        }

        // Khôi phục số lượng sử dụng
        restoreVoucherQuantity(hoaDon.getIdPhieuGiamGia().getId());

        // Xóa phiếu giảm giá khỏi hóa đơn
        hoaDon.setIdPhieuGiamGia(null);

        // Lưu hóa đơn
        return hoaDonRepository.save(hoaDon);
    }

    /**
     * Khôi phục số lượng sử dụng của phiếu giảm giá
     */
    private void restoreVoucherQuantity(Integer voucherId) {
        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(voucherId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá với ID: " + voucherId));

        if (phieuGiamGia.getSoLuongDung() != null) {
            phieuGiamGia.setSoLuongDung(phieuGiamGia.getSoLuongDung() + 1);
            phieuGiamGiaRepository.save(phieuGiamGia);
        }
    }

    /**
     * Kiểm tra tính hợp lệ của phiếu giảm giá khi áp dụng
     */
    private void validateVoucherForApplication(PhieuGiamGia phieuGiamGia) {
        Instant now = Instant.now();

        // Kiểm tra trạng thái
        if (phieuGiamGia.getTrangThai() == null || !phieuGiamGia.getTrangThai()) {
            throw new RuntimeException("Phiếu giảm giá không hoạt động");
        }

        // Kiểm tra xóa mềm
        if (phieuGiamGia.getDeleted() != null && phieuGiamGia.getDeleted()) {
            throw new RuntimeException("Phiếu giảm giá đã bị xóa");
        }

        // Kiểm tra ngày bắt đầu
        if (phieuGiamGia.getNgayBatDau() != null && phieuGiamGia.getNgayBatDau().isAfter(now)) {
            throw new RuntimeException("Phiếu giảm giá chưa có hiệu lực");
        }

        // Kiểm tra ngày kết thúc
        if (phieuGiamGia.getNgayKetThuc() != null && phieuGiamGia.getNgayKetThuc().isBefore(now)) {
            throw new RuntimeException("Phiếu giảm giá đã hết hạn");
        }
    }

    /**
     * Kiểm tra phiếu giảm giá có thể áp dụng cho khách hàng không
     */
    public boolean canApplyVoucherForCustomer(Integer voucherId, Integer customerId) {
        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(voucherId)
                .orElse(null);

        if (phieuGiamGia == null) {
            return false;
        }

        try {
            validateVoucherForApplication(phieuGiamGia);
        } catch (RuntimeException e) {
            return false;
        }

        // Kiểm tra số lượng
        if (phieuGiamGia.getSoLuongDung() != null && phieuGiamGia.getSoLuongDung() <= 0) {
            return false;
        }

        // Nếu là phiếu riêng tư, kiểm tra khách hàng có quyền sử dụng không
        if (Boolean.TRUE.equals(phieuGiamGia.getRiengTu())) {
            if (customerId == null) {
                return false;
            }
            
            KhachHang khachHang = khachHangRepository.findById(customerId).orElse(null);
            if (khachHang == null) {
                return false;
            }

            // Kiểm tra khách hàng có trong danh sách phiếu cá nhân không
            Optional<PhieuGiamGiaCaNhan> personalVoucher = phieuGiamGiaCaNhanRepository
                    .findByIdPhieuGiamGiaAndIdKhachHang(phieuGiamGia, khachHang);
            
            return personalVoucher.isPresent();
        }

        return true; // Phiếu công khai
    }
}
