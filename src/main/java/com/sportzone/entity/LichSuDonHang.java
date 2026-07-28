package com.sportzone.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuDonHang")
public class LichSuDonHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLS")
    private Integer maLS;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaDH", nullable = false)
    private DonHang donHang;

    @Column(name = "TrangThai", nullable = false, length = 100)
    private String trangThai;

    @Column(name = "ThoiGian", nullable = false)
    private LocalDateTime thoiGian;

    @Column(name = "GhiChu", length = 500)
    private String ghiChu;

    public LichSuDonHang() {}

    public LichSuDonHang(DonHang donHang, String trangThai, LocalDateTime thoiGian, String ghiChu) {
        this.donHang = donHang;
        this.trangThai = trangThai;
        this.thoiGian = thoiGian;
        this.ghiChu = ghiChu;
    }

    public Integer getMaLS() {
        return maLS;
    }

    public void setMaLS(Integer maLS) {
        this.maLS = maLS;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
