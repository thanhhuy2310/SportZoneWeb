package com.sportzone.service;

import com.sportzone.entity.NguoiDung;
import com.sportzone.entity.Notification;
import com.sportzone.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void notifyRole(String role, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setTargetRole(role);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyUser(NguoiDung user, String title, String content, String type) {
        if (user == null) {
            return;
        }
        Notification notification = new Notification();
        notification.setTargetUser(user);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getVisibleNotifications(NguoiDung user) {
        if (user == null) {
            return List.of();
        }
        return notificationRepository.findVisibleNotifications(user.getMaND(), user.getVaiTro());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(NguoiDung user) {
        if (user == null) {
            return 0;
        }
        return notificationRepository.countUnreadVisibleNotifications(user.getMaND(), user.getVaiTro());
    }

    @Transactional
    public boolean markAsRead(Integer notificationId, NguoiDung user) {
        if (user == null) {
            return false;
        }
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification == null || !isVisibleTo(notification, user)) {
            return false;
        }
        if (!Boolean.TRUE.equals(notification.getReadStatus())) {
            notification.setReadStatus(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
        return true;
    }

    @Transactional
    public void markAllAsRead(NguoiDung user) {
        for (Notification notification : getVisibleNotifications(user)) {
            if (!Boolean.TRUE.equals(notification.getReadStatus())) {
                notification.setReadStatus(true);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }
        }
    }

    private boolean isVisibleTo(Notification notification, NguoiDung user) {
        return (notification.getTargetUser() != null
                        && user.getMaND().equals(notification.getTargetUser().getMaND()))
                || (notification.getTargetUser() == null
                        && user.getVaiTro().equals(notification.getTargetRole()));
    }
}
