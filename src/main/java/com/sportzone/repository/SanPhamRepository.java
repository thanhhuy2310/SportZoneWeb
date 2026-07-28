package com.sportzone.repository;

import com.sportzone.entity.SanPham;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    @Query(
            """
            SELECT DISTINCT sp
            FROM SanPham sp
            LEFT JOIN BienTheSanPham bt ON bt.sanPham = sp
            WHERE (:q IS NULL OR LOWER(sp.tenSP) LIKE LOWER(CONCAT('%', :q, '%')))
                AND (:brand IS NULL OR sp.thuongHieu.maTH = :brand)
                AND (:category IS NULL OR sp.loaiGiay.maLoai = :category)
                AND (:size IS NULL OR bt.sizeGiay.maSize = :size)
                AND (:sale IS NULL OR (
                            :sale = true
                            AND sp.giaKhuyenMai IS NOT NULL
                            AND sp.giaKhuyenMai > 0
                            AND sp.giaKhuyenMai < sp.gia
                ))
            ORDER BY sp.ngayTao DESC
            """)
    List<SanPham> search(
            @Param("q") String q,
            @Param("brand") Integer brand,
            @Param("category") Integer category,
            @Param("size") Integer size,
            @Param("sale") Boolean sale);

    List<SanPham> findTop8ByOrderByLuotXemDesc();

    List<SanPham> findTop8ByOrderByNgayTaoDesc();

    List<SanPham> findByLoaiGiay_MaLoaiOrderByLuotXemDesc(Integer maLoai, Pageable pageable);

    List<SanPham> findByThuongHieu_MaTHOrderByLuotXemDesc(Integer maTH, Pageable pageable);

    List<SanPham> findAllByOrderByLuotXemDesc(Pageable pageable);

    @Query(
            """
            SELECT sp
            FROM SanPham sp
            WHERE sp.giaKhuyenMai IS NOT NULL
                AND sp.giaKhuyenMai > 0
                AND sp.giaKhuyenMai < sp.gia
            """)
    List<SanPham> findSaleProducts();
}
