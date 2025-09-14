package com.example.sd_28_phostep_be.dto.bill.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {
    private Integer khachHangId;
    private String tenKhachHang;
    private String soDienThoaiKhachHang;
    private String diaChiKhachHang;
    private String email;
}
