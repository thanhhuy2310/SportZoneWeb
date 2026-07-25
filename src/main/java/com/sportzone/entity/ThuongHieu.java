package com.sportzone.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ThuongHieu")
public class ThuongHieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTH")
    private Integer maTH;

    @Column(name = "TenTH")
    private String tenTH;

    @Column(name = "MoTa")
    private String moTa;

    @Column(name = "Logo")
    private String logo;

    @Column(name = "TrangThai")
    private Boolean trangThai = true;

    public Integer getMaTH() {
        return maTH;
    }

    public void setMaTH(Integer maTH) {
        this.maTH = maTH;
    }

    public String getTenTH() {
        return tenTH;
    }

    public void setTenTH(String tenTH) {
        this.tenTH = tenTH;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }
}
