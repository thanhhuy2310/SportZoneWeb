package com.sportzone.repository;

import com.sportzone.entity.YeuThich;
import com.sportzone.entity.YeuThichId;
import com.sportzone.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface YeuThichRepository
        extends JpaRepository<YeuThich, YeuThichId> {

    @Query("""
            SELECT y.sanPham
            FROM YeuThich y
            WHERE y.nguoiDung.maND = :maND
            """)
    List<SanPham> findSanPhamByMaND(Integer maND);

    boolean existsByNguoiDung_MaNDAndSanPham_MaSP(
            Integer maND,
            Integer maSP
    );

    void deleteByNguoiDung_MaNDAndSanPham_MaSP(
            Integer maND,
            Integer maSP
    );
}