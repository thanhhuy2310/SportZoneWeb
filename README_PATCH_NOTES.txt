# SportZone - New Features

Phiên bản này bổ sung một số chức năng giúp hệ thống quản lý bán giày hoạt động chuyên nghiệp và sát với quy trình doanh nghiệp.

---

# 1. Order History (Lịch sử đơn hàng)

## Mục đích

Lưu lại toàn bộ quá trình thay đổi trạng thái của đơn hàng.

Ví dụ:

```
Đã tạo
↓
Đã xác nhận
↓
Đang giao
↓
Đã giao
```

## Bảng sử dụng

```
LichSuDonHang
```

## Dữ liệu lưu

- Mã đơn hàng
- Trạng thái
- Thời gian cập nhật
- Ghi chú (nếu có)

---

# 2. Stock Movement (Lịch sử kho)

## Mục đích

Theo dõi mọi thay đổi về số lượng tồn kho của từng biến thể sản phẩm.

Các trường hợp được ghi nhận:

- Nhập hàng
- Khách đặt hàng
- Khách trả hàng
- Hủy đơn hàng
- Điều chỉnh tồn kho

## Bảng sử dụng

```
StockMovement
```

## Các loại giao dịch

| Loại | Ý nghĩa |
|------|----------|
| IMPORT | Nhập kho |
| ORDER | Khách mua |
| RETURN | Trả hàng |
| CANCEL | Hủy đơn |
| ADJUSTMENT | Điều chỉnh tồn |

Ví dụ:

```
Tồn kho trước : 100

Khách mua : 2

Tồn kho sau : 98
```

---

# 3. Audit Log (Nhật ký hệ thống)

## Mục đích

Ghi lại mọi thao tác quan trọng của người dùng trong hệ thống.

Ví dụ:

- Thêm sản phẩm
- Sửa sản phẩm
- Xóa banner
- Cập nhật đơn hàng

## Bảng sử dụng

```
AuditLog
```

Thông tin được lưu:

- Người thực hiện
- Hành động
- Đối tượng tác động
- Dữ liệu trước khi sửa
- Dữ liệu sau khi sửa
- Địa chỉ IP
- Thời gian

---

# 4. Notification (Thông báo)

## Mục đích

Gửi thông báo đến người dùng hoặc quản trị viên.

Ví dụ:

- Có đơn hàng mới
- Kho sắp hết hàng
- Đơn hàng đã giao thành công
- Có chương trình khuyến mãi

## Bảng sử dụng

```
Notification
```

Hỗ trợ:

- Gửi theo User
- Gửi theo Role
- Đánh dấu đã đọc / chưa đọc

---

# 5. Product View History (Lịch sử xem sản phẩm)

## Mục đích

Lưu lại các sản phẩm mà khách hàng đã xem.

Có thể sử dụng để:

- Hiển thị "Sản phẩm đã xem gần đây"
- Gợi ý sản phẩm
- Phân tích hành vi người dùng

## Bảng sử dụng

```
ProductViewHistory
```

---

# Luồng hoạt động

### Khi khách đặt hàng

```
Khách đặt hàng
        │
        ▼
Tạo đơn hàng
        │
        ▼
Lưu lịch sử đơn hàng
        │
        ▼
Cập nhật tồn kho
        │
        ▼
Lưu Stock Movement
        │
        ▼
Gửi thông báo cho Admin
```

---

# API đề xuất

## Order History

```
GET /admin/orders/{id}/history
```

## Stock Movement

```
GET /admin/stock-movements

GET /admin/product-variants/{id}/movements
```

## Audit Log

```
GET /admin/audit-logs
```

## Notification

```
GET /notifications

PUT /notifications/{id}/read
```

## Product View History

```
POST /products/{id}/view

GET /users/me/recently-viewed
```

---
