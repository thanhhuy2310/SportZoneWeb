package com.sportzone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "YeuThich")
public class YeuThich {

    @EmbeddedId private YeuThichId id;

    @ManyToOne
    @MapsId("maND")
    @JoinColumn(name = "MaND")
    private NguoiDung nguoiDung;

    @ManyToOne
    @MapsId("maSP")
    @JoinColumn(name = "MaSP")
    private SanPham sanPham;

    @Column(name = "NgayThem")
    private LocalDateTime ngayThem;

    public YeuThichId getId() {
        return id;
    }

    public void setId(YeuThichId id) {
        this.id = id;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public LocalDateTime getNgayThem() {
        return ngayThem;
    }

    public void setNgayThem(LocalDateTime ngayThem) {
        this.ngayThem = ngayThem;
    }
}
