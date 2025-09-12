package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.modal.product.KichCo;
import com.example.sd_28_phostep_be.repository.product.KichCoRepository;
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
public class KichCoService {

    @Autowired
    private KichCoRepository kichCoRepository;

    public Page<KichCo> getAllWithPagination(String keyword, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return kichCoRepository.findAllWithKeyword(keyword, pageable);
    }

    public List<KichCo> getAll() {
        return kichCoRepository.findAll();
    }

    public Optional<KichCo> findById(Integer id) {
        return kichCoRepository.findById(id);
    }

    public KichCo save(KichCo kichCo) {
        if (kichCo.getId() == null) {
            kichCo.setNgayTao(Instant.now());
            kichCo.setDeleted(false);
        }
        kichCo.setNgayCapNhat(Instant.now());
        return kichCoRepository.save(kichCo);
    }

    public KichCo update(Integer id, KichCo kichCo) {
        Optional<KichCo> existingKichCo = kichCoRepository.findById(id);
        if (existingKichCo.isPresent()) {
            KichCo updated = existingKichCo.get();
            updated.setTenKichCo(kichCo.getTenKichCo());
            updated.setNgayCapNhat(Instant.now());
            return kichCoRepository.save(updated);
        }
        return null;
    }

    public KichCo toggleStatus(Integer id) {
        Optional<KichCo> existingKichCo = kichCoRepository.findById(id);
        if (existingKichCo.isPresent()) {
            KichCo kichCo = existingKichCo.get();
            kichCo.setDeleted(!kichCo.getDeleted());
            kichCo.setNgayCapNhat(Instant.now());
            return kichCoRepository.save(kichCo);
        }
        return null;
    }

    public boolean checkNameExists(String tenKichCo, Integer excludeId) {
        if (excludeId != null) {
            return kichCoRepository.existsByTenKichCoAndIdNot(tenKichCo, excludeId);
        }
        return kichCoRepository.existsByTenKichCo(tenKichCo);
    }
}
