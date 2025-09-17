package com.example.sd_28_phostep_be.service.auth;

import com.example.sd_28_phostep_be.dto.auth.LoginRequest;
import com.example.sd_28_phostep_be.dto.auth.LoginResponse;
import com.example.sd_28_phostep_be.modal.account.KhachHang;
import com.example.sd_28_phostep_be.modal.account.NhanVien;
import com.example.sd_28_phostep_be.modal.account.TaiKhoan;
import com.example.sd_28_phostep_be.repository.account.KhachHang.KhachHangRepository;
import com.example.sd_28_phostep_be.repository.account.NhanVien.NhanVienRepository;
import com.example.sd_28_phostep_be.repository.account.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthClientService {
    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // Tìm tài khoản theo tên đăng nhập
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhap(loginRequest.getTenDangNhap());

            if (!taiKhoanOpt.isPresent()) {
                return new LoginResponse(false, "Tên đăng nhập không tồn tại");
            }

            TaiKhoan taiKhoan = taiKhoanOpt.get();

            // Kiểm tra mật khẩu
            if (!taiKhoan.getMatKhau().equals(loginRequest.getMatKhau())) {
                return new LoginResponse(false, "Mật khẩu không chính xác");
            }

            // Kiểm tra quyền hạn
            if (taiKhoan.getIdQuyenHan() == null) {
                return new LoginResponse(false, "Tài khoản chưa được phân quyền");
            }

            Integer capQuyenHan = taiKhoan.getIdQuyenHan().getCapQuyenHan();

            // Cho phép admin (1), nhân viên (2) và khách hàng (3) đăng nhập
            if (capQuyenHan == null || (capQuyenHan != 1 && capQuyenHan != 2 && capQuyenHan != 3)) {
                return new LoginResponse(false, "Tài khoản không có quyền truy cập hệ thống");
            }

            // Nếu là nhân viên (capQuyenHan = 2), kiểm tra trạng thái nhân viên
            if (capQuyenHan == 2) {
                Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByIdTaiKhoan(taiKhoan);

                if (!nhanVienOpt.isPresent()) {
                    return new LoginResponse(false, "Không tìm thấy thông tin nhân viên");
                }

                NhanVien nhanVien = nhanVienOpt.get();

                // Kiểm tra nhân viên có hoạt động không (deleted = true thì cho đăng nhập, deleted = false thì không cho)
                if (nhanVien.getDeleted() == null || !nhanVien.getDeleted()) {
                    return new LoginResponse(false, "Tài khoản nhân viên đã bị vô hiệu hóa");
                }
            }

            // Nếu là khách hàng (capQuyenHan = 3), kiểm tra trạng thái khách hàng
            if (capQuyenHan == 3) {
                Optional<KhachHang> khachHangOpt = khachHangRepository.findByTaiKhoan(taiKhoan);

                if (!khachHangOpt.isPresent()) {
                    return new LoginResponse(false, "Không tìm thấy thông tin khách hàng");
                }

                KhachHang khachHang = khachHangOpt.get();

                // Kiểm tra khách hàng có hoạt động không (deleted = true thì cho đăng nhập, deleted = false thì không cho)
                if (khachHang.getDeleted() == null || !khachHang.getDeleted()) {
                    return new LoginResponse(false, "Tài khoản khách hàng đã bị vô hiệu hóa");
                }
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
            response.setTenQuyen(taiKhoan.getIdQuyenHan().getMa());
            response.setCapQuyenHan(capQuyenHan);

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
