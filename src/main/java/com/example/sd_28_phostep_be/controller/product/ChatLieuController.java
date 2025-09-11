package com.example.sd_28_phostep_be.controller.product;

import com.example.sd_28_phostep_be.modal.product.ChatLieu;
import com.example.sd_28_phostep_be.service.product.ChatLieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat-lieu")
@CrossOrigin(origins = "*")
public class ChatLieuController {

    @Autowired
    private ChatLieuService chatLieuService;

    @GetMapping
    public ResponseEntity<Page<ChatLieu>> getAllWithPagination(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        Page<ChatLieu> chatLieuPage = chatLieuService.getAllWithPagination(keyword, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(chatLieuPage);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ChatLieu>> getAll() {
        List<ChatLieu> chatLieuList = chatLieuService.getAll();
        return ResponseEntity.ok(chatLieuList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChatLieu> getById(@PathVariable Integer id) {
        Optional<ChatLieu> chatLieu = chatLieuService.findById(id);
        return chatLieu.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ChatLieu chatLieu) {
        try {
            // Validate required fields
            if (chatLieu.getTenChatLieu() == null || chatLieu.getTenChatLieu().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên chất liệu không được để trống");
            }
            
            if (chatLieu.getMaChatLieu() == null || chatLieu.getMaChatLieu().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã chất liệu không được để trống");
            }

            // Check if code already exists
            if (chatLieuService.existsByMaChatLieu(chatLieu.getMaChatLieu())) {
                return ResponseEntity.badRequest().body("Mã chất liệu đã tồn tại");
            }

            ChatLieu savedChatLieu = chatLieuService.save(chatLieu);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedChatLieu);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi tạo chất liệu: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody ChatLieu chatLieu) {
        try {
            // Validate required fields
            if (chatLieu.getTenChatLieu() == null || chatLieu.getTenChatLieu().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên chất liệu không được để trống");
            }
            
            if (chatLieu.getMaChatLieu() == null || chatLieu.getMaChatLieu().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã chất liệu không được để trống");
            }

            // Check if code already exists for other records
            if (chatLieuService.existsByMaChatLieuAndIdNot(chatLieu.getMaChatLieu(), id)) {
                return ResponseEntity.badRequest().body("Mã chất liệu đã tồn tại");
            }

            ChatLieu updatedChatLieu = chatLieuService.update(id, chatLieu);
            if (updatedChatLieu != null) {
                return ResponseEntity.ok(updatedChatLieu);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật chất liệu: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<?> toggleStatus(@PathVariable Integer id) {
        try {
            ChatLieu updatedChatLieu = chatLieuService.toggleStatus(id);
            if (updatedChatLieu != null) {
                String statusText = updatedChatLieu.getDeleted() ? "vô hiệu hóa" : "kích hoạt";
                return ResponseEntity.ok().body("Đã " + statusText + " chất liệu thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi cập nhật trạng thái chất liệu: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            boolean deleted = chatLieuService.deleteById(id);
            if (deleted) {
                return ResponseEntity.ok().body("Xóa chất liệu thành công");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Có lỗi xảy ra khi xóa chất liệu: " + e.getMessage());
        }
    }
}
