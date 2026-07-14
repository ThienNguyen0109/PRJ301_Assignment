<%--
    Document   : login
    Created on : May 28, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ include file="/WEB-INF/jspf/customer-i18n.jspf" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng Nhập - E-Vehicle Rental</title>
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }

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
                background: url('<%= request.getContextPath() %>/assets/images/backgound/electric-car-auth-bg.jpg') center 58% / cover no-repeat;
                transform: scale(1);
                animation: backgroundDrift 18s ease-in-out infinite alternate;
                z-index: 0;
            }

            .login-shell {
                width: min(1120px, 100%);
                display: grid;
                grid-template-columns: minmax(0, 1fr) 420px;
                gap: 56px;
                align-items: stretch;
                animation: shellReveal 0.8s ease-out both;
                position: relative;
                z-index: 2;
            }

            .login-shell::before,
            .login-shell::after {
                content: "";
                position: fixed;
                inset: 0;
                pointer-events: none;
                z-index: -1;
            }

            .login-shell::before {
                background:
                    radial-gradient(ellipse at 48% 77%, rgba(210, 235, 255, 0.26), transparent 20%),
                    radial-gradient(ellipse at 58% 78%, rgba(248, 223, 157, 0.18), transparent 24%),
                    radial-gradient(ellipse at 50% 88%, rgba(58, 191, 184, 0.12), transparent 30%);
                mix-blend-mode: screen;
                animation: headlightPulse 4.8s ease-in-out infinite;
            }

            .login-shell::after {
                inset: -18%;
                background: linear-gradient(108deg, transparent 0%, transparent 42%, rgba(255,255,255,0) 45%, rgba(255,255,255,0.18) 49%, rgba(248,223,157,0.13) 51%, rgba(255,255,255,0) 56%, transparent 100%);
                mix-blend-mode: screen;
                transform: translateX(-48%) rotate(0.001deg);
                animation: lightSweep 8.5s ease-in-out infinite;
            }

            .brand-panel,
            .login-container { border-radius: 8px; overflow: hidden; }

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

            .brand-title .accent { display: block; color: #f8df9d; }

            .brand-copy {
                max-width: 460px;
                color: rgba(248, 250, 252, 0.78);
                line-height: 1.7;
                font-size: 16px;
            }

            .brand-points { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 28px; }
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

            .login-container {
                background: linear-gradient(180deg, rgba(12, 22, 38, 0.88), rgba(8, 17, 31, 0.82));
                border: 1px solid rgba(248, 223, 157, 0.26);
                box-shadow: 0 32px 90px rgba(0, 0, 0, 0.48);
                backdrop-filter: blur(18px);
                padding: 44px;
                align-self: center;
                position: relative;
                animation: cardReveal 0.85s ease-out 0.22s both;
            }

            .login-container::before {
                content: "";
                position: absolute;
                inset: 0;
                background: linear-gradient(115deg, transparent 0%, rgba(248, 223, 157, 0.11) 42%, transparent 58%);
                transform: translateX(-120%);
                animation: cardSheen 5.8s ease-in-out infinite;
                pointer-events: none;
            }

            .login-container > * { position: relative; }
            .login-container h1 { color: #f8fafc; margin-bottom: 10px; text-align: center; font-size: 28px; font-weight: 800; }
            .login-container .subtitle { color: rgba(248,250,252,.62); text-align: center; margin-bottom: 30px; font-size: 14px; }
            .form-group { margin-bottom: 20px; }
            label { display: block; color: rgba(248,250,252,.9); font-weight: 700; margin-bottom: 8px; font-size: 14px; }

            input[type="email"], input[type="password"] {
                width: 100%;
                padding: 13px 14px;
                border: 1px solid rgba(255, 255, 255, 0.14);
                border-radius: 7px;
                background: rgba(255, 255, 255, 0.08);
                color: #f8fafc;
                font-size: 14px;
                transition: border-color .25s, box-shadow .25s, background .25s, transform .25s;
            }

            input[type="email"]:focus, input[type="password"]:focus {
                outline: none;
                border-color: rgba(214, 169, 78, .85);
                box-shadow: 0 0 0 4px rgba(214, 169, 78, .16);
                background: rgba(255,255,255,.12);
                transform: translateY(-1px);
            }

            input::placeholder { color: rgba(248,250,252,.48); }

            .error-message, .success-message {
                padding: 12px 14px;
                border-radius: 7px;
                margin-bottom: 20px;
                font-size: 14px;
                line-height: 1.5;
            }

            .error-message { color: #7f1d1d; background-color: #fee2e2; border: 1px solid #fecaca; }
            .success-message { color: #14532d; background-color: #dcfce7; border: 1px solid #bbf7d0; }

            .login-btn, .google-login-btn {
                width: 100%;
                min-height: 48px;
                border-radius: 7px;
                font-size: 16px;
                font-weight: 800;
                cursor: pointer;
                transition: transform .25s, box-shadow .25s, filter .25s;
            }

            .login-btn {
                padding: 13px;
                color: #09111f;
                border: 1px solid rgba(218, 183, 99, .55);
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                box-shadow: 0 12px 28px rgba(180, 122, 31, .2);
            }

            .login-btn:hover, .google-login-btn:hover { transform: translateY(-2px); box-shadow: 0 16px 36px rgba(0,0,0,.3); }

            .divider { display: flex; align-items: center; gap: 12px; margin: 20px 0; color: rgba(248,250,252,.56); font-size: 13px; font-weight: 700; }
            .divider::before, .divider::after { content: ""; flex: 1; height: 1px; background: rgba(255,255,255,.14); }

            .google-login-btn {
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
                color: #111827;
                background: #fff;
                border: 1px solid rgba(255,255,255,.2);
                text-decoration: none;
            }

            .google-icon {
                width: 20px;
                height: 20px;
                display: inline-grid;
                place-items: center;
                border-radius: 50%;
                color: #fff;
                background: conic-gradient(from -45deg, #4285f4 0 25%, #34a853 0 50%, #fbbc05 0 75%, #ea4335 0);
                font-size: 12px;
                font-weight: 900;
            }

            .helper-row { text-align: right; margin-top: -8px; margin-bottom: 20px; font-size: 14px; }
            .footer-links { text-align: center; margin-top: 22px; font-size: 14px; color: rgba(248,250,252,.62); }
            .footer-links a, .helper-row a { color: #f8df9d; font-weight: 800; text-decoration: none; }
            .footer-links a:hover, .helper-row a:hover { text-decoration: underline; }

            @keyframes backgroundDrift { from { transform: scale(1) translate3d(0,0,0); } to { transform: scale(1.045) translate3d(-12px,-10px,0); } }
            @keyframes shellReveal { from { opacity: 0; transform: translateY(18px); } to { opacity: 1; transform: translateY(0); } }
            @keyframes brandReveal { from { opacity: 0; transform: translateX(-26px); } to { opacity: 1; transform: translateX(0); } }
            @keyframes cardReveal { from { opacity: 0; transform: translateX(24px) scale(.98); } to { opacity: 1; transform: translateX(0) scale(1); } }
            @keyframes haloFloat { from { transform: translateY(0) scale(1); opacity: .55; } to { transform: translateY(-14px) scale(1.04); opacity: .9; } }
            @keyframes cardSheen { 0%, 58% { transform: translateX(-120%); } 78%, 100% { transform: translateX(120%); } }
            @keyframes headlightPulse { 0%, 100% { opacity: .38; filter: blur(2px); transform: scale(1); } 48% { opacity: .78; filter: blur(5px); transform: scale(1.05); } }
            @keyframes lightSweep { 0%, 46% { opacity: 0; transform: translateX(-55%) rotate(.001deg); } 58% { opacity: .65; } 76%, 100% { opacity: 0; transform: translateX(55%) rotate(.001deg); } }

            @media (prefers-reduced-motion: reduce) { *, *::before, *::after { animation-duration: .01ms !important; animation-iteration-count: 1 !important; scroll-behavior: auto !important; } }

            .language-switch {
                position: absolute;
                top: 18px;
                right: 18px;
                display: inline-flex;
                gap: 4px;
                padding: 4px;
                border-radius: 999px;
                background: rgba(255,255,255,.12);
                border: 1px solid rgba(255,255,255,.2);
            }
            .language-switch a {
                min-width: 34px;
                padding: 7px 9px;
                border-radius: 999px;
                color: rgba(248,250,252,.78);
                text-decoration: none;
                text-align: center;
                font-size: 12px;
                font-weight: 900;
            }
            .login-container .language-switch a { color: #334155; }
            .language-switch a.active {
                color: #09111f;
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
            }
            @media (max-width: 860px) { .login-shell { grid-template-columns: 1fr; } .brand-panel { min-height: auto; } .brand-mark { margin-bottom: 34px; } }
            @media (max-width: 520px) { body { padding: 16px; } .brand-panel, .login-container { padding: 26px; } .brand-title { font-size: 30px; } }

            .auth-bg-video {
                position: fixed;
                inset: 0;
                width: 100%;
                height: 100%;
                object-fit: cover;
                z-index: 0;
                filter: saturate(1.08) contrast(1.06);
            }

            body {
                padding: 32px;
                background: #f7f8fb;
            }

            body::before {
                background:
                    radial-gradient(circle at 72% 24%, rgba(248, 223, 157, 0.22), transparent 28%),
                    linear-gradient(90deg, rgba(7, 17, 31, 0.92) 0%, rgba(7, 17, 31, 0.72) 45%, rgba(255, 255, 255, 0.52) 100%);
            }

            body::after {
                display: none;
            }

            .login-shell {
                width: min(1180px, 100%);
                grid-template-columns: minmax(0, 1.15fr) 450px;
                gap: 34px;
                min-height: min(720px, calc(100vh - 64px));
                align-items: center;
            }

            .brand-panel {
                min-height: 620px;
                padding: clamp(36px, 5vw, 64px);
                border-radius: 24px;
                background:
                    radial-gradient(circle at 42% 22%, rgba(43, 114, 190, .16), transparent 30%),
                    radial-gradient(circle at 70% 78%, rgba(248, 223, 157, .12), transparent 34%),
                    linear-gradient(135deg, rgba(0, 5, 13, .78), rgba(2, 8, 20, .66));
                border: 1px solid rgba(248, 223, 157, 0.28);
                box-shadow: 0 32px 90px rgba(0, 0, 0, 0.28);
                backdrop-filter: blur(16px) saturate(1.08);
                -webkit-backdrop-filter: blur(16px) saturate(1.08);
            }

            .brand-mark {
                gap: 14px;
                padding: 0;
                color: #f8df9d;
                border: 0;
                background: transparent;
                box-shadow: none;
            }

            .brand-mark img {
                width: min(540px, 86vw);
                height: 178px;
                object-fit: cover;
                padding: 0;
                border-radius: 18px;
                background: #00050d;
                filter: drop-shadow(0 20px 34px rgba(0, 0, 0, .22));
            }

            .brand-title {
                max-width: 650px;
                font-size: clamp(46px, 6vw, 74px);
                letter-spacing: 0;
            }

            .brand-copy {
                color: rgba(248,250,252,.84);
                font-size: 17px;
            }

            .login-container {
                background: rgba(255,255,255,.66);
                border: 1px solid rgba(255,255,255,.46);
                box-shadow: 0 28px 80px rgba(15,23,42,.24);
                backdrop-filter: blur(20px) saturate(1.1);
                -webkit-backdrop-filter: blur(20px) saturate(1.1);
                color: #101827;
            }

            .login-container::before {
                background: linear-gradient(115deg, transparent 0%, rgba(248, 223, 157, 0.26) 42%, transparent 58%);
            }

            .login-container h1 {
                color: #101827;
            }

            .login-container .subtitle,
            .footer-links {
                color: #64748b;
            }

            label {
                color: #101827;
            }

            input[type="email"],
            input[type="password"] {
                color: #101827;
                background: #fff;
                border-color: rgba(16,24,39,.14);
            }

            input[type="email"]:focus,
            input[type="password"]:focus {
                background: #fff;
            }

            input::placeholder {
                color: #94a3b8;
            }

            .helper-row a,
            .footer-links a {
                color: #a86f08;
            }

            .divider {
                color: #64748b;
            }

            .divider::before,
            .divider::after {
                background: rgba(16,24,39,.1);
            }

            .google-login-btn {
                color: #101827;
                border-color: rgba(16,24,39,.12);
                box-shadow: 0 12px 30px rgba(15,23,42,.08);
            }


            .language-switch {
                position: absolute;
                top: 18px;
                right: 18px;
                display: inline-flex;
                gap: 4px;
                padding: 4px;
                border-radius: 999px;
                background: rgba(255,255,255,.12);
                border: 1px solid rgba(255,255,255,.2);
            }
            .language-switch a {
                min-width: 34px;
                padding: 7px 9px;
                border-radius: 999px;
                color: rgba(248,250,252,.78);
                text-decoration: none;
                text-align: center;
                font-size: 12px;
                font-weight: 900;
            }
            .login-container .language-switch a { color: #334155; }
            .language-switch a.active {
                color: #09111f;
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
            }
            @media (max-width: 860px) {
                .login-shell {
                    grid-template-columns: 1fr;
                }

                .brand-panel {
                    min-height: 430px;
                }
            }
        </style>
    </head>
    <body>
        <video class="auth-bg-video" autoplay muted loop playsinline aria-hidden="true">
            <source src="<%= request.getContextPath() %>/assets/video/istockphoto-902026438-640_adpp_is.mp4" type="video/mp4">
        </video>
        <div class="login-shell">
            <section class="brand-panel">
                <div class="brand-mark">
                    <img src="<%= request.getContextPath() %>/assets/images/logo/logo.png" alt="E-Vehicle Rental">
                </div>
                <div class="brand-kicker"><fmt:message key="auth.kicker"/></div>
                <h2 class="brand-title"><fmt:message key="auth.titleLine1"/> <span class="accent"><fmt:message key="auth.titleLine2"/></span></h2>
                <p class="brand-copy"><fmt:message key="auth.copy"/></p>
                <div class="brand-points">
                    <span><fmt:message key="auth.pointWallet"/></span>
                    <span><fmt:message key="auth.pointBooking"/></span>
                    <span><fmt:message key="auth.pointStation"/></span>
                </div>
            </section>

            <div class="login-container">
                <%@ include file="/WEB-INF/jspf/customer-language-switch.jspf" %>
                <h1><fmt:message key="auth.loginTitle"/></h1>
                <p class="subtitle"><fmt:message key="auth.subtitle"/></p>

                <%
                    Object successMsg = session.getAttribute("registrationSuccess");
                    if (successMsg != null) {
                %>
                    <div class="success-message"><%= successMsg %></div>
                <%
                        session.removeAttribute("registrationSuccess");
                    }
                %>

                <%
                    Object loginError = session.getAttribute("loginError");
                    if (loginError != null) {
                %>
                    <div class="error-message"><%= loginError %></div>
                <%
                        session.removeAttribute("loginError");
                    }
                %>

                <%
                    String error = (String) request.getAttribute("error");
                    if (error != null && !error.isEmpty()) {
                %>
                    <div class="error-message"><%= error %></div>
                <% } %>

                <form action="login" method="POST" accept-charset="UTF-8">
                    <div class="form-group">
                        <label for="email"><fmt:message key="auth.email"/></label>
                        <input type="email" id="email" name="email" placeholder="Email" required value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>">
                    </div>

                    <div class="form-group">
                        <label for="password"><fmt:message key="auth.password"/></label>
                        <input type="password" id="password" name="password" placeholder="Password" required>
                    </div>

                    <div class="helper-row">
                        <a href="<%= request.getContextPath() %>?action=reset-password"><fmt:message key="auth.forgotPassword"/></a>
                    </div>

                    <button type="submit" class="login-btn"><fmt:message key="auth.loginButton"/></button>
                </form>

                <div class="divider"><fmt:message key="auth.or"/></div>

                <a class="google-login-btn" href="<%= request.getContextPath() %>?action=google-login">
                    <span class="google-icon">G</span>
                    <fmt:message key="auth.googleLogin"/>
                </a>

                <div class="footer-links">
                    <a href="<%= request.getContextPath() %>?action=register"><fmt:message key="auth.registerLink"/></a>
                </div>
            </div>
        </div>
    </body>
</html>
