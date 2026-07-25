package com.sportzone.repository;

import com.sportzone.entity.DoiTraHang;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoiTraHangRepository extends JpaRepository<DoiTraHang, Integer> {

    List<DoiTraHang> findByNguoiDung_MaNDOrderByNgayYeuCauDesc(Integer maND);

    List<DoiTraHang> findAllByOrderByNgayYeuCauDesc();

    Optional<DoiTraHang> findByDonHang_MaDH(Integer maDH);

    boolean existsByDonHang_MaDH(Integer maDH);
}
