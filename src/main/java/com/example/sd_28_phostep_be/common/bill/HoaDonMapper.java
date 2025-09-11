package com.example.sd_28_phostep_be.common.bill;

import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDTOResponse;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import org.springframework.stereotype.Component;

@Component
public class HoaDonMapper {

    public HoaDonDTOResponse mapToDto(HoaDon hoaDon) {
        if (hoaDon == null) {
            return null;
        }

        HoaDonDTOResponse dto = new HoaDonDTOResponse();
        dto.setId(hoaDon.getId());
        dto.setMa(hoaDon.getMa());
        
        // Map employee info
        if (hoaDon.getIdNhanVien() != null) {
            dto.setIdNhanVien(hoaDon.getIdNhanVien().getId());
            dto.setMaNhanVien(hoaDon.getIdNhanVien().getMa());
        }
        
        dto.setTenKhachHang(hoaDon.getTenKhachHang());
        dto.setSoDienThoaiKhachHang(hoaDon.getSoDienThoaiKhachHang());
        dto.setTongTienSauGiam(hoaDon.getTongTienSauGiam());
        dto.setPhiVanChuyen(hoaDon.getPhiVanChuyen());
        dto.setNgayTao(hoaDon.getNgayTao());
        dto.setLoaiDon(hoaDon.getLoaiDon());
        dto.setTrangThai(hoaDon.getTrangThai());
        dto.setDeleted(hoaDon.getDeleted());
        
        return dto;
    }

//    @Named("formatDate")
//    default String formatDate(java.util.Date date) {
//        if (date == null) return null;
//        return new java.text.SimpleDateFormat("dd-MM-yyyy").format(date);
//    }

}
