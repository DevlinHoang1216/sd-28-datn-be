package com.example.sd_28_phostep_be.controller.account.KhachHang;

import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.service.account.Client.impl.KhachHang.KhachHangServices;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/khach-hang")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class KhachHangController {
    private final KhachHangServices khachHangServices;

    public KhachHangController(KhachHangServices khachHangServices) {
        this.khachHangServices = khachHangServices;
    }

    @GetMapping
    public List<KhachHang> getall() {
        return khachHangServices.getall();
    }
}
