package com.sportzone.entity;
import jakarta.persistence.*;
@Entity @Table(name="LoaiGiay")
public class LoaiGiay {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="MaLoai") private Integer maLoai;
    @Column(name="TenLoai") private String tenLoai;
    @Column(name="MoTa") private String moTa;
    @Column(name="Icon") private String icon;
    @Column(name="TrangThai") private Boolean trangThai=true;
    public Integer getMaLoai(){return maLoai;} public void setMaLoai(Integer maLoai){this.maLoai=maLoai;}
    public String getTenLoai(){return tenLoai;} public void setTenLoai(String tenLoai){this.tenLoai=tenLoai;}
    public String getMoTa(){return moTa;} public void setMoTa(String moTa){this.moTa=moTa;}
    public String getIcon(){return icon;} public void setIcon(String icon){this.icon=icon;}
    public Boolean getTrangThai(){return trangThai;} public void setTrangThai(Boolean trangThai){this.trangThai=trangThai;}
}
