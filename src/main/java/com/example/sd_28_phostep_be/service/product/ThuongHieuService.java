package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.modal.product.ThuongHieu;
import com.example.sd_28_phostep_be.repository.product.ThuongHieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ThuongHieuService {

    @Autowired
    private ThuongHieuRepository thuongHieuRepository;

    public Page<ThuongHieu> getAllWithPagination(String keyword, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return thuongHieuRepository.findAllWithKeyword(keyword, pageable);
    }

    public List<ThuongHieu> getAll() {
        return thuongHieuRepository.findAll();
    }

    public Optional<ThuongHieu> findById(Integer id) {
        return thuongHieuRepository.findById(id);
    }

    public ThuongHieu save(ThuongHieu thuongHieu) {
        if (thuongHieu.getId() == null) {
            thuongHieu.setNgayTao(Instant.now());
            thuongHieu.setDeleted(false);
        }
        thuongHieu.setNgayCapNhat(Instant.now());
        return thuongHieuRepository.save(thuongHieu);
    }

    public ThuongHieu update(Integer id, ThuongHieu thuongHieu) {
        Optional<ThuongHieu> existingThuongHieu = thuongHieuRepository.findById(id);
        if (existingThuongHieu.isPresent()) {
            ThuongHieu updated = existingThuongHieu.get();
            updated.setTenThuongHieu(thuongHieu.getTenThuongHieu());
            updated.setNgayCapNhat(Instant.now());
            return thuongHieuRepository.save(updated);
        }
        return null;
    }


    public ThuongHieu toggleStatus(Integer id) {
        Optional<ThuongHieu> existingThuongHieu = thuongHieuRepository.findById(id);
        if (existingThuongHieu.isPresent()) {
            ThuongHieu thuongHieu = existingThuongHieu.get();
            thuongHieu.setDeleted(!thuongHieu.getDeleted());
            thuongHieu.setNgayCapNhat(Instant.now());
            return thuongHieuRepository.save(thuongHieu);
        }
        return null;
    }

    public boolean checkNameExists(String tenThuongHieu, Integer excludeId) {
        if (excludeId != null) {
            return thuongHieuRepository.existsByTenThuongHieuAndIdNot(tenThuongHieu, excludeId);
        }
        return thuongHieuRepository.existsByTenThuongHieu(tenThuongHieu);
    }
}
