package com.example.sd_28_phostep_be.controller.sale.DotGiamGia;


import com.example.sd_28_phostep_be.dto.sale.request.DotGiamGia.DotGiamGiaDTO;
import com.example.sd_28_phostep_be.dto.sale.response.DotGiamGia.DotGiamGiaDetailResponse;
import com.example.sd_28_phostep_be.modal.sale.DotGiamGia;
import com.example.sd_28_phostep_be.service.sale.impl.DotGiamGia.DotGiamGiaServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dot-giam-gia")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class DotGiamGiaController {
    private final DotGiamGiaServices dotGiamGiaServices;


    public DotGiamGiaController(DotGiamGiaServices dotGiamGiaServices) {
        this.dotGiamGiaServices = dotGiamGiaServices;
    }

    @GetMapping
    public List<DotGiamGia> getall() {
        return dotGiamGiaServices.getall();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDotGiamGia(@RequestBody DotGiamGiaDTO dto) {
        DotGiamGia dot = dotGiamGiaServices.addDotGiamGia(dto);
        return ResponseEntity.ok(dot);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<DotGiamGiaDetailResponse> getDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(dotGiamGiaServices.getDotGiamGiaDetail(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DotGiamGia> update(@PathVariable Integer id, @RequestBody DotGiamGiaDTO dto) {
        return ResponseEntity.ok(dotGiamGiaServices.updateDotGiamGia(id, dto));
    }
}
