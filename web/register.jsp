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

            .auth-shell {
                width: min(1160px, 100%);
                display: grid;
                grid-template-columns: minmax(0, 1fr) 500px;
                gap: 56px;
                align-items: stretch;
                animation: shellReveal 0.8s ease-out both;
                position: relative;
                z-index: 2;
            }

            .auth-shell::before,
            .auth-shell::after {
                content: "";
                position: fixed;
                inset: 0;
                pointer-events: none;
                z-index: -1;
            }

            .auth-shell::before {
                background:
                    radial-gradient(ellipse at 48% 77%, rgba(210, 235, 255, 0.26), transparent 20%),
                    radial-gradient(ellipse at 58% 78%, rgba(248, 223, 157, 0.18), transparent 24%),
                    radial-gradient(ellipse at 50% 88%, rgba(58, 191, 184, 0.12), transparent 30%);
                mix-blend-mode: screen;
                animation: headlightPulse 4.8s ease-in-out infinite;
            }

            .auth-shell::after {
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
            .register-container {
                border-radius: 8px;
                overflow: hidden;
            }

            .brand-panel {
                position: relative;
                color: white;
                padding: 42px 0 42px 10px;
                min-height: 590px;
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

            .register-container {
                background: linear-gradient(180deg, rgba(12, 22, 38, 0.88), rgba(8, 17, 31, 0.82));
                border: 1px solid rgba(248, 223, 157, 0.26);
                box-shadow: 0 32px 90px rgba(0, 0, 0, 0.48);
                backdrop-filter: blur(18px);
                padding: 38px;
                align-self: center;
                position: relative;
                animation: cardReveal 0.85s ease-out 0.22s both;
            }

            .register-container::before {
                content: "";
                position: absolute;
                inset: 0;
                background: linear-gradient(115deg, transparent 0%, rgba(248, 223, 157, 0.11) 42%, transparent 58%);
                transform: translateX(-120%);
                animation: cardSheen 5.8s ease-in-out infinite;
                pointer-events: none;
            }

            .register-container > * {
                position: relative;
            }

            .register-container h1 {
                color: #f8fafc;
                margin-bottom: 10px;
                text-align: center;
                font-size: 28px;
                font-weight: 800;
            }

            .register-container .subtitle {
                color: rgba(248, 250, 252, 0.62);
                text-align: center;
                margin-bottom: 28px;
                font-size: 14px;
            }

            .form-group {
                margin-bottom: 18px;
            }

            label {
                display: block;
                color: rgba(248, 250, 252, 0.9);
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
                background: rgba(255, 255, 255, 0.08);
                color: #f8fafc;
                font-size: 14px;
                transition: border-color 0.25s, box-shadow 0.25s, background 0.25s, transform 0.25s;
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
                transition: transform 0.25s, box-shadow 0.25s, filter 0.25s;
            }

            .register-btn:hover {
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

            .login-link {
                text-align: center;
                margin-top: 22px;
                font-size: 14px;
                color: rgba(248, 250, 252, 0.62);
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

                .brand-title {
                    font-size: 30px;
                }

                .password-group {
                    grid-template-columns: 1fr;
                    gap: 0;
                }
            }

            .auth-bg-video {
                position: fixed;
                inset: 0;
                width: 100%;
                height: 100%;
                object-fit: cover;
                z-index: 0;
                filter: saturate(1.08) contrast(1.06);
            }

            body::before {
                background:
                    radial-gradient(circle at 72% 24%, rgba(248, 223, 157, 0.22), transparent 28%),
                    linear-gradient(90deg, rgba(7, 17, 31, 0.92) 0%, rgba(7, 17, 31, 0.72) 45%, rgba(255, 255, 255, 0.52) 100%);
            }

            body::after { display: none; }

            .auth-shell {
                width: min(1180px, 100%);
                grid-template-columns: minmax(0, 1.05fr) 520px;
                gap: 34px;
                min-height: min(760px, calc(100vh - 64px));
                align-items: center;
            }

            .brand-panel {
                min-height: 650px;
                padding: clamp(36px, 5vw, 64px);
                border-radius: 24px;
                background:
                    linear-gradient(90deg, rgba(7, 17, 31, 0.82), rgba(7, 17, 31, 0.38)),
                    rgba(7, 17, 31, 0.22);
                border: 1px solid rgba(248, 223, 157, 0.2);
                box-shadow: 0 32px 90px rgba(0, 0, 0, 0.28);
                backdrop-filter: blur(5px);
            }

            .brand-mark {
                gap: 14px;
                padding: 0;
                border: 0;
                background: transparent;
                box-shadow: none;
            }

            .brand-mark img {
                width: 260px;
                height: 92px;
                object-fit: cover;
                padding: 0;
                border-radius: 18px;
                background: #061120;
            }

            .register-container {
                color: #101827;
                background: rgba(255,255,255,.94);
                border: 1px solid rgba(16,24,39,.08);
                box-shadow: 0 28px 80px rgba(15,23,42,.2);
            }

            .register-container h1,
            label { color: #101827; }

            .register-container .subtitle,
            .login-link { color: #64748b; }

            input[type="text"],
            input[type="email"],
            input[type="password"],
            input[type="tel"] {
                color: #101827;
                background: #fff;
            }

            input::placeholder { color: #94a3b8; }
        </style>
    </head>
    <body>
        <video class="auth-bg-video" autoplay muted loop playsinline aria-hidden="true">
            <source src="<%= request.getContextPath() %>/assets/video/istockphoto-902026438-640_adpp_is.mp4" type="video/mp4">
        </video>
        <div class="auth-shell">
            <section class="brand-panel">
                <div class="brand-mark">
                    <img src="<%= request.getContextPath() %>/assets/images/logo/logo.png" alt="E-Vehicle Rental">
                </div>
                <div class="brand-kicker">Start Your Electric Journey</div>
                <h2 class="brand-title">Tài khoản mới <span class="accent">cho hành trình xanh.</span></h2>
                <p class="brand-copy">Đăng ký để nạp ví, thuê xe điện và quản lý lịch sử giao dịch trong một trải nghiệm nhanh gọn.</p>
                <div class="brand-points">
                    <span>Xác minh OTP</span>
                    <span>Quản lý ví</span>
                    <span>Ưu đãi thuê xe</span>
                </div>
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
                    Đã có tài khoản? <a href="<%= request.getContextPath() %>?action=login">Đăng Nhập</a>
                </div>
            </div>
        </div>
    </body>
</html>
