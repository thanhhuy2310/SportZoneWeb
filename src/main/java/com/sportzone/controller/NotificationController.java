package com.sportzone.controller;

import com.sportzone.entity.NguoiDung;
import com.sportzone.repository.LoaiGiayRepository;
import com.sportzone.repository.ThuongHieuRepository;
import com.sportzone.service.CartService;
import com.sportzone.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotificationController extends BaseController {

    private final NotificationService notificationService;

    public NotificationController(
            CartService cartService,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository,
            NotificationService notificationService) {
        super(cartService, thuongHieuRepository, loaiGiayRepository);
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("notifications", notificationService.getVisibleNotifications(user));
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Integer id, HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        notificationService.markAsRead(id, user);
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead(HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        notificationService.markAllAsRead(user);
        return "redirect:/notifications";
    }
}
