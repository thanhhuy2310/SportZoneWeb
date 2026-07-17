package com.sportzone.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "SanPham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaSP")
    private Integer maSP;

    @Column(name = "TenSP")
    private String tenSP;

    @ManyToOne
    @JoinColumn(name = "MaTH")
    private ThuongHieu thuongHieu;

    @ManyToOne
    @JoinColumn(name = "MaLoai")
    private LoaiGiay loaiGiay;

    @Column(name = "Gia")
    private BigDecimal gia;

    @Column(name = "GiaKhuyenMai")
    private BigDecimal giaKhuyenMai;

    @Column(name = "MoTa")
    private String moTa;

    @Column(name = "AnhDaiDien")
    private String anhDaiDien;

    @Column(name = "LuotXem")
    private Integer luotXem = 0;

    @Column(name = "DiemDanhGia")
    private Double diemDanhGia = 0.0;

    @Column(name = "TrangThai")
    private String trangThai = "Đang bán";

    @Column(name = "NgayTao")
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
        if (luotXem == null) {
            luotXem = 0;
        }
        if (diemDanhGia == null) {
            diemDanhGia = 0.0;
        }
        if (trangThai == null) {
            trangThai = "Đang bán";
        }
    }

    public BigDecimal giaHienThi() {
        if (gia == null) {
            return BigDecimal.ZERO;
        }
        if (giaKhuyenMai != null
                && giaKhuyenMai.compareTo(BigDecimal.ZERO) > 0
                && giaKhuyenMai.compareTo(gia) < 0) {
            return giaKhuyenMai;
        }
        return gia;
    }

    public boolean coGiamGia() {
        return gia != null
                && giaKhuyenMai != null
                && giaKhuyenMai.compareTo(BigDecimal.ZERO) > 0
                && giaKhuyenMai.compareTo(gia) < 0;
    }

    public int phanTramGiam() {
        if (!coGiamGia()) {
            return 0;
        }
        return gia.subtract(giaKhuyenMai)
                .multiply(BigDecimal.valueOf(100))
                .divide(gia, 0, RoundingMode.HALF_UP)
                .intValue();
    }

    public String getImageUrl() {
        String fallback = "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=900&h=900&fit=crop&auto=format";

        if (anhDaiDien != null && !anhDaiDien.isBlank()) {
            String image = anhDaiDien.trim();
            if (image.startsWith("http://") || image.startsWith("https://")) {
                return image;
            }
        }

        String key = tenSP == null ? "" : tenSP.toLowerCase();

        if (key.contains("air force") || key.contains("af1")) {
            return "https://images.unsplash.com/photo-1610664676282-55c8de64f746?w=900&h=900&fit=crop&auto=format";
        }
        if (key.contains("superstar") || key.contains("adidas")) {
            return "https://images.unsplash.com/photo-1715773408837-b7074beb12d5?w=900&h=900&fit=crop&auto=format";
        }
        if (key.contains("puma")) {
            return "https://images.unsplash.com/photo-1641745900305-d121f24aa737?w=900&h=900&fit=crop&auto=format";
        }
        if (key.contains("converse")) {
            return "https://images.unsplash.com/photo-1634624943287-6e1f2d103201?w=900&h=900&fit=crop&auto=format";
        }
        if (key.contains("vans")) {
            return "https://images.unsplash.com/photo-1604004893018-e70402f96425?w=900&h=900&fit=crop&auto=format";
        }
        if (key.contains("balance")) {
            return "https://images.unsplash.com/photo-1641745900309-75ceed0153e1?w=900&h=900&fit=crop&auto=format";
        }
        if (key.contains("jordan")) {
            return "https://images.unsplash.com/photo-1596644882922-41e7685a1074?w=900&h=900&fit=crop&auto=format";
        }
        if (key.contains("metcon") || key.contains("training")) {
            return "https://images.unsplash.com/photo-1708214837986-c3c3592c7a3e?w=900&h=900&fit=crop&auto=format";
        }
        if (key.contains("mercurial") || key.contains("predator") || key.contains("bóng đá")) {
            return "https://images.unsplash.com/photo-1768647417374-5a31c61dc5d0?w=900&h=900&fit=crop&auto=format";
        }

        return fallback;
    }

    public Integer getMaSP() {
        return maSP;
    }

    public void setMaSP(Integer maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public ThuongHieu getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(ThuongHieu thuongHieu) {
        this.thuongHieu = thuongHieu;
    }

    public LoaiGiay getLoaiGiay() {
        return loaiGiay;
    }

    public void setLoaiGiay(LoaiGiay loaiGiay) {
        this.loaiGiay = loaiGiay;
    }

    public BigDecimal getGia() {
        return gia;
    }

    public void setGia(BigDecimal gia) {
        this.gia = gia;
    }

    public BigDecimal getGiaKhuyenMai() {
        return giaKhuyenMai;
    }

    public void setGiaKhuyenMai(BigDecimal giaKhuyenMai) {
        this.giaKhuyenMai = giaKhuyenMai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getAnhDaiDien() {
        return anhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }

    public Integer getLuotXem() {
        return luotXem;
    }

    public void setLuotXem(Integer luotXem) {
        this.luotXem = luotXem;
    }

    public Double getDiemDanhGia() {
        return diemDanhGia;
    }

    public void setDiemDanhGia(Double diemDanhGia) {
        this.diemDanhGia = diemDanhGia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}
