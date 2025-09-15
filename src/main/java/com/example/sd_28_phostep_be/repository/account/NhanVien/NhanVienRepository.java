package com.example.sd_28_phostep_be.repository.account.NhanVien;

import com.example.sd_28_phostep_be.dto.account.response.NhanVien.NhanVienDTOResponse;
import com.example.sd_28_phostep_be.modal.account.NhanVien;
import com.example.sd_28_phostep_be.modal.account.TaiKhoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    @Query("""
            SELECT new com.example.sd_28_phostep_be.dto.account.response.NhanVien.NhanVienDTOResponse(
            nv.id,
            nv.ma,
            nv.tenNhanVien,
            nv.ngaySinh,
            nv.gioiTinh,
            nv.idTaiKhoan.soDienThoai,
            nv.cccd,
            nv.diaChiCuThe,
            nv.deleted
            ) 
            FROM NhanVien nv
            WHERE (
            :keyword IS NULL
            OR nv.ma LIKE %:keyword%
            OR nv.tenNhanVien LIKE %:keyword%
            OR nv.idTaiKhoan.soDienThoai LIKE %:keyword%
            OR nv.cccd LIKE %:keyword%
            OR nv.diaChiCuThe LIKE %:keyword%
            OR (:keywordAsDate IS NOT NULL AND nv.ngaySinh = :keywordAsDate)
            )
            AND (:gioiTinh IS NULL OR nv.gioiTinh = :gioiTinh)
            AND (:trangThai IS NULL OR nv.deleted = :trangThai)
            """)
    Page<NhanVienDTOResponse> getAllNV(
            @Param("keyword") String keyWord,
            @Param("keywordAsDate") Date keywordAsDate,
            @Param("gioiTinh") Boolean gioiTinh,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable);

    // Tìm nhân viên theo tài khoản
    Optional<NhanVien> findByIdTaiKhoan(TaiKhoan taiKhoan);

}
