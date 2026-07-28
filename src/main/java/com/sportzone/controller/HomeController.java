package com.sportzone.controller;

import com.sportzone.entity.LienHe;
import com.sportzone.repository.BienTheSanPhamRepository;
import com.sportzone.repository.LienHeRepository;
import com.sportzone.repository.LoaiGiayRepository;
import com.sportzone.repository.SanPhamRepository;
import com.sportzone.repository.SizeGiayRepository;
import com.sportzone.repository.ThuongHieuRepository;
import com.sportzone.service.CartService;
import com.sportzone.service.ProductRecommendationService;
import com.sportzone.service.RecentlyViewedProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController extends BaseController {

    private final SanPhamRepository sanPhamRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final SizeGiayRepository sizeGiayRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final LienHeRepository lienHeRepository;
    private final RecentlyViewedProductService recentlyViewedProductService;
    private final ProductRecommendationService productRecommendationService;

    public HomeController(
            CartService cartService,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository,
            SanPhamRepository sanPhamRepository,
            SizeGiayRepository sizeGiayRepository,
            BienTheSanPhamRepository bienTheSanPhamRepository,
            LienHeRepository lienHeRepository,
            RecentlyViewedProductService recentlyViewedProductService,
            ProductRecommendationService productRecommendationService) {
        super(cartService, thuongHieuRepository, loaiGiayRepository);

        this.sanPhamRepository = sanPhamRepository;
        this.thuongHieuRepository = thuongHieuRepository;
        this.sizeGiayRepository = sizeGiayRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.lienHeRepository = lienHeRepository;
        this.recentlyViewedProductService = recentlyViewedProductService;
        this.productRecommendationService = productRecommendationService;
    }

    @ModelAttribute("menSizes")
    public Object menSizes() {
        return sizeGiayRepository.findMenSizes();
    }

    @ModelAttribute("womenSizes")
    public Object womenSizes() {
        return sizeGiayRepository.findWomenSizes();
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featured", sanPhamRepository.findTop8ByOrderByLuotXemDesc());
        model.addAttribute("newProducts", sanPhamRepository.findTop8ByOrderByNgayTaoDesc());
        model.addAttribute("saleProducts", sanPhamRepository.findSaleProducts());

        return "home";
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer brand,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Boolean sale,
            Model model) {
        model.addAttribute("products", sanPhamRepository.search(q, brand, category, size, sale));
        model.addAttribute("allSizes", sizeGiayRepository.findAllOrderByNumber());

        model.addAttribute("q", q);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSize", size);
        model.addAttribute("sale", sale);

        return "products";
    }

    @GetMapping("/products/{id}")
    public String detail(@PathVariable Integer id, Model model, HttpSession session) {
        var product = sanPhamRepository.findById(id).orElse(null);
        var user = (com.sportzone.entity.NguoiDung) session.getAttribute("user");

        model.addAttribute("product", product);

        if (product != null) {
            recentlyViewedProductService.recordView(user, product);
            model.addAttribute("recentlyViewed", recentlyViewedProductService.getRecentlyViewed(user, id));
            model.addAttribute("recommendations", productRecommendationService.getRecommendations(product, user));
            model.addAttribute(
                    "frequentlyBoughtTogether", productRecommendationService.getFrequentlyBoughtTogether(product));
            var variants = bienTheSanPhamRepository.findAvailableVariantsByProduct(id);

            model.addAttribute("variants", variants);
            model.addAttribute("tongTonKho", bienTheSanPhamRepository.tongTonKhoTheoSanPham(id));

            model.addAttribute(
                    "sizes",
                    variants.stream().map(v -> v.getSizeGiay()).filter(s -> s != null).distinct().toList());

            model.addAttribute(
                    "colors",
                    variants.stream().map(v -> v.getMauSac()).filter(m -> m != null).distinct().toList());
        }

        return "product-detail";
    }

    @GetMapping("/brands")
    public String brands(Model model) {
        model.addAttribute("brands", thuongHieuRepository.findAll());

        return "brands";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("lienHe", new LienHe());

        return "contact";
    }

    @PostMapping("/contact")
    public String sendContact(@ModelAttribute LienHe lienHe) {
        lienHe.setTrangThai("Pending");
        lienHeRepository.save(lienHe);

        return "redirect:/contact?success";
    }
}
