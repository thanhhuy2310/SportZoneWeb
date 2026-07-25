package com.sportzone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DoiTraHang")
public class DoiTraHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDT")
    private Integer maDT;

    @ManyToOne
    @JoinColumn(name = "MaDH")
    private DonHang donHang;

    @ManyToOne
    @JoinColumn(name = "MaND")
    private NguoiDung nguoiDung;

    @Column(name = "LyDo")
    private String lyDo;

    @Column(name = "TrangThai")
    private String trangThai;

    @Column(name = "NgayYeuCau")
    private LocalDateTime ngayYeuCau;

    @Column(name = "NgayXuLy")
    private LocalDateTime ngayXuLy;

    @PrePersist
    public void prePersist() {
        if (ngayYeuCau == null) {
            ngayYeuCau = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = "Requested";
        }
    }

    public Integer getMaDT() {
        return maDT;
    }

    public void setMaDT(Integer maDT) {
        this.maDT = maDT;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public void setNguoiDung(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayYeuCau() {
        return ngayYeuCau;
    }

    public void setNgayYeuCau(LocalDateTime ngayYeuCau) {
        this.ngayYeuCau = ngayYeuCau;
    }

    public LocalDateTime getNgayXuLy() {
        return ngayXuLy;
    }

    public void setNgayXuLy(LocalDateTime ngayXuLy) {
        this.ngayXuLy = ngayXuLy;
    }
}
