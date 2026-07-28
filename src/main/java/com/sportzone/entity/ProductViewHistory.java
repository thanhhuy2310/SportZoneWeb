package com.sportzone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ProductViewHistory",
        uniqueConstraints = @UniqueConstraint(name = "UQ_ProductViewHistory_User_Product", columnNames = {"UserId", "ProductId"}))
public class ProductViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HistoryId")
    private Integer historyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UserId", nullable = false)
    private NguoiDung user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ProductId", nullable = false)
    private SanPham product;

    @Column(name = "ViewedAt", nullable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    public void prePersist() {
        if (viewedAt == null) {
            viewedAt = LocalDateTime.now();
        }
    }

    public Integer getHistoryId() { return historyId; }
    public void setHistoryId(Integer historyId) { this.historyId = historyId; }
    public NguoiDung getUser() { return user; }
    public void setUser(NguoiDung user) { this.user = user; }
    public SanPham getProduct() { return product; }
    public void setProduct(SanPham product) { this.product = product; }
    public LocalDateTime getViewedAt() { return viewedAt; }
    public void setViewedAt(LocalDateTime viewedAt) { this.viewedAt = viewedAt; }
}
