package com.example.sd_28_phostep_be.dto.bill.response;

import com.example.sd_28_phostep_be.modal.account.NhanVien;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonDetailResponse {
    // Thông tin hóa đơn
    private Integer id;
    private String maHoaDon;
    private String loaiDon;
    private Short trangThai;
    private String maGiamGia;
    private BigDecimal tienGiam;
    private Double phanTramGiam;
    private Date ngayTao;
    private Date ngayThanhToan;
    private String tenKhachHang;
    private String soDienThoaiKhachHang;
    private String diaChiKhachHang;
    private String email;
    private String ghiChu;
    private BigDecimal tienSanPham;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private BigDecimal phiVanChuyen;

    // Thông tin nhân viên
    private String maNhanVien;
    private String tenNhanVien;

    // Thông tin thanh toán
    private List<ThanhToanInfo> thanhToanInfos;

    // Thông tin chi tiết sản phẩm
    private List<SanPhamChiTietInfo> sanPhamChiTietInfos;

    // Lịch sử hóa đơn
    private List<LichSuHoaDonInfo> lichSuHoaDonInfos;

    // DTO con cho thanh toán
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThanhToanInfo {
        private String maHinhThucThanhToan;
        private String kieuThanhToan;
        private BigDecimal tienChuyenKhoan;
        private BigDecimal tienMat;
    }

    // DTO con cho sản phẩm chi tiết
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SanPhamChiTietInfo {
        private Integer hoaDonChiTietId;
        private Integer chiTietSanPhamId;
        private Integer idSanPham;
        private String maChiTietSanPham;
        private Integer idHoaDon;
        private String maSanPham;
        private String tenSanPham;
        private BigDecimal giaBan;
        private String ghiChu;
        private String mauSac;
        private String kichCo;
        private String chatLieu;
        private String duongDan;
        private Integer soLuong; // Số lượng đã mua trong hóa đơn
        private String moTaChiTiet;

        public SanPhamChiTietInfo(Integer hoaDonChiTietId, Integer chiTietSanPhamId, Integer idSanPham, 
                                  Integer idHoaDon, String maSanPham, String tenSanPham,
                                  BigDecimal giaBan, String ghiChu, String mauSac,
                                  String kichCo, String chatLieu, String duongDan) {
            this.hoaDonChiTietId = hoaDonChiTietId;
            this.chiTietSanPhamId = chiTietSanPhamId;
            this.idHoaDon = idHoaDon;
            this.maSanPham = maSanPham;
            this.idSanPham = idSanPham;
            this.tenSanPham = tenSanPham;
            this.giaBan = giaBan;
            this.ghiChu = ghiChu;
            this.mauSac = mauSac;
            this.kichCo = kichCo;
            this.chatLieu = chatLieu;
            this.duongDan = duongDan;
        }
    }

    // DTO con cho lịch sử hóa đơn
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LichSuHoaDonInfo {
        private String ma;
        private String hanhDong;
        private Date thoiGian;  // Changed from Instant to Date to match entity
        private String tenNhanVien;
        private Integer idHoaDon;
        private Short trangThai;
    }

    // Builder Pattern
    public static class Builder {
        private final HoaDonDetailResponse response;

        public Builder() {
            response = new HoaDonDetailResponse();
        }

        public Builder withHoaDonInfo(HoaDon hoaDon, PhieuGiamGia phieuGiamGia) {
            response.id = hoaDon.getId();
            response.maHoaDon = hoaDon.getMa();
            response.loaiDon = hoaDon.getLoaiDon();
            response.trangThai = hoaDon.getTrangThai();
            response.maGiamGia = phieuGiamGia != null ? phieuGiamGia.getMa() : null;
            response.tienGiam = phieuGiamGia != null && phieuGiamGia.getSoTienGiamToiDa() != null
                    ? BigDecimal.valueOf(phieuGiamGia.getSoTienGiamToiDa()) : BigDecimal.ZERO;
            response.phanTramGiam = phieuGiamGia != null ? phieuGiamGia.getPhanTramGiamGia() : 0.0;
            response.ngayTao = hoaDon.getNgayTao();
            response.ngayThanhToan = hoaDon.getNgayThanhToan();
            response.tenKhachHang = hoaDon.getTenKhachHang();
            response.soDienThoaiKhachHang = hoaDon.getSoDienThoaiKhachHang();
            response.diaChiKhachHang = hoaDon.getDiaChiKhachHang();
            response.email = hoaDon.getEmail();
            response.ghiChu = hoaDon.getGhiChu();
            response.tienSanPham = hoaDon.getTienSanPham();
            response.tongTien = hoaDon.getTongTien();
            response.tongTienSauGiam = hoaDon.getTongTienSauGiam();
            response.phiVanChuyen = hoaDon.getPhiVanChuyen();
            return this;
        }

        public Builder withNhanVienInfo(NhanVien nhanVien) {
            response.maNhanVien = nhanVien.getMa();
            response.tenNhanVien = nhanVien.getTenNhanVien();
            return this;
        }

        public Builder withThanhToanInfos(List<ThanhToanInfo> thanhToanInfos) {
            response.thanhToanInfos = thanhToanInfos;
            return this;
        }

        public Builder withSanPhamChiTietInfos(List<SanPhamChiTietInfo> sanPhamChiTietInfos) {
            response.sanPhamChiTietInfos = sanPhamChiTietInfos;
            return this;
        }

        public Builder withLichSuHoaDonInfos(List<LichSuHoaDonInfo> lichSuHoaDonInfos) {
            response.lichSuHoaDonInfos = lichSuHoaDonInfos;
            return this;
        }

        public HoaDonDetailResponse build() {
            return response;
        }
    }
}
