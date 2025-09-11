package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.modal.product.DeGiay;
import com.example.sd_28_phostep_be.repository.product.DeGiayRepository;
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
public class DeGiayService {

    @Autowired
    private DeGiayRepository deGiayRepository;

    public Page<DeGiay> getAllWithPagination(String keyword, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            return deGiayRepository.findAllWithKeyword(keyword.trim(), pageable);
        } else {
            return deGiayRepository.findAllActive(pageable);
        }
    }

    public List<DeGiay> getAll() {
        return deGiayRepository.findAll();
    }

    public DeGiay save(DeGiay deGiay) {
        if (deGiay.getId() == null) {
            // Creating new DeGiay
            if (deGiayRepository.existsByMaDeGiay(deGiay.getMaDeGiay())) {
                throw new RuntimeException("Mã đế giày đã tồn tại");
            }
            deGiay.setNgayTao(Instant.now());
        } else {
            // Updating existing DeGiay
            if (deGiayRepository.existsByMaDeGiayAndIdNot(deGiay.getMaDeGiay(), deGiay.getId())) {
                throw new RuntimeException("Mã đế giày đã tồn tại");
            }
        }
        deGiay.setNgayCapNhat(Instant.now());
        deGiay.setDeleted(false);
        return deGiayRepository.save(deGiay);
    }

    public DeGiay update(Integer id, DeGiay deGiayDetails) {
        DeGiay deGiay = deGiayRepository.findByIdAndNotDeleted(id);
        if (deGiay == null) {
            throw new RuntimeException("Không tìm thấy đế giày với ID: " + id);
        }

        // Check for duplicate code
        if (deGiayRepository.existsByMaDeGiayAndIdNot(deGiayDetails.getMaDeGiay(), id)) {
            throw new RuntimeException("Mã đế giày đã tồn tại");
        }

        deGiay.setTenDeGiay(deGiayDetails.getTenDeGiay());
        deGiay.setMaDeGiay(deGiayDetails.getMaDeGiay());
        deGiay.setNgayCapNhat(Instant.now());

        return deGiayRepository.save(deGiay);
    }

    public void delete(Integer id) {
        DeGiay deGiay = deGiayRepository.findByIdAndNotDeleted(id);
        if (deGiay == null) {
            throw new RuntimeException("Không tìm thấy đế giày với ID: " + id);
        }
        
        // Soft delete
        deGiay.setDeleted(true);
        deGiay.setNgayCapNhat(Instant.now());
        deGiayRepository.save(deGiay);
    }

    public Optional<DeGiay> findById(Integer id) {
        DeGiay deGiay = deGiayRepository.findByIdAndNotDeleted(id);
        return Optional.ofNullable(deGiay);
    }

    public boolean existsByMaDeGiay(String maDeGiay) {
        return deGiayRepository.existsByMaDeGiay(maDeGiay);
    }

    public DeGiay toggleStatus(Integer id) {
        Optional<DeGiay> existingDeGiay = deGiayRepository.findById(id);
        if (existingDeGiay.isPresent()) {
            DeGiay deGiay = existingDeGiay.get();
            deGiay.setDeleted(!deGiay.getDeleted());
            deGiay.setNgayCapNhat(Instant.now());
            return deGiayRepository.save(deGiay);
        }
        return null;
    }
}
