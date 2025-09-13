package com.example.sd_28_phostep_be.service.sale.impl.DotGiamGia;

import com.example.sd_28_phostep_be.dto.product.response.sanpham.SanPhamDetailResponse;
import com.example.sd_28_phostep_be.dto.sale.request.DotGiamGia.DotGiamGiaDTO;
import com.example.sd_28_phostep_be.dto.sale.response.DotGiamGia.DotGiamGiaDetailResponse;
import com.example.sd_28_phostep_be.modal.product.ChiTietSanPham;
import com.example.sd_28_phostep_be.modal.sale.ChiTietDotGiamGia;
import com.example.sd_28_phostep_be.modal.sale.DotGiamGia;
import com.example.sd_28_phostep_be.repository.product.ChiTietSanPhamRepository;
import com.example.sd_28_phostep_be.repository.sale.DotGiamGia.ChiTietDotGiamGiaRepository;
import com.example.sd_28_phostep_be.repository.sale.DotGiamGia.DotGiamGiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class DotGiamGiaServices {
    private final DotGiamGiaRepository dotGiamGiaRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ChiTietDotGiamGiaRepository chiTietDotGiamGiaRepository;

    public DotGiamGiaServices(DotGiamGiaRepository dotGiamGiaRepository, ChiTietSanPhamRepository chiTietSanPhamRepository, ChiTietDotGiamGiaRepository chiTietDotGiamGiaRepository) {
        this.dotGiamGiaRepository = dotGiamGiaRepository;
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
        this.chiTietDotGiamGiaRepository = chiTietDotGiamGiaRepository;
    }


    public List<DotGiamGia> getall() {
        return dotGiamGiaRepository.findAll();
    }

    @Transactional
    public DotGiamGia addDotGiamGia(DotGiamGiaDTO dto) {
        // map từ DTO sang Entity
        DotGiamGia dot = DotGiamGia.builder()
                .ma(dto.getMa())
                .tenDotGiamGia(dto.getTenDotGiamGia())
                .loaiGiamGiaApDung(dto.getLoaiGiamGiaApDung())
                .giaTriGiamGia(dto.getGiaTriGiamGia())
                .soTienGiamToiDa(dto.getSoTienGiamToiDa())
                .ngayBatDau(dto.getNgayBatDau())
                .ngayKetThuc(dto.getNgayKetThuc())
                .trangThai(true)
                .deleted(false)
                .build();

        DotGiamGia savedDot = dotGiamGiaRepository.save(dot);

        // Lấy danh sách sản phẩm áp dụng
        List<ChiTietSanPham> products = chiTietSanPhamRepository.findAllById(dto.getListSanPhamId());

        for (ChiTietSanPham sp : products) {
            BigDecimal giaBanDau = sp.getGiaBan();
            BigDecimal giaSauKhiGiam = tinhGiaSauKhiGiam(savedDot, giaBanDau);

            ChiTietDotGiamGia ctgg = ChiTietDotGiamGia.builder()
                    .idDotGiamGia(savedDot)
                    .idChiTietSp(sp)
                    .ma("CTGG-" + sp.getId() + "-" + savedDot.getId())
                    .giaBanDau(giaBanDau)
                    .giaSauKhiGiam(giaSauKhiGiam)
                    .deleted(false)
                    .build();

            chiTietDotGiamGiaRepository.save(ctgg);
        }

        return savedDot;
    }

    private BigDecimal tinhGiaSauKhiGiam(DotGiamGia dot, BigDecimal giaBan) {
        BigDecimal giaSau;
        if ("PHAN_TRAM".equalsIgnoreCase(dot.getLoaiGiamGiaApDung())) {
            // giảm % theo giaTriGiamGia
            giaSau = giaBan.subtract(
                    giaBan.multiply(dot.getGiaTriGiamGia()).divide(BigDecimal.valueOf(100))
            );
        } else {
            // giảm số tiền trực tiếp
            giaSau = giaBan.subtract(dot.getGiaTriGiamGia());
        }

        // check giới hạn số tiền giảm tối đa
        if (dot.getSoTienGiamToiDa() != null) {
            BigDecimal soTienGiamThucTe = giaBan.subtract(giaSau);
            if (soTienGiamThucTe.compareTo(dot.getSoTienGiamToiDa()) > 0) {
                giaSau = giaBan.subtract(dot.getSoTienGiamToiDa());
            }
        }

        // không cho < 0
        return giaSau.max(BigDecimal.ZERO);
    }

    @Transactional
    public DotGiamGiaDetailResponse getDotGiamGiaDetail(Integer id) {
        DotGiamGia dot = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá"));

        // lấy danh sách chi tiết sản phẩm thuộc đợt giảm giá
        List<ChiTietDotGiamGia> chiTietList =
                chiTietDotGiamGiaRepository.findByIdDotGiamGia_IdAndDeletedFalse(id);

        // map sang DTO sản phẩm
        List<SanPhamDetailResponse> sanPhamDtos = chiTietList.stream()
                .map(ct -> SanPhamDetailResponse.builder()
                        .idSanPham(ct.getIdChiTietSp().getId())
                        .tenSanPham(ct.getIdChiTietSp().getIdSanPham().getTenSanPham())
                        .maSanPham(ct.getIdChiTietSp().getMa())
                        .giaBanDau(ct.getGiaBanDau())
                        .giaSauKhiGiam(ct.getGiaSauKhiGiam())
                        .build())
                .toList();

        // build response
        return DotGiamGiaDetailResponse.builder()
                .id(dot.getId())
                .ma(dot.getMa())
                .tenDotGiamGia(dot.getTenDotGiamGia())
                .loaiGiamGiaApDung(dot.getLoaiGiamGiaApDung())
                .giaTriGiamGia(dot.getGiaTriGiamGia())
                .soTienGiamToiDa(dot.getSoTienGiamToiDa())
                .ngayBatDau(dot.getNgayBatDau().atStartOfDay().toInstant(ZoneOffset.UTC)) // convert LocalDate -> Instant
                .ngayKetThuc(dot.getNgayKetThuc().atStartOfDay().toInstant(ZoneOffset.UTC))
                .trangThai(dot.getTrangThai())
                .danhSachSanPham(sanPhamDtos)
                .build();
    }

}
