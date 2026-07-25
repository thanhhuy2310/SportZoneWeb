package com.sportzone.repository;

import com.sportzone.entity.DonHang;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    List<DonHang> findByNguoiDung_MaNDOrderByNgayDatDesc(Integer maND);

    List<DonHang> findByNguoiDung_MaND(Integer maND);

    List<DonHang> findTop5ByOrderByNgayDatDesc();

    List<DonHang> findAllByOrderByNgayDatDesc();

    @Query(
            """
            SELECT COALESCE(SUM(d.tongTien), 0)
            FROM DonHang d
            WHERE d.trangThaiThanhToan = 'Paid'
                  OR d.trangThaiDonHang = 'Delivered'
            """)
    BigDecimal doanhThu();

    @Query(
            """
            SELECT MONTH(d.ngayDat), COALESCE(SUM(d.tongTien), 0), COUNT(d)
            FROM DonHang d
            WHERE d.trangThaiThanhToan = 'Paid'
                  OR d.trangThaiDonHang = 'Delivered'
            GROUP BY MONTH(d.ngayDat)
            ORDER BY MONTH(d.ngayDat)
            """)
    List<Object[]> doanhThuVaDonHangTheoThang();
}
