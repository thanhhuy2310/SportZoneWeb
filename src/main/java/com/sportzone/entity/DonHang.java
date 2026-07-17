package com.sportzone.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Entity @Table(name="DonHang")
public class DonHang {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="MaDH") private Integer maDH;
    @ManyToOne @JoinColumn(name="MaND") private NguoiDung nguoiDung;
    @Column(name="NgayDat") private LocalDateTime ngayDat;
    @Column(name="HoTenNhan") private String hoTenNhan;
    @Column(name="SdtNhan") private String sdtNhan;
    @Column(name="DiaChiNhan") private String diaChiNhan;
    @Column(name="TamTinh") private BigDecimal tamTinh;
    @Column(name="PhiVanChuyen") private BigDecimal phiVanChuyen;
    @Column(name="GiamGia") private BigDecimal giamGia;
    @Column(name="TongTien") private BigDecimal tongTien;
    @Column(name="PhuongThucThanhToan") private String phuongThucThanhToan;
    @Column(name="TrangThaiThanhToan") private String trangThaiThanhToan;
    @Column(name="TrangThaiDonHang") private String trangThaiDonHang;
    @Column(name="GhiChu") private String ghiChu;
    @OneToMany(mappedBy="donHang", cascade=CascadeType.ALL) private List<ChiTietDonHang> chiTiet=new ArrayList<>();
    @PrePersist public void pre(){if(ngayDat==null)ngayDat=LocalDateTime.now(); if(phiVanChuyen==null)phiVanChuyen=BigDecimal.valueOf(30000); if(giamGia==null)giamGia=BigDecimal.ZERO; if(trangThaiDonHang==null)trangThaiDonHang="Chờ xác nhận"; if(trangThaiThanhToan==null)trangThaiThanhToan="Chưa thanh toán";}
    public Integer getMaDH(){return maDH;} public void setMaDH(Integer maDH){this.maDH=maDH;}
    public NguoiDung getNguoiDung(){return nguoiDung;} public void setNguoiDung(NguoiDung nguoiDung){this.nguoiDung=nguoiDung;}
    public LocalDateTime getNgayDat(){return ngayDat;} public void setNgayDat(LocalDateTime ngayDat){this.ngayDat=ngayDat;}
    public String getHoTenNhan(){return hoTenNhan;} public void setHoTenNhan(String hoTenNhan){this.hoTenNhan=hoTenNhan;}
    public String getSdtNhan(){return sdtNhan;} public void setSdtNhan(String sdtNhan){this.sdtNhan=sdtNhan;}
    public String getDiaChiNhan(){return diaChiNhan;} public void setDiaChiNhan(String diaChiNhan){this.diaChiNhan=diaChiNhan;}
    public BigDecimal getTamTinh(){return tamTinh;} public void setTamTinh(BigDecimal tamTinh){this.tamTinh=tamTinh;}
    public BigDecimal getPhiVanChuyen(){return phiVanChuyen;} public void setPhiVanChuyen(BigDecimal phiVanChuyen){this.phiVanChuyen=phiVanChuyen;}
    public BigDecimal getGiamGia(){return giamGia;} public void setGiamGia(BigDecimal giamGia){this.giamGia=giamGia;}
    public BigDecimal getTongTien(){return tongTien;} public void setTongTien(BigDecimal tongTien){this.tongTien=tongTien;}
    public String getPhuongThucThanhToan(){return phuongThucThanhToan;} public void setPhuongThucThanhToan(String phuongThucThanhToan){this.phuongThucThanhToan=phuongThucThanhToan;}
    public String getTrangThaiThanhToan(){return trangThaiThanhToan;} public void setTrangThaiThanhToan(String trangThaiThanhToan){this.trangThaiThanhToan=trangThaiThanhToan;}
    public String getTrangThaiDonHang(){return trangThaiDonHang;} public void setTrangThaiDonHang(String trangThaiDonHang){this.trangThaiDonHang=trangThaiDonHang;}
    public String getGhiChu(){return ghiChu;} public void setGhiChu(String ghiChu){this.ghiChu=ghiChu;}
    public List<ChiTietDonHang> getChiTiet(){return chiTiet;} public void setChiTiet(List<ChiTietDonHang> chiTiet){this.chiTiet=chiTiet;}
}
