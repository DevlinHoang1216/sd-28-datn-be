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
        }
        mauSac.setNgayCapNhat(Instant.now());
        return mauSacRepository.save(mauSac);
    }

    public MauSac update(Integer id, MauSac mauSac) {
        Optional<MauSac> existingMauSac = mauSacRepository.findById(id);
        if (existingMauSac.isPresent()) {
            MauSac updated = existingMauSac.get();
            updated.setTenMauSac(mauSac.getTenMauSac());
            updated.setMaMauSac(mauSac.getMaMauSac());
            updated.setHex(mauSac.getHex());
            updated.setNgayCapNhat(Instant.now());
            return mauSacRepository.save(updated);
        }
        return null;
    }

    public boolean deleteById(Integer id) {
        if (mauSacRepository.existsById(id)) {
            mauSacRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByMaMauSac(String maMauSac) {
        return mauSacRepository.existsByMaMauSac(maMauSac);
    }

    public boolean existsByMaMauSacAndIdNot(String maMauSac, Integer id) {
        return mauSacRepository.existsByMaMauSacAndIdNot(maMauSac, id);
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
}
