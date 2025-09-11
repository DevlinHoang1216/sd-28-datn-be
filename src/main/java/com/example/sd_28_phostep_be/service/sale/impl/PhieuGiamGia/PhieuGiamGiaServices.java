package com.example.sd_28_phostep_be.service.sale.impl.PhieuGiamGia;

import com.example.sd_28_phostep_be.dto.sale.request.PhieuGiamGia.PhieuGiamGiaDTO;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import com.example.sd_28_phostep_be.repository.sale.PhieuGiamGia.PhieuGiamGiaRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
@Service
public class PhieuGiamGiaServices {
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;

    public PhieuGiamGiaServices(PhieuGiamGiaRepository phieuGiamGiaRepository) {
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
    }

    public List<PhieuGiamGia> getall() {
        Instant now = Instant.now();
        List<PhieuGiamGia> list = phieuGiamGiaRepository.findAll();
        list.forEach(pgg -> {
            if (pgg.getNgayKetThuc() != null && pgg.getNgayKetThuc().isBefore(now)) {
                if (Boolean.TRUE.equals(pgg.getTrangThai())) {
                    pgg.setTrangThai(false);
                    phieuGiamGiaRepository.save(pgg); // update lại trạng thái
                }
            }
        });
        return list;
    }

    public PhieuGiamGia add(PhieuGiamGiaDTO dto) {
        Instant now = Instant.now();

        // Check ngày bắt đầu
        if (dto.getNgayBatDau() != null && dto.getNgayBatDau().isBefore(now)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được nhỏ hơn ngày hiện tại");
        }

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
                .trangThai(dto.getNgayKetThuc() != null && dto.getNgayKetThuc().isBefore(now) ? false : true)
                .build();

        return phieuGiamGiaRepository.save(pgg);
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

        return phieuGiamGiaRepository.save(existingPgg);
    }
}
