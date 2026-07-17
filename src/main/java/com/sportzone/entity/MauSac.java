package com.sportzone.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "MauSac")
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaMau")
    private Integer maMau;

    @Column(name = "TenMau")
    private String tenMau;

    @Column(name = "MaMauHex")
    private String maMauHex;

    public Integer getMaMau() {
        return maMau;
    }

    public void setMaMau(Integer maMau) {
        this.maMau = maMau;
    }

    public String getTenMau() {
        return tenMau;
    }

    public void setTenMau(String tenMau) {
        this.tenMau = tenMau;
    }

    public String getMaMauHex() {
        return maMauHex;
    }

    public void setMaMauHex(String maMauHex) {
        this.maMauHex = maMauHex;
    }
}
