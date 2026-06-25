# Late Return Implementation Plan

Phần trả xe hiện tại chỉ hoàn tất Rental và cập nhật trạng thái Vehicle. Chưa tính hoặc lưu phí trả trễ vì bảng `Rental` hiện tại chưa có `actual_return_date` và `late_fee`.

## 1. Thay đổi database

Chạy migration có kiểm tra cột trước khi thêm:

```sql
IF COL_LENGTH('dbo.Rental', 'actual_return_date') IS NULL
    ALTER TABLE dbo.Rental ADD actual_return_date DATE NULL;

IF COL_LENGTH('dbo.Rental', 'late_fee') IS NULL
BEGIN
    ALTER TABLE dbo.Rental ADD late_fee DECIMAL(10,2) NOT NULL
        CONSTRAINT DF_Rental_LateFee DEFAULT 0;
    ALTER TABLE dbo.Rental ADD CONSTRAINT CK_Rental_LateFee CHECK (late_fee >= 0);
END;
```

Nếu nghiệp vụ cần tính trễ theo giờ, đổi `actual_return_date` thành `DATETIME2` và bổ sung giờ nhận/trả dự kiến. Với quy tắc tính theo ngày hiện tại, kiểu `DATE` là đủ.

-> Chỉ cần tính theo ngày, trễ 1 ngày mới bắt đầu tính tiền trễ hạn không tính theo giờ.

## 2. Cập nhật JPA Entity

Thêm vào `Rental`:

- `Date actualReturnDate` ánh xạ `actual_return_date`.
- `BigDecimal lateFee` ánh xạ `late_fee`, mặc định `BigDecimal.ZERO`.

Không dùng `ddl-generation=create-tables` trên database đang có dữ liệu. Schema phải được cập nhật có kiểm soát trước khi deploy entity mới.

## 3. Tính phí trong ReturnService

Tại thời điểm Confirm Return:

```text
actualReturnDate = currentDate
lateDays = max(0, DAYS.between(endDate, actualReturnDate))
lateFee = pricePerDay * lateDays
```

Lấy `pricePerDay` từ `Vehicle -> VehicleModel`. Dùng `BigDecimal` cho toàn bộ phép tính tiền.

Trong cùng transaction:

1. Khóa Rental và Vehicle.
2. Kiểm tra Rental `RENTED` và Vehicle `RENTED`.
3. Gán `actualReturnDate`, `lateFee`, Rental `COMPLETED`.
4. Cập nhật Vehicle theo tình trạng xe.
5. Tạo Incident/Maintenance nếu xe hư hỏng.
6. Tạo Rental Status History.
7. Commit.

## 4. Quyết định nghiệp vụ còn thiếu

Trước khi code cần thống nhất cách thu `lateFee`:

- Trừ trực tiếp Wallet hay tạo Payment mới.
->Tạo payment mới bằng VNPay hoặc cho thanh toán bằng tiền mặt nếu khách hàng không chuyển khoản

- Cho phép Staff xác nhận trả xe trước rồi ghi nhận công nợ hay không.
-> Cho staff xác nhận trả xe trước rồi sau đó thanh toán phần phí trả trễ sau

Không nên chỉ lưu `late_fee` mà không có trạng thái thanh toán của khoản phí này.

## 5. UI và kiểm thử

- Hiển thị cảnh báo số ngày trễ và phí trước popup Confirm Return.
- Kiểm thử trả đúng hạn, sớm hạn, trễ một ngày và nhiều ngày.
- Kiểm thử NORMAL và DAMAGED đều lưu phí đúng.
- Kiểm thử hai Staff xác nhận cùng lúc; chỉ một transaction được thành công.
- Kiểm thử rollback để Rental, Vehicle, Incident, Maintenance và History không bị cập nhật dở dang.
