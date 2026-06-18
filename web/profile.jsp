<%--
    Document   : profile
    Created on : June 8, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Profile - E-Vehicle Rental</title>
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
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
                position: sticky; top: 0; z-index: 10; color: white; padding: 18px 38px;
                display: flex; justify-content: space-between; align-items: center;
                background: rgba(9, 17, 31, 0.9);
                border-bottom: 1px solid rgba(218, 183, 99, 0.32);
                box-shadow: 0 18px 45px rgba(5, 10, 18, 0.24);
                backdrop-filter: blur(18px);
            }
            .navbar h1 { font-size: 25px; font-weight: 800; }
            .navbar-menu { display: flex; gap: 12px; align-items: center; }
            .navbar a {
                color: white; text-decoration: none; padding: 10px 16px; border-radius: 7px;
                font-weight: 600; transition: background 0.25s, color 0.25s, transform 0.25s;
            }
            .navbar a:hover, .navbar a.active {
                color: #f0d28a; background: rgba(255,255,255,0.08); transform: translateY(-1px);
            }
            .logout-btn {
                background: linear-gradient(135deg, #d14f54 0%, #f28b61 100%);
                box-shadow: 0 12px 28px rgba(209,79,84,0.28);
            }
            .container {
                max-width: 1120px; margin: 34px auto; padding: 0 28px;
                display: grid; grid-template-columns: 1fr 1fr; gap: 24px;
            }
            .profile-card, .wallet-card {
                border-radius: 8px; border: 1px solid rgba(218,183,99,0.2);
                box-shadow: 0 22px 60px rgba(8,17,31,0.14);
            }
            .profile-card {
                position: relative; overflow: hidden; padding: 30px; background: rgba(255,255,255,0.94);
            }
            .profile-card::before {
                content: ""; position: absolute; inset: 0; pointer-events: none;
                background: linear-gradient(135deg, rgba(218,183,99,0.14), transparent 34%, rgba(58,191,184,0.08));
            }
            .profile-card > *, .wallet-card > * { position: relative; }
            .card-title {
                color: #111827; margin-bottom: 18px; padding-bottom: 13px;
                border-bottom: 1px solid rgba(17,24,39,0.12); font-size: 25px; font-weight: 800;
            }
            .user-info { font-size: 15px; color: #566070; line-height: 2.1; }
            .user-info strong { color: #111827; display: inline-block; min-width: 112px; }
            .wallet-card {
                position: relative; overflow: hidden; color: white; padding: 30px;
                background:
                    linear-gradient(135deg, rgba(15,28,49,0.96), rgba(47,61,91,0.95)),
                    linear-gradient(135deg, #d6a94e, #3abfb8);
            }
            .wallet-card::after {
                content: ""; position: absolute; right: -56px; top: -56px;
                width: 190px; height: 190px; border: 1px solid rgba(218,183,99,0.36); border-radius: 50%;
            }
            .wallet-card .card-title { color: white; border-bottom: 1px solid rgba(255,255,255,0.18); }
            .wallet-label { font-size: 13px; opacity: 0.84; margin-bottom: 12px; }
            .wallet-balance { color: #f8df9d; font-size: 34px; font-weight: 800; margin-bottom: 22px; }
            .wallet-actions { display: flex; gap: 12px; flex-wrap: wrap; }
            .primary-btn, .secondary-btn {
                display: inline-block; padding: 11px 18px; border-radius: 7px;
                text-decoration: none; font-weight: 800; transition: transform 0.25s, box-shadow 0.25s;
            }
            .primary-btn {
                color: #09111f; background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                border: 1px solid rgba(218,183,99,0.55); box-shadow: 0 12px 28px rgba(180,122,31,0.2);
            }
            .secondary-btn { color: #111827; background: #ffffff; box-shadow: 0 12px 28px rgba(0,0,0,0.16); }
            .primary-btn:hover, .secondary-btn:hover { transform: translateY(-2px); box-shadow: 0 16px 34px rgba(0,0,0,0.24); }
            @media (max-width: 860px) { .container { grid-template-columns: 1fr; } }
            @media (max-width: 640px) {
                .navbar { padding: 14px 18px; align-items: flex-start; gap: 12px; flex-direction: column; }
                .navbar-menu { width: 100%; justify-content: space-between; }
                .container { margin: 22px auto; padding: 0 16px; }
                .profile-card, .wallet-card { padding: 22px; }
            }
        </style>
    </head>
    <body>
        <div class="navbar">
            <h1>🚗 E-Vehicle Rental System</h1>
            <div class="navbar-menu">
                <a href="${pageContext.request.contextPath}?action=home">Trang Chủ</a>
                <a href="${pageContext.request.contextPath}?action=profile" class="active">Profile</a>
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
            </div>
        </div>

        <div class="container">
            <div class="profile-card">
                <h2 class="card-title">👤 Thông Tin Cá Nhân</h2>
                <div class="user-info">
                    <div><strong>Tên:</strong> ${displayName}</div>
                    <div><strong>Email:</strong> ${displayEmail}</div>
                    <div><strong>Số điện thoại:</strong> ${empty profileUser.phone ? 'Chưa cập nhật' : profileUser.phone}</div>
                    <div><strong>Vai trò:</strong> ${empty profileUser.role ? 'CUSTOMER' : profileUser.role.value}</div>
                    <div><strong>Trạng thái:</strong> ${empty profileUser.status ? 'ACTIVE' : profileUser.status}</div>
                </div>
            </div>

            <div class="wallet-card">
                <h2 class="card-title">💳 Ví của bạn</h2>
                <p class="wallet-label">Số dư hiện tại</p>
                <div class="wallet-balance">
                    <c:choose>
                        <c:when test="${not empty wallet}">
                            <fmt:formatNumber value="${wallet.balance}" pattern="#,##0.00" /> VND
                        </c:when>
                        <c:otherwise>0 VND</c:otherwise>
                    </c:choose>
                </div>
                <div class="wallet-actions">
                    <a class="primary-btn" href="${pageContext.request.contextPath}?action=wallet">Nạp tiền</a>
                    <a class="secondary-btn" href="${pageContext.request.contextPath}?action=wallet">Quản lý ví</a>
                </div>
            </div>
        </div>
    </body>
</html>
