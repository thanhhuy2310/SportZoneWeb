package com.sportzone.repository;

import com.sportzone.entity.ProductViewHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductViewHistoryRepository extends JpaRepository<ProductViewHistory, Integer> {

    Optional<ProductViewHistory> findByUser_MaNDAndProduct_MaSP(Integer maND, Integer maSP);

    List<ProductViewHistory> findTop10ByUser_MaNDOrderByViewedAtDesc(Integer maND);
}
