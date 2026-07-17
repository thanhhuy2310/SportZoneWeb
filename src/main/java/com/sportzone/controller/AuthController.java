package com.sportzone.controller;

import com.sportzone.entity.NguoiDung;
import com.sportzone.repository.DonHangRepository;
import com.sportzone.repository.LoaiGiayRepository;
import com.sportzone.repository.NguoiDungRepository;
import com.sportzone.repository.ThuongHieuRepository;
import com.sportzone.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController extends BaseController {

    private final NguoiDungRepository nguoiDungRepository;
    private final DonHangRepository donHangRepository;

    public AuthController(
            CartService cartService,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository,
            NguoiDungRepository nguoiDungRepository,
            DonHangRepository donHangRepository
    ) {
        super(cartService, thuongHieuRepository, loaiGiayRepository);
        this.nguoiDungRepository = nguoiDungRepository;
        this.donHangRepository = donHangRepository;
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
            Model model
    ) {
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
    public String register(@ModelAttribute NguoiDung nguoiDung, Model model) {
        if (nguoiDungRepository.existsByEmail(nguoiDung.getEmail())) {
            model.addAttribute("error", "Email đã tồn tại");
            model.addAttribute("nguoiDung", nguoiDung);
            return "register";
        }

        nguoiDung.setVaiTro("USER");
        nguoiDung.setTrangThai(Boolean.TRUE);
        nguoiDungRepository.save(nguoiDung);

        return "redirect:/login?registered";
    }


    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam String diaChi,
            HttpSession session
    ) {
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
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
