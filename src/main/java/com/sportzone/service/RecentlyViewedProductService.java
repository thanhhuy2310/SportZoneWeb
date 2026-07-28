package com.sportzone.service;

import com.sportzone.entity.NguoiDung;
import com.sportzone.entity.ProductViewHistory;
import com.sportzone.entity.SanPham;
import com.sportzone.repository.ProductViewHistoryRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecentlyViewedProductService {

    private final ProductViewHistoryRepository productViewHistoryRepository;

    public RecentlyViewedProductService(ProductViewHistoryRepository productViewHistoryRepository) {
        this.productViewHistoryRepository = productViewHistoryRepository;
    }

    @Transactional
    public void recordView(NguoiDung user, SanPham product) {
        if (user == null || product == null || !"USER".equals(user.getVaiTro())) {
            return;
        }

        ProductViewHistory history = productViewHistoryRepository
                .findByUser_MaNDAndProduct_MaSP(user.getMaND(), product.getMaSP())
                .orElseGet(ProductViewHistory::new);
        history.setUser(user);
        history.setProduct(product);
        history.setViewedAt(LocalDateTime.now());
        productViewHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<SanPham> getRecentlyViewed(NguoiDung user, Integer excludedProductId) {
        if (user == null || !"USER".equals(user.getVaiTro())) {
            return List.of();
        }
        return productViewHistoryRepository.findTop10ByUser_MaNDOrderByViewedAtDesc(user.getMaND()).stream()
                .map(ProductViewHistory::getProduct)
                .filter(product -> !product.getMaSP().equals(excludedProductId))
                .toList();
    }
}
