package com.sportzone.controller;

import com.sportzone.entity.NguoiDung;
import com.sportzone.entity.YeuThich;
import com.sportzone.entity.YeuThichId;
import com.sportzone.repository.LoaiGiayRepository;
import com.sportzone.repository.SanPhamRepository;
import com.sportzone.repository.ThuongHieuRepository;
import com.sportzone.repository.YeuThichRepository;
import com.sportzone.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WishlistController extends BaseController {

    private final YeuThichRepository yeuThichRepository;
    private final SanPhamRepository sanPhamRepository;

    public WishlistController(
            CartService cartService,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository,
            YeuThichRepository yeuThichRepository,
            SanPhamRepository sanPhamRepository) {
        super(cartService, thuongHieuRepository, loaiGiayRepository);

        this.yeuThichRepository = yeuThichRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    @GetMapping("/wishlist")
    public String wishlist(HttpSession session, Model model) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");

        if (user == null) {
            model.addAttribute("products", java.util.List.of());

            return "wishlist";
        }

        model.addAttribute("products", yeuThichRepository.findSanPhamByMaND(user.getMaND()));

        return "wishlist";
    }

    @GetMapping("/wishlist/add/{id}")
    public String addWishlist(
            @PathVariable Integer id,
            HttpSession session,
            @RequestHeader(value = "Referer", required = false) String referer) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!yeuThichRepository.existsByNguoiDung_MaNDAndSanPham_MaSP(user.getMaND(), id)) {
            YeuThich yeuThich = new YeuThich();

            yeuThich.setId(new YeuThichId(user.getMaND(), id));

            yeuThich.setNguoiDung(user);

            yeuThich.setSanPham(sanPhamRepository.findById(id).orElse(null));

            yeuThichRepository.save(yeuThich);
        }

        return "redirect:" + (referer != null ? referer : "/products");
    }

    @GetMapping("/wishlist/remove/{id}")
    public String removeWishlist(@PathVariable Integer id, HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        yeuThichRepository.deleteByNguoiDung_MaNDAndSanPham_MaSP(user.getMaND(), id);

        return "redirect:/wishlist";
    }
}
