package com.sportzone.repository;

import com.sportzone.entity.BienTheSanPham;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BienTheSanPhamRepository extends JpaRepository<BienTheSanPham, Integer> {

    Optional<BienTheSanPham> findFirstBySanPham_MaSP(Integer maSP);

    List<BienTheSanPham> findBySanPham_MaSP(Integer maSP);

    @Query(
            """
            SELECT bt
            FROM BienTheSanPham bt
            JOIN FETCH bt.sanPham sp
            JOIN FETCH bt.sizeGiay sg
            JOIN FETCH bt.mauSac ms
            WHERE sp.maSP = :maSP
                AND bt.trangThai = true
                AND bt.soLuongTon > 0
            ORDER BY sg.tenSize, ms.tenMau
            """)
    List<BienTheSanPham> findAvailableVariantsByProduct(@Param("maSP") Integer maSP);

    @Query(
            """
            SELECT COALESCE(SUM(bt.soLuongTon), 0)
            FROM BienTheSanPham bt
            WHERE bt.sanPham.maSP = :maSP
                AND bt.trangThai = true
            """)
    Integer tongTonKhoTheoSanPham(@Param("maSP") Integer maSP);
}
