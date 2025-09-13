package com.example.sd_28_phostep_be.service.account.KhachHang;

import com.example.sd_28_phostep_be.dto.account.request.KhachHang.KhachHangCreateRequest;
import com.example.sd_28_phostep_be.dto.account.request.KhachHang.KhachHangUpdateRequest;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDTOResponse;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface KhachHangService {
    
    Page<KhachHangDTOResponse> getAllKhachHang(
            String keyword,
            Instant keywordAsDate,
            Short gioiTinh,
            Boolean trangThai,
            Pageable pageable
    );
    
    KhachHangDetailResponse getKhachHangById(Integer id);
    
    KhachHangDetailResponse createKhachHang(KhachHangCreateRequest request);
    
    KhachHangDetailResponse updateKhachHang(Integer id, KhachHangUpdateRequest request);
    
    void deleteKhachHang(Integer id);
    
    void restoreKhachHang(Integer id);
}
