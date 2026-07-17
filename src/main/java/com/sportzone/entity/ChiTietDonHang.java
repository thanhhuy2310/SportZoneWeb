package com.sportzone.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity @Table(name="ChiTietDonHang")
public class ChiTietDonHang {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="MaCTDH") private Integer maCTDH;
    @ManyToOne @JoinColumn(name="MaDH") private DonHang donHang;
    @ManyToOne @JoinColumn(name="MaBT") private BienTheSanPham bienThe;
    @Column(name="SoLuong") private Integer soLuong;
    @Column(name="DonGia") private BigDecimal donGia;
    public Integer getMaCTDH(){return maCTDH;} public void setMaCTDH(Integer maCTDH){this.maCTDH=maCTDH;}
    public DonHang getDonHang(){return donHang;} public void setDonHang(DonHang donHang){this.donHang=donHang;}
    public BienTheSanPham getBienThe(){return bienThe;} public void setBienThe(BienTheSanPham bienThe){this.bienThe=bienThe;}
    public Integer getSoLuong(){return soLuong;} public void setSoLuong(Integer soLuong){this.soLuong=soLuong;}
    public BigDecimal getDonGia(){return donGia;} public void setDonGia(BigDecimal donGia){this.donGia=donGia;}
    public BigDecimal thanhTien(){return donGia == null || soLuong == null ? BigDecimal.ZERO : donGia.multiply(BigDecimal.valueOf(soLuong));}
}
