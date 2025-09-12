package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.modal.product.MauSac;
import com.example.sd_28_phostep_be.repository.product.MauSacRepository;
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
public class MauSacService {

    @Autowired
    private MauSacRepository mauSacRepository;

    public Page<MauSac> getAllWithPagination(String keyword, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return mauSacRepository.findAllWithKeyword(keyword, pageable);
    }

    public List<MauSac> getAll() {
        return mauSacRepository.findAll();
    }

    public Optional<MauSac> findById(Integer id) {
        return mauSacRepository.findById(id);
    }

    public MauSac save(MauSac mauSac) {
        if (mauSac.getId() == null) {
            mauSac.setNgayTao(Instant.now());
            mauSac.setDeleted(false);
        }
        mauSac.setNgayCapNhat(Instant.now());
        return mauSacRepository.save(mauSac);
    }

    public MauSac update(Integer id, MauSac mauSac) {
        Optional<MauSac> existingMauSac = mauSacRepository.findById(id);
        if (existingMauSac.isPresent()) {
            MauSac updated = existingMauSac.get();
            updated.setTenMauSac(mauSac.getTenMauSac());
            updated.setHex(mauSac.getHex());
            updated.setNgayCapNhat(Instant.now());
            return mauSacRepository.save(updated);
        }
        return null;
    }

    public MauSac toggleStatus(Integer id) {
        Optional<MauSac> existingMauSac = mauSacRepository.findById(id);
        if (existingMauSac.isPresent()) {
            MauSac mauSac = existingMauSac.get();
            mauSac.setDeleted(!mauSac.getDeleted());
            mauSac.setNgayCapNhat(Instant.now());
            return mauSacRepository.save(mauSac);
        }
        return null;
    }

    public boolean checkNameExists(String tenMauSac, Integer excludeId) {
        if (excludeId != null) {
            return mauSacRepository.existsByTenMauSacAndIdNot(tenMauSac, excludeId);
        }
        return mauSacRepository.existsByTenMauSac(tenMauSac);
    }

    public boolean checkNameAndHexExists(String tenMauSac, String hex, Integer excludeId) {
        if (excludeId != null) {
            return mauSacRepository.existsByTenMauSacAndHexAndIdNot(tenMauSac, hex, excludeId);
        }
        return mauSacRepository.existsByTenMauSacAndHex(tenMauSac, hex);
    }
}
