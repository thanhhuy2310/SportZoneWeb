package com.sportzone.controller;

import com.sportzone.entity.ChiTietDonHang;
import com.sportzone.entity.DoiTraHang;
import com.sportzone.entity.DonHang;
import com.sportzone.entity.LoaiGiay;
import com.sportzone.entity.NguoiDung;
import com.sportzone.entity.SanPham;
import com.sportzone.entity.ThuongHieu;
import com.sportzone.repository.AuditLogRepository;
import com.sportzone.repository.BienTheSanPhamRepository;
import com.sportzone.repository.DoiTraHangRepository;
import com.sportzone.repository.DonHangRepository;
import com.sportzone.repository.LienHeRepository;
import com.sportzone.repository.LoaiGiayRepository;
import com.sportzone.repository.NguoiDungRepository;
import com.sportzone.repository.SanPhamRepository;
import com.sportzone.repository.StockMovementRepository;
import com.sportzone.repository.ThuongHieuRepository;
import com.sportzone.service.AuditLogService;
import com.sportzone.service.CartService;
import com.sportzone.service.NotificationService;
import com.sportzone.service.OrderStatusHistoryService;
import com.sportzone.service.StockMovementService;
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
    private final OrderStatusHistoryService orderStatusHistoryService;
    private final StockMovementRepository stockMovementRepository;
    private final AuditLogRepository auditLogRepository;
    private final StockMovementService stockMovementService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public AdminController(
            CartService cartService,
            SanPhamRepository sanPhamRepository,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository,
            NguoiDungRepository nguoiDungRepository,
            DonHangRepository donHangRepository,
            DoiTraHangRepository doiTraHangRepository,
            BienTheSanPhamRepository bienTheSanPhamRepository,
            LienHeRepository lienHeRepository,
            OrderStatusHistoryService orderStatusHistoryService,
            StockMovementRepository stockMovementRepository,
            AuditLogRepository auditLogRepository,
            StockMovementService stockMovementService,
            AuditLogService auditLogService,
            NotificationService notificationService) {
        super(cartService, thuongHieuRepository, loaiGiayRepository);

        this.sanPhamRepository = sanPhamRepository;
        this.thuongHieuRepository = thuongHieuRepository;
        this.loaiGiayRepository = loaiGiayRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.donHangRepository = donHangRepository;
        this.doiTraHangRepository = doiTraHangRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.lienHeRepository = lienHeRepository;
        this.orderStatusHistoryService = orderStatusHistoryService;
        this.stockMovementRepository = stockMovementRepository;
        this.auditLogRepository = auditLogRepository;
        this.stockMovementService = stockMovementService;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    private boolean isAdmin(HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");

        return user != null
                && ("ADMIN".equals(user.getVaiTro()) || "NHANVIEN".equals(user.getVaiTro()));
    }

    private boolean isSystemAdmin(HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        return user != null && "ADMIN".equals(user.getVaiTro());
    }

    private NguoiDung currentUser(HttpSession session) {
        return (NguoiDung) session.getAttribute("user");
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

        boolean newProduct = product.getMaSP() == null;
        SanPham savedProduct = sanPhamRepository.save(product);
        auditLogService.record(
                currentUser(session),
                newProduct ? "CREATE_PRODUCT" : "UPDATE_PRODUCT",
                "SanPham",
                savedProduct.getMaSP(),
                null,
                savedProduct.getTenSP(),
                null,
                "SUCCESS");

        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            sanPhamRepository.deleteById(id);
            auditLogService.record(
                    currentUser(session), "DELETE_PRODUCT", "SanPham", id, null, null, null, "SUCCESS");
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

        boolean newCategory = category.getMaLoai() == null;
        LoaiGiay savedCategory = loaiGiayRepository.save(category);
        auditLogService.record(
                currentUser(session),
                newCategory ? "CREATE_CATEGORY" : "UPDATE_CATEGORY",
                "LoaiGiay",
                savedCategory.getMaLoai(),
                null,
                savedCategory.getTenLoai(),
                null,
                "SUCCESS");

        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            loaiGiayRepository.deleteById(id);
            auditLogService.record(
                    currentUser(session), "DELETE_CATEGORY", "LoaiGiay", id, null, null, null, "SUCCESS");
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

        if (paymentStatus != null && !paymentStatus.isBlank()) {
            order.setTrangThaiThanhToan(paymentStatus);
        } else if ("Delivered".equals(status)) {
            order.setTrangThaiThanhToan("Paid");
        }

        updateOrderStatus(order, status, "Order status updated by administrator.", session);

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
        updateOrderStatus(order, "Confirmed", "Order confirmed by administrator.", session);

        return "redirect:/admin/orders?confirmed";
    }

    @GetMapping("/orders/shipping/{id}")
    public String shippingOrder(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var order = donHangRepository.findById(id).orElseThrow();
        updateOrderStatus(order, "Shipping", "Order handed over for shipping.", session);

        return "redirect:/admin/orders?shipping";
    }

    @GetMapping("/orders/delivered/{id}")
    public String deliveredOrder(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var order = donHangRepository.findById(id).orElseThrow();
        order.setTrangThaiThanhToan("Paid");

        updateOrderStatus(order, "Delivered", "Order delivered.", session);

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
            restoreStock(order, "CANCEL", currentUser(session));
        }

        if (!"Paid".equals(order.getTrangThaiThanhToan())) {
            order.setTrangThaiThanhToan("Failed");
        }

        updateOrderStatus(order, "Cancelled", "Order cancelled by administrator.", session);

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
        boolean returnRequestChanged = !"Approved".equals(request.getTrangThai());

        request.setTrangThai("Approved");
        request.setNgayXuLy(LocalDateTime.now());

        doiTraHangRepository.save(request);

        if (order != null && returnRequestChanged) {
            orderStatusHistoryService.appendStatus(
                    order, "Return Approved", "Return request approved by administrator.");
        }

        if (order != null && !"Returned".equals(order.getTrangThaiDonHang())) {
            restoreStock(order, "RETURN", currentUser(session));

            order.setTrangThaiThanhToan("Refunded");

            DonHang savedOrder = updateOrderStatus(
                    order, "Returned", "Order returned after return approval.", session);
            orderStatusHistoryService.appendStatus(
                    savedOrder, "Refunded", "Payment refunded after return approval.");
            notificationService.notifyUser(
                    savedOrder.getNguoiDung(),
                    "Return approved",
                    "Your return for order #" + savedOrder.getMaDH() + " has been approved.",
                    "RETURN_APPROVED");
        }

        return "redirect:/admin/returns?approved";
    }

    @GetMapping("/returns/reject/{id}")
    public String rejectReturn(@PathVariable Integer id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        DoiTraHang request = doiTraHangRepository.findById(id).orElseThrow();
        boolean returnRequestChanged = !"Rejected".equals(request.getTrangThai());
        request.setTrangThai("Rejected");
        request.setNgayXuLy(LocalDateTime.now());
        doiTraHangRepository.save(request);

        if (request.getDonHang() != null && returnRequestChanged) {
            orderStatusHistoryService.appendStatus(
                    request.getDonHang(), "Rejected", "Return request rejected by administrator.");
            notificationService.notifyUser(
                    request.getDonHang().getNguoiDung(),
                    "Return rejected",
                    "Your return request for order #" + request.getDonHang().getMaDH() + " was rejected.",
                    "RETURN_REJECTED");
        }

        return "redirect:/admin/returns?rejected";
    }

    @GetMapping("/inventory")
    public String inventory(HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("variants", bienTheSanPhamRepository.findAll());
        return "admin/inventory";
    }

    @PostMapping("/inventory/import")
    public String importInventory(
            @RequestParam Integer variantId,
            @RequestParam int quantity,
            @RequestParam(required = false) String note,
            HttpSession session) {
        if (!isSystemAdmin(session) || quantity <= 0) {
            return "redirect:/admin/inventory?error";
        }
        stockMovementService.changeStock(
                variantId,
                quantity,
                "IMPORT",
                null,
                "MANUAL",
                currentUser(session),
                note,
                null);
        return "redirect:/admin/inventory?imported";
    }

    @PostMapping("/inventory/adjust")
    public String adjustInventory(
            @RequestParam Integer variantId,
            @RequestParam int quantity,
            @RequestParam(required = false) String note,
            HttpSession session) {
        if (!isSystemAdmin(session) || quantity < 0) {
            return "redirect:/admin/inventory?error";
        }
        stockMovementService.adjustStock(variantId, quantity, currentUser(session), note, null);
        return "redirect:/admin/inventory?adjusted";
    }

    @GetMapping("/stock-movements")
    public String stockMovements(HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("movements", stockMovementRepository.findAllByOrderByCreatedAtDesc());
        return "admin/stock-movements";
    }

    @GetMapping("/audit-logs")
    public String auditLogs(
            @RequestParam(required = false) String action, HttpSession session, Model model) {
        if (!isSystemAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute(
                "logs",
                action == null || action.isBlank()
                        ? auditLogRepository.findAllByOrderByCreatedAtDesc()
                        : auditLogRepository.findByActionContainingIgnoreCaseOrderByCreatedAtDesc(action));
        model.addAttribute("action", action);
        return "admin/audit-logs";
    }

    private DonHang updateOrderStatus(
            DonHang order, String status, String note, HttpSession session) {
        return orderStatusHistoryService.updateOrderStatus(
                order, status, note, currentUser(session), null);
    }

    private void restoreStock(DonHang order, String movementType, NguoiDung actor) {
        if (order.getChiTiet() == null) {
            return;
        }

        for (ChiTietDonHang detail : order.getChiTiet()) {
            if (detail.getBienThe() == null || detail.getSoLuong() == null) {
                continue;
            }

            stockMovementService.changeStock(
                    detail.getBienThe().getMaBT(),
                    detail.getSoLuong(),
                    movementType,
                    order.getMaDH(),
                    "DonHang",
                    actor,
                    "Stock restored for order status change.",
                    null);
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
