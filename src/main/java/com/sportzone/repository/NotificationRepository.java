package com.sportzone.repository;

import com.sportzone.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query("SELECT n FROM Notification n LEFT JOIN n.targetUser u WHERE u.maND = :userId OR (n.targetUser IS NULL AND n.targetRole = :role) ORDER BY n.createdAt DESC")
    List<Notification> findVisibleNotifications(@Param("userId") Integer userId, @Param("role") String role);

    @Query("SELECT COUNT(n) FROM Notification n LEFT JOIN n.targetUser u WHERE n.readStatus = false AND (u.maND = :userId OR (n.targetUser IS NULL AND n.targetRole = :role))")
    long countUnreadVisibleNotifications(@Param("userId") Integer userId, @Param("role") String role);
}
