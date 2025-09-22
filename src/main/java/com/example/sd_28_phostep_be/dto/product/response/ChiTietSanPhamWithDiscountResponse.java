package com.example.sd_28_phostep_be.dto.product.response;

import java.math.BigDecimal;

public class ChiTietSanPhamWithDiscountResponse {
    private Integer id;
    private String ma;
    private BigDecimal giaBan;
    private Integer soLuongTonKho;
    
    // Product information
    private Integer idSanPham; // Add product ID field
    private String tenSanPham;
    private String tenDanhMuc;
    private String tenThuongHieu;
    private String tenChatLieu;
    private String tenDeGiay;
    
    // Variant information
    private String tenMauSac;
    private String hexMauSac;
    private String tenKichCo;
    
    // Image information
    private String urlAnhSanPham;
    
    // Discount information
    private BigDecimal giaGiamGia;
    private String tenDotGiamGia;
    private boolean hasDiscount;
    
    // Constructors
    public ChiTietSanPhamWithDiscountResponse() {}
    
    public ChiTietSanPhamWithDiscountResponse(Integer id, String ma, BigDecimal giaBan, Integer soLuongTonKho,
                                            Integer idSanPham, String tenSanPham, String tenDanhMuc, String tenThuongHieu,
                                            String tenChatLieu, String tenDeGiay, String tenMauSac, String hexMauSac,
                                            String tenKichCo, String urlAnhSanPham, BigDecimal giaGiamGia,
                                            String tenDotGiamGia, boolean hasDiscount) {
        this.id = id;
        this.ma = ma;
        this.giaBan = giaBan;
        this.soLuongTonKho = soLuongTonKho;
        this.idSanPham = idSanPham;
        this.tenSanPham = tenSanPham;
        this.tenDanhMuc = tenDanhMuc;
        this.tenThuongHieu = tenThuongHieu;
        this.tenChatLieu = tenChatLieu;
        this.tenDeGiay = tenDeGiay;
        this.tenMauSac = tenMauSac;
        this.hexMauSac = hexMauSac;
        this.tenKichCo = tenKichCo;
        this.urlAnhSanPham = urlAnhSanPham;
        this.giaGiamGia = giaGiamGia;
        this.tenDotGiamGia = tenDotGiamGia;
        this.hasDiscount = hasDiscount;
    }
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }
    
    public BigDecimal getGiaBan() { return giaBan; }
    public void setGiaBan(BigDecimal giaBan) { this.giaBan = giaBan; }
    
    public Integer getSoLuongTonKho() { return soLuongTonKho; }
    public void setSoLuongTonKho(Integer soLuongTonKho) { this.soLuongTonKho = soLuongTonKho; }
    
    public Integer getIdSanPham() { return idSanPham; }
    public void setIdSanPham(Integer idSanPham) { this.idSanPham = idSanPham; }
    
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    
    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String tenDanhMuc) { this.tenDanhMuc = tenDanhMuc; }
    
    public String getTenThuongHieu() { return tenThuongHieu; }
    public void setTenThuongHieu(String tenThuongHieu) { this.tenThuongHieu = tenThuongHieu; }
    
    public String getTenChatLieu() { return tenChatLieu; }
    public void setTenChatLieu(String tenChatLieu) { this.tenChatLieu = tenChatLieu; }
    
    public String getTenDeGiay() { return tenDeGiay; }
    public void setTenDeGiay(String tenDeGiay) { this.tenDeGiay = tenDeGiay; }
    
    public String getTenMauSac() { return tenMauSac; }
    public void setTenMauSac(String tenMauSac) { this.tenMauSac = tenMauSac; }
    
    public String getHexMauSac() { return hexMauSac; }
    public void setHexMauSac(String hexMauSac) { this.hexMauSac = hexMauSac; }
    
    public String getTenKichCo() { return tenKichCo; }
    public void setTenKichCo(String tenKichCo) { this.tenKichCo = tenKichCo; }
    
    public String getUrlAnhSanPham() { return urlAnhSanPham; }
    public void setUrlAnhSanPham(String urlAnhSanPham) { this.urlAnhSanPham = urlAnhSanPham; }
    
    public BigDecimal getGiaGiamGia() { return giaGiamGia; }
    public void setGiaGiamGia(BigDecimal giaGiamGia) { this.giaGiamGia = giaGiamGia; }
    
    public String getTenDotGiamGia() { return tenDotGiamGia; }
    public void setTenDotGiamGia(String tenDotGiamGia) { this.tenDotGiamGia = tenDotGiamGia; }
    
    public boolean isHasDiscount() { return hasDiscount; }
    public void setHasDiscount(boolean hasDiscount) { this.hasDiscount = hasDiscount; }
}
