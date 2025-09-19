package com.example.sd_28_phostep_be.controller.Client;

import com.example.sd_28_phostep_be.dto.account.request.KhachHang.DiaChiKhachHangRequest;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.DiaChiKhachHangResponse;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.KhachHangProfileResponse;
import com.example.sd_28_phostep_be.dto.account.response.KhachHang.OrderHistoryResponse;
import com.example.sd_28_phostep_be.dto.statistics.KhachHangOverviewResponse;
import com.example.sd_28_phostep_be.modal.account.TaiKhoan;
import com.example.sd_28_phostep_be.service.account.Client.impl.KhachHang.KhachHangClientService;
import com.example.sd_28_phostep_be.repository.account.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/khach-hang-client")
@CrossOrigin(origins = "*")
public class KhachHangClientController {

    @Autowired
    private KhachHangClientService khachHangClientService;
    
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;


    /**
     * Get current logged-in customer profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getCustomerProfile(@RequestParam Integer taiKhoanId) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            KhachHangProfileResponse profile = khachHangClientService.getCustomerProfile(taiKhoan);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy thông tin khách hàng thành công");
            response.put("data", profile);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy thông tin khách hàng: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get customer overview/dashboard statistics
     */
    @GetMapping("/overview")
    public ResponseEntity<?> getCustomerOverview(@RequestParam Integer taiKhoanId) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            KhachHangOverviewResponse overview = khachHangClientService.getCustomerOverview(taiKhoanOpt.get());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy tổng quan khách hàng thành công");
            response.put("data", overview);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy tổng quan khách hàng: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get customer order history with pagination
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getCustomerOrderHistory(
            @RequestParam Integer taiKhoanId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderHistoryResponse> orders = khachHangClientService.getCustomerOrderHistory(taiKhoanOpt.get(), pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy lịch sử đơn hàng thành công");
            response.put("data", orders);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy lịch sử đơn hàng: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get customer order details by order ID
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getCustomerOrderDetails(
            @RequestParam Integer taiKhoanId,
            @PathVariable String orderId) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            // Get order details with items
            Map<String, Object> orderDetails = khachHangClientService.getCustomerOrderDetails(taiKhoanOpt.get(), orderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy chi tiết đơn hàng thành công");
            response.put("data", orderDetails);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Debug endpoint to check customer orders
     */
    @GetMapping("/debug/orders")
    public ResponseEntity<?> debugCustomerOrders(@RequestParam Integer taiKhoanId) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            Map<String, Object> debugInfo = khachHangClientService.debugCustomerOrders(taiKhoanOpt.get());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Debug thông tin khách hàng");
            response.put("data", debugInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi debug: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all addresses for current customer
     */
    @GetMapping("/addresses")
    public ResponseEntity<?> getCustomerAddresses(@RequestParam Integer taiKhoanId) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            List<DiaChiKhachHangResponse> addresses = khachHangClientService.getCustomerAddresses(taiKhoanOpt.get());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lấy danh sách địa chỉ thành công");
            response.put("data", addresses);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi lấy danh sách địa chỉ: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Add new address for current customer
     */
    @PostMapping("/addresses")
    public ResponseEntity<?> addCustomerAddress(
            @RequestParam Integer taiKhoanId,
            @RequestBody DiaChiKhachHangRequest request) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            DiaChiKhachHangResponse newAddress = khachHangClientService.addCustomerAddress(taiKhoanOpt.get(), request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thêm địa chỉ thành công");
            response.put("data", newAddress);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi thêm địa chỉ: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Update customer address
     */
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<?> updateCustomerAddress(
            @RequestParam Integer taiKhoanId,
            @PathVariable Integer addressId,
            @RequestBody DiaChiKhachHangRequest request) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            DiaChiKhachHangResponse updatedAddress = khachHangClientService.updateCustomerAddress(taiKhoanOpt.get(), addressId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật địa chỉ thành công");
            response.put("data", updatedAddress);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi cập nhật địa chỉ: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Delete customer address
     */
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<?> deleteCustomerAddress(
            @RequestParam Integer taiKhoanId,
            @PathVariable Integer addressId) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            khachHangClientService.deleteCustomerAddress(taiKhoanOpt.get(), addressId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa địa chỉ thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi xóa địa chỉ: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Set address as default
     */
    @PutMapping("/addresses/{addressId}/set-default")
    public ResponseEntity<?> setDefaultAddress(
            @RequestParam Integer taiKhoanId,
            @PathVariable Integer addressId) {
        try {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(taiKhoanId);
            if (taiKhoanOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Không tìm thấy tài khoản");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            khachHangClientService.setDefaultAddress(taiKhoanOpt.get(), addressId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đặt địa chỉ mặc định thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi đặt địa chỉ mặc định: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
