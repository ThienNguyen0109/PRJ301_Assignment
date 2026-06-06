<%-- 
    Document   : login
    Created on : May 28, 2026, 7:47:20 AM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng Nhập - E-Vehicle Rental</title>
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

            .login-shell {
                width: min(980px, 100%);
                display: grid;
                grid-template-columns: 1fr 450px;
                gap: 24px;
                align-items: stretch;
            }

            .brand-panel,
            .login-container {
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

            .login-container {
                background: rgba(255, 255, 255, 0.94);
                padding: 42px;
                align-self: center;
            }

            .login-container h1 {
                color: #111827;
                margin-bottom: 10px;
                text-align: center;
                font-size: 28px;
                font-weight: 800;
            }

            .login-container .subtitle {
                color: #566070;
                text-align: center;
                margin-bottom: 30px;
                font-size: 14px;
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
            input[type="password"] {
                width: 100%;
                padding: 13px 14px;
                border: 1px solid rgba(17, 24, 39, 0.14);
                border-radius: 7px;
                background: rgba(255, 255, 255, 0.84);
                color: #172033;
                font-size: 14px;
                transition: border-color 0.25s, box-shadow 0.25s, background 0.25s;
            }

            input[type="email"]:focus,
            input[type="password"]:focus {
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
                display: <%= request.getAttribute("error") != null ? "block" : "none" %>;
            }

            .success-message {
                color: #14532d;
                background-color: #dcfce7;
                border: 1px solid #bbf7d0;
                display: block;
            }

            .login-btn {
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

            .login-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 16px 36px rgba(180, 122, 31, 0.3);
            }

            .helper-row {
                text-align: right;
                margin-top: -8px;
                margin-bottom: 20px;
                font-size: 14px;
            }

            .footer-links {
                text-align: center;
                margin-top: 22px;
                font-size: 14px;
            }

            .footer-links a,
            .helper-row a {
                color: #b47a1f;
                font-weight: 800;
                text-decoration: none;
            }

            .footer-links a:hover,
            .helper-row a:hover {
                text-decoration: underline;
            }

            @media (max-width: 860px) {
                .login-shell {
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
                .login-container {
                    padding: 26px;
                }

                .brand-panel h2 {
                    font-size: 30px;
                }
            }
        </style>
    </head>
    <body>
        <div class="login-shell">
            <section class="brand-panel">
                <div class="brand-mark">🚗 E-Vehicle Rental</div>
                <h2>Di chuyển xanh với trải nghiệm cao cấp.</h2>
                <p>Đăng nhập để quản lý ví, thuê xe điện và theo dõi các giao dịch của bạn trong một không gian hiện đại.</p>
            </section>

            <div class="login-container">
                <h1>Đăng Nhập</h1>
                <p class="subtitle">E-Vehicle Rental System</p>

                <% 
                    Object successMsg = session.getAttribute("registrationSuccess");
                    if (successMsg != null) {
                %>
                    <div class="success-message">
                        <%= successMsg %>
                    </div>
                <% 
                        session.removeAttribute("registrationSuccess");
                    }
                %>

                <% 
                    String error = (String) request.getAttribute("error");
                    if (error != null && !error.isEmpty()) {
                %>
                    <div class="error-message">
                        <%= error %>
                    </div>
                <% } %>

                <form action="login" method="POST" accept-charset="UTF-8">
                    <div class="form-group">
                        <label for="email">Email</label>
                        <input 
                            type="email" 
                            id="email" 
                            name="email" 
                            placeholder="Nhập email của bạn"
                            required
                            value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>"
                        >
                    </div>

                    <div class="form-group">
                        <label for="password">Mật Khẩu</label>
                        <input 
                            type="password" 
                            id="password" 
                            name="password" 
                            placeholder="Nhập mật khẩu của bạn"
                            required
                        >
                    </div>

                    <div class="helper-row">
                        <a href="<%= request.getContextPath() %>?page=reset-password">Quên mật khẩu?</a>
                    </div>

                    <button type="submit" class="login-btn">Đăng Nhập</button>
                </form>

                <div class="footer-links">
                    <a href="<%= request.getContextPath() %>?page=register">Đăng ký tài khoản</a>
                </div>
            </div>
        </div>
    </body>
</html>
