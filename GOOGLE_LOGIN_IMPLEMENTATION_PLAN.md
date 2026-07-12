# Google Login Implementation Plan

Tài liệu này mô tả cách thêm chức năng đăng nhập bằng Google cho dự án E-Vehicle Rental mà không thay đổi database và không thay đổi model `Account` hiện tại.

## 1. Quyết định chính

Không sửa DB.

Không thêm cột vào bảng `Account`.

Không sửa `Account.java`.

Google Login sẽ dùng `email` từ Google để tìm hoặc tạo account trong hệ thống.

Vì bảng `Account` hiện tại đang yêu cầu `password` không được null, account được tạo từ Google sẽ có password random đã hash bằng BCrypt. Password này chỉ để thỏa schema, người dùng không cần biết và không dùng để đăng nhập thường.

## 2. Mục tiêu

Cho phép người dùng đăng nhập bằng tài khoản Google.

Sau khi Google xác thực thành công:

- Nếu email đã tồn tại trong hệ thống và account `ACTIVE`, cho đăng nhập vào account đó.
- Nếu email đã tồn tại nhưng account `INACTIVE`, không cho đăng nhập.
- Nếu email chưa tồn tại, tự tạo account mới role `CUSTOMER`, tạo wallet, rồi đăng nhập.
- Không tạo `ADMIN` hoặc `STAFF` tự động từ Google.
- Không bypass phân quyền hiện tại.
- Redirect theo role hiện tại:
  - `ADMIN` -> `?action=admin-dashboard`
  - `STAFF` -> `?action=staff-dashboard`
  - `CUSTOMER` -> `?action=home`

## 3. Flow tổng quan

Dùng OAuth 2.0 Authorization Code Flow phía server.

Flow:

1. User bấm nút `Đăng nhập bằng Google` ở `login.jsp`.
2. Browser gọi `/google-login`.
3. `GoogleLoginController` tạo `state` random và lưu vào session.
4. Controller redirect user sang Google OAuth URL.
5. User chọn Google account và đồng ý.
6. Google redirect về `/google-callback?code=...&state=...`.
7. `GoogleCallbackController` kiểm tra `state`.
8. Server dùng `code` gọi Google token endpoint để lấy `id_token`.
9. Server verify `id_token`.
10. Lấy thông tin user từ Google:
    - `email`
    - `email_verified`
    - `name`
11. `GoogleAuthService` tìm hoặc tạo account trong DB.
12. Set session giống login thường.
13. Redirect theo role.

Tài liệu Google tham khảo:

- https://developers.google.com/identity/protocols/oauth2/web-server
- https://developers.google.com/identity/gsi/web/guides/verify-google-id-token

## 4. Google Cloud Console Setup

Tạo OAuth Client:

1. Vào Google Cloud Console.
2. Tạo hoặc chọn project.
3. Vào `APIs & Services` -> `OAuth consent screen`.
4. Cấu hình app name, support email, developer contact.
5. Vào `Credentials`.
6. Tạo `OAuth client ID`.
7. Application type: `Web application`.
8. Thêm Authorized redirect URI.

Local:

```text
http://localhost:8080/PRJ301-EVehvicleRental/google-callback
```

Ngrok:

```text
https://YOUR-NGROK-DOMAIN.ngrok-free.app/PRJ301-EVehvicleRental/google-callback
```

Lưu lại:

```text
GOOGLE_CLIENT_ID
GOOGLE_CLIENT_SECRET
GOOGLE_REDIRECT_URI
```

Không commit `client_secret` lên Git.

## 5. Config trong dự án

Tạo file config không commit:

```text
src/java/config/google-oauth.properties
```

Nội dung:

```properties
google.client.id=YOUR_CLIENT_ID
google.client.secret=YOUR_CLIENT_SECRET
google.redirect.uri=http://localhost:8080/PRJ301-EVehvicleRental/google-callback
```

Thêm vào `.gitignore`:

```gitignore
src/java/config/google-oauth.properties
```

Tạo file sample để team biết cần config gì:

```text
src/java/config/google-oauth.example.properties
```

Nội dung:

```properties
google.client.id=
google.client.secret=
google.redirect.uri=http://localhost:8080/PRJ301-EVehvicleRental/google-callback
```

## 6. Database và model

Không thay đổi.

Không thêm các cột sau:

```text
auth_provider
google_sub
avatar_url
```

Không sửa `Account.java`.

Khi tạo account Google mới, hệ thống sẽ map vào các cột hiện tại:

| Cột hiện tại | Giá trị |
| --- | --- |
| `account_id` | UUID mới |
| `email` | Email từ Google |
| `password` | BCrypt hash của chuỗi random |
| `full_name` | Name từ Google |
| `phone` | `null` hoặc chuỗi rỗng tùy validation hiện tại |
| `is_verified` | `true` |
| `role` | `CUSTOMER` |
| `status` | `ACTIVE` |
| `created_at` | thời gian hiện tại |

## 7. Business rules

### Email chưa tồn tại

Tạo account mới:

- Role: `CUSTOMER`
- Status: `ACTIVE`
- Is verified: `true`
- Password: random string được hash bằng `PasswordUtil.hashPassword`
- Tạo wallet cho customer
- Login ngay sau khi tạo thành công

### Email đã tồn tại

Không tạo account mới.

Nếu account `ACTIVE`:

- Login vào account đó.
- Redirect theo role của account hiện có.

Nếu account `INACTIVE`:

- Không cho login.
- Redirect về login với lỗi:

```text
Tài khoản của bạn đã bị vô hiệu hóa.
```

### Email Google chưa verified

Không cho login.

Thông báo:

```text
Email Google chưa được xác minh.
```

### Không tạo role admin/staff tự động

Google account mới luôn là `CUSTOMER`.

Nếu muốn Google login vào `ADMIN` hoặc `STAFF`, email đó phải đã tồn tại trong DB với role tương ứng.

## 8. DTO cần thêm

Tạo:

```text
src/java/dtos/GoogleUserProfile.java
```

Fields:

```java
private String email;
private boolean emailVerified;
private String name;
```

Có thể giữ thêm `sub` và `picture` trong DTO nếu muốn parse đầy đủ, nhưng không lưu vào DB:

```java
private String sub;
private String picture;
```

## 9. DAO cần dùng

Hiện tại đã có:

```java
Account getAccountByEmail(String email);
boolean createAccount(Account account);
```

Nếu các method này đang hoạt động ổn thì không cần thêm DAO mới.

Có thể cần thêm hoặc tái sử dụng method tạo wallet cho account mới. Nếu wallet creation đang nằm trong `RegistrationService`, nên tách thành method dùng chung để Google Login gọi lại.

Gợi ý service/helper:

```java
public void createWalletForCustomer(String accountId)
```

## 10. Service cần thêm

Tạo:

```text
src/java/services/GoogleAuthService.java
```

Nhiệm vụ:

- Build Google authorization URL.
- Exchange authorization code lấy token.
- Verify ID token.
- Parse Google profile.
- Tìm account theo email.
- Nếu chưa có account thì tạo customer mới.
- Tạo wallet cho customer mới.

Pseudo logic:

```java
public Account loginWithGoogle(GoogleUserProfile profile) {
    if (!profile.isEmailVerified()) {
        throw new IllegalStateException("Email Google chưa được xác minh.");
    }

    Account account = accountDAO.getAccountByEmail(profile.getEmail());

    if (account != null) {
        if (!"ACTIVE".equals(account.getStatus())) {
            throw new IllegalStateException("Tài khoản của bạn đã bị vô hiệu hóa.");
        }
        return account;
    }

    Account newAccount = new Account();
    newAccount.setAccountId(UUID.randomUUID().toString());
    newAccount.setEmail(profile.getEmail());
    newAccount.setPassword(PasswordUtil.hashPassword(UUID.randomUUID().toString()));
    newAccount.setFullName(profile.getName());
    newAccount.setPhone(null);
    newAccount.setIsVerified(true);
    newAccount.setRole(Role.CUSTOMER);
    newAccount.setStatus("ACTIVE");
    newAccount.setCreatedAt(new Timestamp(System.currentTimeMillis()));

    accountDAO.createAccount(newAccount);
    createWalletForCustomer(newAccount.getAccountId());

    return accountDAO.getAccountByEmail(profile.getEmail());
}
```

## 11. Controllers cần thêm

### GoogleLoginController

File:

```text
src/java/controllers/GoogleLoginController.java
```

Mapping:

```java
@WebServlet(name = "GoogleLoginController", urlPatterns = {"/google-login"})
```

Nhiệm vụ:

- Tạo `state` random.
- Lưu state vào session.
- Redirect sang Google authorization URL.

### GoogleCallbackController

File:

```text
src/java/controllers/GoogleCallbackController.java
```

Mapping:

```java
@WebServlet(name = "GoogleCallbackController", urlPatterns = {"/google-callback"})
```

Nhiệm vụ:

- Nhận `code`, `state`, `error`.
- Nếu Google trả `error`, redirect login kèm message.
- Check `state` giống session.
- Gọi `GoogleAuthService`.
- Set session giống `LoginController`.
- Redirect theo role.

Session set:

```java
session.setAttribute("user", account);
session.setAttribute("userId", account.getAccountId());
session.setAttribute("userEmail", account.getEmail());
session.setAttribute("userRole", account.getRole().getValue());
session.setAttribute("userName", account.getFullName());
```

Redirect theo role:

```java
private String getRedirectPageByRole(Account account) {
    if (account != null && account.getRole() == Role.ADMIN) {
        return "?action=admin-dashboard";
    }
    if (account != null && account.getRole() == Role.STAFF) {
        return "?action=staff-dashboard";
    }
    return "?action=home";
}
```

## 12. MainController

Có thể thêm action này để đồng bộ cách điều hướng:

```java
} else if (action.equals("google-login")) {
    url = "/google-login";
}
```

Callback nên giữ URL riêng:

```text
/google-callback
```

Lý do:

- Google Console cần redirect URI cố định.
- Callback là endpoint kỹ thuật OAuth, không phải page bình thường.

## 13. UI Login

File:

```text
web/login.jsp
```

Thêm nút:

```jsp
<a class="google-login-btn" href="<%= request.getContextPath() %>/google-login">
    Đăng nhập bằng Google
</a>
```

Hoặc nếu muốn đi qua `MainController`:

```jsp
<a class="google-login-btn" href="<%= request.getContextPath() %>?action=google-login">
    Đăng nhập bằng Google
</a>
```

CSS gợi ý:

```css
.google-login-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    width: 100%;
    min-height: 48px;
    border: 1px solid rgba(15, 23, 42, 0.14);
    border-radius: 12px;
    color: #111827;
    background: #fff;
    font-weight: 800;
    text-decoration: none;
}
```

## 14. Security checklist

Bắt buộc:

- Dùng `state` chống CSRF.
- Verify `id_token`.
- Check `aud` đúng `GOOGLE_CLIENT_ID`.
- Check issuer là Google.
- Check `email_verified = true`.
- Không login account `INACTIVE`.
- Không commit `client_secret`.
- Không lưu access token nếu chưa cần gọi Google API.
- Không log token ra console hoặc file log.

Nên làm:

- Generate random password placeholder bằng `UUID`.
- Hash placeholder bằng `PasswordUtil.hashPassword`.
- Redirect URI phải khớp chính xác với Google Console.

## 15. Ngrok notes

Ngrok free thường đổi domain mỗi lần restart.

Mỗi lần domain đổi, cần cập nhật:

1. Google Console Authorized redirect URI:

```text
https://NEW-NGROK-DOMAIN.ngrok-free.app/PRJ301-EVehvicleRental/google-callback
```

2. `google.redirect.uri` trong config local.

Nếu không cập nhật, Google sẽ báo:

```text
redirect_uri_mismatch
```

Nếu muốn đỡ đổi thủ công:

- Dùng ngrok static domain nếu account hỗ trợ.
- Hoặc chỉ test Google Login bằng localhost trong lúc dev.

## 16. Test cases

### Case 1 - Google email chưa tồn tại

Expected:

- Tạo account mới role `CUSTOMER`.
- `is_verified = true`.
- `status = ACTIVE`.
- Password được hash bằng BCrypt.
- Tạo wallet.
- Redirect `?action=home`.

### Case 2 - Google email đã tồn tại là CUSTOMER

Expected:

- Login vào account cũ.
- Không tạo account mới.
- Redirect `?action=home`.

### Case 3 - Google email đã tồn tại là STAFF

Expected:

- Login vào account staff nếu `ACTIVE`.
- Redirect `?action=staff-dashboard`.

### Case 4 - Google email đã tồn tại là ADMIN

Expected:

- Login vào account admin nếu `ACTIVE`.
- Redirect `?action=admin-dashboard`.

### Case 5 - Account INACTIVE

Expected:

- Không login.
- Redirect login với lỗi:

```text
Tài khoản của bạn đã bị vô hiệu hóa.
```

### Case 6 - State sai

Expected:

- Không login.
- Redirect login với lỗi:

```text
Phiên đăng nhập Google không hợp lệ. Vui lòng thử lại.
```

### Case 7 - Google email chưa verified

Expected:

- Không login.
- Redirect login với lỗi:

```text
Email Google chưa được xác minh.
```

## 17. Thứ tự implement đề xuất

1. Thêm config Google OAuth.
2. Thêm DTO `GoogleUserProfile`.
3. Thêm `GoogleAuthService`.
4. Tái sử dụng `AccountDAO.getAccountByEmail` và `AccountDAO.createAccount`.
5. Tách hoặc tái sử dụng logic tạo wallet cho customer mới.
6. Thêm `GoogleLoginController`.
7. Thêm `GoogleCallbackController`.
8. Thêm nút Google Login vào `login.jsp`.
9. Thêm action `google-login` vào `MainController` nếu muốn.
10. Test local.
11. Test ngrok.

## 18. Giới hạn của hướng không đổi DB/model

Hướng này đủ tốt cho demo và ít ảnh hưởng hệ thống hiện tại, nhưng có một số giới hạn:

- Không biết account nào được tạo bằng Google.
- Không lưu được Google `sub`, nên hệ thống định danh Google user bằng email.
- Không lưu avatar Google.
- Không quản lý được nhiều provider login sau này.

Nếu sau này muốn làm social login bài bản hơn, có thể nâng cấp thêm `auth_provider`, `google_sub`, `avatar_url`. Phần đó là optional, không cần làm trong scope hiện tại.
