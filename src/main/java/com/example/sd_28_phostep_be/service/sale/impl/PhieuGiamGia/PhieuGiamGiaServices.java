package com.example.sd_28_phostep_be.service.sale.impl.PhieuGiamGia;

import com.example.sd_28_phostep_be.dto.sale.request.PhieuGiamGia.PhieuGiamGiaDTO;
import com.example.sd_28_phostep_be.dto.sale.response.PhieuGiamGia.PhieuGiamGiaDetailResponse;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGiaCaNhan;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.sale.PhieuGiamGia.PhieuGiamGiaCaNhanRepository;
import com.example.sd_28_phostep_be.repository.sale.PhieuGiamGia.PhieuGiamGiaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    public PhieuGiamGiaServices(PhieuGiamGiaRepository phieuGiamGiaRepository, KhachHangRepository khachHangRepository, PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository) {
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
        this.khachHangRepository = khachHangRepository;
        this.phieuGiamGiaCaNhanRepository = phieuGiamGiaCaNhanRepository;
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

        // validate ngày
        if (dto.getNgayBatDau() != null && dto.getNgayBatDau().isBefore(now)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được nhỏ hơn ngày hiện tại");
        }

        // 1️⃣ Tạo phiếu giảm giá chung
        PhieuGiamGia pgg = PhieuGiamGia.builder()
                .ma(dto.getMa())
                .tenPhieuGiamGia(dto.getTenPhieuGiamGia())
                .loaiPhieuGiamGia(dto.getLoaiPhieuGiamGia())
                .phanTramGiamGia(dto.getPhanTramGiamGia())
                .soTienGiamToiDa(dto.getSoTienGiamToiDa())
                .hoaDonToiThieu(dto.getHoaDonToiThieu())
                .soLuongDung(dto.getSoLuongDung())
                .ngayBatDau(dto.getNgayBatDau())
                .ngayKetThuc(dto.getNgayKetThuc())
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
                        .ma(saved.getMa() + "-" + khachHang.getMa()) // tạo mã riêng VD: PGG01-KH001
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

        // Check ngày bắt đầu
        if (dto.getNgayBatDau() != null && dto.getNgayBatDau().isBefore(now)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được nhỏ hơn ngày hiện tại");
        }

        // Update fields
        existingPgg.setMa(dto.getMa());
        existingPgg.setTenPhieuGiamGia(dto.getTenPhieuGiamGia());
        existingPgg.setLoaiPhieuGiamGia(dto.getLoaiPhieuGiamGia());
        existingPgg.setPhanTramGiamGia(dto.getPhanTramGiamGia());
        existingPgg.setSoTienGiamToiDa(dto.getSoTienGiamToiDa());
        existingPgg.setHoaDonToiThieu(dto.getHoaDonToiThieu());
        existingPgg.setSoLuongDung(dto.getSoLuongDung());
        existingPgg.setNgayBatDau(dto.getNgayBatDau());
        existingPgg.setNgayKetThuc(dto.getNgayKetThuc());
        existingPgg.setRiengTu(dto.getRiengTu());
        existingPgg.setMoTa(dto.getMoTa());
        existingPgg.setTrangThai(dto.getNgayKetThuc() != null && dto.getNgayKetThuc().isBefore(now) ? false : true);

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
                    .ma(pgg.getMa() + "-" + kh.getId())
                    .ngayNhan(Instant.now())
                    .ngayHetHan(pgg.getNgayKetThuc())
                    .trangThai(true)
                    .deleted(false)
                    .build();
            phieuGiamGiaCaNhanRepository.save(newPggCn);
        }
    }
}
