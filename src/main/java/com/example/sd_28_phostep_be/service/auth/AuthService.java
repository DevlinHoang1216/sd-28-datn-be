package com.example.sd_28_phostep_be.service.auth;

import com.example.sd_28_phostep_be.dto.auth.LoginRequest;
import com.example.sd_28_phostep_be.dto.auth.LoginResponse;
import com.example.sd_28_phostep_be.modal.account.TaiKhoan;
import com.example.sd_28_phostep_be.repository.account.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;
    
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // Tìm tài khoản theo tên đăng nhập (không quan tâm deleted status)
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhap(loginRequest.getTenDangNhap());
            
            if (!taiKhoanOpt.isPresent()) {
                return new LoginResponse(false, "Tên đăng nhập không tồn tại");
            }
            
            TaiKhoan taiKhoan = taiKhoanOpt.get();
            
            // Kiểm tra mật khẩu (so sánh trực tiếp, không mã hóa)
            if (!taiKhoan.getMatKhau().equals(loginRequest.getMatKhau())) {
                return new LoginResponse(false, "Mật khẩu không chính xác");
            }
            
            // Đăng nhập thành công - set deleted = true (đang đăng nhập)
            taiKhoan.setDeleted(true);
            taiKhoanRepository.save(taiKhoan);
            
            // Tạo response
            LoginResponse response = new LoginResponse();
            response.setId(taiKhoan.getId());
            response.setMa(taiKhoan.getMa());
            response.setTenDangNhap(taiKhoan.getTenDangNhap());
            response.setEmail(taiKhoan.getEmail());
            response.setSoDienThoai(taiKhoan.getSoDienThoai());
            
            // Thông tin quyền hạn
            if (taiKhoan.getIdQuyenHan() != null) {
                response.setTenQuyen(taiKhoan.getIdQuyenHan().getMa());
                response.setCapQuyenHan(taiKhoan.getIdQuyenHan().getCapQuyenHan());
            }
            
            response.setSuccess(true);
            response.setMessage("Đăng nhập thành công");
            
            return response;
            
        } catch (Exception e) {
            return new LoginResponse(false, "Lỗi hệ thống: " + e.getMessage());
        }
    }
    
    public LoginResponse logout(Integer userId) {
        try {
            if (userId != null) {
                // Tìm tài khoản theo ID
                Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findById(userId);
                
                if (taiKhoanOpt.isPresent()) {
                    TaiKhoan taiKhoan = taiKhoanOpt.get();
                    // Đăng xuất - set deleted = false (không đăng nhập)
                    taiKhoan.setDeleted(false);
                    taiKhoanRepository.save(taiKhoan);
                }
            }
            
            return new LoginResponse(true, "Đăng xuất thành công");
            
        } catch (Exception e) {
            return new LoginResponse(true, "Đăng xuất thành công"); // Vẫn trả về success dù có lỗi
        }
    }
}
