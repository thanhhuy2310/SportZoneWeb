package com.sportzone.repository;

import com.sportzone.entity.MaGiamGia;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Integer> {

    Optional<MaGiamGia> findByCodeIgnoreCase(String code);
}
