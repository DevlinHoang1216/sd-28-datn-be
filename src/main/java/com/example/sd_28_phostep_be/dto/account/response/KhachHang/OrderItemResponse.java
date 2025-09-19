package com.example.sd_28_phostep_be.dto.account.response.KhachHang;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Integer id;
    private String tenSanPham;
    private String maSanPham;
    private String anhSanPham;
    private String mauSac;
    private String kichCo;
    private String chatLieu;
    private String thuongHieu;
    private Integer soLuong;
    private BigDecimal giaBan;
    private BigDecimal thanhTien;
    private String moTa;

    // Constructors
    public OrderItemResponse() {}

    public OrderItemResponse(Integer id, String tenSanPham, String maSanPham, String anhSanPham,
                            String mauSac, String kichCo, String chatLieu, String thuongHieu,
                            Integer soLuong, BigDecimal giaBan, BigDecimal thanhTien, String moTa) {
        this.id = id;
        this.tenSanPham = tenSanPham;
        this.maSanPham = maSanPham;
        this.anhSanPham = anhSanPham;
        this.mauSac = mauSac;
        this.kichCo = kichCo;
        this.chatLieu = chatLieu;
        this.thuongHieu = thuongHieu;
        this.soLuong = soLuong;
        this.giaBan = giaBan;
        this.thanhTien = thanhTien;
        this.moTa = moTa;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }

    public String getAnhSanPham() { return anhSanPham; }
    public void setAnhSanPham(String anhSanPham) { this.anhSanPham = anhSanPham; }

    public String getMauSac() { return mauSac; }
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }

    public String getKichCo() { return kichCo; }
    public void setKichCo(String kichCo) { this.kichCo = kichCo; }

    public String getChatLieu() { return chatLieu; }
    public void setChatLieu(String chatLieu) { this.chatLieu = chatLieu; }

    public String getThuongHieu() { return thuongHieu; }
    public void setThuongHieu(String thuongHieu) { this.thuongHieu = thuongHieu; }

    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

    public BigDecimal getGiaBan() { return giaBan; }
    public void setGiaBan(BigDecimal giaBan) { this.giaBan = giaBan; }

    public BigDecimal getThanhTien() { return thanhTien; }
    public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}
