package com.sportzone.repository;

import com.sportzone.entity.ChiTietDonHang;
import com.sportzone.entity.SanPham;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Integer> {

    @Query(
            """
            SELECT ct2.bienThe.sanPham
            FROM ChiTietDonHang ct1
            JOIN ct1.donHang d
            JOIN ChiTietDonHang ct2 ON ct2.donHang = d
            WHERE ct1.bienThe.sanPham.maSP = :productId
                AND ct2.bienThe.sanPham.maSP <> :productId
                AND d.trangThaiDonHang NOT IN ('Cancelled', 'Returned')
            GROUP BY ct2.bienThe.sanPham
            ORDER BY COUNT(ct2) DESC
            """)
    List<SanPham> findFrequentlyBoughtTogether(@Param("productId") Integer productId);

    @Query(
            """
            SELECT DISTINCT ct.bienThe.sanPham.maSP
            FROM ChiTietDonHang ct
            WHERE ct.donHang.nguoiDung.maND = :userId
                AND ct.donHang.trangThaiDonHang NOT IN ('Cancelled', 'Returned')
            """)
    List<Integer> findPurchasedProductIds(@Param("userId") Integer userId);
}
