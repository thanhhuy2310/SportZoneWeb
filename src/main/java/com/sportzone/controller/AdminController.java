package com.sportzone.controller;

import com.sportzone.entity.ChiTietDonHang;
import com.sportzone.entity.DoiTraHang;
import com.sportzone.entity.DonHang;
import com.sportzone.entity.LoaiGiay;
import com.sportzone.entity.NguoiDung;
import com.sportzone.entity.SanPham;
import com.sportzone.entity.ThuongHieu;
import com.sportzone.repository.BienTheSanPhamRepository;
import com.sportzone.repository.DoiTraHangRepository;
import com.sportzone.repository.DonHangRepository;
import com.sportzone.repository.LienHeRepository;
import com.sportzone.repository.LoaiGiayRepository;
import com.sportzone.repository.NguoiDungRepository;
import com.sportzone.repository.SanPhamRepository;
import com.sportzone.repository.ThuongHieuRepository;
import com.sportzone.service.CartService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {

    private final SanPhamRepository sanPhamRepository;
    private final ThuongHieuRepository thuongHieuRepository;
    private final LoaiGiayRepository loaiGiayRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final DonHangRepository donHangRepository;
    private final DoiTraHangRepository doiTraHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final LienHeRepository lienHeRepository;

    public AdminController(
            CartService cartService,
            SanPhamRepository sanPhamRepository,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository,
            NguoiDungRepository nguoiDungRepository,
            DonHangRepository donHangRepository,
            DoiTraHangRepository doiTraHangRepository,
            BienTheSanPhamRepository bienTheSanPhamRepository,
            LienHeRepository lienHeRepository) {
        super(cartService, thuongHieuRepository, loaiGiayRepository);

        this.sanPhamRepository = sanPhamRepository;
        this.thuongHieuRepository = thuongHieuRepository;
        this.loaiGiayRepository = loaiGiayRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.donHangRepository = donHangRepository;
        this.doiTraHangRepository = doiTraHangRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.lienHeRepository = lienHeRepository;
    }

    private boolean isAdmin(HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");

        return user != null
                && ("ADMIN".equals(user.getVaiTro()) || "NHANVIEN".equals(user.getVaiTro()));
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("totalProducts", sanPhamRepository.count());
        model.addAttribute("totalBrands", thuongHieuRepository.count());
        model.addAttribute("totalUsers", nguoiDungRepository.count());
        model.addAttribute("totalOrders", donHangRepository.count());
        model.addAttribute("revenue", donHangRepository.doanhThu());
        model.addAttribute("recentOrders", donHangRepository.findTop5ByOrderByNgayDatDesc());

        String[] monthNames = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        BigDecimal[] revenueByMonth = new BigDecimal[12];
        Long[] ordersByMonth = new Long[12];

        for (int i = 0; i < 12; i++) {
            revenueByMonth[i] = BigDecimal.ZERO;
            ordersByMonth[i] = 0L;
        }

        for (Object[] row : donHangRepository.doanhThuVaDonHangTheoThang()) {
            Integer month = ((Number) row[0]).intValue();
            BigDecimal revenueMonth = new BigDecimal(row[1].toString());
            Long orderCount = ((Number) row[2]).longValue();

            revenueByMonth[month - 1] = revenueMonth;
            ordersByMonth[month - 1] = orderCount;
        }

        List<RevenueRow> revenueRows = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            revenueRows.add(new RevenueRow(monthNames[i], revenueByMonth[i], ordersByMonth[i]));
        }
        model.addAttribute("revenueRows", revenueRows);

        StringBuilder labels = new StringBuilder("[");
        StringBuilder revenues = new StringBuilder("[");
        StringBuilder orders = new StringBuilder("[");

        for (int i = 0; i < 12; i++) {
            labels.append("'").append(monthNames[i]).append("'");
            revenues.append(revenueByMonth[i]);
            orders.append(ordersByMonth[i]);

            if (i < 11) {
                labels.append(",");
                revenues.append(",");
                orders.append(",");
            }
        }

        labels.append("]");
        revenues.append("]");
        orders.append("]");

        model.addAttribute("chartLabels", labels.toString());
        model.addAttribute("chartRevenue", revenues.toString());
        model.addAttribute("chartOrders", orders.toString());

        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("products", sanPhamRepository.findAll());

        return "admin/products";
    }

    @GetMapping({"/products/add", "/products/create"})
    public String addProduct(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("product", new SanPham());
        model.addAttribute("brands", thuongHieuRepository.findAll());
        model.addAttribute("categories", loaiGiayRepository.findAll());

        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("product", sanPhamRepository.findById(id).orElseThrow());

        model.addAttribute("brands", thuongHieuRepository.findAll());
        model.addAttribute("categories", loaiGiayRepository.findAll());

        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(
            @ModelAttribute SanPham product,
            @RequestParam Integer maTH,
            @RequestParam Integer maLoai,
            HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        product.setThuongHieu(thuongHieuRepository.findById(maTH).orElseThrow());

        product.setLoaiGiay(loaiGiayRepository.findById(maLoai).orElseThrow());

        if (product.getTrangThai() == null || product.getTrangThai().isBlank()) {
            product.setTrangThai("Active");
        }

        sanPhamRepository.save(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            sanPhamRepository.deleteById(id);
        } catch (Exception e) {
            return "redirect:/admin/products?deleteError";
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/brands")
    public String brands(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("brands", thuongHieuRepository.findAll());

        return "admin/brands";
    }

    @GetMapping({"/brands/add", "/brands/create"})
    public String addBrand(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("brand", new ThuongHieu());

        return "admin/brand-form";
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("brand", thuongHieuRepository.findById(id).orElseThrow());

        return "admin/brand-form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(@ModelAttribute ThuongHieu brand, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (brand.getTrangThai() == null) {
            brand.setTrangThai(true);
        }

        thuongHieuRepository.save(brand);

        return "redirect:/admin/brands";
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            thuongHieuRepository.deleteById(id);
        } catch (Exception e) {
            return "redirect:/admin/brands?deleteError";
        }

        return "redirect:/admin/brands";
    }

    @GetMapping("/categories")
    public String categories(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("categories", loaiGiayRepository.findAll());

        return "admin/categories";
    }

    @GetMapping({"/categories/add", "/categories/create"})
    public String addCategory(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("category", new LoaiGiay());

        return "admin/category-form";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable Integer id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("category", loaiGiayRepository.findById(id).orElseThrow());

        return "admin/category-form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute LoaiGiay category, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        if (category.getTrangThai() == null) {
            category.setTrangThai(true);
        }

        loaiGiayRepository.save(category);

        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            loaiGiayRepository.deleteById(id);
        } catch (Exception e) {
            return "redirect:/admin/categories?deleteError";
        }

        return "redirect:/admin/categories";
    }

    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("orders", donHangRepository.findAllByOrderByNgayDatDesc());

        model.addAttribute(
                "statuses", Arrays.asList("Pending", "Confirmed", "Shipping", "Delivered", "Cancelled"));

        model.addAttribute("paymentStatuses", Arrays.asList("Unpaid", "Paid", "Failed", "Refunded"));

        return "admin/orders";
    }

    @PostMapping("/orders/status")
    public String updateStatus(
            @RequestParam Integer maDH,
            @RequestParam String status,
            @RequestParam(required = false) String paymentStatus,
            HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var order = donHangRepository.findById(maDH).orElseThrow();

        order.setTrangThaiDonHang(status);

        if (paymentStatus != null && !paymentStatus.isBlank()) {
            order.setTrangThaiThanhToan(paymentStatus);
        } else if ("Delivered".equals(status)) {
            order.setTrangThaiThanhToan("Paid");
        }

        donHangRepository.save(order);

        return "redirect:/admin/orders";
    }

    @GetMapping("/customers")
    public String customers(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("customers", nguoiDungRepository.findAll());

        return "admin/customers";
    }

    @GetMapping("/contacts")
    public String contacts(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("contacts", lienHeRepository.findAll());

        return "admin/contacts";
    }

    @GetMapping("/contacts/resolve/{id}")
    public String resolveContact(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var contact = lienHeRepository.findById(id).orElseThrow();

        contact.setTrangThai("Resolved");

        lienHeRepository.save(contact);

        return "redirect:/admin/contacts";
    }

    @GetMapping("/orders/confirm/{id}")
    public String confirmOrder(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var order = donHangRepository.findById(id).orElseThrow();
        order.setTrangThaiDonHang("Confirmed");

        donHangRepository.save(order);

        return "redirect:/admin/orders?confirmed";
    }

    @GetMapping("/orders/shipping/{id}")
    public String shippingOrder(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var order = donHangRepository.findById(id).orElseThrow();
        order.setTrangThaiDonHang("Shipping");

        donHangRepository.save(order);

        return "redirect:/admin/orders?shipping";
    }

    @GetMapping("/orders/delivered/{id}")
    public String deliveredOrder(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var order = donHangRepository.findById(id).orElseThrow();
        order.setTrangThaiDonHang("Delivered");
        order.setTrangThaiThanhToan("Paid");

        donHangRepository.save(order);

        return "redirect:/admin/orders?delivered";
    }

    @GetMapping("/orders/cancel/{id}")
    public String cancelOrder(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var order = donHangRepository.findById(id).orElseThrow();

        if (!"Cancelled".equals(order.getTrangThaiDonHang())
                && !"Delivered".equals(order.getTrangThaiDonHang())
                && !"Return Approved".equals(order.getTrangThaiDonHang())) {
            restoreStock(order);
        }

        order.setTrangThaiDonHang("Cancelled");

        if (!"Paid".equals(order.getTrangThaiThanhToan())) {
            order.setTrangThaiThanhToan("Failed");
        }

        donHangRepository.save(order);

        return "redirect:/admin/orders?cancelled";
    }

    @GetMapping("/returns")
    public String returns(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("returns", doiTraHangRepository.findAllByOrderByNgayYeuCauDesc());
        return "admin/returns";
    }

    @GetMapping("/returns/approve/{id}")
    public String approveReturn(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        DoiTraHang request = doiTraHangRepository.findById(id).orElseThrow();
        DonHang order = request.getDonHang();

        if (order != null && !"Returned".equals(order.getTrangThaiDonHang())) {
            restoreStock(order);

            order.setTrangThaiDonHang("Returned");
            order.setTrangThaiThanhToan("Refunded");

            donHangRepository.save(order);
        }

        request.setTrangThai("Approved");
        request.setNgayXuLy(LocalDateTime.now());

        doiTraHangRepository.save(request);

        return "redirect:/admin/returns?approved";
    }

    @GetMapping("/returns/reject/{id}")
    public String rejectReturn(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        DoiTraHang request = doiTraHangRepository.findById(id).orElseThrow();
        request.setTrangThai("Rejected");
        request.setNgayXuLy(LocalDateTime.now());
        doiTraHangRepository.save(request);

        return "redirect:/admin/returns?rejected";
    }

    private void restoreStock(DonHang order) {
        if (order.getChiTiet() == null) {
            return;
        }

        for (ChiTietDonHang detail : order.getChiTiet()) {
            if (detail.getBienThe() == null || detail.getSoLuong() == null) {
                continue;
            }

            var bienThe = detail.getBienThe();
            int currentStock = bienThe.getSoLuongTon() == null ? 0 : bienThe.getSoLuongTon();
            bienThe.setSoLuongTon(currentStock + detail.getSoLuong());
            bienTheSanPhamRepository.save(bienThe);
        }
    }

    public static class RevenueRow {
        private final String month;
        private final BigDecimal revenue;
        private final Long orders;

        public RevenueRow(String month, BigDecimal revenue, Long orders) {
            this.month = month;
            this.revenue = revenue;
            this.orders = orders;
        }

        public String getMonth() {
            return month;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public Long getOrders() {
            return orders;
        }
    }
}
