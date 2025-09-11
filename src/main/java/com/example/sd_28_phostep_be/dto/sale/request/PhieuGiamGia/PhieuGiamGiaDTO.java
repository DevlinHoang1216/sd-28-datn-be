package com.example.sd_28_phostep_be.dto.sale.request.PhieuGiamGia;

import java.time.Instant;
import java.util.List;

public class PhieuGiamGiaDTO {
    private String ma;
    private String tenPhieuGiamGia;
    private String loaiPhieuGiamGia;
    private Double phanTramGiamGia;
    private Double soTienGiamToiDa;
    private Double hoaDonToiThieu;
    private Integer soLuongDung;
    private Instant ngayBatDau;
    private Instant ngayKetThuc;
    private Boolean riengTu;
    private String moTa;
    private List<Integer> khachHangIds; // ✅ thêm danh sách ID khách hàng

    public PhieuGiamGiaDTO() {
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTenPhieuGiamGia() {
        return tenPhieuGiamGia;
    }

    public void setTenPhieuGiamGia(String tenPhieuGiamGia) {
        this.tenPhieuGiamGia = tenPhieuGiamGia;
    }

    public String getLoaiPhieuGiamGia() {
        return loaiPhieuGiamGia;
    }

    public void setLoaiPhieuGiamGia(String loaiPhieuGiamGia) {
        this.loaiPhieuGiamGia = loaiPhieuGiamGia;
    }

    public Double getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    public void setPhanTramGiamGia(Double phanTramGiamGia) {
        this.phanTramGiamGia = phanTramGiamGia;
    }

    public Double getSoTienGiamToiDa() {
        return soTienGiamToiDa;
    }

    public void setSoTienGiamToiDa(Double soTienGiamToiDa) {
        this.soTienGiamToiDa = soTienGiamToiDa;
    }

    public Double getHoaDonToiThieu() {
        return hoaDonToiThieu;
    }

    public void setHoaDonToiThieu(Double hoaDonToiThieu) {
        this.hoaDonToiThieu = hoaDonToiThieu;
    }

    public Integer getSoLuongDung() {
        return soLuongDung;
    }

    public void setSoLuongDung(Integer soLuongDung) {
        this.soLuongDung = soLuongDung;
    }

    public Instant getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(Instant ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public Instant getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(Instant ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public Boolean getRiengTu() {
        return riengTu;
    }

    public void setRiengTu(Boolean riengTu) {
        this.riengTu = riengTu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public List<Integer> getKhachHangIds() {
        return khachHangIds;
    }

    public void setKhachHangIds(List<Integer> khachHangIds) {
        this.khachHangIds = khachHangIds;
    }
}
