package com.example.sd_28_phostep_be.controller.sell.PhieuGiamGia;


import com.example.sd_28_phostep_be.dto.sale.request.PhieuGiamGia.PhieuGiamGiaDTO;
import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import com.example.sd_28_phostep_be.service.sale.impl.PhieuGiamGia.PhieuGiamGiaServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phieu-giam-gia")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class PhieuGiamGiaController {

    private final PhieuGiamGiaServices phieuGiamGiaService;

    public PhieuGiamGiaController(PhieuGiamGiaServices phieuGiamGiaService) {
        this.phieuGiamGiaService = phieuGiamGiaService;
    }

    // ==================== GET ALL ====================
   @GetMapping
    public List<PhieuGiamGia> getall() {
       return phieuGiamGiaService.getall();
   }
    @PostMapping("/add")
    public ResponseEntity<PhieuGiamGia> add(@RequestBody PhieuGiamGiaDTO pgg) {
        return ResponseEntity.ok(phieuGiamGiaService.add(pgg));
    }
}
