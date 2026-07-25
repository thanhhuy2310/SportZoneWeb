package com.sportzone.controller;

import com.sportzone.repository.LoaiGiayRepository;
import com.sportzone.repository.ThuongHieuRepository;
import com.sportzone.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class BaseController {

    protected final CartService cartService;
    protected final ThuongHieuRepository thuongHieuRepository;
    protected final LoaiGiayRepository loaiGiayRepository;

    public BaseController(
            CartService cartService,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository) {
        this.cartService = cartService;
        this.thuongHieuRepository = thuongHieuRepository;
        this.loaiGiayRepository = loaiGiayRepository;
    }

    @ModelAttribute("cartCount")
    public int cartCount(HttpSession session) {
        return cartService.count(session);
    }

    @ModelAttribute("loginUser")
    public Object loginUser(HttpSession session) {
        return session.getAttribute("user");
    }

    @ModelAttribute("navBrands")
    public Object navBrands() {
        return thuongHieuRepository.findAll();
    }

    @ModelAttribute("navCategories")
    public Object navCategories() {
        return loaiGiayRepository.findAll();
    }
}
