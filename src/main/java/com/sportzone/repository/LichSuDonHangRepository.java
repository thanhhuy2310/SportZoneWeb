package com.sportzone.repository;

import com.sportzone.entity.LichSuDonHang;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LichSuDonHangRepository extends JpaRepository<LichSuDonHang, Integer> {

    List<LichSuDonHang> findByDonHang_MaDHOrderByThoiGianAscMaLSAsc(Integer maDH);
}
