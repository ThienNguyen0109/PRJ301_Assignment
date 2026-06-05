<%-- 
    Document   : wallet
    Created on : June 5, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="daos.WalletDAO"%>
<%@page import="daos.WalletTransactionDAO"%>
<%@page import="models.Wallet"%>
<%@page import="models.WalletTransaction"%>
<%@page import="models.Account"%>
<%@page import="java.util.List"%>
<%@page import="java.text.DecimalFormat"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Ví - E-Vehicle Rental</title>
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
                max-width: 1080px;
                margin: 34px auto;
                padding: 0 28px;
            }

            .balance-card,
            .section {
                border-radius: 8px;
                border: 1px solid rgba(218, 183, 99, 0.2);
                box-shadow: 0 22px 60px rgba(8, 17, 31, 0.14);
            }

            .balance-card {
                position: relative;
                overflow: hidden;
                color: white;
                padding: 34px;
                margin-bottom: 24px;
                background:
                    linear-gradient(135deg, rgba(15, 28, 49, 0.96), rgba(47, 61, 91, 0.95)),
                    linear-gradient(135deg, #d6a94e, #3abfb8);
            }

            .balance-card::after {
                content: "";
                position: absolute;
                right: -62px;
                top: -62px;
                width: 210px;
                height: 210px;
                border: 1px solid rgba(218, 183, 99, 0.36);
                border-radius: 50%;
            }

            .balance-card > * {
                position: relative;
            }

            .balance-label {
                font-size: 14px;
                opacity: 0.86;
                margin-bottom: 12px;
            }

            .balance-amount {
                color: #f8df9d;
                font-size: 42px;
                font-weight: 800;
                margin-bottom: 22px;
            }

            .topup-btn,
            .submit-btn {
                color: #09111f;
                border: 1px solid rgba(218, 183, 99, 0.55);
                border-radius: 7px;
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                font-weight: 800;
                cursor: pointer;
                box-shadow: 0 12px 28px rgba(180, 122, 31, 0.2);
                transition: transform 0.25s, box-shadow 0.25s;
            }

            .topup-btn {
                padding: 11px 20px;
            }

            .topup-btn:hover,
            .submit-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 16px 36px rgba(180, 122, 31, 0.3);
            }

            .section {
                position: relative;
                overflow: hidden;
                background: rgba(255, 255, 255, 0.92);
                padding: 24px;
                margin-bottom: 24px;
            }

            .section::before {
                content: "";
                position: absolute;
                inset: 0;
                pointer-events: none;
                background: linear-gradient(135deg, rgba(218, 183, 99, 0.12), transparent 36%, rgba(58, 191, 184, 0.08));
            }

            .section > * {
                position: relative;
            }

            .section h2 {
                color: #111827;
                margin-bottom: 22px;
                border-bottom: 1px solid rgba(17, 24, 39, 0.12);
                padding-bottom: 13px;
                font-size: 27px;
                font-weight: 800;
            }

            .form-group {
                margin-bottom: 17px;
            }

            label {
                display: block;
                color: #111827;
                font-weight: 700;
                margin-bottom: 8px;
                font-size: 14px;
            }

            input[type="number"] {
                width: 100%;
                padding: 13px 14px;
                border: 1px solid rgba(17, 24, 39, 0.14);
                border-radius: 7px;
                background: rgba(255, 255, 255, 0.84);
                color: #172033;
                font-size: 14px;
                transition: border-color 0.25s, box-shadow 0.25s, background 0.25s;
            }

            input[type="number"]:focus {
                outline: none;
                border-color: rgba(214, 169, 78, 0.85);
                box-shadow: 0 0 0 4px rgba(214, 169, 78, 0.16);
                background: #ffffff;
            }

            .preset-amounts {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 12px;
                margin-bottom: 16px;
            }

            .preset-btn {
                padding: 12px;
                background: rgba(255, 255, 255, 0.7);
                border: 1px solid rgba(218, 183, 99, 0.52);
                border-radius: 7px;
                color: #8f621b;
                font-weight: 800;
                cursor: pointer;
                transition: background 0.25s, transform 0.25s, box-shadow 0.25s;
            }

            .preset-btn:hover {
                color: #09111f;
                background: #f8df9d;
                transform: translateY(-2px);
                box-shadow: 0 12px 26px rgba(180, 122, 31, 0.16);
            }

            .submit-btn {
                width: 100%;
                padding: 13px;
                font-size: 16px;
            }

            .error-message,
            .success-message {
                padding: 12px 14px;
                border-radius: 7px;
                margin-bottom: 17px;
                line-height: 1.5;
            }

            .error-message {
                background-color: #fee2e2;
                color: #7f1d1d;
                border: 1px solid #fecaca;
                display: <%= request.getParameter("error") != null ? "block" : "none" %>;
            }

            .success-message {
                background-color: #dcfce7;
                color: #14532d;
                border: 1px solid #bbf7d0;
                display: <%= session.getAttribute("topupSuccess") != null ? "block" : "none" %>;
            }

            .table-wrap {
                width: 100%;
                overflow-x: auto;
            }

            .transaction-table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
                min-width: 720px;
            }

            .transaction-table th {
                background: rgba(9, 17, 31, 0.92);
                color: #f8df9d;
                padding: 13px;
                text-align: left;
                font-weight: 800;
                border-bottom: 1px solid rgba(218, 183, 99, 0.32);
            }

            .transaction-table td {
                padding: 13px;
                border-bottom: 1px solid rgba(17, 24, 39, 0.08);
                color: #566070;
                background: rgba(255, 255, 255, 0.62);
            }

            .transaction-table tr:hover td {
                background: rgba(248, 223, 157, 0.16);
            }

            .topup-type {
                background: #dcfce7;
                color: #14532d;
                padding: 5px 9px;
                border-radius: 7px;
                font-size: 12px;
                font-weight: 800;
            }

            .amount-positive {
                color: #15803d;
                font-weight: 800;
            }

            .no-data {
                text-align: center;
                color: #7d8794;
                padding: 34px;
            }

            @media (max-width: 760px) {
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

                .balance-card,
                .section {
                    padding: 22px;
                }

                .balance-amount {
                    font-size: 32px;
                }

                .preset-amounts {
                    grid-template-columns: 1fr;
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
            WalletDAO walletDAO = new WalletDAO();
            Wallet wallet = walletDAO.getWalletByAccountId(user.getAccountId());

            WalletTransactionDAO transactionDAO = new WalletTransactionDAO();
            List<WalletTransaction> transactions = transactionDAO.getTransactionsByWalletId(
                wallet != null ? wallet.getWalletId() : "");

            DecimalFormat df = new DecimalFormat("#,##0.00");

            Boolean topupSuccess = (Boolean) session.getAttribute("topupSuccess");
            Long topupAmount = (Long) session.getAttribute("topupSuccessAmount");
            if (topupSuccess != null && topupSuccess) {
                session.removeAttribute("topupSuccess");
                session.removeAttribute("topupSuccessAmount");
            }
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
            <div class="balance-card">
                <div class="balance-label">💰 Số dư ví của bạn</div>
                <div class="balance-amount">
                    <%= wallet != null ? df.format(wallet.getBalance()) + " VND" : "0 VND" %>
                </div>
                <button class="topup-btn" onclick="document.getElementById('topupForm').scrollIntoView({behavior: 'smooth'});">
                    + Nạp tiền
                </button>
            </div>

            <div class="section" id="topupForm">
                <h2>💳 Nạp tiền vào ví</h2>

                <% if (topupSuccess != null && topupSuccess) { %>
                    <div class="success-message">
                        ✓ Nạp tiền thành công! Đã cộng <%= topupAmount != null ? df.format(topupAmount) : "0" %> VND vào ví.
                    </div>
                <% } %>

                <% String error = request.getParameter("error");
                   if (error != null && !error.isEmpty()) { %>
                    <div class="error-message">
                        ✕
                        <% if ("payment_failed".equals(error)) { %>
                            Thanh toán thất bại. Vui lòng thử lại.
                        <% } else if ("invalid_hash".equals(error)) { %>
                            Lỗi xác thực. Vui lòng thử lại.
                        <% } else if ("order_mismatch".equals(error)) { %>
                            Mã đơn hàng không khớp. Vui lòng thử lại.
                        <% } else if ("update_failed".equals(error)) { %>
                            Lỗi cập nhật ví. Vui lòng liên hệ hỗ trợ.
                        <% } else if ("system_error".equals(error)) { %>
                            Lỗi hệ thống. Vui lòng thử lại.
                        <% } %>
                    </div>
                <% } %>

                <form action="<%= request.getContextPath() %>/topup" method="POST">
                    <div class="form-group">
                        <label>Chọn số tiền nạp:</label>
                        <div class="preset-amounts">
                            <button type="button" class="preset-btn" onclick="setAmount(100000)">100,000</button>
                            <button type="button" class="preset-btn" onclick="setAmount(200000)">200,000</button>
                            <button type="button" class="preset-btn" onclick="setAmount(500000)">500,000</button>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="amount">Hoặc nhập số tiền (VND) *</label>
                        <input type="number" id="amount" name="amount" placeholder="Nhập số tiền từ 10,000 đến 100,000,000" 
                               min="10000" max="100000000" step="1000" required>
                    </div>

                    <button type="submit" class="submit-btn">Nạp tiền qua VNPay</button>
                </form>
            </div>

            <div class="section">
                <h2>📝 Lịch sử giao dịch</h2>

                <% if (transactions != null && !transactions.isEmpty()) { %>
                    <div class="table-wrap">
                        <table class="transaction-table">
                            <thead>
                                <tr>
                                    <th>Ngày giờ</th>
                                    <th>Loại</th>
                                    <th>Số tiền</th>
                                    <th>Mô tả</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (WalletTransaction trans : transactions) { %>
                                    <tr>
                                        <td><%= trans.getCreatedAt() %></td>
                                        <td><span class="topup-type"><%= trans.getType().getValue() %></span></td>
                                        <td class="amount-positive">+<%= df.format(trans.getAmount()) %> VND</td>
                                        <td><%= trans.getDescription() %></td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } else { %>
                    <div class="no-data">
                        Chưa có giao dịch nào
                    </div>
                <% } %>
            </div>
        </div>

        <script>
            function setAmount(amount) {
                document.getElementById('amount').value = amount;
            }
        </script>
    </body>
</html>
