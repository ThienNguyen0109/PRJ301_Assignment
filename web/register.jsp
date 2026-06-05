<%-- 
    Document   : register
    Created on : June 1, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng Ký - E-Vehicle Rental</title>
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

            .auth-shell {
                width: min(1040px, 100%);
                display: grid;
                grid-template-columns: 1fr 520px;
                gap: 24px;
                align-items: stretch;
            }

            .brand-panel,
            .register-container {
                border-radius: 8px;
                border: 1px solid rgba(218, 183, 99, 0.22);
                box-shadow: 0 24px 70px rgba(8, 17, 31, 0.28);
                overflow: hidden;
            }

            .brand-panel {
                position: relative;
                color: white;
                padding: 38px;
                min-height: 590px;
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

            .register-container {
                background: rgba(255, 255, 255, 0.94);
                padding: 38px;
                align-self: center;
            }

            .register-container h1 {
                color: #111827;
                margin-bottom: 10px;
                text-align: center;
                font-size: 28px;
                font-weight: 800;
            }

            .register-container .subtitle {
                color: #566070;
                text-align: center;
                margin-bottom: 28px;
                font-size: 14px;
            }

            .form-group {
                margin-bottom: 18px;
            }

            label {
                display: block;
                color: #111827;
                font-weight: 700;
                margin-bottom: 8px;
                font-size: 14px;
            }

            input[type="text"],
            input[type="email"],
            input[type="password"],
            input[type="tel"] {
                width: 100%;
                padding: 13px 14px;
                border: 1px solid rgba(17, 24, 39, 0.14);
                border-radius: 7px;
                background: rgba(255, 255, 255, 0.84);
                color: #172033;
                font-size: 14px;
                transition: border-color 0.25s, box-shadow 0.25s, background 0.25s;
            }

            input:focus {
                outline: none;
                border-color: rgba(214, 169, 78, 0.85);
                box-shadow: 0 0 0 4px rgba(214, 169, 78, 0.16);
                background: #ffffff;
            }

            .error-message {
                color: #7f1d1d;
                background-color: #fee2e2;
                padding: 12px 14px;
                border-radius: 7px;
                margin-bottom: 20px;
                display: <%= request.getAttribute("error") != null ? "block" : "none" %>;
                border: 1px solid #fecaca;
                line-height: 1.5;
                font-size: 14px;
            }

            .password-group {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 14px;
            }

            .register-btn {
                width: 100%;
                padding: 13px;
                color: #09111f;
                border: 1px solid rgba(218, 183, 99, 0.55);
                border-radius: 7px;
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                font-size: 16px;
                font-weight: 800;
                cursor: pointer;
                margin-top: 8px;
                box-shadow: 0 12px 28px rgba(180, 122, 31, 0.2);
                transition: transform 0.25s, box-shadow 0.25s;
            }

            .register-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 16px 36px rgba(180, 122, 31, 0.3);
            }

            .login-link {
                text-align: center;
                margin-top: 22px;
                font-size: 14px;
                color: #566070;
            }

            .login-link a {
                color: #b47a1f;
                text-decoration: none;
                font-weight: 800;
            }

            .login-link a:hover {
                text-decoration: underline;
            }

            @media (max-width: 900px) {
                .auth-shell {
                    grid-template-columns: 1fr;
                }

                .brand-panel {
                    min-height: auto;
                }

                .brand-mark {
                    margin-bottom: 34px;
                }
            }

            @media (max-width: 600px) {
                body {
                    padding: 16px;
                }

                .brand-panel,
                .register-container {
                    padding: 26px;
                }

                .brand-panel h2 {
                    font-size: 30px;
                }

                .password-group {
                    grid-template-columns: 1fr;
                    gap: 0;
                }
            }
        </style>
    </head>
    <body>
        <div class="auth-shell">
            <section class="brand-panel">
                <div class="brand-mark">🚗 E-Vehicle Rental</div>
                <h2>Tạo tài khoản cho hành trình xanh.</h2>
                <p>Đăng ký để nạp ví, thuê xe điện và quản lý lịch sử giao dịch trong một trải nghiệm cao cấp, nhanh gọn.</p>
            </section>

            <div class="register-container">
                <h1>Đăng Ký</h1>
                <p class="subtitle">Tạo tài khoản E-Vehicle Rental</p>

                <% 
                    String error = (String) request.getAttribute("error");
                    if (error != null && !error.isEmpty()) {
                %>
                    <div class="error-message">
                        <%= error %>
                    </div>
                <% } %>

                <form action="register" method="POST" accept-charset="UTF-8">
                    <div class="form-group">
                        <label for="fullName">Họ và Tên *</label>
                        <input 
                            type="text" 
                            id="fullName" 
                            name="fullName" 
                            placeholder="Nhập họ và tên"
                            required
                            value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : "" %>"
                        >
                    </div>

                    <div class="form-group">
                        <label for="email">Email *</label>
                        <input 
                            type="email" 
                            id="email" 
                            name="email" 
                            placeholder="Nhập email"
                            required
                            value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
                        >
                    </div>

                    <div class="form-group">
                        <label for="phone">Số Điện Thoại</label>
                        <input 
                            type="tel" 
                            id="phone" 
                            name="phone" 
                            placeholder="Nhập số điện thoại (10-11 chữ số)"
                            value="<%= request.getAttribute("phone") != null ? request.getAttribute("phone") : "" %>"
                        >
                    </div>

                    <div class="password-group">
                        <div class="form-group">
                            <label for="password">Mật Khẩu *</label>
                            <input 
                                type="password" 
                                id="password" 
                                name="password" 
                                placeholder="Nhập mật khẩu"
                                required
                            >
                        </div>

                        <div class="form-group">
                            <label for="confirmPassword">Xác Nhận Mật Khẩu *</label>
                            <input 
                                type="password" 
                                id="confirmPassword" 
                                name="confirmPassword" 
                                placeholder="Xác nhận mật khẩu"
                                required
                            >
                        </div>
                    </div>

                    <button type="submit" class="register-btn">Đăng Ký</button>
                </form>

                <div class="login-link">
                    Đã có tài khoản? <a href="<%= request.getContextPath() %>?page=login">Đăng Nhập</a>
                </div>
            </div>
        </div>
    </body>
</html>
