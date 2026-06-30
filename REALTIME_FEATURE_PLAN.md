# Realtime Feature Implementation Plan

## Mục tiêu

Thêm realtime để các màn hình quan trọng tự cập nhật khi dữ liệu thay đổi, giảm việc staff/admin phải refresh thủ công và làm hệ thống giống một app vận hành thật hơn.

Dự án hiện tại dùng Java JSP, Servlet, JPA/JDBC, SQL Server. Hướng phù hợp nhất là dùng WebSocket trong Java Web App, kết hợp fallback polling nhẹ nếu môi trường deploy không ổn định WebSocket.

## Những chức năng nên gắn realtime

## 1. Staff Pickup Management

### Lý do

Staff cần thấy ngay booking mới khi khách vừa thanh toán thành công. Nếu không realtime, staff phải refresh trang `Pickup Management` mới thấy đơn `BOOKED`.

### Khi nào cần bắn event

- Customer booking thành công bằng Wallet.
- VNPay callback thành công.
- Rental chuyển sang `BOOKED`.
- Staff confirm pickup, rental chuyển `BOOKED -> RENTED`.
- Staff mark no-show, rental chuyển `BOOKED -> NO_SHOW`.

### Event đề xuất

```text
RENTAL_BOOKED
PICKUP_CONFIRMED
RENTAL_NO_SHOW
```

### UI cần cập nhật

- Badge số lượng `Vehicles Waiting For Pickup`.
- Bảng Pickup Management thêm/xóa dòng tương ứng.
- Dashboard staff recent activities.

### Ưu tiên

P1. Đây là phần realtime đáng làm đầu tiên.

## 2. Staff Return Management

### Lý do

Khi staff confirm pickup, xe chuyển sang `RENTED`, đơn đó cần xuất hiện ngay ở Return Management. Khi staff confirm return, đơn cần biến mất khỏi danh sách đang thuê.

### Khi nào cần bắn event

- Rental chuyển `BOOKED -> RENTED`.
- Rental chuyển `RENTED -> COMPLETED`.
- Vehicle chuyển `RENTED -> AVAILABLE`.
- Vehicle chuyển `RENTED -> MAINTENANCE`.

### Event đề xuất

```text
RENTAL_RENTED
RENTAL_COMPLETED
VEHICLE_RETURNED
VEHICLE_MOVED_TO_MAINTENANCE
```

### UI cần cập nhật

- Bảng Return Management.
- Badge `Vehicles Currently Rented`.
- Badge `Vehicles Waiting For Return`.
- Recent activities.

### Ưu tiên

P1.

## 3. Vehicle Availability On Home Page

### Lý do

Customer đang xem danh sách xe có thể bị stale. Ví dụ một xe vừa được người khác booking thành công, nhưng trang home vẫn hiển thị còn xe nếu chưa refresh.

### Khi nào cần bắn event

- Booking thành công.
- Vehicle status chuyển `AVAILABLE -> RENTED`.
- Return thành công, vehicle chuyển `RENTED -> AVAILABLE`.
- Maintenance completed, vehicle chuyển `MAINTENANCE -> AVAILABLE`.

### Event đề xuất

```text
VEHICLE_AVAILABILITY_CHANGED
VEHICLE_RENTED
VEHICLE_AVAILABLE
```

### UI cần cập nhật

- Số lượng `Remaining`.
- Danh sách model đang có tại trạm.
- Nếu customer đang ở vehicle detail, hiển thị cảnh báo nếu xe vừa bị người khác đặt.

### Ưu tiên

P1 hoặc P2. Nên làm sau staff pickup/return vì có ảnh hưởng UX customer.

## 4. Admin Dashboard Reports

### Lý do

Admin Dashboard, Financial Reports, Station Performance, Model Performance hiện đã lấy data thật. Nếu có realtime, các card số liệu sẽ tự cập nhật khi phát sinh payment, booking, return, extra charge.

### Khi nào cần bắn event

- Payment `SUCCESS`, `PENDING`, `FAILED`.
- Extra charge tạo mới hoặc được thanh toán.
- Rental status thay đổi.
- Vehicle status thay đổi.

### Event đề xuất

```text
ADMIN_METRICS_CHANGED
PAYMENT_CHANGED
EXTRA_CHARGE_CHANGED
FLEET_STATUS_CHANGED
```

### UI cần cập nhật

- Total Revenue.
- Booking Revenue.
- Extra Charges.
- Pending Payments.
- Station utilization.
- Model performance.
- Recent rentals.

### Ưu tiên

P2. Tốt cho demo, nhưng không quan trọng bằng realtime vận hành staff.

## 5. Wallet Balance Realtime

### Lý do

Customer nạp tiền hoặc thanh toán bằng ví xong thì số dư ví ở Profile/Home/Wallet nên cập nhật ngay.

### Khi nào cần bắn event

- Topup VNPay thành công.
- Booking thanh toán bằng Wallet thành công.
- Extra charge thanh toán bằng Wallet nếu sau này có.

### Event đề xuất

```text
WALLET_BALANCE_CHANGED
WALLET_TRANSACTION_CREATED
```

### UI cần cập nhật

- Wallet card ở Profile.
- Wallet page balance.
- Transaction history.

### Ưu tiên

P2.

## 6. Maintenance Management Realtime

### Lý do

Khi staff trả xe bị hư hỏng, xe chuyển vào maintenance. Trang Maintenance Management nên nhận dòng mới ngay. Khi mark completed, dashboard và home cũng cần cập nhật availability.

### Khi nào cần bắn event

- Incident report được tạo.
- Vehicle maintenance được tạo `PENDING`.
- Maintenance chuyển `PENDING -> COMPLETED`.
- Vehicle chuyển `MAINTENANCE -> AVAILABLE`.

### Event đề xuất

```text
INCIDENT_CREATED
MAINTENANCE_CREATED
MAINTENANCE_COMPLETED
```

### UI cần cập nhật

- Maintenance Management table.
- Incident Management table.
- Staff Dashboard cards.
- Admin performance metrics.

### Ưu tiên

P2.

## 7. Notification Center

### Lý do

Realtime không chỉ là auto-refresh bảng. Nên có notification nhỏ ở góc màn hình để user biết chuyện gì vừa xảy ra.

### Ví dụ thông báo

- Staff: `New booking is waiting for pickup.`
- Staff: `Vehicle returned and moved to maintenance.`
- Customer: `Your VNPay payment was successful.`
- Admin: `New damage fee payment received.`

### UI đề xuất

- Bell icon ở topbar.
- Toast notification ở góc phải.
- Notification dropdown hiển thị 5 event gần nhất.

### Ưu tiên

P3. Làm sau khi realtime data update đã ổn.

## Kiến trúc đề xuất

## Option A: Java WebSocket

Phù hợp nhất với dự án hiện tại.

### Thành phần

```text
WebSocket Endpoint
RealtimeSessionRegistry
RealtimeEvent
RealtimeEventPublisher
Client-side JS listener
```

### Luồng hoạt động

1. User login vào app.
2. JSP mở WebSocket connection.
3. Server lưu session WebSocket theo role hoặc userId.
4. Khi service xử lý xong transaction, gọi `RealtimeEventPublisher.publish(...)`.
5. Publisher gửi JSON event tới đúng nhóm client.
6. Browser nhận event và cập nhật UI.

### Endpoint đề xuất

```text
/ws/realtime
```

### Event JSON mẫu

```json
{
  "type": "RENTAL_BOOKED",
  "target": "STAFF",
  "message": "New booking is waiting for pickup.",
  "data": {
    "rentalId": "RENT001",
    "customerName": "Nguyen Van A",
    "vehicle": "VF5",
    "status": "BOOKED"
  },
  "createdAt": "2026-06-30T10:30:00"
}
```

## Option B: Polling

Dễ implement hơn nhưng kém realtime hơn.

### Cách làm

Frontend gọi API mỗi 5-10 giây:

```text
/api/staff/summary
/api/admin/metrics
/api/wallet/balance
```

### Khi nên dùng

- Nếu WebSocket lỗi với Tomcat config.
- Nếu cần demo nhanh.
- Nếu chưa muốn thay đổi nhiều code.

## Option C: Hybrid

Khuyến nghị thực tế:

- WebSocket cho event quan trọng.
- Polling nhẹ mỗi 30-60 giây để tự đồng bộ lại nếu miss event.

## Event Target Design

Nên chia target theo role:

```text
CUSTOMER:{accountId}
STAFF
ADMIN
ALL
```

### Ví dụ

Booking thành công:

```text
CUSTOMER:{customerId} -> booking success notification
STAFF -> new pickup booking
ADMIN -> metrics changed
```

Return xe hư:

```text
STAFF -> maintenance created
ADMIN -> incident and maintenance metrics changed
CUSTOMER:{customerId} -> return completed / extra charge created
```

## Những service nên publish event

## BookingService

Sau khi booking thành công:

```text
RENTAL_BOOKED
PAYMENT_CHANGED
VEHICLE_AVAILABILITY_CHANGED
ADMIN_METRICS_CHANGED
WALLET_BALANCE_CHANGED nếu trả bằng wallet
```

## VNPayCallbackController hoặc VNPay payment service

Sau callback thành công:

```text
PAYMENT_CHANGED
RENTAL_BOOKED
WALLET_BALANCE_CHANGED nếu topup wallet
```

## PickupService

Sau confirm pickup:

```text
PICKUP_CONFIRMED
RENTAL_RENTED
FLEET_STATUS_CHANGED
```

Sau mark no-show:

```text
RENTAL_NO_SHOW
VEHICLE_AVAILABLE
FLEET_STATUS_CHANGED
```

## ReturnService

Sau confirm return bình thường:

```text
RENTAL_COMPLETED
VEHICLE_AVAILABLE
FLEET_STATUS_CHANGED
```

Sau return damaged:

```text
RENTAL_COMPLETED
INCIDENT_CREATED
MAINTENANCE_CREATED
VEHICLE_MOVED_TO_MAINTENANCE
FLEET_STATUS_CHANGED
```

## MaintenanceService

Sau mark completed:

```text
MAINTENANCE_COMPLETED
VEHICLE_AVAILABLE
VEHICLE_AVAILABILITY_CHANGED
```

## ExtraChargeService

Sau tạo charge hoặc thanh toán charge:

```text
EXTRA_CHARGE_CHANGED
PAYMENT_CHANGED
ADMIN_METRICS_CHANGED
```

## Giai đoạn implement đề xuất

## Phase 1: Realtime nền tảng

1. Tạo WebSocket endpoint `/ws/realtime`.
2. Tạo `RealtimeEvent` DTO.
3. Tạo `RealtimeSessionRegistry`.
4. Tạo `RealtimeEventPublisher`.
5. Tạo file JS dùng chung `realtime.js`.
6. Include JS vào layout staff/admin/customer.

## Phase 2: Staff realtime

1. Publish event trong `BookingService`.
2. Publish event trong `PickupService`.
3. Publish event trong `ReturnService`.
4. Staff Dashboard nhận event và update cards/recent activities.
5. Pickup Management nhận `RENTAL_BOOKED`.
6. Return Management nhận `RENTAL_RENTED` và `RENTAL_COMPLETED`.

## Phase 3: Customer realtime

1. Home nhận `VEHICLE_AVAILABILITY_CHANGED`.
2. Wallet/Profile nhận `WALLET_BALANCE_CHANGED`.
3. Booking detail nhận payment status realtime.

## Phase 4: Admin realtime

1. Admin Dashboard nhận `ADMIN_METRICS_CHANGED`.
2. Financial Reports nhận `PAYMENT_CHANGED`.
3. Station Performance nhận `FLEET_STATUS_CHANGED`.
4. Model Performance nhận `RENTAL_BOOKED`, `RENTAL_COMPLETED`, `INCIDENT_CREATED`.

## Phase 5: Notification Center

1. Tạo notification bell trên topbar.
2. Tạo toast notification component.
3. Lưu notification gần nhất trong browser memory.
4. Nếu muốn nâng cấp tiếp, thêm bảng `Notification`.

## Có cần thêm bảng Notification không?

Không bắt buộc ở phase đầu.

Nếu chỉ cần realtime update UI thì không cần DB.

Nên thêm bảng `Notification` nếu muốn:

- User offline vẫn xem lại thông báo.
- Có trạng thái đã đọc/chưa đọc.
- Có lịch sử notification.

### Bảng gợi ý

```sql
CREATE TABLE Notification (
    notification_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    account_id UNIQUEIDENTIFIER NULL,
    role_target VARCHAR(20) NULL,
    type VARCHAR(50) NOT NULL,
    title NVARCHAR(255) NOT NULL,
    message NVARCHAR(MAX),
    is_read BIT DEFAULT 0,
    created_at DATETIME2 DEFAULT GETDATE()
);
```

## Ưu tiên tổng thể

| Priority | Feature |
|---|---|
| P1 | Staff Pickup realtime |
| P1 | Staff Return realtime |
| P1 | Vehicle availability realtime |
| P2 | Wallet balance realtime |
| P2 | Maintenance and incident realtime |
| P2 | Admin dashboard/report realtime |
| P3 | Notification center |

## Khuyến nghị cho dự án hiện tại

Nên bắt đầu từ Staff Pickup và Return vì đây là phần thể hiện realtime rõ nhất khi demo:

1. Customer booking thành công.
2. Staff dashboard tự hiện booking mới.
3. Staff confirm pickup.
4. Return page tự có đơn đang thuê.
5. Staff confirm return.
6. Home/Admin tự cập nhật trạng thái xe.

Luồng này có giá trị demo cao, dễ giải thích, và bám sát nghiệp vụ thuê xe điện.
