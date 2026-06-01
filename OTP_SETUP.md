# Hướng Dẫn Cấu Hình OTP Email

## Cấu Hình Gmail SMTP cho OTP

### Bước 1: Tạo App Password từ Gmail

1. Đăng nhập vào [Google Account](https://myaccount.google.com/)
2. Vào **Security** → **App passwords**
3. Chọn **Mail** và **Windows Computer**
4. Google sẽ tạo App Password (16 ký tự)
5. Copy App Password này

### Bước 2: Cập Nhật EmailService

File: `src/java/services/EmailService.java`

```java
private static final String SENDER_EMAIL = "your-email@gmail.com"; // Thay bằng Gmail của bạn
private static final String SENDER_PASSWORD = "xxxx xxxx xxxx xxxx"; // Thay bằng App Password
```

### Bước 3: Thêm Mail Library

Thêm `mail.jar` vào `web/lib/` nếu chưa có:
- Download từ [javax.mail](https://javaee.github.io/javamail/)
- Hoặc sử dụng Maven/Gradle

### Bước 4: Test

1. Compile và run project
2. Vào `http://localhost:8080/PRJ301-EVehvicleRental/register`
3. Nhập thông tin đăng ký
4. Kiểm tra email nhận được OTP

## Cấu Trúc Dự Án

```
src/java/
├── models/
│   ├── Account.java
│   ├── Role.java
│   ├── Wallet.java
│   ├── WalletTransaction.java
│   └── TransactionType.java
├── daos/
│   ├── IAccountDAO.java
│   ├── AccountDAO.java
│   ├── IWalletDAO.java
│   └── WalletDAO.java
├── services/
│   ├── EmailService.java
│   ├── OTPService.java
│   └── RegistrationService.java
├── controllers/
│   ├── HomeServlet.java
│   ├── LoginServlet.java
│   ├── LogoutServlet.java
│   ├── RegisterServlet.java
│   └── VerifyOTPServlet.java
└── filters/
    └── AuthenticationFilter.java

web/
├── index.jsp
├── login.jsp
├── register.jsp
├── verify-otp.jsp
├── dashboard.jsp
└── lib/
    ├── sqljdbc4.jar
    └── mail.jar
```

## Flow Đăng Ký

1. User truy cập `/register`
2. Nhập: Họ tên, Email, Mật khẩu, Số điện thoại
3. System validate dữ liệu
4. Generate OTP 6 chữ số
5. Gửi OTP qua email
6. User vào `/verify-otp` và nhập OTP
7. Nếu OTP đúng:
   - Tạo Account trong database
   - Tạo Wallet với balance = 0
   - Xóa OTP khỏi session
   - Redirect đến login
8. Nếu OTP sai hoặc hết hạn: Hiển thị lỗi

## OTP Validation

- Độ dài: 6 chữ số
- Hết hạn: 5 phút
- Lưu trong: HttpSession
- Không dùng bảng database

## Ghi Chú

- **Email Configuration**: Cần cấu hình Gmail App Password trong `EmailService.java`
- **Mail Library**: Cần thêm `mail.jar` vào classpath
- **Session Timeout**: Thiết lập trong `web.xml` nếu cần
- **OTP Format**: Chỉ chấp nhận 6 chữ số
