package com.sportzone.repository;

import com.sportzone.entity.NguoiDung;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    Optional<NguoiDung> findByEmailAndMatKhau(String email, String matKhau);

    boolean existsByEmail(String email);
}
