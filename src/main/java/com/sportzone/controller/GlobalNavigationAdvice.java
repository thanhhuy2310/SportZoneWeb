package com.sportzone.controller;

import com.sportzone.entity.NguoiDung;
import com.sportzone.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalNavigationAdvice {

    private final NotificationService notificationService;

    public GlobalNavigationAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute("unreadNotificationCount")
    public long unreadNotificationCount(HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        return notificationService.getUnreadCount(user);
    }

    @ModelAttribute("isUser")
    public boolean isUser(HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        return user != null && "USER".equals(user.getVaiTro());
    }
}
