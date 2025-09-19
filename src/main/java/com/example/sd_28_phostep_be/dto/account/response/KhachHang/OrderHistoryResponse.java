package com.example.sd_28_phostep_be.dto.account.response.KhachHang;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class OrderHistoryResponse {
    private Integer id;
    private String ma;
    private String tenKhachHang;
    private String soDienThoaiKhachHang;
    private String diaChiKhachHang;
    private String email;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private Short trangThai;
    private Date ngayTao;
    private Date ngayThanhToan;
    private String loaiDon;
    private BigDecimal phiVanChuyen;
    private String ghiChu;
    private String maVanDon;
    private List<OrderItemResponse> items;

    // Constructors
    public OrderHistoryResponse() {}

    public OrderHistoryResponse(Integer id, String ma, String tenKhachHang, String soDienThoaiKhachHang, 
                               String diaChiKhachHang, String email, BigDecimal tongTien, 
                               BigDecimal tongTienSauGiam, Short trangThai, Date ngayTao, 
                               Date ngayThanhToan, String loaiDon, BigDecimal phiVanChuyen, 
                               String ghiChu, String maVanDon, List<OrderItemResponse> items) {
        this.id = id;
        this.ma = ma;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoaiKhachHang = soDienThoaiKhachHang;
        this.diaChiKhachHang = diaChiKhachHang;
        this.email = email;
        this.tongTien = tongTien;
        this.tongTienSauGiam = tongTienSauGiam;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
        this.ngayThanhToan = ngayThanhToan;
        this.loaiDon = loaiDon;
        this.phiVanChuyen = phiVanChuyen;
        this.ghiChu = ghiChu;
        this.maVanDon = maVanDon;
        this.items = items;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getSoDienThoaiKhachHang() { return soDienThoaiKhachHang; }
    public void setSoDienThoaiKhachHang(String soDienThoaiKhachHang) { this.soDienThoaiKhachHang = soDienThoaiKhachHang; }

    public String getDiaChiKhachHang() { return diaChiKhachHang; }
    public void setDiaChiKhachHang(String diaChiKhachHang) { this.diaChiKhachHang = diaChiKhachHang; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }

    public BigDecimal getTongTienSauGiam() { return tongTienSauGiam; }
    public void setTongTienSauGiam(BigDecimal tongTienSauGiam) { this.tongTienSauGiam = tongTienSauGiam; }

    public Short getTrangThai() { return trangThai; }
    public void setTrangThai(Short trangThai) { this.trangThai = trangThai; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(Date ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }

    public String getLoaiDon() { return loaiDon; }
    public void setLoaiDon(String loaiDon) { this.loaiDon = loaiDon; }

    public BigDecimal getPhiVanChuyen() { return phiVanChuyen; }
    public void setPhiVanChuyen(BigDecimal phiVanChuyen) { this.phiVanChuyen = phiVanChuyen; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getMaVanDon() { return maVanDon; }
    public void setMaVanDon(String maVanDon) { this.maVanDon = maVanDon; }

    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
}
