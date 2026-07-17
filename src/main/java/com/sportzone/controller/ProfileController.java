package com.sportzone.controller;

import com.sportzone.entity.DoiTraHang;
import com.sportzone.entity.DonHang;
import com.sportzone.entity.NguoiDung;
import com.sportzone.repository.DoiTraHangRepository;
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
public class ProfileController extends BaseController {

    private final DonHangRepository donHangRepository;
    private final DoiTraHangRepository doiTraHangRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public ProfileController(
            CartService cartService,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository,
            DonHangRepository donHangRepository,
            DoiTraHangRepository doiTraHangRepository,
            NguoiDungRepository nguoiDungRepository
    ) {
        super(cartService, thuongHieuRepository, loaiGiayRepository);
        this.donHangRepository = donHangRepository;
        this.doiTraHangRepository = doiTraHangRepository;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("loginUser", user);
        model.addAttribute("orders", donHangRepository.findByNguoiDung_MaNDOrderByNgayDatDesc(user.getMaND()));
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(HttpSession session, Model model) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("loginUser", user);
        model.addAttribute("user", user);
        return "profile-edit";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String hoTen,
            @RequestParam String email,
            @RequestParam(required = false) String soDienThoai,
            @RequestParam(required = false) String diaChi,
            HttpSession session
    ) {
        NguoiDung userSession = (NguoiDung) session.getAttribute("user");
        if (userSession == null) {
            return "redirect:/login";
        }

        NguoiDung user = nguoiDungRepository.findById(userSession.getMaND()).orElseThrow();
        user.setHoTen(hoTen);
        user.setEmail(email);
        user.setSoDienThoai(soDienThoai);
        user.setDiaChi(diaChi);

        nguoiDungRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/profile?updated";
    }

    @GetMapping("/settings")
    public String settings(HttpSession session, Model model) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("loginUser", user);
        return "settings";
    }

    @GetMapping("/invoice/{id}")
    public String invoice(@PathVariable Integer id, HttpSession session, Model model) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        DonHang order = donHangRepository.findById(id).orElseThrow();
        boolean isOwner = order.getNguoiDung() != null && order.getNguoiDung().getMaND().equals(user.getMaND());
        boolean isAdmin = "ADMIN".equals(user.getVaiTro()) || "NHANVIEN".equals(user.getVaiTro());

        if (!isOwner && !isAdmin) {
            return "redirect:/profile";
        }

        model.addAttribute("loginUser", user);
        model.addAttribute("order", order);
        return "invoice";
    }

    @PostMapping("/returns/request/{id}")
    public String requestReturn(
            @PathVariable Integer id,
            @RequestParam(required = false) String lyDo,
            HttpSession session
    ) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        DonHang order = donHangRepository.findById(id).orElseThrow();
        boolean isOwner = order.getNguoiDung() != null && order.getNguoiDung().getMaND().equals(user.getMaND());

        if (!isOwner || !"Delivered".equals(order.getTrangThaiDonHang()) || doiTraHangRepository.existsByDonHang_MaDH(id)) {
            return "redirect:/profile?returnError";
        }

        DoiTraHang request = new DoiTraHang();
        request.setDonHang(order);
        request.setNguoiDung(user);
        request.setLyDo(lyDo == null || lyDo.isBlank() ? "Customer return request" : lyDo);
        request.setTrangThai("Requested");
        doiTraHangRepository.save(request);

        order.setTrangThaiDonHang("Return Requested");
        donHangRepository.save(order);

        return "redirect:/profile?returnRequested";
    }
}
