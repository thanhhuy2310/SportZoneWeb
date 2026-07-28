package com.sportzone.repository;

import com.sportzone.entity.StockMovement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {

    List<StockMovement> findByProductVariant_MaBTOrderByCreatedAtDesc(Integer maBT);

    List<StockMovement> findAllByOrderByCreatedAtDesc();
}
