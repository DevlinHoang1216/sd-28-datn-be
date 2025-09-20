package com.example.sd_28_phostep_be.repository.sale.PhieuGiamGia;

import com.example.sd_28_phostep_be.modal.sale.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {
    // Tìm phiếu giảm giá theo ID (đã có sẵn từ JpaRepository)
    Optional<PhieuGiamGia> findById(Long id);

    // Tìm phiếu giảm giá theo mã
    Optional<PhieuGiamGia> findByMa(String ma);

    // Tìm phiếu giảm giá chưa bị xóa
    List<PhieuGiamGia> findByDeletedFalse();

    // Tìm phiếu giảm giá theo trạng thái
    List<PhieuGiamGia> findByTrangThaiAndDeletedFalse(Boolean trangThai);

    // Tìm phiếu giảm giá đang hoạt động
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = true AND p.deleted = false AND p.ngayBatDau <= :now AND p.ngayKetThuc >= :now")
    List<PhieuGiamGia> findActiveVouchers(@Param("now") Instant now);

    // Tìm phiếu giảm giá đã hết hạn
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.ngayKetThuc < :now AND p.deleted = false")
    List<PhieuGiamGia> findExpiredVouchers(@Param("now") Instant now);

    // Tìm phiếu giảm giá theo tên (tìm kiếm gần đúng)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.deleted = false AND (LOWER(p.tenPhieuGiamGia) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.ma) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<PhieuGiamGia> findByKeyword(@Param("keyword") String keyword);

    // Kiểm tra mã phiếu giảm giá đã tồn tại chưa (trừ ID hiện tại khi update)
    @Query("SELECT COUNT(p) > 0 FROM PhieuGiamGia p WHERE p.ma = :ma AND p.id != :id AND p.deleted = false")
    boolean existsByMaAndIdNot(@Param("ma") String ma, @Param("id") Long id);

    // Kiểm tra mã phiếu giảm giá đã tồn tại chưa (khi tạo mới)
    boolean existsByMaAndDeletedFalse(String ma);

    // Tìm phiếu giảm giá công khai đang hoạt động (cho client API)
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = true AND p.deleted = false " +
           "AND p.ngayBatDau <= :now AND p.ngayKetThuc >= :now " +
           "AND (p.riengTu = false OR p.riengTu IS NULL)")
    List<PhieuGiamGia> findActivePublicVouchers(@Param("now") Instant now);
}
