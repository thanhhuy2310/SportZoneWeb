package com.sportzone.repository;
import com.sportzone.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface NguoiDungRepository extends JpaRepository<NguoiDung,Integer>{
    Optional<NguoiDung> findByEmailAndMatKhau(String email,String matKhau);
    boolean existsByEmail(String email);
}
