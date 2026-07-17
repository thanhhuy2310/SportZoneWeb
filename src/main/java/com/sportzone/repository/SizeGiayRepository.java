package com.sportzone.repository;

import com.sportzone.entity.SizeGiay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SizeGiayRepository extends JpaRepository<SizeGiay, Integer> {

    @Query(
            value = """
                    SELECT *
                    FROM SizeGiay
                    WHERE TRY_CAST(TenSize AS INT) BETWEEN 39 AND 45
                    ORDER BY TRY_CAST(TenSize AS INT)
                    """,
            nativeQuery = true
    )
    List<SizeGiay> findMenSizes();

    @Query(
            value = """
                    SELECT *
                    FROM SizeGiay
                    WHERE TRY_CAST(TenSize AS INT) BETWEEN 36 AND 40
                    ORDER BY TRY_CAST(TenSize AS INT)
                    """,
            nativeQuery = true
    )
    List<SizeGiay> findWomenSizes();

    @Query(
            value = """
                    SELECT *
                    FROM SizeGiay
                    ORDER BY TRY_CAST(TenSize AS INT)
                    """,
            nativeQuery = true
    )
    List<SizeGiay> findAllOrderByNumber();
}
