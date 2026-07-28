package com.sportzone.service;

import com.sportzone.entity.NguoiDung;
import com.sportzone.entity.SanPham;
import com.sportzone.repository.ChiTietDonHangRepository;
import com.sportzone.repository.SanPhamRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductRecommendationService {

    private static final int MAX_RECOMMENDATIONS = 8;

    private final SanPhamRepository sanPhamRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    public ProductRecommendationService(
            SanPhamRepository sanPhamRepository, ChiTietDonHangRepository chiTietDonHangRepository) {
        this.sanPhamRepository = sanPhamRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
    }

    @Transactional(readOnly = true)
    public List<SanPham> getFrequentlyBoughtTogether(SanPham product) {
        if (product == null) {
            return List.of();
        }
        return chiTietDonHangRepository.findFrequentlyBoughtTogether(product.getMaSP()).stream()
                .limit(4)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SanPham> getRecommendations(SanPham product, NguoiDung user) {
        if (product == null || user == null || !"USER".equals(user.getVaiTro())) {
            return List.of();
        }

        Set<Integer> excludedIds = new HashSet<>(chiTietDonHangRepository.findPurchasedProductIds(user.getMaND()));
        excludedIds.add(product.getMaSP());
        List<SanPham> recommendations = new ArrayList<>();

        if (product.getLoaiGiay() != null) {
            appendUnique(
                    recommendations,
                    sanPhamRepository.findByLoaiGiay_MaLoaiOrderByLuotXemDesc(
                            product.getLoaiGiay().getMaLoai(), PageRequest.of(0, MAX_RECOMMENDATIONS)),
                    excludedIds);
        }
        if (product.getThuongHieu() != null) {
            appendUnique(
                    recommendations,
                    sanPhamRepository.findByThuongHieu_MaTHOrderByLuotXemDesc(
                            product.getThuongHieu().getMaTH(), PageRequest.of(0, MAX_RECOMMENDATIONS)),
                    excludedIds);
        }
        appendUnique(
                recommendations,
                sanPhamRepository.findAllByOrderByLuotXemDesc(PageRequest.of(0, MAX_RECOMMENDATIONS)),
                excludedIds);
        return recommendations.stream().limit(MAX_RECOMMENDATIONS).toList();
    }

    private void appendUnique(List<SanPham> target, List<SanPham> candidates, Set<Integer> excludedIds) {
        for (SanPham candidate : candidates) {
            if (!excludedIds.contains(candidate.getMaSP())
                    && target.stream().noneMatch(product -> product.getMaSP().equals(candidate.getMaSP()))) {
                target.add(candidate);
            }
        }
    }
}
