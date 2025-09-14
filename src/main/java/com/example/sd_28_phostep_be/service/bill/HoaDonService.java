package com.example.sd_28_phostep_be.service.bill;

import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDTOResponse;
import com.example.sd_28_phostep_be.dto.bill.response.HoaDonDetailResponse;
import com.example.sd_28_phostep_be.dto.bill.request.UpdateCustomerRequest;
import com.example.sd_28_phostep_be.modal.bill.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface HoaDonService {
    Page<HoaDonDTOResponse> getHoaDonAndFilters(String keyword, Long minAmount, Long maxAmount,
                                                Timestamp startDate, Timestamp endDate, Short trangThai, String loaiDon, Pageable pageable);

    HoaDonDetailResponse getHoaDonDetail(Integer id);

    Map<String, Long> getStatusCounts();

    Map<String, Long> getPriceRange();

    HoaDonDTOResponse updateHoaDonStatus(Integer id, Short trangThai, Integer idNhanVien);

    HoaDonDTOResponse updateHoaDonStatusWithNote(Integer id, Short trangThai, Integer idNhanVien, String ghiChu);

    HoaDonDTOResponse getHoaDonByMa(String maHoaDon);

    // Sales counter specific methods
    List<HoaDon> getPendingInvoicesForSales();
    
    HoaDon createPendingInvoice();
    
    HoaDon updatePendingInvoiceCustomer(Integer id, UpdateCustomerRequest request);
    
    void deletePendingInvoice(Integer id);

}
