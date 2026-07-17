SPORTZONE FINAL PATCH

Các chức năng đã thêm/sửa:
1. Đổi trả hàng
   - User gửi yêu cầu đổi trả trong trang Profile khi đơn đã Delivered.
   - Admin xử lý tại /admin/returns.
   - Approve return sẽ đổi trạng thái đơn sang Return Approved, thanh toán Refunded và cộng lại tồn kho.

2. Hóa đơn thanh toán
   - User/Admin xem hóa đơn tại /invoice/{maDH}.
   - Hóa đơn hiển thị sản phẩm, size, màu, số lượng, giá, subtotal, shipping, discount và total.

3. Ràng buộc tồn kho
   - Thêm vào giỏ hàng không vượt quá tồn kho.
   - Cập nhật giỏ hàng không vượt quá tồn kho.
   - Đặt hàng thành công sẽ trừ SoLuongTon của BienTheSanPham.
   - Hủy đơn trước Delivered sẽ cộng lại tồn kho.
   - Duyệt đổi trả sẽ cộng lại tồn kho.

4. Mã giảm giá
   - Có Apply Coupon tại Checkout.
   - Discount hiển thị ngay trong Order Summary.
   - Khi đặt hàng mới trừ lượt sử dụng mã giảm giá.

5. Đồng bộ tiền tệ
   - Database vẫn lưu VND.
   - Giao diện hiển thị USD theo tỷ giá 1 USD = 26000 VND.
   - Đã sửa Cart, Checkout, Profile, Invoice, Admin Orders, Admin Products, Dashboard, Product cards.

Cần chạy file SQL_PATCH_RETURN_INVOICE_STOCK_USD.sql trong SQL Server trước khi chạy project.
