package com.sportzone.model;

import com.sportzone.entity.BienTheSanPham;
import com.sportzone.entity.SanPham;

import java.math.BigDecimal;

public class CartItem {

    private BienTheSanPham bienThe;
    private int soLuong;

    public CartItem(BienTheSanPham bienThe, int soLuong) {
        this.bienThe = bienThe;
        this.soLuong = soLuong;
    }

    public BienTheSanPham getBienThe() {
        return bienThe;
    }

    public SanPham getSanPham() {
        return bienThe.getSanPham();
    }

    public int getSoLuong() {
        return soLuong;
    }

    public BigDecimal thanhTien() {
        return getSanPham()
                .giaHienThi()
                .multiply(BigDecimal.valueOf(soLuong));
    }
}