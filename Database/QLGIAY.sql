

IF DB_ID('QLGIAY') IS NOT NULL
BEGIN
    ALTER DATABASE QLGIAY SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE QLGIAY;
END
GO

CREATE DATABASE QLGIAY;
GO

USE QLGIAY;
GO

/*========================================================
    1. NGƯỜI DÙNG / PHÂN QUYỀN
========================================================*/
CREATE TABLE NguoiDung (
    MaND INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    SoDienThoai VARCHAR(15),
    MatKhau VARCHAR(255) NOT NULL,
    VaiTro NVARCHAR(20) NOT NULL DEFAULT 'USER',
    Avatar NVARCHAR(1000),
    DiaChi NVARCHAR(255),
    TrangThai BIT DEFAULT 1,
    NgayTao DATETIME DEFAULT GETDATE(),

    CONSTRAINT CHK_ND_VAITRO CHECK (VaiTro IN ('ADMIN', 'NHANVIEN', 'USER')),
    CONSTRAINT CHK_ND_EMAIL CHECK (Email LIKE '%@%')
);
GO

/*========================================================
    2. THƯƠNG HIỆU
========================================================*/
CREATE TABLE ThuongHieu (
    MaTH INT IDENTITY(1,1) PRIMARY KEY,
    TenTH NVARCHAR(100) NOT NULL UNIQUE,
    MoTa NVARCHAR(500),
    Logo NVARCHAR(1000),
    TrangThai BIT DEFAULT 1
);
GO

/*========================================================
    3. LOẠI GIÀY / DANH MỤC
========================================================*/
CREATE TABLE LoaiGiay (
    MaLoai INT IDENTITY(1,1) PRIMARY KEY,
    TenLoai NVARCHAR(100) NOT NULL UNIQUE,
    MoTa NVARCHAR(500),
    Icon NVARCHAR(100),
    TrangThai BIT DEFAULT 1
);
GO

/*========================================================
    4. MÀU SẮC
========================================================*/
CREATE TABLE MauSac (
    MaMau INT IDENTITY(1,1) PRIMARY KEY,
    TenMau NVARCHAR(50) NOT NULL UNIQUE,
    MaMauHex VARCHAR(20)
);
GO

/*========================================================
    5. SIZE GIÀY
========================================================*/
CREATE TABLE SizeGiay (
    MaSize INT IDENTITY(1,1) PRIMARY KEY,
    TenSize NVARCHAR(20) NOT NULL UNIQUE
);
GO

/*========================================================
    6. SẢN PHẨM
========================================================*/
CREATE TABLE SanPham (
    MaSP INT IDENTITY(1,1) PRIMARY KEY,
    TenSP NVARCHAR(200) NOT NULL,
    MaTH INT NOT NULL,
    MaLoai INT NOT NULL,
    Gia DECIMAL(18,2) NOT NULL,
    GiaKhuyenMai DECIMAL(18,2),
    MoTa NVARCHAR(MAX),
    AnhDaiDien NVARCHAR(1000),
    LuotXem INT DEFAULT 0,
    DiemDanhGia FLOAT DEFAULT 0,
    TrangThai NVARCHAR(30) DEFAULT 'Active',
    NgayTao DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_SP_TH FOREIGN KEY (MaTH) REFERENCES ThuongHieu(MaTH),
    CONSTRAINT FK_SP_LOAI FOREIGN KEY (MaLoai) REFERENCES LoaiGiay(MaLoai),
    CONSTRAINT CHK_SP_GIA CHECK (Gia > 0),
    CONSTRAINT CHK_SP_GIAKM CHECK (GiaKhuyenMai IS NULL OR GiaKhuyenMai >= 0),
    CONSTRAINT CHK_SP_DANHGIA CHECK (DiemDanhGia BETWEEN 0 AND 5),
    CONSTRAINT CHK_SP_TRANGTHAI CHECK (TrangThai IN ('Active', 'Out of stock', 'Inactive', 'Hidden', N'Đang bán', N'Hết hàng', N'Ngừng bán'))
);
GO

/*========================================================
    7. BIẾN THỂ SẢN PHẨM: SIZE + MÀU + TỒN KHO
========================================================*/
CREATE TABLE BienTheSanPham (
    MaBT INT IDENTITY(1,1) PRIMARY KEY,
    MaSP INT NOT NULL,
    MaSize INT NOT NULL,
    MaMau INT NOT NULL,
    SoLuongTon INT NOT NULL DEFAULT 0,
    SKU VARCHAR(50) UNIQUE,
    TrangThai BIT DEFAULT 1,

    CONSTRAINT FK_BT_SP FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP),
    CONSTRAINT FK_BT_SIZE FOREIGN KEY (MaSize) REFERENCES SizeGiay(MaSize),
    CONSTRAINT FK_BT_MAU FOREIGN KEY (MaMau) REFERENCES MauSac(MaMau),
    CONSTRAINT CHK_BT_TON CHECK (SoLuongTon >= 0),
    CONSTRAINT UQ_BT_SP_SIZE_MAU UNIQUE (MaSP, MaSize, MaMau)
);
GO

/*========================================================
    8. ẢNH SẢN PHẨM
========================================================*/
CREATE TABLE HinhAnhSanPham (
    MaHA INT IDENTITY(1,1) PRIMARY KEY,
    MaSP INT NOT NULL,
    DuongDan NVARCHAR(1000) NOT NULL,
    LaAnhChinh BIT DEFAULT 0,

    CONSTRAINT FK_HA_SP FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
);
GO

/*========================================================
    9. GIỎ HÀNG
========================================================*/
CREATE TABLE GioHang (
    MaGH INT IDENTITY(1,1) PRIMARY KEY,
    MaND INT NOT NULL UNIQUE,
    NgayTao DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_GH_ND FOREIGN KEY (MaND) REFERENCES NguoiDung(MaND)
);
GO

CREATE TABLE ChiTietGioHang (
    MaGH INT NOT NULL,
    MaBT INT NOT NULL,
    SoLuong INT NOT NULL,
    NgayThem DATETIME DEFAULT GETDATE(),

    PRIMARY KEY (MaGH, MaBT),
    CONSTRAINT FK_CTGH_GH FOREIGN KEY (MaGH) REFERENCES GioHang(MaGH),
    CONSTRAINT FK_CTGH_BT FOREIGN KEY (MaBT) REFERENCES BienTheSanPham(MaBT),
    CONSTRAINT CHK_CTGH_SL CHECK (SoLuong > 0)
);
GO

/*========================================================
    10. YÊU THÍCH
========================================================*/
CREATE TABLE YeuThich (
    MaND INT NOT NULL,
    MaSP INT NOT NULL,
    NgayThem DATETIME DEFAULT GETDATE(),

    PRIMARY KEY (MaND, MaSP),
    CONSTRAINT FK_YT_ND FOREIGN KEY (MaND) REFERENCES NguoiDung(MaND),
    CONSTRAINT FK_YT_SP FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP)
);
GO

/*========================================================
    11. MÃ GIẢM GIÁ
========================================================*/
CREATE TABLE MaGiamGia (
    MaMGG INT IDENTITY(1,1) PRIMARY KEY,
    Code VARCHAR(50) NOT NULL UNIQUE,
    PhanTramGiam INT NOT NULL,
    SoLuong INT NOT NULL DEFAULT 0,
    NgayBatDau DATETIME DEFAULT GETDATE(),
    NgayKetThuc DATETIME NULL,
    TrangThai BIT DEFAULT 1,

    CONSTRAINT CHK_MGG_PT CHECK (PhanTramGiam BETWEEN 1 AND 100),
    CONSTRAINT CHK_MGG_SL CHECK (SoLuong >= 0)
);
GO

/*========================================================
    12. ĐƠN HÀNG
========================================================*/
CREATE TABLE DonHang (
    MaDH INT IDENTITY(1,1) PRIMARY KEY,
    MaND INT NOT NULL,
    NgayDat DATETIME DEFAULT GETDATE(),
    HoTenNhan NVARCHAR(100) NOT NULL,
    SdtNhan VARCHAR(15) NOT NULL,
    DiaChiNhan NVARCHAR(255) NOT NULL,
    TamTinh DECIMAL(18,2) DEFAULT 0,
    PhiVanChuyen DECIMAL(18,2) DEFAULT 30000,
    GiamGia DECIMAL(18,2) DEFAULT 0,
    TongTien DECIMAL(18,2) DEFAULT 0,
    PhuongThucThanhToan NVARCHAR(50) DEFAULT 'Cash',
    TrangThaiThanhToan NVARCHAR(30) DEFAULT 'Unpaid',
    TrangThaiDonHang NVARCHAR(30) DEFAULT 'Pending',
    GhiChu NVARCHAR(MAX),

    CONSTRAINT FK_DH_ND FOREIGN KEY (MaND) REFERENCES NguoiDung(MaND),
    CONSTRAINT CHK_DH_TT_DON CHECK (
        TrangThaiDonHang IN (
            'Pending',
            'Confirmed',
            'Shipping',
            'Delivered',
            'Cancelled',
            'Return Requested',
            'Returned'
        )
    ),
    CONSTRAINT CHK_DH_TT_THANHTOAN CHECK (
        TrangThaiThanhToan IN (
            'Unpaid',
            'Paid',
            'Failed',
            'Refunded'
        )
    ),
    CONSTRAINT CHK_DH_PTTT CHECK (
        PhuongThucThanhToan IN (
            'Cash',
            'Bank Transfer',
            'VNPay',
            'MoMo'
        )
    ),
    CONSTRAINT CHK_DH_TIEN CHECK (
        TamTinh >= 0 AND PhiVanChuyen >= 0 AND GiamGia >= 0 AND TongTien >= 0
    )
);
GO

CREATE TABLE ChiTietDonHang (
    MaCTDH INT IDENTITY(1,1) PRIMARY KEY,
    MaDH INT NOT NULL,
    MaBT INT NOT NULL,
    SoLuong INT NOT NULL,
    DonGia DECIMAL(18,2) NOT NULL,
    ThanhTien AS (SoLuong * DonGia) PERSISTED,

    CONSTRAINT FK_CTDH_DH FOREIGN KEY (MaDH) REFERENCES DonHang(MaDH),
    CONSTRAINT FK_CTDH_BT FOREIGN KEY (MaBT) REFERENCES BienTheSanPham(MaBT),
    CONSTRAINT CHK_CTDH_SL CHECK (SoLuong > 0),
    CONSTRAINT CHK_CTDH_GIA CHECK (DonGia > 0)
);
GO

/*========================================================
    13. HÓA ĐƠN THANH TOÁN
========================================================*/
CREATE TABLE HoaDon (
    MaHD INT IDENTITY(1,1) PRIMARY KEY,
    MaDH INT NOT NULL UNIQUE,
    NgayLap DATETIME DEFAULT GETDATE(),
    TamTinh DECIMAL(18,2) DEFAULT 0,
    PhiVanChuyen DECIMAL(18,2) DEFAULT 0,
    GiamGia DECIMAL(18,2) DEFAULT 0,
    TongTien DECIMAL(18,2) NOT NULL DEFAULT 0,
    PhuongThucThanhToan NVARCHAR(50),
    TrangThaiThanhToan NVARCHAR(30),
    GhiChu NVARCHAR(500),

    CONSTRAINT FK_HD_DH FOREIGN KEY (MaDH) REFERENCES DonHang(MaDH)
);
GO

/*========================================================
    14. ĐỔI TRẢ HÀNG
========================================================*/
CREATE TABLE DoiTraHang (
    MaDT INT IDENTITY(1,1) PRIMARY KEY,
    MaDH INT NOT NULL,
    MaND INT NOT NULL,
    LyDo NVARCHAR(500) NOT NULL,
    TrangThai NVARCHAR(30) DEFAULT 'Pending',
    NgayYeuCau DATETIME DEFAULT GETDATE(),
    NgayXuLy DATETIME NULL,
    GhiChu NVARCHAR(500),

    CONSTRAINT FK_DT_DH FOREIGN KEY (MaDH) REFERENCES DonHang(MaDH),
    CONSTRAINT FK_DT_ND FOREIGN KEY (MaND) REFERENCES NguoiDung(MaND),
    CONSTRAINT CHK_DT_TT CHECK (
        TrangThai IN ('Pending', 'Approved', 'Rejected', 'Completed')
    )
);
GO

/*========================================================
    15. ĐÁNH GIÁ SẢN PHẨM
========================================================*/
CREATE TABLE DanhGiaSanPham (
    MaDG INT IDENTITY(1,1) PRIMARY KEY,
    MaND INT NOT NULL,
    MaSP INT NOT NULL,
    MaDH INT,
    SoDiem INT NOT NULL,
    NoiDung NVARCHAR(MAX),
    Anh NVARCHAR(1000),
    NgayDanhGia DATETIME DEFAULT GETDATE(),
    TrangThai BIT DEFAULT 1,

    CONSTRAINT FK_DG_ND FOREIGN KEY (MaND) REFERENCES NguoiDung(MaND),
    CONSTRAINT FK_DG_SP FOREIGN KEY (MaSP) REFERENCES SanPham(MaSP),
    CONSTRAINT FK_DG_DH FOREIGN KEY (MaDH) REFERENCES DonHang(MaDH),
    CONSTRAINT CHK_DG_DIEM CHECK (SoDiem BETWEEN 1 AND 5)
);
GO

/*========================================================
    16. BANNER TRANG CHỦ
========================================================*/
CREATE TABLE Banner (
    MaBanner INT IDENTITY(1,1) PRIMARY KEY,
    TieuDe NVARCHAR(255),
    HinhAnh NVARCHAR(1000) NOT NULL,
    LienKet NVARCHAR(255),
    ViTri NVARCHAR(50),
    TrangThai BIT DEFAULT 1
);
GO

/*========================================================
    17. LIÊN HỆ
========================================================*/
CREATE TABLE LienHe (
    MaLH INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(15),
    TieuDe NVARCHAR(200),
    NoiDung NVARCHAR(MAX) NOT NULL,
    NgayGui DATETIME DEFAULT GETDATE(),
    TrangThai NVARCHAR(30) DEFAULT 'Pending',

    CONSTRAINT CHK_LH_EMAIL CHECK (Email LIKE '%@%'),
    CONSTRAINT CHK_LH_TT CHECK (TrangThai IN ('Pending', 'Resolved'))
);
GO
/*========================================================
    18. LICH SU DON HANG
========================================================*/
Create table LichSuDonHang(
    MaLS int identity(1,1) Primary key not null,
    MaDH int not null,
    TrangThai NVARCHAR(100),
    ThoiGian DATETIME DEFAULT GETDATE(),
    GhiChu NVARCHAR(100)
    Constraint fk_lsdh_dh foreign key (MaDH) references DonHang(MaDH)
    )



/*========================================================
    INDEX
========================================================*/
CREATE INDEX IDX_SP_TEN ON SanPham(TenSP);
CREATE INDEX IDX_SP_TH_LOAI ON SanPham(MaTH, MaLoai);
CREATE INDEX IDX_SP_TRANGTHAI ON SanPham(TrangThai);
CREATE INDEX IDX_BT_SP ON BienTheSanPham(MaSP);
CREATE INDEX IDX_DH_ND ON DonHang(MaND);
CREATE INDEX IDX_DH_NGAY ON DonHang(NgayDat);
CREATE INDEX IDX_DH_TRANGTHAI ON DonHang(TrangThaiDonHang);
CREATE INDEX IDX_HD_DH ON HoaDon(MaDH);
CREATE INDEX IDX_DT_DH ON DoiTraHang(MaDH);
GO

/*========================================================
    VIEW: SẢN PHẨM HIỂN THỊ TRÊN WEBSITE
========================================================*/
CREATE VIEW VW_SanPhamHienThi AS
SELECT
    SP.MaSP,
    SP.TenSP,
    TH.TenTH,
    LG.TenLoai,
    SP.Gia,
    SP.GiaKhuyenMai,
    SP.AnhDaiDien,
    SP.LuotXem,
    SP.DiemDanhGia,
    ISNULL(SUM(BT.SoLuongTon), 0) AS TongTonKho,
    SP.TrangThai
FROM SanPham SP
JOIN ThuongHieu TH ON SP.MaTH = TH.MaTH
JOIN LoaiGiay LG ON SP.MaLoai = LG.MaLoai
LEFT JOIN BienTheSanPham BT ON SP.MaSP = BT.MaSP
GROUP BY
    SP.MaSP, SP.TenSP, TH.TenTH, LG.TenLoai,
    SP.Gia, SP.GiaKhuyenMai, SP.AnhDaiDien,
    SP.LuotXem, SP.DiemDanhGia, SP.TrangThai;
GO

/*========================================================
    VIEW: DOANH THU THEO THÁNG
========================================================*/
CREATE VIEW VW_DoanhThuThang AS
SELECT
    YEAR(NgayDat) AS Nam,
    MONTH(NgayDat) AS Thang,
    SUM(TongTien) AS DoanhThu,
    COUNT(*) AS SoDonHang
FROM DonHang
WHERE TrangThaiThanhToan = 'Paid'
   OR TrangThaiDonHang = 'Delivered'
GROUP BY YEAR(NgayDat), MONTH(NgayDat);
GO

/*========================================================
    TRIGGER: CẬP NHẬT ĐIỂM ĐÁNH GIÁ TRUNG BÌNH
========================================================*/
CREATE TRIGGER TRG_CapNhatDiemDanhGia
ON DanhGiaSanPham
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE SP
    SET DiemDanhGia = ISNULL(X.DiemTB, 0)
    FROM SanPham SP
    LEFT JOIN (
        SELECT MaSP, AVG(CAST(SoDiem AS FLOAT)) AS DiemTB
        FROM DanhGiaSanPham
        WHERE TrangThai = 1
        GROUP BY MaSP
    ) X ON SP.MaSP = X.MaSP
    WHERE SP.MaSP IN (
        SELECT MaSP FROM inserted
        UNION
        SELECT MaSP FROM deleted
    );
END;
GO

/*========================================================
    TRIGGER: TRỪ TỒN KHO KHI THÊM CHI TIẾT ĐƠN HÀNG
========================================================*/
CREATE TRIGGER TRG_TruTonKhoKhiDatHang
ON ChiTietDonHang
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
        FROM inserted I
        JOIN BienTheSanPham BT ON I.MaBT = BT.MaBT
        WHERE BT.SoLuongTon < I.SoLuong
    )
    BEGIN
        RAISERROR(N'Số lượng tồn kho không đủ', 16, 1);
        ROLLBACK TRANSACTION;
        RETURN;
    END

    UPDATE BT
    SET BT.SoLuongTon = BT.SoLuongTon - I.SoLuong
    FROM BienTheSanPham BT
    JOIN inserted I ON BT.MaBT = I.MaBT;
END;
GO

/*========================================================
    TRIGGER: TỰ TẠO/CẬP NHẬT HÓA ĐƠN KHI ĐƠN PAID HOẶC DELIVERED
========================================================*/
CREATE TRIGGER TRG_TaoHoaDon
ON DonHang
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO HoaDon (
        MaDH,
        TamTinh,
        PhiVanChuyen,
        GiamGia,
        TongTien,
        PhuongThucThanhToan,
        TrangThaiThanhToan,
        GhiChu
    )
    SELECT
        I.MaDH,
        I.TamTinh,
        I.PhiVanChuyen,
        I.GiamGia,
        I.TongTien,
        I.PhuongThucThanhToan,
        I.TrangThaiThanhToan,
        N'Hóa đơn được tạo tự động'
    FROM inserted I
    WHERE (I.TrangThaiThanhToan = 'Paid' OR I.TrangThaiDonHang = 'Delivered')
      AND NOT EXISTS (
          SELECT 1 FROM HoaDon HD WHERE HD.MaDH = I.MaDH
      );

    UPDATE HD
    SET
        HD.TamTinh = I.TamTinh,
        HD.PhiVanChuyen = I.PhiVanChuyen,
        HD.GiamGia = I.GiamGia,
        HD.TongTien = I.TongTien,
        HD.PhuongThucThanhToan = I.PhuongThucThanhToan,
        HD.TrangThaiThanhToan = I.TrangThaiThanhToan
    FROM HoaDon HD
    JOIN inserted I ON HD.MaDH = I.MaDH;
END;
GO

/*========================================================
    TRIGGER: CỘNG LẠI TỒN KHO KHI ĐƠN BỊ HỦY HOẶC ĐỔI TRẢ XONG
    Lưu ý: chỉ cộng khi chuyển sang Cancelled hoặc Returned.
========================================================*/
CREATE TRIGGER TRG_CongTonKhoKhiHuyHoacTra
ON DonHang
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE BT
    SET BT.SoLuongTon = BT.SoLuongTon + CT.SoLuong
    FROM BienTheSanPham BT
    JOIN ChiTietDonHang CT ON BT.MaBT = CT.MaBT
    JOIN inserted I ON CT.MaDH = I.MaDH
    JOIN deleted D ON I.MaDH = D.MaDH
    WHERE I.TrangThaiDonHang IN ('Cancelled', 'Returned')
      AND D.TrangThaiDonHang NOT IN ('Cancelled', 'Returned');
END;
GO

/*========================================================
    DỮ LIỆU MẪU
========================================================*/
INSERT INTO NguoiDung(HoTen, Email, SoDienThoai, MatKhau, VaiTro, Avatar, DiaChi) VALUES
(N'Quản trị hệ thống', 'admin@gmail.com', '0900000001', '123', 'ADMIN', 'admin.png', N'TP.HCM'),
(N'Nhân viên bán hàng', 'staff@gmail.com', '0900000002', '123', 'NHANVIEN', 'staff.png', N'TP.HCM'),
(N'Nguyễn Thành Huy', 'user@gmail.com', '0900000003', '123', 'USER', 'user1.png', N'Bình Dương'),
(N'Trần Khánh Vy', 'vy@gmail.com', '0900000004', '123', 'USER', 'user2.png', N'TP.HCM');
GO

INSERT INTO ThuongHieu(TenTH, MoTa, Logo) VALUES
(N'Nike', N'Thương hiệu giày thể thao nổi tiếng toàn cầu', 'https://images.unsplash.com/photo-1623788975845-7d3e0adbae7c?w=400&h=400&fit=crop&auto=format'),
(N'Adidas', N'Thương hiệu thể thao đến từ Đức', 'https://images.unsplash.com/photo-1715773408837-b7074beb12d5?w=400&h=400&fit=crop&auto=format'),
(N'Puma', N'Thương hiệu giày thể thao phong cách trẻ trung', 'https://images.unsplash.com/photo-1641745900305-d121f24aa737?w=400&h=400&fit=crop&auto=format'),
(N'Converse', N'Dòng giày thời trang năng động', 'https://images.unsplash.com/photo-1634624943287-6e1f2d103201?w=400&h=400&fit=crop&auto=format'),
(N'New Balance', N'Giày chạy bộ và lifestyle chất lượng cao', 'https://images.unsplash.com/photo-1641745900309-75ceed0153e1?w=400&h=400&fit=crop&auto=format'),
(N'Vans', N'Giày thời trang đường phố', 'https://images.unsplash.com/photo-1779675789055-078177dcb8c5?w=400&h=400&fit=crop&auto=format');
GO

INSERT INTO LoaiGiay(TenLoai, MoTa, Icon) VALUES
(N'Giày chạy bộ', N'Phù hợp chạy bộ, đi bộ và luyện tập', 'directions_run'),
(N'Giày bóng đá', N'Giày đá bóng sân cỏ nhân tạo và sân cỏ tự nhiên', 'sports_soccer'),
(N'Giày bóng rổ', N'Giày hỗ trợ bật nhảy và bảo vệ cổ chân', 'sports_basketball'),
(N'Giày tập gym', N'Giày luyện tập trong phòng gym', 'fitness_center'),
(N'Giày thời trang', N'Giày sneaker đi học, đi chơi hằng ngày', 'checkroom');
GO

INSERT INTO MauSac(TenMau, MaMauHex) VALUES
(N'Trắng', '#FFFFFF'),
(N'Đen', '#000000'),
(N'Đỏ', '#FF0000'),
(N'Xám', '#808080'),
(N'Xanh navy', '#001F3F');
GO

INSERT INTO SizeGiay(TenSize) VALUES
('36'), ('37'), ('38'), ('39'), ('40'), ('41'), ('42'), ('43');
GO

INSERT INTO SanPham
(TenSP, MaTH, MaLoai, Gia, GiaKhuyenMai, MoTa, AnhDaiDien, LuotXem, DiemDanhGia, TrangThai)
VALUES
(N'Nike Air Max 270 Black White', 1, 1, 3890000, 3490000, N'Mẫu giày chạy bộ phong cách hiện đại, đệm khí êm ái, phù hợp đi chơi và luyện tập.', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800&h=800&fit=crop&auto=format', 1800, 4.9, 'Active'),
(N'Nike Air Force 1 Low White', 1, 5, 2990000, 2690000, N'Sneaker trắng kinh điển, dễ phối đồ, phù hợp đi học, đi chơi và sử dụng hằng ngày.', 'https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=800&h=800&fit=crop&auto=format', 1600, 4.8, 'Active'),
(N'Nike Jordan 1 Mid Red Black', 1, 3, 4290000, 3890000, N'Giày bóng rổ Jordan 1 Mid thiết kế nổi bật, phong cách streetwear cao cấp.', 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=800&h=800&fit=crop&auto=format', 2100, 4.9, 'Active'),
(N'Nike Metcon 9 Training', 1, 4, 3290000, 2990000, N'Giày tập gym chắc chắn, hỗ trợ nâng tạ, luyện tập cường độ cao.', 'https://images.unsplash.com/photo-1543508282-6319a3e2621f?w=800&h=800&fit=crop&auto=format', 980, 4.6, 'Active'),
(N'Adidas Ultraboost Light', 2, 1, 4590000, 4190000, N'Giày chạy bộ Adidas Ultraboost với đệm êm, thiết kế thể thao cao cấp.', 'https://images.unsplash.com/photo-1587563871167-1ee9c731aefb?w=800&h=800&fit=crop&auto=format', 1750, 4.8, 'Active'),
(N'Adidas Superstar Core Black', 2, 5, 2590000, 2390000, N'Adidas Superstar cổ điển, mũi sò đặc trưng, phù hợp phong cách lifestyle.', 'https://images.unsplash.com/photo-1518002171953-a080ee817e1f?w=800&h=800&fit=crop&auto=format', 1300, 4.7, 'Active'),
(N'Adidas NMD R1 Triple Black', 2, 5, 3690000, 3290000, N'Sneaker Adidas NMD thiết kế tối giản, năng động, phù hợp thời trang đường phố.', 'https://images.unsplash.com/photo-1556906781-9a412961c28c?w=800&h=800&fit=crop&auto=format', 1190, 4.6, 'Active'),
(N'Adidas Predator Accuracy TF', 2, 2, 2390000, 2190000, N'Giày bóng đá sân cỏ nhân tạo, hỗ trợ kiểm soát bóng và bám sân tốt.', 'https://images.unsplash.com/photo-1511886929837-354d827aae26?w=800&h=800&fit=crop&auto=format', 860, 4.5, 'Active'),
(N'Puma RS-X Reinvention', 3, 5, 2890000, 2490000, N'Giày Puma RS-X phong cách trẻ trung, màu sắc nổi bật, phù hợp outfit năng động.', 'https://images.unsplash.com/photo-1641745900305-d121f24aa737?w=800&h=800&fit=crop&auto=format', 1020, 4.6, 'Active'),
(N'Puma Future Rider Play On', 3, 1, 2290000, 1990000, N'Giày chạy bộ nhẹ, đế êm, thiết kế thể thao hiện đại.', 'https://images.unsplash.com/photo-1641745900309-75ceed0153e1?w=800&h=800&fit=crop&auto=format', 900, 4.5, 'Active'),
(N'Converse Chuck Taylor 70 High', 4, 5, 1890000, NULL, N'Converse cổ cao phong cách cổ điển, phù hợp học sinh sinh viên.', 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=800&h=800&fit=crop&auto=format', 1250, 4.7, 'Active'),
(N'Converse Run Star Hike', 4, 5, 2590000, 2290000, N'Mẫu Converse đế cao cá tính, phù hợp phong cách thời trang đường phố.', 'https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=800&h=800&fit=crop&auto=format', 870, 4.5, 'Active'),
(N'New Balance 574 Grey', 5, 1, 2750000, 2490000, N'New Balance 574 êm chân, kiểu dáng lifestyle, phù hợp sử dụng hằng ngày.', 'https://images.unsplash.com/photo-1539185441755-769473a23570?w=800&h=800&fit=crop&auto=format', 1100, 4.7, 'Active'),
(N'New Balance 327 White Green', 5, 5, 3190000, 2890000, N'Giày New Balance 327 thiết kế retro hiện đại, phối màu trẻ trung.', 'https://images.unsplash.com/photo-1560769629-975ec94e6a86?w=800&h=800&fit=crop&auto=format', 960, 4.6, 'Active'),
(N'Vans Old Skool Black White', 6, 5, 1650000, 1490000, N'Vans Old Skool đen trắng, biểu tượng streetwear dễ phối đồ.', 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=800&h=800&fit=crop&auto=format', 1450, 4.6, 'Active'),
(N'Vans Sk8-Hi Classic', 6, 5, 1890000, 1690000, N'Giày Vans cổ cao phong cách skateboarding, bền bỉ và cá tính.', 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?w=800&h=800&fit=crop&auto=format', 800, 4.4, 'Active');
GO

/* Thêm biến thể tồn kho cho mỗi sản phẩm */
INSERT INTO BienTheSanPham(MaSP, MaSize, MaMau, SoLuongTon, SKU)
SELECT MaSP, 5, 2, 20, CONCAT('SP', FORMAT(MaSP, '00'), '-S40-BLK')
FROM SanPham;

INSERT INTO BienTheSanPham(MaSP, MaSize, MaMau, SoLuongTon, SKU)
SELECT MaSP, 6, 1, 15, CONCAT('SP', FORMAT(MaSP, '00'), '-S41-WHT')
FROM SanPham;

INSERT INTO BienTheSanPham(MaSP, MaSize, MaMau, SoLuongTon, SKU)
SELECT MaSP, 7, 4, 10, CONCAT('SP', FORMAT(MaSP, '00'), '-S42-GRY')
FROM SanPham;
GO

INSERT INTO HinhAnhSanPham(MaSP, DuongDan, LaAnhChinh)
SELECT MaSP, AnhDaiDien, 1
FROM SanPham;
GO

INSERT INTO GioHang(MaND) VALUES (3), (4);
GO

INSERT INTO YeuThich(MaND, MaSP) VALUES
(3,1),
(3,8),
(4,5);
GO

INSERT INTO MaGiamGia(Code, PhanTramGiam, SoLuong, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('SPORT10', 10, 100, GETDATE(), DATEADD(DAY, 30, GETDATE()), 1),
('SALE20', 20, 50, GETDATE(), DATEADD(DAY, 15, GETDATE()), 1),
('HUY30', 30, 30, GETDATE(), DATEADD(DAY, 10, GETDATE()), 1);
GO

INSERT INTO DonHang(
    MaND,
    HoTenNhan,
    SdtNhan,
    DiaChiNhan,
    TamTinh,
    PhiVanChuyen,
    GiamGia,
    TongTien,
    PhuongThucThanhToan,
    TrangThaiThanhToan,
    TrangThaiDonHang,
    GhiChu
) VALUES
(3, N'Nguyễn Thành Huy', '0900000003', N'Bình Dương', 5080000, 30000, 100000, 5010000, 'VNPay', 'Paid', 'Delivered', N'Giao giờ hành chính'),
(4, N'Trần Khánh Vy', '0900000004', N'TP.HCM', 1890000, 30000, 0, 1920000, 'Cash', 'Unpaid', 'Pending', N'');
GO

INSERT INTO ChiTietDonHang(MaDH, MaBT, SoLuong, DonGia) VALUES
(1, 1, 1, 2690000),
(1, 4, 1, 2390000),
(2, 31, 1, 1890000);
GO

INSERT INTO DanhGiaSanPham(MaND, MaSP, MaDH, SoDiem, NoiDung, Anh) VALUES
(3, 1, 1, 5, N'Giày đẹp, form chuẩn, giao hàng nhanh.', 'review1.jpg'),
(3, 6, 1, 4, N'Sản phẩm tốt, đóng gói cẩn thận.', NULL);
GO

INSERT INTO Banner(TieuDe, HinhAnh, LienKet, ViTri, TrangThai) VALUES
(N'Air Max Campaign', 'https://images.unsplash.com/photo-1731132198530-e4b2dc51d511?w=1920&h=900&fit=crop&auto=format', '/products?brand=1', N'Trang chủ', 1),
(N'Ultraboost Campaign', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=1920&h=900&fit=crop&auto=format', '/products?brand=2', N'Trang chủ', 1),
(N'Flash Sale 40%', 'https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=1920&h=900&fit=crop&auto=format', '/products?sale=true', N'Trang chủ', 1);
GO

INSERT INTO LienHe(HoTen, Email, SoDienThoai, TieuDe, NoiDung, TrangThai) VALUES
(N'Nguyễn Thành Huy', 'user@gmail.com', '0900000003', N'Hỏi về size giày', N'Tôi muốn tư vấn size Nike Air Force 1.', 'Pending');
GO

/* Tạo hóa đơn cho đơn mẫu đã thanh toán */
INSERT INTO HoaDon(MaDH, TamTinh, PhiVanChuyen, GiamGia, TongTien, PhuongThucThanhToan, TrangThaiThanhToan, GhiChu)
SELECT MaDH, TamTinh, PhiVanChuyen, GiamGia, TongTien, PhuongThucThanhToan, TrangThaiThanhToan, N'Hóa đơn mẫu'
FROM DonHang
WHERE TrangThaiThanhToan = 'Paid';
GO

/*========================================================
    KIỂM TRA NHANH
========================================================*/
SELECT * FROM NguoiDung;
SELECT * FROM ThuongHieu;
SELECT * FROM LoaiGiay;
SELECT * FROM SanPham;
SELECT * FROM BienTheSanPham;
SELECT * FROM VW_SanPhamHienThi;
SELECT * FROM DonHang;
SELECT * FROM ChiTietDonHang;
SELECT * FROM HoaDon;
SELECT * FROM MaGiamGia;
SELECT * FROM DoiTraHang;
SELECT * FROM VW_DoanhThuThang;
GO

/*========================================================
    PLATFORM FEATURES
========================================================*/

USE QLGIAY;
GO

IF OBJECT_ID(N'dbo.LichSuDonHang', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LichSuDonHang (
        MaLS INT IDENTITY(1, 1) NOT NULL PRIMARY KEY,
        MaDH INT NOT NULL,
        TrangThai NVARCHAR(100) NOT NULL,
        ThoiGian DATETIME2 NOT NULL CONSTRAINT DF_LichSuDonHang_ThoiGian DEFAULT GETDATE(),
        GhiChu NVARCHAR(500) NULL,
        CONSTRAINT FK_LichSuDonHang_DonHang FOREIGN KEY (MaDH) REFERENCES dbo.DonHang(MaDH)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_LichSuDonHang_MaDH_ThoiGian' AND object_id = OBJECT_ID(N'dbo.LichSuDonHang'))
    CREATE INDEX IX_LichSuDonHang_MaDH_ThoiGian ON dbo.LichSuDonHang(MaDH, ThoiGian, MaLS);
GO

IF OBJECT_ID(N'dbo.StockMovement', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.StockMovement (
        MovementId INT IDENTITY(1, 1) NOT NULL PRIMARY KEY,
        ProductVariantId INT NOT NULL,
        MovementType NVARCHAR(30) NOT NULL,
        Quantity INT NOT NULL,
        BeforeQuantity INT NOT NULL,
        AfterQuantity INT NOT NULL,
        ReferenceId INT NULL,
        ReferenceType NVARCHAR(50) NULL,
        CreatedBy INT NULL,
        CreatedAt DATETIME2 NOT NULL CONSTRAINT DF_StockMovement_CreatedAt DEFAULT GETDATE(),
        Note NVARCHAR(500) NULL,
        CONSTRAINT CK_StockMovement_MovementType CHECK (MovementType IN (N'IMPORT', N'ORDER', N'RETURN', N'CANCEL', N'ADJUSTMENT')),
        CONSTRAINT CK_StockMovement_Quantity CHECK (Quantity > 0),
        CONSTRAINT CK_StockMovement_AfterQuantity CHECK (AfterQuantity >= 0),
        CONSTRAINT FK_StockMovement_BienTheSanPham FOREIGN KEY (ProductVariantId) REFERENCES dbo.BienTheSanPham(MaBT),
        CONSTRAINT FK_StockMovement_NguoiDung FOREIGN KEY (CreatedBy) REFERENCES dbo.NguoiDung(MaND)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_StockMovement_Variant_CreatedAt' AND object_id = OBJECT_ID(N'dbo.StockMovement'))
    CREATE INDEX IX_StockMovement_Variant_CreatedAt ON dbo.StockMovement(ProductVariantId, CreatedAt DESC);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_StockMovement_Reference' AND object_id = OBJECT_ID(N'dbo.StockMovement'))
    CREATE INDEX IX_StockMovement_Reference ON dbo.StockMovement(ReferenceType, ReferenceId);
GO

IF OBJECT_ID(N'dbo.AuditLog', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.AuditLog (
        AuditId INT IDENTITY(1, 1) NOT NULL PRIMARY KEY,
        UserId INT NULL,
        [Action] NVARCHAR(100) NOT NULL,
        [Entity] NVARCHAR(100) NOT NULL,
        EntityId INT NULL,
        OldValue NVARCHAR(MAX) NULL,
        NewValue NVARCHAR(MAX) NULL,
        IPAddress NVARCHAR(45) NULL,
        CreatedAt DATETIME2 NOT NULL CONSTRAINT DF_AuditLog_CreatedAt DEFAULT GETDATE(),
        Result NVARCHAR(30) NOT NULL CONSTRAINT DF_AuditLog_Result DEFAULT N'SUCCESS',
        CONSTRAINT FK_AuditLog_NguoiDung FOREIGN KEY (UserId) REFERENCES dbo.NguoiDung(MaND)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_AuditLog_CreatedAt' AND object_id = OBJECT_ID(N'dbo.AuditLog'))
    CREATE INDEX IX_AuditLog_CreatedAt ON dbo.AuditLog(CreatedAt DESC);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_AuditLog_Action_CreatedAt' AND object_id = OBJECT_ID(N'dbo.AuditLog'))
    CREATE INDEX IX_AuditLog_Action_CreatedAt ON dbo.AuditLog([Action], CreatedAt DESC);
GO

IF OBJECT_ID(N'dbo.Notification', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Notification (
        NotificationId INT IDENTITY(1, 1) NOT NULL PRIMARY KEY,
        Title NVARCHAR(200) NOT NULL,
        Content NVARCHAR(MAX) NOT NULL,
        [Type] NVARCHAR(50) NOT NULL,
        TargetRole NVARCHAR(30) NULL,
        TargetUser INT NULL,
        ReadStatus BIT NOT NULL CONSTRAINT DF_Notification_ReadStatus DEFAULT 0,
        CreatedAt DATETIME2 NOT NULL CONSTRAINT DF_Notification_CreatedAt DEFAULT GETDATE(),
        ReadAt DATETIME2 NULL,
        CONSTRAINT CK_Notification_Target CHECK (TargetRole IS NOT NULL OR TargetUser IS NOT NULL),
        CONSTRAINT FK_Notification_NguoiDung FOREIGN KEY (TargetUser) REFERENCES dbo.NguoiDung(MaND)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Notification_User_Read_CreatedAt' AND object_id = OBJECT_ID(N'dbo.Notification'))
    CREATE INDEX IX_Notification_User_Read_CreatedAt ON dbo.Notification(TargetUser, ReadStatus, CreatedAt DESC);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_Notification_Role_Read_CreatedAt' AND object_id = OBJECT_ID(N'dbo.Notification'))
    CREATE INDEX IX_Notification_Role_Read_CreatedAt ON dbo.Notification(TargetRole, ReadStatus, CreatedAt DESC);
GO

IF OBJECT_ID(N'dbo.ProductViewHistory', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ProductViewHistory (
        HistoryId INT IDENTITY(1, 1) NOT NULL PRIMARY KEY,
        UserId INT NOT NULL,
        ProductId INT NOT NULL,
        ViewedAt DATETIME2 NOT NULL CONSTRAINT DF_ProductViewHistory_ViewedAt DEFAULT GETDATE(),
        CONSTRAINT UQ_ProductViewHistory_User_Product UNIQUE (UserId, ProductId),
        CONSTRAINT FK_ProductViewHistory_NguoiDung FOREIGN KEY (UserId) REFERENCES dbo.NguoiDung(MaND),
        CONSTRAINT FK_ProductViewHistory_SanPham FOREIGN KEY (ProductId) REFERENCES dbo.SanPham(MaSP)
    );
END;
GO

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_ProductViewHistory_User_ViewedAt' AND object_id = OBJECT_ID(N'dbo.ProductViewHistory'))
    CREATE INDEX IX_ProductViewHistory_User_ViewedAt ON dbo.ProductViewHistory(UserId, ViewedAt DESC);
GO
