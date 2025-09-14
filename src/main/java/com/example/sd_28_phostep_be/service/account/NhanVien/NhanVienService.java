package com.example.sd_28_phostep_be.service.account.NhanVien;

import com.example.sd_28_phostep_be.dto.account.request.NhanVien.NhanVienCreateRequest;
import com.example.sd_28_phostep_be.dto.account.request.NhanVien.NhanVienUpdateRequest;
import com.example.sd_28_phostep_be.dto.account.response.NhanVien.NhanVienDTOResponse;
import com.example.sd_28_phostep_be.dto.account.response.NhanVien.NhanVienDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Date;

public interface NhanVienService {
    
    Page<NhanVienDTOResponse> getAllNhanVien(
            String keyword,
            Date keywordAsDate,
            Boolean gioiTinh,
            Boolean trangThai,
            Pageable pageable
    );
    
    NhanVienDetailResponse getNhanVienById(Integer id);
    
    NhanVienDetailResponse createNhanVien(NhanVienCreateRequest request);
    
    NhanVienDetailResponse updateNhanVien(Integer id, NhanVienUpdateRequest request);
    
    void deleteNhanVien(Integer id);
    
    void restoreNhanVien(Integer id);
}
