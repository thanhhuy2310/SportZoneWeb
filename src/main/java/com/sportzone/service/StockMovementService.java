package com.sportzone.service;

import com.sportzone.entity.BienTheSanPham;
import com.sportzone.entity.NguoiDung;
import com.sportzone.entity.StockMovement;
import com.sportzone.repository.BienTheSanPhamRepository;
import com.sportzone.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockMovementService {

    private static final int LOW_STOCK_LIMIT = 5;

    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final StockMovementRepository stockMovementRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public StockMovementService(
            BienTheSanPhamRepository bienTheSanPhamRepository,
            StockMovementRepository stockMovementRepository,
            AuditLogService auditLogService,
            NotificationService notificationService) {
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional
    public BienTheSanPham changeStock(
            Integer variantId,
            int quantityChange,
            String movementType,
            Integer referenceId,
            String referenceType,
            NguoiDung actor,
            String note,
            String ipAddress) {
        if (quantityChange == 0) {
            throw new IllegalArgumentException("Stock change must not be zero.");
        }

        BienTheSanPham variant = bienTheSanPhamRepository.findById(variantId).orElseThrow();
        int beforeQuantity = variant.getSoLuongTon() == null ? 0 : variant.getSoLuongTon();
        int afterQuantity = beforeQuantity + quantityChange;
        if (afterQuantity < 0) {
            throw new IllegalArgumentException("Insufficient stock.");
        }

        variant.setSoLuongTon(afterQuantity);
        BienTheSanPham savedVariant = bienTheSanPhamRepository.save(variant);

        StockMovement movement = new StockMovement();
        movement.setProductVariant(savedVariant);
        movement.setMovementType(movementType);
        movement.setQuantity(Math.abs(quantityChange));
        movement.setBeforeQuantity(beforeQuantity);
        movement.setAfterQuantity(afterQuantity);
        movement.setReferenceId(referenceId);
        movement.setReferenceType(referenceType);
        movement.setCreatedBy(actor);
        movement.setNote(note);
        stockMovementRepository.save(movement);

        auditLogService.record(
                actor,
                "UPDATE_INVENTORY",
                "BienTheSanPham",
                variantId,
                String.valueOf(beforeQuantity),
                String.valueOf(afterQuantity),
                ipAddress,
                "SUCCESS");
        notifyStockLevel(savedVariant, beforeQuantity, afterQuantity);

        return savedVariant;
    }

    @Transactional
    public BienTheSanPham adjustStock(
            Integer variantId, int targetQuantity, NguoiDung actor, String note, String ipAddress) {
        BienTheSanPham variant = bienTheSanPhamRepository.findById(variantId).orElseThrow();
        int currentQuantity = variant.getSoLuongTon() == null ? 0 : variant.getSoLuongTon();
        if (targetQuantity < 0) {
            throw new IllegalArgumentException("Stock must not be negative.");
        }
        if (targetQuantity == currentQuantity) {
            return variant;
        }
        return changeStock(
                variantId,
                targetQuantity - currentQuantity,
                "ADJUSTMENT",
                null,
                "MANUAL",
                actor,
                note,
                ipAddress);
    }

    private void notifyStockLevel(BienTheSanPham variant, int beforeQuantity, int afterQuantity) {
        String productName = variant.getSanPham() == null ? "Product variant" : variant.getSanPham().getTenSP();
        if (afterQuantity == 0 && beforeQuantity > 0) {
            notificationService.notifyRole(
                    "ADMIN", "Out of stock", productName + " is out of stock.", "OUT_OF_STOCK");
        } else if (afterQuantity <= LOW_STOCK_LIMIT && beforeQuantity > LOW_STOCK_LIMIT) {
            notificationService.notifyRole(
                    "ADMIN",
                    "Low stock",
                    productName + " has only " + afterQuantity + " item(s) left.",
                    "LOW_STOCK");
        }
    }
}
