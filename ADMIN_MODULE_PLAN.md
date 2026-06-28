# Admin Module Plan

## 1. Muc Tieu

Trang Admin dung de quan ly toan bo he thong E-Vehicle Rental:

- Quan ly tai chinh: doanh thu, thanh toan, phu phi, vi nguoi dung.
- Theo doi hieu suat station va vehicle model.
- CRUD du lieu nen tang cua he thong.
- Giam thao tac truc tiep tren database khi demo/van hanh.

File nay tap trung vao viec chia task CRUD truoc cho team, sau do moi mo rong dashboard/report.

## 2. Role Va Dieu Huong

Admin sau khi dang nhap se vao trang Admin Dashboard rieng.

Customer khong duoc truy cap admin bang cach sua URL.

Staff khong duoc truy cap admin bang cach sua URL.

Can co filter/guard:

```text
Session user.role == ADMIN
```

Neu khong hop le:

```text
Redirect login
hoac response 403
```

## 3. Dashboard Admin Can Co

### 3.1. Financial Overview

Thong tin can hien thi:

- Total Revenue
- Booking Revenue
- Late Fee Revenue
- Damage Fee Revenue
- Wallet Topup Total
- Pending Payments
- Failed Payments
- Refunded/Cancelled Rentals neu sau nay co ho tro

Nguon du lieu:

```text
Payment
Extra_Charge
Rental
Wallet_Transaction
```

Filter:

- Today
- This Week
- This Month
- Custom Date Range
- Payment Method: WALLET, VNPAY, CASH
- Payment Type: BOOKING, LATE_FEE, DAMAGE_FEE, OTHER

### 3.2. Station Performance

Thong tin can hien thi theo tung station:

- So luong xe dang co
- So xe AVAILABLE
- So xe RENTED
- So xe MAINTENANCE
- So booking phat sinh
- Doanh thu booking
- Doanh thu phu phi
- Ty le su dung xe

Nguon du lieu:

```text
Station
Vehicle
Rental
Payment
Extra_Charge
```

### 3.3. Vehicle Model Performance

Thong tin can hien thi theo tung model:

- So xe cua model
- So booking cua model
- Doanh thu cua model
- So lan bi incident
- So lan vao maintenance
- Average battery when returned neu can
- Top model duoc thue nhieu nhat

Nguon du lieu:

```text
Vehicle_Model
Vehicle
Rental
Payment
Incident_Report
Vehicle_Maintenance
```

## 4. CRUD Priority Cho Team

Nen lam CRUD theo thu tu duoi day. Cac bang transaction nhu Rental, Payment nen uu tien view/detail truoc, khong nen cho Admin update lung tung de tranh sai nghiep vu.

## 5. Master Data CRUD - Uu Tien Cao

Day la nhom nen giao teammate lam truoc vi it phu thuoc transaction.

### 5.1. Station CRUD

Model:

```text
Station
```

Chuc nang:

- List station
- Search theo name/address
- Create station
- Update station
- Delete station neu chua co vehicle/rental lien quan
- Neu da co lien quan thi nen dung soft-disable sau nay, hien tai co the chan delete

Field can quan ly:

```text
station_id
name
address
```

Validation:

- Name bat buoc
- Address bat buoc
- Khong trung ten station neu co the

Do uu tien:

```text
P1
```

### 5.2. Category CRUD

Model:

```text
Category
```

Chuc nang:

- List category
- Search category
- Create category
- Update category
- Delete category neu chua co Vehicle_Model lien quan

Field can quan ly:

```text
category_id
name
```

Validation:

- Name bat buoc
- Khong trung name

Do uu tien:

```text
P1
```

### 5.3. Vehicle Model CRUD

Model:

```text
Vehicle_Model
```

Chuc nang:

- List vehicle model
- Search theo model name/category
- Create vehicle model
- Update vehicle model
- Delete neu chua co Vehicle lien quan
- Upload/gan thumbnail image
- Quan ly description, seat count, price per day

Field can quan ly:

```text
model_id
category_id
name
description
seat_count
price_per_day
```

Validation:

- Category bat buoc
- Name bat buoc
- Seat count > 0
- Price per day > 0
- Khong trung model name trong cung category neu co the

Do uu tien:

```text
P1
```

### 5.4. Vehicle Model Image CRUD

Model:

```text
Vehicle_Model_Image
```

Chuc nang:

- List images theo model
- Add image
- Update image URL/path
- Delete image
- Set thumbnail/main image neu sau nay them cot is_primary

Field can quan ly:

```text
image_id
model_id
image_url
```

Validation:

- Model bat buoc
- Image URL/path bat buoc
- File/URL phai hop le

Do uu tien:

```text
P2
```

### 5.5. Vehicle CRUD

Model:

```text
Vehicle
```

Chuc nang:

- List vehicle
- Search theo license plate/model/station/status
- Create vehicle
- Update vehicle
- Update station cua vehicle
- Update battery level
- Update status: AVAILABLE, RENTED, MAINTENANCE
- Delete vehicle neu chua co Rental lien quan

Field can quan ly:

```text
vehicle_id
model_id
station_id
license_plate
battery_level
color
status
```

Validation:

- Model bat buoc
- Station bat buoc
- License plate bat buoc va unique
- Battery level 0 - 100
- Status chi nam trong AVAILABLE, RENTED, MAINTENANCE
- Khong cho set AVAILABLE neu vehicle dang co Rental status RENTED

Do uu tien:

```text
P1
```

## 6. Account Va User Management

### 6.1. Account CRUD

Model:

```text
Account
```

Chuc nang:

- List account
- Search theo name/email/phone/role/status
- Create account Admin/Staff/Customer
- Update profile account
- Update role
- Lock/Unlock account
- Reset password tam thoi neu can
- Khong nen hard delete account da co rental/payment

Field can quan ly:

```text
account_id
full_name
email
phone
password
role
status
created_at
```

Validation:

- Email bat buoc va unique
- Role chi nam trong ADMIN, STAFF, CUSTOMER
- Status hop le
- Password hash/ma hoa theo logic hien tai cua project
- Admin khong tu khoa tai khoan cua chinh minh

Do uu tien:

```text
P1
```

### 6.2. Wallet Management

Model:

```text
Wallet
Wallet_Transaction
```

Chuc nang nen lam:

- View wallet theo customer
- View wallet transaction
- Search transaction theo customer/date/type
- Admin manual adjustment neu can cho demo

Khuyen nghi:

- Khong lam CRUD day du voi Wallet_Transaction.
- Wallet_Transaction nen la audit log, chi tao qua nghiep vu.
- Neu can dieu chinh so du, tao transaction type ADJUSTMENT sau nay.

Do uu tien:

```text
P3
```

## 7. Discount CRUD

### 7.1. Discount CRUD

Model:

```text
Discount
```

Chuc nang:

- List discount
- Search theo code/status/date
- Create discount
- Update discount
- Disable discount
- Delete discount neu chua duoc dung

Field can quan ly:

```text
discount_id
code
discount_type
discount_value
quantity
start_date
end_date
status
```

Validation:

- Code bat buoc va unique
- Discount value > 0
- Quantity >= 0
- Start date <= End date
- Khong sua code neu discount da duoc dung trong Rental_Discount

Do uu tien:

```text
P2
```

### 7.2. Rental Discount

Model:

```text
Rental_Discount
```

Chuc nang nen lam:

- View discount da ap dung cho rental
- Search theo rental/discount/customer

Khuyen nghi:

- Khong can CRUD day du.
- Chi tao qua booking flow.

Do uu tien:

```text
P4
```

## 8. Transaction Data - View/Detail Truoc, CRUD Sau

### 8.1. Rental Management

Model:

```text
Rental
Rental_Status_History
```

Chuc nang nen lam truoc:

- List rentals
- Search theo rental id/customer/email/phone/license plate/status/date
- View rental detail
- View status history
- Filter status: BOOKED, RENTED, COMPLETED, CANCELLED, NO_SHOW
- Export report neu can

Chuc nang can can nhac:

- Cancel rental boi Admin
- Force update status chi khi demo can

Khuyen nghi:

- Khong cho Admin sua truc tiep amount/date/status tuy tien.
- Moi thay doi status nen di qua service va insert `Rental_Status_History`.

Do uu tien:

```text
P2
```

### 8.2. Payment Management

Model:

```text
Payment
```

Chuc nang nen lam:

- List payments
- Search theo rental/payment method/status/type/date
- View payment detail
- Filter PENDING/SUCCESS/FAILED
- Filter payment method: WALLET, VNPAY, CASH
- Filter payment type: BOOKING, LATE_FEE, DAMAGE_FEE, OTHER

Khuyen nghi:

- Khong cho update amount truc tiep.
- Neu can fix payment, tao action rieng: mark failed, retry, confirm cash.

Do uu tien:

```text
P2
```

### 8.3. Extra Charge Management

Model:

```text
Extra_Charge
```

Chuc nang:

- List extra charges
- Search theo rental/customer/type/status/date
- View charge detail
- Filter type: LATE_FEE, DAMAGE_FEE, CLEANING_FEE, LOST_ACCESSORY, OTHER
- Filter status: UNPAID, PENDING, PAID, CANCELLED
- Tao manual charge neu can: CLEANING_FEE, LOST_ACCESSORY, OTHER
- Mark paid neu thu CASH
- Cancel charge neu tao sai

Validation:

- Rental bat buoc
- Amount > 0
- Khong sua amount neu charge da PAID
- Neu charge lien quan incident thi gan incident_id

Do uu tien:

```text
P2
```

## 9. Operation Management

### 9.1. Incident Report Management

Model:

```text
Incident_Report
```

Chuc nang:

- List incident
- Search theo rental/vehicle/severity/date
- View incident detail
- Filter severity: LOW, MEDIUM, HIGH
- Update description neu can
- Link sang Rental detail
- Link sang Vehicle detail
- Link sang Extra Charge neu co damage fee

Khuyen nghi:

- Incident nen tao tu return flow.
- Admin chi nen view/update note, khong nen delete.

Do uu tien:

```text
P3
```

### 9.2. Vehicle Maintenance Management

Model:

```text
Vehicle_Maintenance
```

Chuc nang:

- List maintenance
- Search theo vehicle/status/date
- View maintenance detail
- Update description
- Mark completed
- Khi completed thi update Vehicle.status = AVAILABLE

Validation:

- Chi mark completed neu status = PENDING
- Vehicle phai dang MAINTENANCE truoc khi completed

Do uu tien:

```text
P2
```

### 9.3. Review Management

Model:

```text
Review
```

Chuc nang:

- List reviews
- Search theo customer/vehicle/rating/date
- View review detail
- Hide/Delete review neu noi dung khong phu hop

Khuyen nghi:

- Neu DB chua co status cho review, co the de view-only truoc.
- Sau nay them `status` de moderation tot hon.

Do uu tien:

```text
P4
```

## 10. De Xuat Chia Task Cho Team

### Teammate 1 - Master Data

Lam:

- Station CRUD
- Category CRUD
- Vehicle Model CRUD
- Vehicle Model Image CRUD

### Teammate 2 - Vehicle Inventory

Lam:

- Vehicle CRUD
- Vehicle filter/search
- Vehicle detail
- Vehicle status validation

### Teammate 3 - Account Va Discount

Lam:

- Account management
- Staff/Admin/Customer filter
- Discount CRUD
- Rental_Discount view

### Teammate 4 - Transaction Viewer

Lam:

- Rental list/detail
- Rental_Status_History view
- Payment list/detail
- Extra_Charge list/detail

### Teammate 5 - Operation/Admin Report

Lam:

- Maintenance management
- Incident management
- Admin dashboard summary cards
- Station performance report
- Vehicle model performance report

## 11. Admin Sidebar De Xuat

```text
Dashboard
Financial Reports
Station Performance
Model Performance

Accounts
Stations
Categories
Vehicle Models
Vehicles
Discounts

Rentals
Payments
Extra Charges
Incidents
Maintenance
Reviews

Profile
Logout
```

## 12. Thu Tu Implement De It Loi

Lam theo thu tu:

```text
1. Tao Admin layout:
   - sidebar
   - topbar
   - content area
   - common table component style

2. Tao AdminController/MainController route:
   - action=admin-dashboard
   - action=admin-stations
   - action=admin-categories
   - ...

3. Lam CRUD master data:
   - Station
   - Category
   - Vehicle Model
   - Vehicle

4. Lam Account/Discount.

5. Lam Rental/Payment/ExtraCharge view-only.

6. Lam Maintenance/Incident management.

7. Lam dashboard/report sau khi da co DAO on dinh.
```

## 13. Ghi Chu Ky Thuat

Nen giu kien truc:

```text
JSP
Servlet/Controller
Service
DAO
JPA Entity
DTO cho report/list phuc tap
```

CRUD don gian co the dung:

```text
AdminStationController
AdminCategoryController
AdminVehicleModelController
AdminVehicleController
AdminAccountController
AdminDiscountController
```

Report nen dung DTO:

```text
AdminFinancialSummaryDTO
StationPerformanceDTO
ModelPerformanceDTO
PaymentReportDTO
```

Khong nen dua logic tinh doanh thu vao JSP.

Tat ca update/delete quan trong nen di qua Service de validate nghiep vu.

