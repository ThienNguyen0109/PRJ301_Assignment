# Extra Charge Implementation Plan

## 1. Mục Tiêu

Refactor phần phí phát sinh trong hệ thống thuê xe điện bằng bảng `Extra_Charge`.

Các khoản phí phát sinh sẽ không nhét thêm vào `Rental`, mà được quản lý riêng:

```text
LATE_FEE
DAMAGE_FEE
CLEANING_FEE
LOST_ACCESSORY
OTHER
```

`Rental` chỉ giữ thông tin đơn thuê chính.

## 2. Quyết Định Về Rental

Bảng `Rental` nên giữ các thông tin chính:

```text
rental_id
customer_id
vehicle_id
pickup_station_id
start_date
end_date
total_days
total_amount
actual_return_date
status
created_at
```

Không thêm các cột sau vào `Rental`:

```text
damage_fee
cleaning_fee
lost_accessory_fee
rescue_fee
```

Project hiện tại đã có `Rental.late_fee`, nên kế hoạch là:

```text
Phase 1:
  Giữ Rental.late_fee để không phá flow hiện tại.
  Đồng thời tạo Extra_Charge type LATE_FEE.

Phase 2:
  Chuyển profile/return/history sang đọc late fee từ Extra_Charge.
  Sau đó có thể không dùng Rental.late_fee nữa.
```

## 3. Database Migration

### 3.1. Tạo Bảng Extra_Charge

```sql
IF OBJECT_ID(N'Extra_Charge', N'U') IS NULL
BEGIN
    CREATE TABLE Extra_Charge (
        charge_id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        rental_id UNIQUEIDENTIFIER NOT NULL,
        incident_id UNIQUEIDENTIFIER NULL,
        charge_type VARCHAR(30) NOT NULL,
        amount DECIMAL(10,2) NOT NULL,
        description NVARCHAR(MAX),
        status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
        created_at DATETIME2 DEFAULT GETDATE(),
        paid_at DATETIME2 NULL,

        CONSTRAINT FK_ExtraCharge_Rental
            FOREIGN KEY (rental_id)
            REFERENCES Rental(rental_id),

        CONSTRAINT FK_ExtraCharge_IncidentReport
            FOREIGN KEY (incident_id)
            REFERENCES Incident_Report(incident_id),

        CONSTRAINT CK_ExtraCharge_Type
            CHECK (charge_type IN (
                'LATE_FEE',
                'DAMAGE_FEE',
                'CLEANING_FEE',
                'LOST_ACCESSORY',
                'OTHER'
            )),

        CONSTRAINT CK_ExtraCharge_Status
            CHECK (status IN (
                'UNPAID',
                'PENDING',
                'PAID',
                'CANCELLED'
            )),

        CONSTRAINT CK_ExtraCharge_Amount
            CHECK (amount >= 0)
    );
END;
GO
```

### 3.2. Thêm Payment.charge_id

```sql
IF COL_LENGTH('dbo.Payment', 'charge_id') IS NULL
BEGIN
    ALTER TABLE dbo.Payment ADD charge_id UNIQUEIDENTIFIER NULL;
END;
GO

IF NOT EXISTS (
    SELECT 1
    FROM sys.foreign_keys
    WHERE name = N'FK_Payment_ExtraCharge'
)
BEGIN
    ALTER TABLE dbo.Payment ADD CONSTRAINT FK_Payment_ExtraCharge
        FOREIGN KEY (charge_id)
        REFERENCES Extra_Charge(charge_id);
END;
GO
```

### 3.3. Mở Rộng Payment.payment_type

`Payment.payment_type` nên hỗ trợ:

```text
BOOKING
LATE_FEE
DAMAGE_FEE
CLEANING_FEE
LOST_ACCESSORY
OTHER
```

## 4. Entity Và Enum Cần Thêm

### 4.1. Entity

```text
models.ExtraCharge
```

Các field chính:

```text
chargeId
rentalId
incidentId
chargeType
amount
description
status
createdAt
paidAt
```

### 4.2. Enum

```text
enums.ExtraChargeType
```

Giá trị:

```text
LATE_FEE
DAMAGE_FEE
CLEANING_FEE
LOST_ACCESSORY
OTHER
```

```text
enums.ExtraChargeStatus
```

Giá trị:

```text
UNPAID
PENDING
PAID
CANCELLED
```

### 4.3. Cập Nhật Payment Entity

Thêm:

```java
@Column(name = "charge_id", columnDefinition = "uniqueidentifier")
private String chargeId;
```

Optional mapping:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "charge_id", referencedColumnName = "charge_id", insertable = false, updatable = false)
private ExtraCharge extraCharge;
```

## 5. DAO Và Service Cần Thêm

### 5.1. DAO

```text
IExtraChargeDAO
ExtraChargeDAO
```

Các method đề xuất:

```java
ExtraCharge createCharge(EntityManager em, ExtraCharge charge);

ExtraCharge findForUpdate(EntityManager em, String chargeId);

List<ExtraCharge> findByRentalId(String rentalId);

List<ExtraCharge> findByCustomerId(String customerId);

BigDecimal sumChargesByRentalId(String rentalId);
```

### 5.2. Service

```text
ExtraChargeService
```

Service chịu trách nhiệm:

```text
Tạo phụ phí
Cập nhật trạng thái phụ phí
Gắn phụ phí với payment
Tính tổng phụ phí của rental
```

## 6. Flow Mới: Trả Xe Trễ

### 6.1. Không Trễ

```text
lateDays = 0
lateFee = 0
Không tạo Extra_Charge
```

### 6.2. Trễ Và Thanh Toán CASH

Transaction:

```text
1. Lock Rental.
2. Lock Vehicle.
3. Tính lateFee.
4. Update Rental.actual_return_date.
5. Set Rental.late_fee = lateFee trong Phase 1.
6. Create Extra_Charge:
   - charge_type = LATE_FEE
   - amount = lateFee
   - status = PAID
7. Create Payment:
   - rental_id = rentalId
   - charge_id = chargeId
   - payment_type = LATE_FEE
   - payment_method = CASH
   - status = SUCCESS
8. Update Rental.status = COMPLETED.
9. Update Vehicle.status = AVAILABLE hoặc MAINTENANCE tùy condition.
10. Insert Rental_Status_History COMPLETED.
11. Commit.
```

### 6.3. Trễ Và Thanh Toán VNPAY

Transaction trước khi redirect:

```text
1. Lock Rental.
2. Lock Vehicle.
3. Tính lateFee.
4. Update Rental.actual_return_date.
5. Set Rental.late_fee = lateFee trong Phase 1.
6. Create Extra_Charge:
   - charge_type = LATE_FEE
   - amount = lateFee
   - status = PENDING
7. Create Payment:
   - rental_id = rentalId
   - charge_id = chargeId
   - payment_type = LATE_FEE
   - payment_method = VNPAY
   - status = PENDING
   - transaction_code = orderId
8. Update Rental.status = COMPLETED.
9. Update Vehicle.status.
10. Insert Rental_Status_History COMPLETED.
11. Commit.
12. Redirect VNPay.
```

VNPay callback success:

```text
1. Find pending Payment by transaction_code.
2. Lock Payment.
3. Lock Extra_Charge by payment.charge_id.
4. Payment.status = SUCCESS.
5. Payment.transaction_code = vnp_TransactionNo.
6. Payment.payment_date = now.
7. Extra_Charge.status = PAID.
8. Extra_Charge.paid_at = now.
9. Commit.
```

VNPay callback fail:

```text
1. Payment.status = FAILED.
2. Extra_Charge.status = UNPAID hoặc CANCELLED.
3. Commit.
```

## 7. Flow Mới: Xe Hư Hỏng

### 7.1. UI Return Detail

Khi staff chọn:

```text
Condition = DAMAGED
```

Hiển thị thêm:

```text
Damage Description
Severity
Damage Fee
Damage Fee Payment Method: CASH / VNPAY
```

### 7.2. Damage Fee = 0

Transaction:

```text
1. Lock Rental.
2. Lock Vehicle.
3. Validate Rental.status = RENTED.
4. Validate Vehicle.status = RENTED.
5. Create Incident_Report.
6. Create Vehicle_Maintenance PENDING.
7. Update Rental.status = COMPLETED.
8. Update Vehicle.status = MAINTENANCE.
9. Insert Rental_Status_History COMPLETED.
10. Commit.
```

Không tạo `Extra_Charge` và `Payment`.

### 7.3. Damage Fee > 0 Và Thanh Toán CASH

Transaction:

```text
1. Lock Rental.
2. Lock Vehicle.
3. Create Incident_Report.
4. Create Vehicle_Maintenance PENDING.
5. Create Extra_Charge:
   - charge_type = DAMAGE_FEE
   - incident_id = incidentId
   - amount = damageFee
   - status = PAID
6. Create Payment:
   - rental_id = rentalId
   - charge_id = chargeId
   - payment_type = DAMAGE_FEE
   - payment_method = CASH
   - status = SUCCESS
7. Update Rental.status = COMPLETED.
8. Update Vehicle.status = MAINTENANCE.
9. Insert Rental_Status_History COMPLETED.
10. Commit.
```

### 7.4. Damage Fee > 0 Và Thanh Toán VNPAY

Transaction trước khi redirect:

```text
1. Lock Rental.
2. Lock Vehicle.
3. Create Incident_Report.
4. Create Vehicle_Maintenance PENDING.
5. Create Extra_Charge:
   - charge_type = DAMAGE_FEE
   - incident_id = incidentId
   - amount = damageFee
   - status = PENDING
6. Create Payment:
   - rental_id = rentalId
   - charge_id = chargeId
   - payment_type = DAMAGE_FEE
   - payment_method = VNPAY
   - status = PENDING
   - transaction_code = orderId
7. Update Rental.status = COMPLETED.
8. Update Vehicle.status = MAINTENANCE.
9. Insert Rental_Status_History COMPLETED.
10. Commit.
11. Redirect VNPay.
```

Callback xử lý giống charge payment chung.

## 8. Chuẩn Hóa VNPay Callback

Nên chuẩn hóa order id:

```text
BOOK_<timestamp>
CHARGE_<chargeId>_<timestamp>
TOPUP_<timestamp>
```

Callback xử lý:

```text
Nếu orderId bắt đầu bằng BOOK:
  xử lý booking payment.

Nếu orderId bắt đầu bằng CHARGE:
  xử lý Extra_Charge payment.

Nếu orderId là topup:
  xử lý nạp ví.
```

Charge callback:

```text
1. Find Payment by transaction_code.
2. Lock Payment.
3. Lock Extra_Charge.
4. Nếu ResponseCode = 00:
   - Payment.status = SUCCESS
   - Extra_Charge.status = PAID
   - Extra_Charge.paid_at = now
5. Nếu thất bại:
   - Payment.status = FAILED
   - Extra_Charge.status = UNPAID hoặc CANCELLED
```

## 9. Cập Nhật Customer Profile

Profile hiện tại đã có lịch sử đơn đặt xe.

Sau khi thêm `Extra_Charge`, mỗi rental nên hiển thị:

```text
Tiền thuê chính
Phụ phí trễ
Phụ phí hư hỏng
Tổng phụ phí
Tổng phải trả
Trạng thái thanh toán phụ phí
```

Ví dụ:

```text
Rental R001
Tiền thuê: 700,000 VND
Phụ phí:
  LATE_FEE: 1,400,000 VND - PAID
  DAMAGE_FEE: 500,000 VND - PENDING
Tổng cộng: 2,600,000 VND
```

DAO profile cần query thêm `Extra_Charge` theo `rental_id`.

## 10. Cập Nhật Staff Incident Management

Incident detail nên hiển thị:

```text
Damage Fee
Extra Charge Status
Payment Status
Payment Method
```

Nếu damage fee đang `PENDING`, staff có thể thấy khách chưa thanh toán xong.

## 11. Thứ Tự Implement Đề Xuất

```text
1. Cập nhật SQL script:
   - Extra_Charge
   - Payment.charge_id
   - Payment.payment_type thêm DAMAGE_FEE/CLEANING_FEE/...

2. Thêm JPA:
   - ExtraCharge entity
   - ExtraChargeType enum
   - ExtraChargeStatus enum
   - Payment.chargeId

3. Thêm DAO/Service:
   - ExtraChargeDAO
   - ExtraChargeService

4. Refactor late fee hiện tại:
   - tạo Extra_Charge LATE_FEE
   - gắn Payment.charge_id
   - callback update Extra_Charge

5. Thêm damage fee vào Return Detail UI.

6. Sửa ReturnService:
   - tạo Extra_Charge DAMAGE_FEE
   - tạo Payment DAMAGE_FEE nếu cần

7. Sửa VNPay callback:
   - xử lý chung cho CHARGE payment

8. Sửa Profile:
   - hiển thị danh sách phụ phí theo rental
   - tổng phụ phí
   - trạng thái thanh toán phụ phí

9. Sửa Incident Detail:
   - hiển thị damage fee/payment status

10. Test đầy đủ các case.
```

## 12. Test Cases

```text
1. Trả đúng hạn, xe bình thường.
2. Trả trễ, thanh toán CASH.
3. Trả trễ, thanh toán VNPAY success.
4. Trả trễ, thanh toán VNPAY fail.
5. Xe hư, damage fee = 0.
6. Xe hư, damage fee > 0, thanh toán CASH.
7. Xe hư, damage fee > 0, thanh toán VNPAY success.
8. Xe hư, damage fee > 0, thanh toán VNPAY fail.
9. Rental có cả LATE_FEE và DAMAGE_FEE.
10. Transaction rollback nếu tạo incident thành công nhưng tạo payment thất bại.
```

## 13. Transaction Boundary

Các thao tác sau nên nằm trong cùng một transaction:

```text
Rental update
Vehicle update
Incident_Report insert
Vehicle_Maintenance insert
Extra_Charge insert
Payment insert
Rental_Status_History insert
```

Mục tiêu là tránh dữ liệu dở dang:

```text
Rental đã COMPLETED nhưng chưa có charge
Vehicle đã MAINTENANCE nhưng chưa có incident
Payment đã PENDING nhưng charge chưa tồn tại
```
