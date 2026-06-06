<%-- 
    Document   : reset-password
    Created on : June 6, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đặt Lại Mật Khẩu - E-Vehicle Rental</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                min-height: 100vh;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                color: #172033;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 28px;
                background:
                    radial-gradient(circle at 16% 12%, rgba(205, 164, 82, 0.2), transparent 28%),
                    radial-gradient(circle at 86% 20%, rgba(58, 191, 184, 0.16), transparent 30%),
                    linear-gradient(135deg, #08111f 0%, #111a2c 46%, #f4f0e8 46%, #f8f6f2 100%);
                background-attachment: fixed;
            }

            .reset-shell {
                width: min(980px, 100%);
                display: grid;
                grid-template-columns: 1fr 450px;
                gap: 24px;
                align-items: stretch;
            }

            .brand-panel,
            .reset-container {
                border-radius: 8px;
                border: 1px solid rgba(218, 183, 99, 0.22);
                box-shadow: 0 24px 70px rgba(8, 17, 31, 0.28);
                overflow: hidden;
            }

            .brand-panel {
                position: relative;
                color: white;
                padding: 38px;
                min-height: 520px;
                background:
                    linear-gradient(135deg, rgba(15, 28, 49, 0.96), rgba(47, 61, 91, 0.94)),
                    linear-gradient(135deg, #d6a94e, #3abfb8);
            }

            .brand-panel::after {
                content: "";
                position: absolute;
                right: -70px;
                bottom: -70px;
                width: 260px;
                height: 260px;
                border-radius: 50%;
                border: 1px solid rgba(218, 183, 99, 0.34);
            }

            .brand-mark {
                font-size: 26px;
                font-weight: 800;
                margin-bottom: 90px;
            }

            .brand-panel h2 {
                max-width: 440px;
                font-size: 38px;
                line-height: 1.18;
                margin-bottom: 18px;
            }

            .brand-panel p {
                max-width: 420px;
                color: rgba(255, 255, 255, 0.78);
                line-height: 1.7;
                font-size: 15px;
            }

            .reset-container {
                background: rgba(255, 255, 255, 0.94);
                padding: 42px;
                align-self: center;
            }

            .reset-container h1 {
                color: #111827;
                margin-bottom: 10px;
                text-align: center;
                font-size: 28px;
                font-weight: 800;
            }

            .subtitle {
                color: #566070;
                text-align: center;
                margin-bottom: 26px;
                font-size: 14px;
                line-height: 1.6;
            }

            .step-list {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 8px;
                margin-bottom: 24px;
            }

            .step {
                padding: 9px 8px;
                border-radius: 7px;
                text-align: center;
                font-size: 12px;
                font-weight: 800;
                color: #7d8794;
                background: rgba(255, 255, 255, 0.62);
                border: 1px solid rgba(17, 24, 39, 0.08);
            }

            .step.active {
                color: #09111f;
                background: #f8df9d;
                border-color: rgba(218, 183, 99, 0.55);
            }

            .form-group {
                margin-bottom: 20px;
            }

            label {
                display: block;
                color: #111827;
                font-weight: 700;
                margin-bottom: 8px;
                font-size: 14px;
            }

            input[type="email"],
            input[type="password"],
            input[type="text"] {
                width: 100%;
                padding: 13px 14px;
                border: 1px solid rgba(17, 24, 39, 0.14);
                border-radius: 7px;
                background: rgba(255, 255, 255, 0.84);
                color: #172033;
                font-size: 14px;
                transition: border-color 0.25s, box-shadow 0.25s, background 0.25s;
            }

            input[type="text"] {
                text-align: center;
                letter-spacing: 7px;
                font-size: 22px;
                font-weight: 800;
                font-family: 'Courier New', monospace;
            }

            input:focus {
                outline: none;
                border-color: rgba(214, 169, 78, 0.85);
                box-shadow: 0 0 0 4px rgba(214, 169, 78, 0.16);
                background: #ffffff;
            }

            .error-message,
            .success-message {
                padding: 12px 14px;
                border-radius: 7px;
                margin-bottom: 20px;
                font-size: 14px;
                line-height: 1.5;
            }

            .error-message {
                color: #7f1d1d;
                background-color: #fee2e2;
                border: 1px solid #fecaca;
            }

            .success-message {
                color: #14532d;
                background-color: #dcfce7;
                border: 1px solid #bbf7d0;
            }

            .reset-btn {
                width: 100%;
                padding: 13px;
                color: #09111f;
                border: 1px solid rgba(218, 183, 99, 0.55);
                border-radius: 7px;
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                font-size: 16px;
                font-weight: 800;
                cursor: pointer;
                box-shadow: 0 12px 28px rgba(180, 122, 31, 0.2);
                transition: transform 0.25s, box-shadow 0.25s;
            }

            .reset-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 16px 36px rgba(180, 122, 31, 0.3);
            }

            .footer-links {
                text-align: center;
                margin-top: 22px;
                font-size: 14px;
            }

            .footer-links a {
                color: #b47a1f;
                font-weight: 800;
                text-decoration: none;
            }

            .footer-links a:hover {
                text-decoration: underline;
            }

            @media (max-width: 860px) {
                .reset-shell {
                    grid-template-columns: 1fr;
                }

                .brand-panel {
                    min-height: auto;
                }

                .brand-mark {
                    margin-bottom: 34px;
                }
            }

            @media (max-width: 520px) {
                body {
                    padding: 16px;
                }

                .brand-panel,
                .reset-container {
                    padding: 26px;
                }

                .brand-panel h2 {
                    font-size: 30px;
                }

                .step-list {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <%
            String step = (String) session.getAttribute("resetStep");
            if (step == null) {
                step = "email";
            }
            String resetEmail = (String) session.getAttribute("resetEmail");
            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");
        %>

        <div class="reset-shell">
            <section class="brand-panel">
                <div class="brand-mark">🚗 E-Vehicle Rental</div>
                <h2>Lấy lại quyền truy cập thật nhẹ nhàng.</h2>
                <p>Xác minh email bằng OTP và tạo mật khẩu mới để tiếp tục quản lý ví, thuê xe điện và theo dõi giao dịch.</p>
            </section>

            <div class="reset-container">
                <h1>Đặt Lại Mật Khẩu</h1>
                <p class="subtitle">Hoàn tất các bước bên dưới để cập nhật mật khẩu tài khoản.</p>

                <div class="step-list">
                    <div class="step <%= "email".equals(step) ? "active" : "" %>">1. Email</div>
                    <div class="step <%= "otp".equals(step) ? "active" : "" %>">2. OTP</div>
                    <div class="step <%= "password".equals(step) ? "active" : "" %>">3. Mật khẩu</div>
                </div>

                <% if (error != null && !error.isEmpty()) { %>
                    <div class="error-message"><%= error %></div>
                <% } %>

                <% if (success != null && !success.isEmpty()) { %>
                    <div class="success-message"><%= success %></div>
                <% } %>

                <% if ("email".equals(step)) { %>
                    <form action="<%= request.getContextPath() %>/reset-password" method="POST" accept-charset="UTF-8">
                        <input type="hidden" name="action" value="requestOtp">
                        <div class="form-group">
                            <label for="email">Email tài khoản</label>
                            <input 
                                type="email" 
                                id="email" 
                                name="email" 
                                placeholder="Nhập email đã đăng ký"
                                required
                                value="<%= resetEmail != null ? resetEmail : "" %>"
                            >
                        </div>
                        <button type="submit" class="reset-btn">Gửi mã OTP</button>
                    </form>
                <% } else if ("otp".equals(step)) { %>
                    <form action="<%= request.getContextPath() %>/reset-password" method="POST" accept-charset="UTF-8">
                        <input type="hidden" name="action" value="verifyOtp">
                        <div class="form-group">
                            <label for="otp">Mã OTP đã gửi tới <%= resetEmail != null ? resetEmail : "email của bạn" %></label>
                            <input 
                                type="text" 
                                id="otp" 
                                name="otp" 
                                placeholder="000000"
                                maxlength="6"
                                pattern="\d{6}"
                                required
                                autofocus
                            >
                        </div>
                        <button type="submit" class="reset-btn">Xác minh OTP</button>
                    </form>
                <% } else { %>
                    <form action="<%= request.getContextPath() %>/reset-password" method="POST" accept-charset="UTF-8">
                        <input type="hidden" name="action" value="updatePassword">
                        <div class="form-group">
                            <label for="newPassword">Mật khẩu mới</label>
                            <input 
                                type="password" 
                                id="newPassword" 
                                name="newPassword" 
                                placeholder="Nhập mật khẩu mới"
                                required
                            >
                        </div>
                        <div class="form-group">
                            <label for="confirmPassword">Xác nhận mật khẩu mới</label>
                            <input 
                                type="password" 
                                id="confirmPassword" 
                                name="confirmPassword" 
                                placeholder="Nhập lại mật khẩu mới"
                                required
                            >
                        </div>
                        <button type="submit" class="reset-btn">Cập nhật mật khẩu</button>
                    </form>
                <% } %>

                <div class="footer-links">
                    <a href="<%= request.getContextPath() %>?page=login">Quay lại đăng nhập</a>
                </div>
            </div>
        </div>
    </body>
</html>
