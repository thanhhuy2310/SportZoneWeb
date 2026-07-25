package com.sportzone.service;

import com.sportzone.model.CartItem;
import com.sportzone.repository.BienTheSanPhamRepository;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final BienTheSanPhamRepository bienTheSanPhamRepository;

    public CartService(BienTheSanPhamRepository bienTheSanPhamRepository) {
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> map(HttpSession session) {
        Object cart = session.getAttribute("cart");

        if (cart == null) {
            Map<Integer, Integer> map = new LinkedHashMap<>();
            session.setAttribute("cart", map);
            return map;
        }

        return (Map<Integer, Integer>) cart;
    }

    public void add(HttpSession session, Integer maBT, int quantity) {
        var bienThe = bienTheSanPhamRepository.findById(maBT).orElse(null);

        if (bienThe == null) {
            return;
        }

        int soLuongTon = bienThe.getSoLuongTon() == null ? 0 : bienThe.getSoLuongTon();
        int soLuongHienTai = map(session).getOrDefault(maBT, 0);
        int soLuongMuonThem = Math.max(1, quantity);

        if (soLuongHienTai + soLuongMuonThem > soLuongTon) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ");
        }

        map(session).put(maBT, soLuongHienTai + soLuongMuonThem);
    }

    public void update(HttpSession session, Integer maBT, int quantity) {
        if (quantity <= 0) {
            map(session).remove(maBT);
            return;
        }

        var bienThe = bienTheSanPhamRepository.findById(maBT).orElse(null);
        if (bienThe == null) {
            map(session).remove(maBT);
            return;
        }

        int soLuongTon = bienThe.getSoLuongTon() == null ? 0 : bienThe.getSoLuongTon();
        if (quantity > soLuongTon) {
            throw new RuntimeException("Số lượng sản phẩm trong kho không đủ");
        }

        map(session).put(maBT, quantity);
    }

    public void remove(HttpSession session, Integer maBT) {
        map(session).remove(maBT);
    }

    public java.util.List<CartItem> items(HttpSession session) {
        java.util.List<CartItem> list = new ArrayList<>();

        for (var entry : map(session).entrySet()) {
            bienTheSanPhamRepository
                    .findById(entry.getKey())
                    .ifPresent(bt -> list.add(new CartItem(bt, entry.getValue())));
        }

        return list;
    }

    public BigDecimal total(HttpSession session) {
        return items(session).stream()
                .map(CartItem::thanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int count(HttpSession session) {
        return map(session).values().stream().mapToInt(Integer::intValue).sum();
    }

    public void clear(HttpSession session) {
        map(session).clear();
    }
}
