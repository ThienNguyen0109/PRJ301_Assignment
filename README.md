# E-Vehicle Rental System

Website thuê xe điện xây dựng bằng Java JSP/Servlet, JPA/JDBC, SQL Server và Tomcat. Dự án hỗ trợ khách hàng tìm xe, đặt xe, thanh toán ví/VNPay, staff giao/trả xe, bảo trì, xử lý phát sinh và admin quản lý dữ liệu, tài chính, hiệu suất.

## Công Nghệ

- Java 8
- JSP, Servlet
- JPA EclipseLink, JDBC
- SQL Server
- Apache Tomcat 9
- NetBeans project
- JSTL
- JavaMail
- BCrypt
- VNPay Sandbox
- Google OAuth Login

## Cấu Trúc Chính

```text
src/java/controllers     Servlet controllers
src/java/services        Business logic
src/java/daos            DAO layer
src/java/models          JPA entities
src/java/dto             DTO objects
src/java/enums           Enum definitions
src/conf/persistence.xml JPA database config
web/                     JSP, CSS, JS, assets
web/WEB-INF/views        Admin/Staff protected JSP views
web/lib                  Required jar libraries
EVehicleRental_DB.sql    Full SQL Server script
```

## Yêu Cầu Cài Đặt

1. JDK 8.
2. Apache Tomcat 9.
3. NetBeans IDE.
4. SQL Server và SQL Server Management Studio.
5. SQL Server JDBC driver đã có trong `web/lib/sqljdbc4.jar`.
6. Các jar cần có trong `web/lib`:
   - `sqljdbc4.jar`
   - `jstl-1.2.jar`
   - `mail-1.4.7.jar`
   - `jbcrypt-0.4.jar`
7. EclipseLink library trong NetBeans vì project đang dùng `${libs.eclipselink.classpath}`.

## Clone Và Mở Dự Án

```bash
git clone <repository-url>
cd PRJ301-EVehvicleRental
```

Mở NetBeans:

1. `File` -> `Open Project`.
2. Chọn thư mục `PRJ301-EVehvicleRental`.
3. Chọn server Tomcat 9.
4. Kiểm tra `Libraries` nếu NetBeans báo thiếu EclipseLink thì thêm EclipseLink/JPA library.

## Setup Database

1. Mở SQL Server Management Studio.
2. Mở file [EVehicleRental_DB.sql](EVehicleRental_DB.sql).
3. Chạy toàn bộ script bằng `Ctrl + A` rồi `Execute`.
4. Script sẽ tạo database `EVehicleRental_DB`, tables, constraints và seed data.

File JPA config nằm tại:

[src/conf/persistence.xml](src/conf/persistence.xml)

Mặc định:

```xml
jdbc:sqlserver://localhost:1433;databaseName=EVehicleRental_DB;encrypt=false;trustServerCertificate=true
user: sa
password: 12345
```

Nếu máy bạn dùng tài khoản SQL Server khác, sửa:

```xml
<property name="javax.persistence.jdbc.user" value="sa"/>
<property name="javax.persistence.jdbc.password" value="12345"/>
```

## Setup Google Login

Dự án đọc Google OAuth config từ file `.env` ở root project. File này không nên commit lên Git.

Tạo file `.env`:

```env
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

Trong Google Cloud Console, OAuth Client type là `Web Application`.

Authorized JavaScript origins:

```text
http://localhost:8080
https://your-ngrok-domain.ngrok-free.dev
```

Authorized redirect URIs:

```text
http://localhost:8080/PRJ301-EVehvicleRental/google-callback
https://your-ngrok-domain.ngrok-free.dev/PRJ301-EVehvicleRental/google-callback
```

Nếu dùng ngrok free, mỗi lần URL ngrok đổi thì cần cập nhật lại origin và redirect URI trên Google Cloud.

## Setup VNPay Sandbox

VNPay được cấu hình trong:

[src/java/services/VNPayService.java](src/java/services/VNPayService.java)

Return URL được build tự động theo request hiện tại, nên chạy localhost hoặc ngrok đều dùng được nếu URL public trỏ đúng Tomcat.

Khi dùng ngrok:

```bash
ngrok http 8080
```

Truy cập app bằng:

```text
https://your-ngrok-domain.ngrok-free.dev/PRJ301-EVehvicleRental
```

Không truy cập root ngrok `/` vì đó chỉ là trang Tomcat root.

## Chạy Dự Án

Trong NetBeans:

1. Clean and Build project.
2. Run project.
3. Mở:

```text
http://localhost:8080/PRJ301-EVehvicleRental
```

Nếu đã đăng nhập, root URL sẽ tự điều hướng theo role:

- Customer -> Home
- Staff -> Staff Dashboard
- Admin -> Admin Dashboard

## Tài Khoản Test

Mật khẩu mặc định của các tài khoản seed: `123456`.

| Role | Email | Password | Ghi chú |
| --- | --- | --- | --- |
| Customer | `nguyenvana@gmail.com` | `123456` | Active |
| Customer | `tranthib@gmail.com` | `123456` | Active |
| Staff | `lephuocc@company.com` | `123456` | Active |
| Admin | `admin_system@domain.com` | `123456` | Active |
| Customer | `giahuy_badluck@gmail.com` | `123456` | Inactive, dùng để test khóa tài khoản |

## Luồng Test Customer

### 1. Đăng Ký Và OTP

1. Vào `?action=register`.
2. Nhập thông tin tài khoản.
3. Hệ thống gửi OTP qua email.
4. Nhập OTP để xác minh tài khoản.
5. Đăng nhập bằng tài khoản vừa tạo.

### 2. Đăng Nhập Google

1. Vào login.
2. Bấm `Đăng nhập bằng Google`.
3. Nếu account Google chưa có trong DB, hệ thống tạo account role `CUSTOMER`.
4. Nếu account chưa có số điện thoại, Home hiển thị popup nhắc cập nhật.
5. Khi booking, nếu chưa có số điện thoại, hệ thống bắt cập nhật ngay trên popup booking.

### 3. Tìm Xe Và Đặt Xe

1. Vào Home.
2. Lọc theo Station hoặc Category.
3. Chọn model xe.
4. Chọn ngày thuê. Không được chọn ngày trong quá khứ.
5. Xem danh sách xe thật còn trống.
6. Chọn xe cụ thể.
7. Vào Booking Page.
8. Nhập mã giảm giá nếu có.
9. Thanh toán bằng Wallet hoặc VNPay.
10. Sau khi thành công, xem Booking Detail và nhận email xác nhận.

### 4. Nạp Ví

1. Vào Wallet.
2. Chọn nhanh số tiền hoặc nhập số tiền từ `10,000` đến `10,000,000` VND.
3. Thanh toán qua VNPay.
4. Callback thành công sẽ cập nhật số dư ví và lịch sử giao dịch.

### 5. Profile

1. Xem thông tin cá nhân.
2. Cập nhật số điện thoại.
3. Xem lịch sử đơn thuê có phân trang.
4. Xem thông tin ví và lịch sử liên quan.

## Luồng Test Staff

Đăng nhập bằng:

```text
lephuocc@company.com / 123456
```

### 1. Pickup Vehicle

1. Vào Staff Dashboard.
2. Vào Pickup Management.
3. Tìm booking status `BOOKED`.
4. Xem chi tiết booking.
5. Bấm `Confirm Pickup`.
6. Rental chuyển `BOOKED -> RENTED`.
7. Hệ thống ghi Rental Status History.

Trường hợp khách không đến:

1. Chọn booking `BOOKED`.
2. Bấm `Mark No Show`.
3. Rental chuyển `NO_SHOW`.
4. Vehicle chuyển về `AVAILABLE`.

### 2. Return Vehicle

1. Vào Return Management.
2. Chọn rental status `RENTED`.
3. Nhập Battery Level.
4. Chọn condition:
   - `NORMAL`
   - `DAMAGED`
5. Nếu `NORMAL` và pin từ 80% trở lên:
   - Rental chuyển `COMPLETED`.
   - Vehicle chuyển `AVAILABLE`.
6. Nếu `NORMAL` nhưng pin dưới 80%:
   - Rental chuyển `COMPLETED`.
   - Vehicle chuyển `MAINTENANCE`.
   - Tạo maintenance để staff sạc pin.
7. Nếu `DAMAGED`:
   - Tạo Incident Report.
   - Tạo Extra Charge nếu có phí hư hỏng.
   - Vehicle chuyển `MAINTENANCE`.

### 3. Maintenance

1. Vào Maintenance Management.
2. Xem xe đang `MAINTENANCE`.
3. Nhập battery level mới sau khi sạc/sửa.
4. Bấm `Mark Completed`.
5. Vehicle chuyển về `AVAILABLE`.

### 4. Incident

1. Vào Incident Management.
2. Xem danh sách incident.
3. Xem chi tiết incident theo rental và vehicle.

## Luồng Test Admin

Đăng nhập bằng:

```text
admin_system@domain.com / 123456
```

### 1. Dashboard

Admin Dashboard hiển thị:

- Total Revenue
- Active Rentals
- Available Vehicles
- Pending Charges
- Recent Rentals

Một số chỉ số có realtime update khi có booking, payment, return, maintenance.

### 2. Financial Reports

Test các filter:

- Custom Date
- Month
- Quarter
- Year

Kiểm tra:

- Revenue chart
- Payment mix
- Payment/charge breakdown
- Pending payments

### 3. Station Performance

Kiểm tra:

- Xe available/rented/maintenance theo station.
- Hiệu suất station.
- Chi tiết station.

### 4. Model Performance

Kiểm tra:

- Model được thuê nhiều.
- Revenue theo model.
- Utilization theo model.

### 5. CRUD Master Data

Admin có thể quản lý:

- Accounts
- Stations
- Categories
- Vehicle Models
- Vehicle Model Images
- Vehicles
- Discounts

Lưu ý Vehicle Model Images upload ảnh vào thư mục assets vehicle và DB lưu đường dẫn ảnh.

### 6. Transactions

Admin xem và xử lý:

- Rentals
- Rental Status History
- Payments
- Extra Charges
- Rental Discounts
- Wallets
- Wallet Transactions

Extra Charges không cho tạo thủ công từ Admin. Extra charge phát sinh từ flow trả xe trễ hoặc hư hỏng.

## Realtime Đã Có

Realtime notification và refresh số liệu được dùng cho:

- Admin Dashboard metrics.
- Admin report metrics.
- Staff Dashboard.
- Booking/payment events.
- Pickup/return/maintenance events.
- Vehicle availability changes.
- Extra charge payment status.

## Một Số Lưu Ý Khi Test

- Cùng một trình duyệt chỉ có một session cho cùng domain/context. Nếu muốn test Customer, Staff, Admin cùng lúc, dùng Chrome thường + Incognito hoặc nhiều browser khác nhau.
- Không commit `build/`, `dist/`, `.war`, `nbproject/private/`, `.env`.
- Nếu NetBeans vẫn báo thiếu thư viện, kiểm tra `web/lib` và EclipseLink library trong Project Properties.
- Nếu lỗi font, kiểm tra project encoding là UTF-8 và database script dùng prefix `N'...'` cho tiếng Việt.
- Nếu VNPay callback không hoạt động qua ngrok, kiểm tra đang truy cập đúng context path `/PRJ301-EVehvicleRental`.

## Troubleshooting

### JSTL URI cannot be resolved

Kiểm tra `web/lib/jstl-1.2.jar` có tồn tại và được add vào project libraries.

### Không connect được SQL Server

Kiểm tra:

- SQL Server đang chạy.
- Port `1433` mở.
- Database `EVehicleRental_DB` đã được tạo.
- `src/conf/persistence.xml` đúng user/password.
- SQL Server cho phép TCP/IP.

### Google login báo thiếu cấu hình

Kiểm tra `.env` có hai biến:

```env
GOOGLE_CLIENT_ID=...
GOOGLE_CLIENT_SECRET=...
```

Sau khi đổi `.env`, restart Tomcat để app đọc lại config.

### Ngrok vào trang Tomcat root

Bạn cần thêm context path:

```text
https://your-ngrok-domain.ngrok-free.dev/PRJ301-EVehvicleRental
```

### Clean and Build làm Git hiện file build/dist

Các thư mục `build/`, `dist/` đã nằm trong `.gitignore`. Nếu trước đó lỡ commit, cần untrack khỏi Git index:

```bash
git rm -r --cached build dist
git commit -m "remove generated build artifacts"
```

