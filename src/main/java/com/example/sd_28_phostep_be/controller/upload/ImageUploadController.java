package com.example.sd_28_phostep_be.controller.upload;

import com.example.sd_28_phostep_be.service.upload.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ImageUploadController {
    
    private final CloudinaryService cloudinaryService;
    
    public ImageUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Upload endpoint is working");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Received file upload request");
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File không được để trống");
            }
            
            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("File phải là hình ảnh");
            }
            
            // Check file size (max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Kích thước file không được vượt quá 10MB");
            }
            
            System.out.println("Starting Cloudinary upload...");
            String imageUrl = cloudinaryService.uploadImage(file);
            System.out.println("Upload successful, URL: " + imageUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("message", "Tải ảnh lên thành công");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Upload error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Lỗi khi tải ảnh lên: " + e.getMessage());
        }
    }
}
