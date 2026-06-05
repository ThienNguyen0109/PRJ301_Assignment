<%-- 
    Document   : home
    Created on : June 5, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.Account"%>
<%@page import="models.Wallet"%>
<%@page import="daos.WalletDAO"%>
<%@page import="java.text.DecimalFormat"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Trang Chủ - E-Vehicle Rental</title>
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
                background:
                    radial-gradient(circle at 12% 8%, rgba(205, 164, 82, 0.16), transparent 28%),
                    radial-gradient(circle at 88% 18%, rgba(58, 191, 184, 0.14), transparent 30%),
                    linear-gradient(135deg, #08111f 0%, #111a2c 38%, #f4f0e8 38%, #f8f6f2 100%);
                background-attachment: fixed;
            }

            .navbar {
                position: sticky;
                top: 0;
                z-index: 10;
                color: white;
                padding: 18px 38px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                background: rgba(9, 17, 31, 0.9);
                border-bottom: 1px solid rgba(218, 183, 99, 0.32);
                box-shadow: 0 18px 45px rgba(5, 10, 18, 0.24);
                backdrop-filter: blur(18px);
            }

            .navbar h1 {
                font-size: 25px;
                font-weight: 800;
                letter-spacing: 0;
            }

            .navbar-menu {
                display: flex;
                gap: 12px;
                align-items: center;
            }

            .navbar a {
                color: white;
                text-decoration: none;
                padding: 10px 16px;
                border-radius: 7px;
                font-weight: 600;
                transition: background 0.25s, color 0.25s, transform 0.25s;
            }

            .navbar a:hover {
                color: #f0d28a;
                background: rgba(255, 255, 255, 0.08);
                transform: translateY(-1px);
            }

            .logout-btn {
                background: linear-gradient(135deg, #d14f54 0%, #f28b61 100%);
                box-shadow: 0 12px 28px rgba(209, 79, 84, 0.28);
            }

            .logout-btn:hover {
                color: white;
                background: linear-gradient(135deg, #c94349 0%, #ec744d 100%);
            }

            .container {
                max-width: 1280px;
                margin: 34px auto;
                padding: 0 28px;
                display: grid;
                grid-template-columns: minmax(0, 1fr) 380px;
                gap: 24px;
            }

            .main-content,
            .user-card,
            .wallet-card {
                border-radius: 8px;
                border: 1px solid rgba(218, 183, 99, 0.2);
                box-shadow: 0 22px 60px rgba(8, 17, 31, 0.14);
            }

            .main-content {
                position: relative;
                overflow: hidden;
                padding: 34px;
                background: rgba(255, 255, 255, 0.92);
            }

            .main-content::before {
                content: "";
                position: absolute;
                inset: 0;
                pointer-events: none;
                background: linear-gradient(135deg, rgba(218, 183, 99, 0.16), transparent 32%, rgba(58, 191, 184, 0.09));
            }

            .main-content > * {
                position: relative;
            }

            .section-title {
                color: #111827;
                margin-bottom: 18px;
                padding-bottom: 13px;
                border-bottom: 1px solid rgba(17, 24, 39, 0.12);
                font-size: 27px;
                font-weight: 800;
            }

            .welcome-copy {
                color: #566070;
                margin-bottom: 30px;
                font-size: 16px;
                line-height: 1.7;
            }

            .product-grid {
                display: grid;
                grid-template-columns: repeat(3, minmax(0, 1fr));
                gap: 18px;
            }

            .product-card {
                padding: 20px 18px;
                text-align: center;
                background: linear-gradient(180deg, #ffffff 0%, #f7f4ee 100%);
                border: 1px solid rgba(17, 24, 39, 0.1);
                border-radius: 8px;
                transition: transform 0.25s, box-shadow 0.25s, border-color 0.25s;
            }

            .product-card:hover {
                transform: translateY(-6px);
                border-color: rgba(218, 183, 99, 0.58);
                box-shadow: 0 18px 38px rgba(8, 17, 31, 0.16);
            }

            .product-card h3 {
                color: #172033;
                margin-bottom: 10px;
                font-size: 21px;
            }

            .product-card p {
                color: #7d8794;
                font-size: 13px;
                margin-bottom: 10px;
            }

            .product-price {
                color: #b47a1f;
                font-size: 20px;
                font-weight: 800;
                margin: 12px 0;
            }

            .rent-btn {
                width: 100%;
                padding: 11px 12px;
                color: #09111f;
                border: 1px solid rgba(218, 183, 99, 0.55);
                border-radius: 7px;
                cursor: pointer;
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                font-weight: 800;
                box-shadow: 0 10px 24px rgba(180, 122, 31, 0.18);
                transition: transform 0.25s, box-shadow 0.25s;
            }

            .rent-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 14px 30px rgba(180, 122, 31, 0.28);
            }

            .sidebar {
                display: flex;
                flex-direction: column;
                gap: 20px;
            }

            .user-card {
                padding: 24px;
                background: rgba(255, 255, 255, 0.94);
            }

            .user-card h3,
            .wallet-card h3 {
                margin-bottom: 15px;
                padding-bottom: 12px;
                font-size: 22px;
                font-weight: 800;
            }

            .user-card h3 {
                color: #111827;
                border-bottom: 1px solid rgba(17, 24, 39, 0.12);
            }

            .user-info {
                font-size: 15px;
                color: #566070;
                line-height: 2;
            }

            .user-info strong {
                color: #111827;
            }

            .wallet-card {
                position: relative;
                overflow: hidden;
                color: white;
                padding: 26px;
                background:
                    linear-gradient(135deg, rgba(15, 28, 49, 0.96), rgba(47, 61, 91, 0.95)),
                    linear-gradient(135deg, #d6a94e, #3abfb8);
            }

            .wallet-card::after {
                content: "";
                position: absolute;
                right: -46px;
                top: -46px;
                width: 160px;
                height: 160px;
                border: 1px solid rgba(218, 183, 99, 0.36);
                border-radius: 50%;
            }

            .wallet-card > * {
                position: relative;
            }

            .wallet-card h3 {
                border-bottom: 1px solid rgba(255, 255, 255, 0.18);
            }

            .wallet-label {
                font-size: 13px;
                opacity: 0.84;
            }

            .wallet-balance {
                color: #f8df9d;
                font-size: 28px;
                font-weight: 800;
                margin: 16px 0;
            }

            .wallet-card a {
                display: inline-block;
                color: #111827;
                background: #ffffff;
                padding: 11px 18px;
                border-radius: 7px;
                text-decoration: none;
                font-weight: 800;
                margin-top: 12px;
                box-shadow: 0 12px 28px rgba(0, 0, 0, 0.18);
                transition: transform 0.25s, box-shadow 0.25s;
            }

            .wallet-card a:hover {
                transform: translateY(-2px);
                box-shadow: 0 16px 34px rgba(0, 0, 0, 0.24);
            }

            @media (max-width: 980px) {
                .container {
                    grid-template-columns: 1fr;
                }

                .product-grid {
                    grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
                }
            }

            @media (max-width: 640px) {
                .navbar {
                    padding: 14px 18px;
                    align-items: flex-start;
                    gap: 12px;
                    flex-direction: column;
                }

                .navbar-menu {
                    width: 100%;
                    justify-content: space-between;
                }

                .container {
                    margin: 22px auto;
                    padding: 0 16px;
                }

                .main-content,
                .user-card,
                .wallet-card {
                    padding: 22px;
                }
            }
        </style>
    </head>
    <body>
        <%
            Object userObj = session.getAttribute("user");
            if (userObj == null) {
                response.sendRedirect(request.getContextPath() + "?page=login");
                return;
            }

            Account user = (Account) userObj;
            String userName = (String) session.getAttribute("userName");
            String userEmail = (String) session.getAttribute("userEmail");
            WalletDAO walletDAO = new WalletDAO();
            Wallet wallet = walletDAO.getWalletByAccountId(user.getAccountId());
            DecimalFormat df = new DecimalFormat("#,##0.00");
        %>

        <div class="navbar">
            <h1>🚗 E-Vehicle Rental System</h1>
            <div class="navbar-menu">
                <a href="<%= request.getContextPath() %>?page=home">Trang Chủ</a>
                <a href="<%= request.getContextPath() %>?page=wallet">Ví</a>
                <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Logout</a>
            </div>
        </div>

        <div class="container">
            <div class="main-content">
                <h2 class="section-title">🏠 Chào mừng bạn</h2>
                <p class="welcome-copy">
                    Khám phá các dịch vụ cho thuê xe điện cao cấp với trải nghiệm nhanh, sạch và hiện đại.
                </p>

                <h2 class="section-title">📋 Các xe có sẵn</h2>
                <div class="product-grid">
                    <div class="product-card">
                        <h3>🔋 Tesla Model 3</h3>
                        <p>Điện, 450km quãng đường</p>
                        <div class="product-price">500,000 VND/ngày</div>
                        <button class="rent-btn">Thuê ngay</button>
                    </div>

                    <div class="product-card">
                        <h3>⚡ VinFast VF e34</h3>
                        <p>Điện, 300km quãng đường</p>
                        <div class="product-price">400,000 VND/ngày</div>
                        <button class="rent-btn">Thuê ngay</button>
                    </div>

                    <div class="product-card">
                        <h3>🚘 BMW i4</h3>
                        <p>Điện, 520km quãng đường</p>
                        <div class="product-price">600,000 VND/ngày</div>
                        <button class="rent-btn">Thuê ngay</button>
                    </div>
                </div>
            </div>

            <div class="sidebar">
                <div class="user-card">
                    <h3>👤 Thông Tin Tài Khoản</h3>
                    <div class="user-info">
                        <div><strong>Tên:</strong> <%= userName %></div>
                        <div><strong>Email:</strong> <%= userEmail %></div>
                        <div><strong>Trạng thái:</strong> Hoạt động</div>
                    </div>
                </div>

                <div class="wallet-card">
                    <h3>💳 Ví của bạn</h3>
                    <p class="wallet-label">Số dư hiện tại</p>
                    <div class="wallet-balance" id="walletBalance">
                        <%= wallet != null ? df.format(wallet.getBalance()) + " VND" : "0 VND" %>
                    </div>
                    <a href="<%= request.getContextPath() %>?page=wallet">Quản lý ví →</a>
                </div>
            </div>
        </div>
    </body>
</html>
