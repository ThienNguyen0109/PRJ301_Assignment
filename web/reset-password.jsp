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
                color: #f8fafc;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 40px;
                position: relative;
                overflow-x: hidden;
                background: #08111f;
            }

            body::before {
                content: "";
                position: fixed;
                inset: 0;
                pointer-events: none;
                background:
                    radial-gradient(circle at 20% 24%, rgba(218, 183, 99, 0.18), transparent 34%),
                    radial-gradient(circle at 82% 18%, rgba(58, 191, 184, 0.12), transparent 32%),
                    linear-gradient(90deg, rgba(8, 17, 31, 0.94) 0%, rgba(8, 17, 31, 0.68) 42%, rgba(8, 17, 31, 0.72) 100%);
                z-index: 1;
            }

            body::after {
                content: "";
                position: fixed;
                inset: -24px;
                pointer-events: none;
                background: url('assets/images/backgound/electric-car-auth-bg.jpg') center 58% / cover no-repeat;
                transform: scale(1);
                animation: backgroundDrift 18s ease-in-out infinite alternate;
                z-index: 0;
            }

            .reset-shell {
                width: min(1120px, 100%);
                display: grid;
                grid-template-columns: minmax(0, 1fr) 450px;
                gap: 56px;
                align-items: stretch;
                animation: shellReveal 0.8s ease-out both;
                position: relative;
                z-index: 2;
            }

            .reset-shell::before,
            .reset-shell::after {
                content: "";
                position: fixed;
                inset: 0;
                pointer-events: none;
                z-index: -1;
            }

            .reset-shell::before {
                background:
                    radial-gradient(ellipse at 48% 77%, rgba(210, 235, 255, 0.26), transparent 20%),
                    radial-gradient(ellipse at 58% 78%, rgba(248, 223, 157, 0.18), transparent 24%),
                    radial-gradient(ellipse at 50% 88%, rgba(58, 191, 184, 0.12), transparent 30%);
                mix-blend-mode: screen;
                animation: headlightPulse 4.8s ease-in-out infinite;
            }

            .reset-shell::after {
                inset: -18%;
                background: linear-gradient(108deg,
                    transparent 0%,
                    transparent 42%,
                    rgba(255, 255, 255, 0.0) 45%,
                    rgba(255, 255, 255, 0.18) 49%,
                    rgba(248, 223, 157, 0.13) 51%,
                    rgba(255, 255, 255, 0.0) 56%,
                    transparent 100%);
                mix-blend-mode: screen;
                transform: translateX(-48%) rotate(0.001deg);
                animation: lightSweep 8.5s ease-in-out infinite;
            }

            .brand-panel,
            .reset-container {
                border-radius: 8px;
                overflow: hidden;
            }

            .brand-panel {
                position: relative;
                color: white;
                padding: 42px 0 42px 10px;
                min-height: 520px;
                display: flex;
                flex-direction: column;
                justify-content: center;
                overflow: visible;
                animation: brandReveal 0.9s ease-out 0.12s both;
            }

            .brand-panel::after {
                content: "";
                position: absolute;
                right: -70px;
                bottom: 30px;
                width: 220px;
                height: 220px;
                border-radius: 50%;
                border: 1px solid rgba(218, 183, 99, 0.22);
                animation: haloFloat 7s ease-in-out infinite alternate;
            }

            .brand-mark {
                display: inline-flex;
                align-items: center;
                gap: 10px;
                width: fit-content;
                padding: 10px 14px;
                border: 1px solid rgba(248, 223, 157, 0.25);
                border-radius: 999px;
                background: rgba(8, 17, 31, 0.42);
                box-shadow: 0 14px 40px rgba(0, 0, 0, 0.22);
                font-size: 21px;
                font-weight: 800;
                margin-bottom: 38px;
                color: #f8df9d;
            }

            .brand-kicker {
                width: fit-content;
                margin-bottom: 18px;
                padding-left: 14px;
                border-left: 3px solid #f8df9d;
                color: rgba(248, 223, 157, 0.88);
                font-size: 13px;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: 3px;
            }

            .brand-title {
                max-width: 560px;
                font-size: 54px;
                line-height: 1.08;
                margin-bottom: 18px;
                letter-spacing: 0;
                text-shadow: 0 18px 44px rgba(0, 0, 0, 0.42);
            }

            .brand-title .accent {
                display: block;
                color: #f8df9d;
            }

            .brand-copy {
                max-width: 460px;
                color: rgba(248, 250, 252, 0.78);
                line-height: 1.7;
                font-size: 16px;
            }

            .brand-points {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                margin-top: 28px;
            }

            .brand-points span {
                padding: 9px 12px;
                border-radius: 999px;
                background: rgba(255, 255, 255, 0.08);
                border: 1px solid rgba(255, 255, 255, 0.12);
                color: rgba(248, 250, 252, 0.82);
                font-size: 13px;
                font-weight: 700;
                backdrop-filter: blur(10px);
            }

            .reset-container {
                background: linear-gradient(180deg, rgba(12, 22, 38, 0.88), rgba(8, 17, 31, 0.82));
                border: 1px solid rgba(248, 223, 157, 0.26);
                box-shadow: 0 32px 90px rgba(0, 0, 0, 0.48);
                backdrop-filter: blur(18px);
                padding: 42px;
                align-self: center;
                position: relative;
                animation: cardReveal 0.85s ease-out 0.22s both;
            }

            .reset-container::before {
                content: "";
                position: absolute;
                inset: 0;
                background: linear-gradient(115deg, transparent 0%, rgba(248, 223, 157, 0.11) 42%, transparent 58%);
                transform: translateX(-120%);
                animation: cardSheen 5.8s ease-in-out infinite;
                pointer-events: none;
            }

            .reset-container > * {
                position: relative;
            }

            .reset-container h1 {
                color: #f8fafc;
                margin-bottom: 10px;
                text-align: center;
                font-size: 28px;
                font-weight: 800;
            }

            .subtitle {
                color: rgba(248, 250, 252, 0.62);
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
                background: rgba(255, 255, 255, 0.08);
                border: 1px solid rgba(255, 255, 255, 0.1);
                transition: transform 0.25s, background 0.25s, color 0.25s;
            }

            .step.active {
                color: #09111f;
                background: #f8df9d;
                border-color: rgba(218, 183, 99, 0.55);
                transform: translateY(-1px);
            }

            .form-group {
                margin-bottom: 20px;
            }

            label {
                display: block;
                color: rgba(248, 250, 252, 0.9);
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
                background: rgba(255, 255, 255, 0.08);
                color: #f8fafc;
                font-size: 14px;
                transition: border-color 0.25s, box-shadow 0.25s, background 0.25s, transform 0.25s;
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
                background: rgba(255, 255, 255, 0.12);
                transform: translateY(-1px);
            }

            input::placeholder {
                color: rgba(248, 250, 252, 0.48);
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
                transition: transform 0.25s, box-shadow 0.25s, filter 0.25s;
            }

            .reset-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 16px 36px rgba(180, 122, 31, 0.3);
                filter: brightness(1.04);
            }

            @keyframes backgroundDrift {
                from { transform: scale(1) translate3d(0, 0, 0); }
                to { transform: scale(1.045) translate3d(-12px, -10px, 0); }
            }

            @keyframes shellReveal {
                from { opacity: 0; transform: translateY(18px); }
                to { opacity: 1; transform: translateY(0); }
            }

            @keyframes brandReveal {
                from { opacity: 0; transform: translateX(-26px); }
                to { opacity: 1; transform: translateX(0); }
            }

            @keyframes cardReveal {
                from { opacity: 0; transform: translateX(24px) scale(0.98); }
                to { opacity: 1; transform: translateX(0) scale(1); }
            }

            @keyframes haloFloat {
                from { transform: translateY(0) scale(1); opacity: 0.55; }
                to { transform: translateY(-14px) scale(1.04); opacity: 0.9; }
            }

            @keyframes cardSheen {
                0%, 58% { transform: translateX(-120%); }
                78%, 100% { transform: translateX(120%); }
            }

            @keyframes headlightPulse {
                0%, 100% { opacity: 0.38; filter: blur(2px); transform: scale(1); }
                48% { opacity: 0.78; filter: blur(5px); transform: scale(1.05); }
            }

            @keyframes lightSweep {
                0%, 46% { opacity: 0; transform: translateX(-55%) rotate(0.001deg); }
                58% { opacity: 0.65; }
                76%, 100% { opacity: 0; transform: translateX(55%) rotate(0.001deg); }
            }

            @media (prefers-reduced-motion: reduce) {
                *, *::before, *::after {
                    animation-duration: 0.01ms !important;
                    animation-iteration-count: 1 !important;
                    scroll-behavior: auto !important;
                }
            }

            .footer-links {
                text-align: center;
                margin-top: 22px;
                font-size: 14px;
                color: rgba(248, 250, 252, 0.62);
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

                .brand-title {
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
                <div class="brand-kicker">Secure Account Recovery</div>
                <h2 class="brand-title">Khôi phục truy cập <span class="accent">nhanh và an toàn.</span></h2>
                <p class="brand-copy">Xác minh email bằng OTP và tạo mật khẩu mới để tiếp tục quản lý ví, thuê xe điện và giao dịch.</p>
                <div class="brand-points">
                    <span>OTP bảo mật</span>
                    <span>3 bước rõ ràng</span>
                    <span>Cập nhật nhanh</span>
                </div>
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
                    <a href="<%= request.getContextPath() %>?action=login">Quay lại đăng nhập</a>
                </div>
            </div>
        </div>
    </body>
</html>
