package com.sportzone.controller;

import com.sportzone.entity.ChiTietDonHang;
import com.sportzone.entity.DonHang;
import com.sportzone.entity.NguoiDung;
import com.sportzone.repository.BienTheSanPhamRepository;
import com.sportzone.repository.DonHangRepository;
import com.sportzone.repository.LoaiGiayRepository;
import com.sportzone.repository.MaGiamGiaRepository;
import com.sportzone.repository.ThuongHieuRepository;
import com.sportzone.service.CartService;
import com.sportzone.service.AuditLogService;
import com.sportzone.service.NotificationService;
import com.sportzone.service.OrderStatusHistoryService;
import com.sportzone.service.StockMovementService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController extends BaseController {

    public static final BigDecimal SHIPPING_FEE = BigDecimal.valueOf(30000);

    private final DonHangRepository donHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final MaGiamGiaRepository maGiamGiaRepository;
    private final OrderStatusHistoryService orderStatusHistoryService;
    private final StockMovementService stockMovementService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public CartController(
            CartService cartService,
            ThuongHieuRepository thuongHieuRepository,
            LoaiGiayRepository loaiGiayRepository,
            DonHangRepository donHangRepository,
            BienTheSanPhamRepository bienTheSanPhamRepository,
            MaGiamGiaRepository maGiamGiaRepository,
            OrderStatusHistoryService orderStatusHistoryService,
            StockMovementService stockMovementService,
            AuditLogService auditLogService,
            NotificationService notificationService) {
        super(cartService, thuongHieuRepository, loaiGiayRepository);
        this.donHangRepository = donHangRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.maGiamGiaRepository = maGiamGiaRepository;
        this.orderStatusHistoryService = orderStatusHistoryService;
        this.stockMovementService = stockMovementService;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @PostMapping("/cart/add/{id}")
    public String add(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer maBT,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestHeader(value = "Referer", required = false) String referer,
            HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        try {
            cartService.add(session, maBT != null ? maBT : id, quantity);
            clearCoupon(session);
            return "redirect:" + (referer != null ? referer : "/products");
        } catch (RuntimeException e) {
            return "redirect:" + (referer != null ? referer : "/products") + "?stockError";
        }
    }

    @GetMapping("/cart")
    public String cart(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("items", cartService.items(session));
        model.addAttribute("total", cartService.total(session));
        return "cart";
    }

    @PostMapping("/cart/update")
    public String update(
            @RequestParam Integer maSP, @RequestParam int quantity, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        try {
            cartService.update(session, maSP, quantity);
            clearCoupon(session);
            return "redirect:/cart";
        } catch (RuntimeException e) {
            return "redirect:/cart?stockError";
        }
    }

    @GetMapping("/cart/remove/{id}")
    public String remove(@PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        cartService.remove(session, id);
        clearCoupon(session);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        if (cartService.items(session).isEmpty()) {
            return "redirect:/cart";
        }
        fillCheckoutModel(session, model, getDiscount(session));
        model.addAttribute(
                "couponCode",
                session.getAttribute("couponCode") == null ? "" : session.getAttribute("couponCode"));
        return "checkout";
    }

    @PostMapping("/checkout/apply-coupon")
    public String applyCoupon(
            @RequestParam(required = false) String couponCode, HttpSession session, Model model) {
        if (cartService.items(session).isEmpty()) {
            return "redirect:/cart";
        }

        if (couponCode == null || couponCode.isBlank()) {
            clearCoupon(session);
            return "redirect:/checkout";
        }

        BigDecimal subtotal = cartService.total(session);
        var coupon = maGiamGiaRepository.findByCodeIgnoreCase(couponCode.trim()).orElse(null);

        if (coupon == null
                || !Boolean.TRUE.equals(coupon.getTrangThai())
                || coupon.getSoLuong() == null
                || coupon.getSoLuong() <= 0) {
            clearCoupon(session);
            return "redirect:/checkout?couponError";
        }

        BigDecimal discount =
                subtotal
                        .multiply(BigDecimal.valueOf(coupon.getPhanTramGiam()))
                        .divide(BigDecimal.valueOf(100));

        session.setAttribute("couponCode", coupon.getCode());
        session.setAttribute("discount", discount);

        fillCheckoutModel(session, model, discount);
        model.addAttribute("couponCode", coupon.getCode());
        return "checkout";
    }

    @PostMapping("/checkout")
    @Transactional
    public String placeOrder(
            @RequestParam String hoTenNhan,
            @RequestParam String sdtNhan,
            @RequestParam String diaChiNhan,
            @RequestParam(defaultValue = "Cash") String phuongThucThanhToan,
            @RequestParam(required = false) String ghiChu,
            HttpSession session) {
        NguoiDung user = (NguoiDung) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        var cartItems = cartService.items(session);
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        try {
            BigDecimal subtotal = cartService.total(session);
            BigDecimal shipping = SHIPPING_FEE;
            BigDecimal discount = getDiscount(session);
            String couponCode =
                    session.getAttribute("couponCode") == null
                            ? null
                            : session.getAttribute("couponCode").toString();

            if (couponCode != null && !couponCode.isBlank()) {
                var coupon = maGiamGiaRepository.findByCodeIgnoreCase(couponCode.trim()).orElse(null);
                if (coupon == null
                        || !Boolean.TRUE.equals(coupon.getTrangThai())
                        || coupon.getSoLuong() == null
                        || coupon.getSoLuong() <= 0) {
                    return "redirect:/checkout?couponError";
                }
                coupon.setSoLuong(coupon.getSoLuong() - 1);
                maGiamGiaRepository.save(coupon);
            }

            for (var item : cartItems) {
                var bienThe = item.getBienThe();
                int tonKho = bienThe.getSoLuongTon() == null ? 0 : bienThe.getSoLuongTon();
                if (tonKho < item.getSoLuong()) {
                    return "redirect:/checkout?stockError";
                }
            }

            BigDecimal total = subtotal.add(shipping).subtract(discount);

            DonHang order = new DonHang();
            order.setNguoiDung(user);
            order.setHoTenNhan(hoTenNhan);
            order.setSdtNhan(sdtNhan);
            order.setDiaChiNhan(diaChiNhan);
            order.setPhuongThucThanhToan(phuongThucThanhToan);
            order.setGhiChu(ghiChu);
            order.setTamTinh(subtotal);
            order.setPhiVanChuyen(shipping);
            order.setGiamGia(discount);
            order.setTongTien(total);
            order.setTrangThaiDonHang("Pending");
            order.setTrangThaiThanhToan("Cash".equalsIgnoreCase(phuongThucThanhToan) ? "Unpaid" : "Paid");

            for (var item : cartItems) {
                var bienThe = item.getBienThe();

                ChiTietDonHang detail = new ChiTietDonHang();
                detail.setDonHang(order);
                detail.setBienThe(bienThe);
                detail.setSoLuong(item.getSoLuong());
                detail.setDonGia(item.getSanPham().giaHienThi());
                order.getChiTiet().add(detail);

            }

            DonHang savedOrder = donHangRepository.save(order);
            for (var item : cartItems) {
                stockMovementService.changeStock(
                        item.getBienThe().getMaBT(),
                        -item.getSoLuong(),
                        "ORDER",
                        savedOrder.getMaDH(),
                        "DonHang",
                        user,
                        "Stock deducted for checkout.",
                        null);
            }
            orderStatusHistoryService.appendStatus(savedOrder, "Pending", "Order created.");
            auditLogService.record(
                    user,
                    "CREATE_ORDER",
                    "DonHang",
                    savedOrder.getMaDH(),
                    null,
                    "Pending",
                    null,
                    "SUCCESS");
            notificationService.notifyRole(
                    "ADMIN", "New order", "Order #" + savedOrder.getMaDH() + " has been created.", "NEW_ORDER");
            cartService.clear(session);
            clearCoupon(session);

            return "redirect:/profile?ordered";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return "redirect:/checkout?stockError";
        }
    }

    private void fillCheckoutModel(HttpSession session, Model model, BigDecimal discount) {
        BigDecimal subtotal = cartService.total(session);
        BigDecimal shipping = SHIPPING_FEE;
        BigDecimal total = subtotal.add(shipping).subtract(discount);

        model.addAttribute("items", cartService.items(session));
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shipping", shipping);
        model.addAttribute("discount", discount);
        model.addAttribute("total", total);
    }

    private BigDecimal getDiscount(HttpSession session) {
        Object discount = session.getAttribute("discount");
        return discount instanceof BigDecimal ? (BigDecimal) discount : BigDecimal.ZERO;
    }

    private void clearCoupon(HttpSession session) {
        session.removeAttribute("discount");
        session.removeAttribute("couponCode");
    }
}
