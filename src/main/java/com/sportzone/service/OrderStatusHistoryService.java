package com.sportzone.service;

import com.sportzone.entity.DonHang;
import com.sportzone.entity.LichSuDonHang;
import com.sportzone.entity.NguoiDung;
import com.sportzone.repository.DonHangRepository;
import com.sportzone.repository.LichSuDonHangRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderStatusHistoryService {

    private final DonHangRepository donHangRepository;
    private final LichSuDonHangRepository lichSuDonHangRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public OrderStatusHistoryService(
            DonHangRepository donHangRepository,
            LichSuDonHangRepository lichSuDonHangRepository,
            AuditLogService auditLogService,
            NotificationService notificationService) {
        this.donHangRepository = donHangRepository;
        this.lichSuDonHangRepository = lichSuDonHangRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional
    public DonHang updateOrderStatus(DonHang donHang, String trangThai, String ghiChu) {
        return updateOrderStatus(donHang, trangThai, ghiChu, null, null);
    }

    @Transactional
    public DonHang updateOrderStatus(
            DonHang donHang, String trangThai, String ghiChu, NguoiDung actor, String ipAddress) {
        String oldStatus = donHang.getTrangThaiDonHang();
        boolean statusChanged = !Objects.equals(donHang.getTrangThaiDonHang(), trangThai);
        donHang.setTrangThaiDonHang(trangThai);

        DonHang savedOrder = donHangRepository.save(donHang);
        if (statusChanged) {
            appendStatus(savedOrder, trangThai, ghiChu);
            auditLogService.record(
                    actor,
                    "UPDATE_ORDER_STATUS",
                    "DonHang",
                    savedOrder.getMaDH(),
                    oldStatus,
                    trangThai,
                    ipAddress,
                    "SUCCESS");
            notifyOrderStatus(savedOrder, trangThai);
        }

        return savedOrder;
    }

    @Transactional
    public void appendStatus(DonHang donHang, String trangThai, String ghiChu) {
        lichSuDonHangRepository.save(new LichSuDonHang(donHang, trangThai, LocalDateTime.now(), ghiChu));
    }

    @Transactional(readOnly = true)
    public List<LichSuDonHang> getHistory(Integer maDH) {
        return lichSuDonHangRepository.findByDonHang_MaDHOrderByThoiGianAscMaLSAsc(maDH);
    }

    private void notifyOrderStatus(DonHang order, String status) {
        if (order.getNguoiDung() == null) {
            return;
        }
        String title = switch (status) {
            case "Confirmed" -> "Order confirmed";
            case "Shipping" -> "Order is shipping";
            case "Delivered" -> "Order delivered";
            case "Cancelled" -> "Order cancelled";
            case "Returned" -> "Return completed";
            default -> null;
        };
        if (title != null) {
            notificationService.notifyUser(
                    order.getNguoiDung(), title, "Order #" + order.getMaDH() + " is now " + status + ".", "ORDER_STATUS");
        }
    }
}
