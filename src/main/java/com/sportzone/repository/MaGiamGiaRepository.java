package com.sportzone.repository;

import com.sportzone.entity.MaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Integer> {

    Optional<MaGiamGia> findByCodeIgnoreCase(String code);
}