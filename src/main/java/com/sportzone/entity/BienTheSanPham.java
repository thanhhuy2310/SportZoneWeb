package com.sportzone.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BienTheSanPham")
public class BienTheSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaBT")
    private Integer maBT;

    @ManyToOne
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "MaSize")
    private SizeGiay sizeGiay;

    @ManyToOne
    @JoinColumn(name = "MaMau")
    private MauSac mauSac;

    @Column(name = "SoLuongTon")
    private Integer soLuongTon;

    @Column(name = "SKU")
    private String sku;

    @Column(name = "TrangThai")
    private Boolean trangThai = true;

    public Integer getMaBT() {
        return maBT;
    }

    public void setMaBT(Integer maBT) {
        this.maBT = maBT;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public SizeGiay getSizeGiay() {
        return sizeGiay;
    }

    public void setSizeGiay(SizeGiay sizeGiay) {
        this.sizeGiay = sizeGiay;
    }

    public MauSac getMauSac() {
        return mauSac;
    }

    public void setMauSac(MauSac mauSac) {
        this.mauSac = mauSac;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }
}
