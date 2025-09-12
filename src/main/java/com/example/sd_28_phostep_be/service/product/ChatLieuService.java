package com.example.sd_28_phostep_be.service.product;

import com.example.sd_28_phostep_be.modal.product.ChatLieu;
import com.example.sd_28_phostep_be.repository.product.ChatLieuRepository;
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
public class ChatLieuService {

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    public Page<ChatLieu> getAllWithPagination(String keyword, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return chatLieuRepository.findAllWithKeyword(keyword, pageable);
    }

    public List<ChatLieu> getAll() {
        return chatLieuRepository.findAll();
    }

    public Optional<ChatLieu> findById(Integer id) {
        return chatLieuRepository.findById(id);
    }

    public ChatLieu save(ChatLieu chatLieu) {
        if (chatLieu.getId() == null) {
            chatLieu.setNgayTao(Instant.now());
            chatLieu.setDeleted(false);
        }
        chatLieu.setNgayCapNhat(Instant.now());
        return chatLieuRepository.save(chatLieu);
    }

    public ChatLieu update(Integer id, ChatLieu chatLieu) {
        Optional<ChatLieu> existingChatLieu = chatLieuRepository.findById(id);
        if (existingChatLieu.isPresent()) {
            ChatLieu updated = existingChatLieu.get();
            updated.setTenChatLieu(chatLieu.getTenChatLieu());
            updated.setNgayCapNhat(Instant.now());
            return chatLieuRepository.save(updated);
        }
        return null;
    }


    public ChatLieu toggleStatus(Integer id) {
        Optional<ChatLieu> existingChatLieu = chatLieuRepository.findById(id);
        if (existingChatLieu.isPresent()) {
            ChatLieu chatLieu = existingChatLieu.get();
            chatLieu.setDeleted(!chatLieu.getDeleted());
            chatLieu.setNgayCapNhat(Instant.now());
            return chatLieuRepository.save(chatLieu);
        }
        return null;
    }

    public boolean checkNameExists(String tenChatLieu, Integer excludeId) {
        if (excludeId != null) {
            return chatLieuRepository.existsByTenChatLieuAndIdNot(tenChatLieu, excludeId);
        }
        return chatLieuRepository.existsByTenChatLieu(tenChatLieu);
    }
}
