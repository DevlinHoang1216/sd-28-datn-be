package com.example.sd_28_phostep_be.common.bill;

import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDetailResponse;
import com.example.sd_28_phostep_be.modal.bill.HoaDonChiTiet;
import com.example.sd_28_phostep_be.modal.bill.LichSuHoaDon;
import com.example.sd_28_phostep_be.modal.sell.HinhThucThanhToan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HoaDonDetailMapper {
    HoaDonDetailMapper INSTANCE = Mappers.getMapper(HoaDonDetailMapper.class);
    
    // Updated after entity relationship changes

    @Mapping(source = "id", target = "hoaDonChiTietId")
    @Mapping(source = "idChiTietSp.id", target = "chiTietSanPhamId")
    @Mapping(source = "idHoaDon.id", target = "idHoaDon")
    @Mapping(source = "idChiTietSp.ma", target = "maChiTietSanPham")
    @Mapping(source = "idChiTietSp.idSanPham.id", target = "idSanPham")
    @Mapping(source = "idChiTietSp.idSanPham.ma", target = "maSanPham")
    @Mapping(source = "idChiTietSp.idSanPham.tenSanPham", target = "tenSanPham")
    @Mapping(source = "gia", target = "giaBan")
    @Mapping(source = "ghiChu", target = "ghiChu")
    @Mapping(source = "idChiTietSp.idMauSac.tenMauSac", target = "mauSac")
    @Mapping(source = "idChiTietSp.idKichCo.tenKichCo", target = "kichCo")
    @Mapping(source = "idChiTietSp.idSanPham.idChatLieu.tenChatLieu", target = "chatLieu")
    @Mapping(source = "idChiTietSp.soLuongTonKho", target = "soLuongTonKho")
    @Mapping(source = "idChiTietSp.moTaChiTiet", target = "moTaChiTiet")
    @Mapping(source = "idChiTietSp.idAnhSanPham.urlAnh", target = "duongDan")
    HoaDonDetailResponse.SanPhamChiTietInfo mapToSanPhamChiTietInfo(HoaDonChiTiet hoaDonChiTiet);

    @Mapping(source = "idPhuongThucThanhToan.ma", target = "maHinhThucThanhToan")
    @Mapping(source = "idPhuongThucThanhToan.kieuThanhToan", target = "kieuThanhToan")
    @Mapping(source = "tienChuyenKhoan", target = "tienChuyenKhoan")
    @Mapping(source = "tienMat", target = "tienMat")
    HoaDonDetailResponse.ThanhToanInfo mapToThanhToanInfo(HinhThucThanhToan hinhThucThanhToan);

    @Mapping(source = "ma", target = "ma")
    @Mapping(source = "hanhDong", target = "hanhDong")
    @Mapping(source = "thoiGian", target = "thoiGian")
    @Mapping(source = "idNhanVien.tenNhanVien", target = "tenNhanVien")
    @Mapping(source = "idHoaDon.id", target = "idHoaDon")
    @Mapping(source = "idHoaDon.trangThai", target = "trangThai")
    HoaDonDetailResponse.LichSuHoaDonInfo mapToLichSuHoaDonInfo(LichSuHoaDon lichSuHoaDon);
}
