package com.sportzone.controller;

import com.sportzone.entity.NguoiDung;
import com.sportzone.repository.DonHangRepository;
import com.sportzone.repository.LoaiGiayRepository;
import com.sportzone.repository.NguoiDungRepository;
import com.sportzone.repository.ThuongHieuRepository;
import com.sportzone.service.CartService;
import com.sportzone.service.AuditLogService;
import com.sportzone.service.NotificationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController extends BaseController {

    private final NguoiDungRepository nguoiDungRepository;
    private final DonHangRepository donHangRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public AuthController(
            CartService cartService,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository,
            NguoiDungRepository nguoiDungRepository,
            DonHangRepository donHangRepository,
            AuditLogService auditLogService,
            NotificationService notificationService) {
        super(cartService, thuongHieuRepository, loaiGiayRepository);
        this.nguoiDungRepository = nguoiDungRepository;
        this.donHangRepository = donHangRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String matKhau,
            HttpSession session,
            Model model,
            HttpServletRequest request) {
        var user = nguoiDungRepository.findByEmailAndMatKhau(email, matKhau);

        if (user.isEmpty()) {
            model.addAttribute("error", "Sai email hoặc mật khẩu");
            return "login";
        }

        if (Boolean.FALSE.equals(user.get().getTrangThai())) {
            model.addAttribute("error", "Tài khoản đã bị khóa");
            return "login";
        }

        session.setAttribute("user", user.get());
        auditLogService.record(
                user.get(),
                "LOGIN",
                "NguoiDung",
                user.get().getMaND(),
                null,
                null,
                auditLogService.getClientIp(request),
                "SUCCESS");

        String role = user.get().getVaiTro();
        if ("ADMIN".equals(role) || "NHANVIEN".equals(role)) {
            return "redirect:/admin";
        }

        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute NguoiDung nguoiDung, Model model, HttpServletRequest request) {
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            model.addAttribute("error", "Email đã tồn tại");
            model.addAttribute("nguoiDung", nguoiDung);
            return "register";
        }

        nguoiDung.setVaiTro("USER");
        nguoiDung.setTrangThai(Boolean.TRUE);
        NguoiDung savedUser = nguoiDungRepository.save(nguoiDung);
        auditLogService.record(
                savedUser,
                "CREATE_USER",
                "NguoiDung",
                savedUser.getMaND(),
                null,
                savedUser.getEmail(),
                auditLogService.getClientIp(request),
                "SUCCESS");
        notificationService.notifyRole(
                "ADMIN", "New user registered", savedUser.getEmail() + " has registered.", "NEW_USER");

        return "redirect:/login?registered";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam String diaChi,
            HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        var currentUser = nguoiDungRepository.findById(user.getMaND()).orElseThrow();
        currentUser.setHoTen(hoTen);
        currentUser.setSoDienThoai(soDienThoai);
        currentUser.setDiaChi(diaChi);

        nguoiDungRepository.save(currentUser);
        session.setAttribute("user", currentUser);

        return "redirect:/profile?updated";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user != null) {
            auditLogService.record(
                    user,
                    "LOGOUT",
                    "NguoiDung",
                    user.getMaND(),
                    null,
                    null,
                    auditLogService.getClientIp(request),
                    "SUCCESS");
        }
        cartService.clear(session);
        Cookie rememberMe = new Cookie("remember-me", "");
        rememberMe.setPath("/");
        rememberMe.setMaxAge(0);
        response.addCookie(rememberMe);
        session.invalidate();
        return "redirect:/login";
    }
}
