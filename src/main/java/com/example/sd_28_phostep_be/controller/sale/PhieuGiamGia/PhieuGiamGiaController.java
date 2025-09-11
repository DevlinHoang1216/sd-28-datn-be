package com.example.sd_28_phostep_be.controller.sale.PhieuGiamGia;


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

    // Lấy phiếu giảm giá theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PhieuGiamGia> getById(@PathVariable Long id) {
        try {
            PhieuGiamGia pgg = phieuGiamGiaService.getById(id);
            return ResponseEntity.ok(pgg);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Cập nhật phiếu giảm giá
    @PutMapping("/{id}")
    public ResponseEntity<PhieuGiamGia> update(@PathVariable Long id, @RequestBody PhieuGiamGiaDTO dto) {
        try {
            PhieuGiamGia updatedPgg = phieuGiamGiaService.update(id, dto);
            return ResponseEntity.ok(updatedPgg);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
