# 📋 Phân tích Logic Thêm Khách Hàng

## 🔍 Luồng tạo khách hàng hiện tại

### Bước 1: Tạo TaiKhoan (với quyền hạn ID = 3)
```java
QuyenHan quyenHan = quyenHanRepository.findById(3)
TaiKhoan taiKhoan = TaiKhoan.builder()
    .ma(generateTaiKhoanMa())
    .tenDangNhap(generateTenDangNhap(request.getTen(), request.getNgaySinh()))
    .email(request.getEmail())
    .soDienThoai(request.getSoDienThoai())
    .matKhau(request.getMatKhau())
    .idQuyenHan(quyenHan)
    .deleted(false)
    .build();
taiKhoan = taiKhoanRepository.save(taiKhoan);
```

### Bước 2: Tạo KhachHang (liên kết với TaiKhoan)
```java
KhachHang khachHang = KhachHang.builder()
    .taiKhoan(taiKhoan)
    .ma(generateKhachHangMa())
    .ten(request.getTen())
    .gioiTinh(request.getGioiTinh())
    .ngaySinh(request.getNgaySinh())
    .cccd(request.getCccd())
    .deleted(false)
    .createdAt(Instant.now())
    .updatedAt(Instant.now())
    .build();
khachHang = khachHangRepository.save(khachHang);
```

### Bước 3: Tạo DiaChiKhachHang (nếu có thông tin địa chỉ)
```java
DiaChiKhachHang diaChi = DiaChiKhachHang.builder()
    .idKhachHang(khachHang)
    .ma(generateDiaChiMa())
    .thanhPho(request.getThanhPho())
    .quan(request.getQuan())
    .phuong(request.getPhuong())
    .diaChiCuThe(request.getDiaChiCuThe())
    .macDinh(true)
    .deleted(false)
    .build();
diaChi = diaChiKhachHangRepository.save(diaChi);
khachHang.setIdDiaChiKhachHang(diaChi);
khachHang = khachHangRepository.save(khachHang);
```

## ⚠️ Vấn đề phát hiện

### 1. **Validation thiếu**
- Không kiểm tra email/soDienThoai trùng lặp
- Không validate định dạng dữ liệu đầu vào
- Không kiểm tra quyền hạn ID=3 có tồn tại không

### 2. **Xử lý lỗi không đầy đủ**
- Nếu tạo TaiKhoan thành công nhưng tạo KhachHang thất bại → dữ liệu rác
- Nếu tạo KhachHang thành công nhưng tạo địa chỉ thất bại → không rollback

### 3. **Logic địa chỉ**
- Địa chỉ chỉ được tạo nếu CẢ 4 trường đều có giá trị
- Nên cho phép tạo khách hàng mà không cần địa chỉ

## ✅ Đề xuất cải thiện

### 1. Thêm validation
### 2. Cải thiện transaction handling
### 3. Tối ưu logic tạo địa chỉ
### 4. Thêm logging để debug
