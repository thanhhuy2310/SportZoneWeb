package com.sportzone.repository;

import com.sportzone.entity.AuditLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    List<AuditLog> findAllByOrderByCreatedAtDesc();

    List<AuditLog> findByActionContainingIgnoreCaseOrderByCreatedAtDesc(String action);
}
