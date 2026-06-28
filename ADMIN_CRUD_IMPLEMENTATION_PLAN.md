# Admin CRUD Implementation Plan

## 1. Muc Tieu

File nay dung de chia viec va implement cac module CRUD cho trang Admin.

Nguyen tac chung:

- Admin moi duoc truy cap.
- Customer va Staff khong duoc truy cap bang URL.
- JSP chi hien thi UI, khong chua business logic.
- Controller nhan request va dieu huong.
- Service validate nghiep vu.
- DAO thao tac database qua JPA.
- Cac bang transaction quan trong nen uu tien View/Detail truoc, han che Update/Delete truc tiep.

## 2. Base Flow Cho Moi CRUD Module

Moi module CRUD nen theo flow chuan:

```text
Admin Sidebar
  -> List Page
      -> Search/Filter
      -> Pagination
      -> Create Button
      -> Row Actions: View / Edit / Delete or Disable

Create
  -> GET form
  -> POST validate
  -> Service create
  -> Redirect list + success message

Edit
  -> GET form with current data
  -> POST validate
  -> Service update
  -> Redirect detail/list + success message

Delete/Disable
  -> POST only
  -> Service check relationship
  -> Hard delete only if safe
  -> Otherwise disable/block action
```

Nen dung POST cho create/update/delete. GET chi dung de hien thi page.

## 3. Folder/File Pattern De Implement

Vi du voi Station:

```text
src/java/controllers/admin/AdminStationController.java
src/java/services/AdminStationService.java
src/java/daos/AdminStationDAO.java
src/java/daos/IAdminStationDAO.java
src/java/dto/AdminStationDTO.java neu can
web/WEB-INF/views/admin/stations/list.jsp
web/WEB-INF/views/admin/stations/form.jsp
web/WEB-INF/views/admin/stations/detail.jsp
```

Neu muon don gian hon cho PRJ demo, co the de controller trong package hien tai:

```text
src/java/controllers/AdminStationController.java
```

Nhung nen thong nhat mot cach cho ca team.

## 4. Route Pattern De Xuat

Dung action qua `MainController`:

```text
?action=admin-stations
?action=admin-station-create
?action=admin-station-edit&id=...
?action=admin-station-detail&id=...
```

POST action:

```text
/admin/stations/create
/admin/stations/update
/admin/stations/delete
```

Hoac dung servlet path truc tiep:

```text
/admin/stations
/admin/stations/form
/admin/stations/save
/admin/stations/delete
```

Khuyen nghi:

- GET van qua `MainController` de dong bo dieu huong.
- POST co the vao servlet action rieng.

## 5. Priority Tong Quan

```text
P1 - Can lam truoc
  Account
  Station
  Category
  Vehicle_Model
  Vehicle

P2 - Lam sau P1
  Vehicle_Model_Image
  Discount
  Rental view/detail
  Payment view/detail
  Extra_Charge view/detail
  Vehicle_Maintenance

P3 - Operation/report
  Incident_Report
  Wallet/Wallet_Transaction view
  Rental_Status_History view

P4 - Co thoi gian thi lam
  Rental_Discount view
  Review moderation
```

## 6. Account CRUD

Model:

```text
Account
```

Muc dich:

Admin quan ly tai khoan CUSTOMER, STAFF, ADMIN.

### 6.1. List Flow

Page:

```text
?action=admin-accounts
```

Hien thi columns:

```text
Full Name
Email
Phone
Role
Status
Created At
Actions
```

Filter:

```text
Keyword: fullName/email/phone
Role: ALL/CUSTOMER/STAFF/ADMIN
Status: ALL/ACTIVE/INACTIVE/LOCKED neu co
```

Actions:

```text
View
Edit
Lock/Unlock
Reset Password
```

### 6.2. Create Flow

Fields:

```text
full_name
email
phone
password
confirm_password
role
status
```

Validation:

- Full name bat buoc.
- Email bat buoc, dung format, unique.
- Password bat buoc khi create.
- Confirm password phai khop.
- Role chi nhan `CUSTOMER`, `STAFF`, `ADMIN`.
- Status default `ACTIVE`.

Service logic:

```text
Check email exists
Hash password neu project co hash
Create Account
Neu role CUSTOMER thi tao Wallet mac dinh
```

### 6.3. Update Flow

Cho phep sua:

```text
full_name
phone
role
status
```

Khong nen sua email neu account da co giao dich.

Validation:

- Admin khong duoc khoa chinh minh.
- Admin khong duoc ha role chinh minh neu la admin duy nhat.
- Neu doi role, phai validate role hop le.

### 6.4. Delete/Disable Flow

Khuyen nghi:

```text
Khong hard delete Account.
Chi update status = INACTIVE/LOCKED.
```

Ly do:

- Account co lien quan Rental, Payment, Wallet, Review.

## 7. Station CRUD

Model:

```text
Station
```

### 7.1. List Flow

Columns:

```text
Station Name
Address
Total Vehicles
Available Vehicles
Rented Vehicles
Maintenance Vehicles
Actions
```

Filter:

```text
Keyword: name/address
```

### 7.2. Create/Update Fields

```text
name
address
```

Validation:

- Name bat buoc.
- Address bat buoc.
- Name nen unique.

### 7.3. Delete Flow

Chi cho delete neu:

```text
Khong co Vehicle gan voi station
Khong co Rental pickup_station_id tro toi station
```

Neu co lien quan:

```text
Chan delete va hien message:
Cannot delete station because vehicles or rentals are linked.
```

## 8. Category CRUD

Model:

```text
Category
```

### 8.1. List Flow

Columns:

```text
Category Name
Total Models
Total Vehicles
Actions
```

Filter:

```text
Keyword: name
```

### 8.2. Create/Update Fields

```text
name
```

Validation:

- Name bat buoc.
- Name unique.

### 8.3. Delete Flow

Chi delete neu:

```text
Khong co Vehicle_Model thuoc category
```

Neu co model:

```text
Chan delete.
```

## 9. Vehicle Model CRUD

Model:

```text
Vehicle_Model
```

### 9.1. List Flow

Columns:

```text
Model Name
Category
Seat Count
Price Per Day
Total Vehicles
Actions
```

Filter:

```text
Keyword: model name
Category
Price range neu can
```

### 9.2. Create/Update Fields

```text
category_id
name
description
seat_count
price_per_day
```

Validation:

- Category bat buoc.
- Name bat buoc.
- Description nen co.
- Seat count > 0.
- Price per day > 0.
- Name unique trong cung category neu co the.

### 9.3. Detail Flow

Hien thi:

```text
Model information
Images cua model
Vehicles thuoc model
Booking count
Revenue placeholder
```

### 9.4. Delete Flow

Chi delete neu:

```text
Khong co Vehicle thuoc model
Khong co Vehicle_Model_Image lien quan hoac delete images truoc
```

Neu da co vehicle:

```text
Chan delete.
```

## 10. Vehicle Model Image CRUD

Model:

```text
Vehicle_Model_Image
```

### 10.1. List Flow

Columns:

```text
Model
Image Preview
Image URL/Path
Actions
```

Filter:

```text
Model
Keyword path
```

### 10.2. Create/Update Fields

```text
model_id
image_url
```

Validation:

- Model bat buoc.
- Image URL/path bat buoc.
- Nen preview anh truoc khi save.

### 10.3. Delete Flow

Co the hard delete image record.

Luu y:

- Neu file anh local khong con dung, co the xoa file sau.
- Demo co the chi xoa DB record truoc.

## 11. Vehicle CRUD

Model:

```text
Vehicle
```

### 11.1. List Flow

Columns:

```text
License Plate
Model
Category
Station
Battery Level
Color
Status
Actions
```

Filter:

```text
Keyword: license plate/model
Station
Category
Status: AVAILABLE/RENTED/MAINTENANCE
Battery range neu can
```

### 11.2. Create Fields

```text
model_id
station_id
license_plate
battery_level
color
status
```

Default:

```text
status = AVAILABLE
battery_level = 100
```

Validation:

- Model bat buoc.
- Station bat buoc.
- License plate bat buoc va unique.
- Battery level 0 - 100.
- Status hop le.

### 11.3. Update Flow

Cho phep sua:

```text
station_id
battery_level
color
status
```

Can than voi status:

```text
Khong cho set AVAILABLE neu vehicle dang co Rental status RENTED.
Khong cho set RENTED bang tay neu khong co Rental dang RENTED.
MAINTENANCE co the set bang tay neu xe can bao tri.
```

### 11.4. Delete Flow

Chi delete neu:

```text
Khong co Rental lien quan.
Khong co Incident_Report lien quan.
Khong co Vehicle_Maintenance lien quan.
```

Neu da co lich su:

```text
Khuyen nghi khong hard delete.
Co the them status retired sau nay neu can.
```

## 12. Discount CRUD

Model:

```text
Discount
```

### 12.1. List Flow

Columns:

```text
Code
Type
Value
Quantity
Start Date
End Date
Status
Actions
```

Filter:

```text
Keyword code
Status
Date range
```

### 12.2. Create/Update Fields

```text
code
discount_type
discount_value
quantity
start_date
end_date
status
```

Validation:

- Code bat buoc va unique.
- Type hop le theo DB hien tai.
- Discount value > 0.
- Quantity >= 0.
- Start date <= End date.
- Status hop le.

### 12.3. Delete/Disable Flow

Neu discount chua duoc dung:

```text
Co the delete.
```

Neu da co trong Rental_Discount:

```text
Khong delete.
Chi disable/status inactive.
```

## 13. Rental Management

Model:

```text
Rental
Rental_Status_History
```

Day la transaction data, khong nen CRUD day du ngay.

### 13.1. List Flow

Columns:

```text
Rental ID
Customer
Vehicle
Station
Start Date
End Date
Total Amount
Status
Actions
```

Filter:

```text
Keyword: rentalId/customer/email/phone/licensePlate
Status
Station
Date range
```

Actions:

```text
View Detail
View Status History
Cancel Rental neu status BOOKED va chua pickup
```

### 13.2. Detail Flow

Hien thi:

```text
Customer info
Vehicle info
Station info
Rental period
Payment info
Discount info
Extra charges
Status history
```

### 13.3. Update Flow

Khong nen cho edit truc tiep.

Neu can action:

```text
Cancel Rental:
  Validate status = BOOKED
  Update Rental.status = CANCELLED
  Update Vehicle.status = AVAILABLE neu can
  Insert Rental_Status_History CANCELLED
```

## 14. Payment Management

Model:

```text
Payment
```

Khong nen CRUD day du. Nen view/detail va action han che.

### 14.1. List Flow

Columns:

```text
Payment ID
Rental ID
Payment Method
Payment Type
Amount
Status
Transaction Code
Payment Date
Actions
```

Filter:

```text
Status: PENDING/SUCCESS/FAILED
Method: WALLET/VNPAY/CASH
Type: BOOKING/LATE_FEE/DAMAGE_FEE/OTHER
Date range
```

### 14.2. Detail Flow

Hien thi:

```text
Payment info
Rental info
Extra charge info neu co charge_id
Transaction code
```

### 14.3. Actions

Cho phep:

```text
View
Mark Failed cho payment PENDING neu can
Confirm Cash cho CASH/PENDING neu co nghiep vu
```

Khong cho:

```text
Sua amount truc tiep
Xoa payment
```

## 15. Extra Charge Management

Model:

```text
Extra_Charge
```

### 15.1. List Flow

Columns:

```text
Charge ID
Rental ID
Charge Type
Amount
Status
Created At
Paid At
Actions
```

Filter:

```text
Keyword: rentalId/customer
Charge Type
Status
Date range
```

### 15.2. Create Manual Charge Flow

Dung cho:

```text
CLEANING_FEE
LOST_ACCESSORY
OTHER
```

Fields:

```text
rental_id
incident_id optional
charge_type
amount
description
status
```

Validation:

- Rental bat buoc.
- Charge type hop le.
- Amount > 0.
- Khong tao charge cho Rental CANCELLED/NO_SHOW neu khong co ly do.

### 15.3. Update Flow

Cho phep sua khi:

```text
status = UNPAID
```

Khong cho sua amount khi:

```text
status = PAID
```

Actions:

```text
Mark Paid
Cancel Charge
View Payment
```

## 16. Incident Report Management

Model:

```text
Incident_Report
```

### 16.1. List Flow

Columns:

```text
Incident ID
Rental ID
Vehicle
Severity
Created At
Actions
```

Filter:

```text
Severity: LOW/MEDIUM/HIGH
Vehicle
Date range
```

### 16.2. Detail Flow

Hien thi:

```text
Rental info
Customer info
Vehicle info
Description
Severity
Extra charges related to incident
Maintenance record related to vehicle
```

### 16.3. Update Flow

Cho phep:

```text
Update description/note
Update severity neu staff/admin danh gia lai
```

Khong nen delete incident.

## 17. Vehicle Maintenance Management

Model:

```text
Vehicle_Maintenance
```

### 17.1. List Flow

Columns:

```text
Maintenance ID
Vehicle
Description
Maintenance Date
Status
Actions
```

Filter:

```text
Status: PENDING/COMPLETED
Vehicle
Date range
```

### 17.2. Create Flow

Dung khi Admin tao maintenance thu cong.

Fields:

```text
vehicle_id
description
maintenance_date
status
```

Validation:

- Vehicle bat buoc.
- Description bat buoc.
- Status default PENDING.
- Khi create PENDING, update Vehicle.status = MAINTENANCE.

### 17.3. Mark Completed Flow

Transaction:

```text
Lock Vehicle_Maintenance
Lock Vehicle
Validate maintenance.status = PENDING
Update maintenance.status = COMPLETED
Update vehicle.status = AVAILABLE
Commit
```

## 18. Wallet Management

Models:

```text
Wallet
Wallet_Transaction
```

Khong nen CRUD day du vi day la financial audit.

### 18.1. Wallet List Flow

Columns:

```text
Customer
Email
Balance
Created At
Actions
```

Filter:

```text
Keyword customer/email
Balance range
```

Actions:

```text
View Transactions
Manual Adjustment neu team muon lam
```

### 18.2. Wallet Transaction View

Columns:

```text
Transaction ID
Wallet
Type
Amount
Description
Created At
```

Filter:

```text
Type: TOPUP/PAYMENT/REFUND neu co
Date range
```

Khong cho delete transaction.

## 19. Rental Discount View

Model:

```text
Rental_Discount
```

Chi can view.

Columns:

```text
Rental ID
Discount Code
Customer
Discount Amount
Created At neu co
```

Filter:

```text
Rental ID
Discount Code
Customer
```

Khong can create/update/delete vi duoc tao tu BookingService.

## 20. Rental Status History View

Model:

```text
Rental_Status_History
```

Chi can view.

Columns:

```text
History ID
Rental ID
Status
Changed At
```

Filter:

```text
Rental ID
Status
Date range
```

Khong cho update/delete vi la audit log.

## 21. Review Management

Model:

```text
Review
```

### 21.1. List Flow

Columns:

```text
Review ID
Customer
Vehicle
Rating
Comment
Created At
Actions
```

Filter:

```text
Rating
Customer
Vehicle
Date range
```

### 21.2. Actions

Neu DB chua co status:

```text
View only
Delete neu bat buoc
```

Khuyen nghi sau nay them:

```text
status: VISIBLE/HIDDEN
```

Sau do lam:

```text
Hide/Show review
```

## 22. Report/Performance Modules

Khong phai CRUD, nhung can implement sau khi CRUD co data on dinh.

### 22.1. Financial Reports

Filters:

```text
View By: Custom Date / Month / Quarter / Year
Payment Method
Payment Type
Status
```

DTO de xuat:

```text
AdminFinancialSummaryDTO
RevenueChartDTO
PaymentMixDTO
```

Metrics:

```text
Total revenue
Booking revenue
Late fee revenue
Damage fee revenue
Pending payment count
Failed payment count
Wallet topup total
```

### 22.2. Station Performance

Filters:

```text
View By: Custom Date / Month / Quarter / Year
Station
```

DTO:

```text
StationPerformanceDTO
```

Metrics:

```text
Total vehicles
Available vehicles
Rented vehicles
Maintenance vehicles
Booking count
Revenue
Utilization rate
```

### 22.3. Model Performance

Filters:

```text
View By: Custom Date / Month / Quarter / Year
Category
Model
```

DTO:

```text
ModelPerformanceDTO
```

Metrics:

```text
Booking count
Revenue
Incident count
Maintenance count
Average utilization
```

## 23. Thu Tu Implement De Xuat

### Phase 1 - Admin Foundation

```text
1. Admin route + role guard
2. Admin layout/sidebar/topbar
3. Common table UI
4. Common message handling
5. Pagination helper
```

### Phase 2 - Master Data CRUD

```text
1. Station CRUD
2. Category CRUD
3. Vehicle Model CRUD
4. Vehicle Model Image CRUD
5. Vehicle CRUD
```

### Phase 3 - User/Promotion

```text
1. Account Management
2. Discount CRUD
```

### Phase 4 - Transaction Viewer

```text
1. Rental list/detail
2. Rental status history
3. Payment list/detail
4. Extra charge list/detail
5. Rental discount view
```

### Phase 5 - Operation

```text
1. Incident management
2. Maintenance management
3. Review management
4. Wallet view
```

### Phase 6 - Reports

```text
1. Financial Reports
2. Station Performance
3. Model Performance
4. Charts with real data
```

## 24. Checklist Cho Moi Module

Moi teammate khi lam module nen tick:

```text
[ ] Route GET list
[ ] Route GET create form
[ ] Route POST create
[ ] Route GET edit form
[ ] Route POST update
[ ] Route POST delete/disable neu co
[ ] DAO list/search/count
[ ] DAO findById
[ ] DAO create
[ ] DAO update
[ ] DAO delete/check related
[ ] Service validation
[ ] JSP list
[ ] JSP form
[ ] JSP detail neu can
[ ] Message success/error
[ ] Pagination
[ ] Role guard ADMIN
[ ] Build successful
```

## 25. Luu Y Quan Trong

- Khong hard delete cac bang da co lich su giao dich.
- Cac thay doi status Rental/Vehicle/Payment phai di qua Service.
- Wallet transaction, payment, rental status history la audit data, khong nen delete.
- Neu can delete master data, phai check relationship truoc.
- Neu DB chua co status cho mot model can soft delete, tam thoi block delete thay vi sua DB lien tuc.
- Tat ca form nen dung UTF-8.
- Sau khi implement moi module phai test voi account ADMIN va test Customer/Staff truy cap bi chan.

