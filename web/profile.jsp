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
                position: relative;
                overflow-x: hidden;
                background:
                    radial-gradient(circle at 14% 10%, rgba(248,223,157,0.26), transparent 27%),
                    radial-gradient(circle at 86% 12%, rgba(58,191,184,0.13), transparent 28%),
                    linear-gradient(180deg, #f7f2e8 0%, #fbfaf5 48%, #eef4f0 100%);
                background-attachment: fixed;
            }
            body::before {
                content: "";
                position: fixed;
                inset: 0;
                pointer-events: none;
                background:
                    linear-gradient(180deg, rgba(7,16,29,0.94) 0%, rgba(7,16,29,0.74) 160px, rgba(7,16,29,0.16) 360px, transparent 62%),
                    radial-gradient(ellipse at 24% 2%, rgba(248,223,157,0.18), transparent 35%),
                    radial-gradient(ellipse at 84% 0%, rgba(58,191,184,0.14), transparent 32%);
                z-index: 0;
            }
            body::after {
                content: "";
                position: fixed;
                inset: -20%;
                pointer-events: none;
                background:
                    linear-gradient(115deg, transparent 0%, transparent 38%, rgba(255,255,255,0.28) 46%, rgba(248,223,157,0.18) 50%, transparent 58%, transparent 100%),
                    radial-gradient(circle at 76% 24%, rgba(58,191,184,0.13), transparent 18%);
                opacity: 0.48;
                mix-blend-mode: screen;
                transform: translateX(-18%) rotate(0.001deg);
                animation: pageLightFlow 12s ease-in-out infinite;
                z-index: 1;
            }
            .navbar {
                position: sticky; top: 0; z-index: 20; color: white; padding: 18px 38px;
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
            .container { max-width: 1120px; margin: 34px auto 56px; padding: 0 28px; position: relative; z-index: 2; }
            .profile-hero {
                position: relative; overflow: hidden; margin-bottom: 24px; padding: 36px;
                border-radius: 8px; color: #ffffff;
                background:
                    linear-gradient(105deg, rgba(8,17,31,0.95), rgba(8,17,31,0.76) 55%, rgba(8,17,31,0.2)),
                    url('assets/images/backgound/electric-car-auth-bg.jpg') center 68% / cover no-repeat;
                border: 1px solid rgba(248,223,157,0.22);
                box-shadow: 0 28px 80px rgba(5,10,18,0.28);
            }
            .profile-hero::after {
                content: ""; position: absolute; right: -70px; top: -70px;
                width: 240px; height: 240px; border-radius: 50%;
                border: 1px solid rgba(248,223,157,0.25);
            }
            .profile-hero > * { position: relative; }
            .profile-identity { display: flex; align-items: center; gap: 18px; }
            .avatar {
                width: 76px; height: 76px; border-radius: 50%;
                display: flex; align-items: center; justify-content: center;
                background: linear-gradient(135deg, #f8df9d, #d6a94e);
                color: #09111f; font-size: 34px; font-weight: 900;
                box-shadow: 0 18px 42px rgba(180,122,31,0.26);
            }
            .profile-hero h2 { font-size: 34px; line-height: 1.15; margin-bottom: 8px; }
            .profile-hero p { color: rgba(248,250,252,0.72); line-height: 1.6; }
            .dashboard-grid { display: grid; grid-template-columns: 1.05fr 0.95fr; gap: 24px; align-items: stretch; }
            .profile-card, .wallet-card, .quick-card {
                border-radius: 8px; border: 1px solid rgba(218,183,99,0.2);
                box-shadow: 0 22px 60px rgba(8,17,31,0.14);
            }
            .profile-card {
                position: relative; overflow: hidden; padding: 30px; background: rgba(255,255,255,0.92);
                backdrop-filter: blur(16px);
            }
            .profile-card::before {
                content: ""; position: absolute; inset: 0; pointer-events: none;
                background: linear-gradient(135deg, rgba(218,183,99,0.14), transparent 34%, rgba(58,191,184,0.08));
            }
            .profile-card > *, .wallet-card > *, .quick-card > * { position: relative; }
            .card-title {
                color: #111827; margin-bottom: 18px; padding-bottom: 13px;
                border-bottom: 1px solid rgba(17,24,39,0.12); font-size: 25px; font-weight: 800;
            }
            .user-info { display: grid; gap: 12px; font-size: 15px; color: #566070; }
            .info-row {
                display: grid; grid-template-columns: 132px minmax(0, 1fr); gap: 12px;
                padding: 13px 14px; border-radius: 8px;
                background: rgba(248,250,252,0.72); border: 1px solid rgba(17,24,39,0.07);
            }
            .user-info strong { color: #111827; }
            .wallet-card {
                position: relative; overflow: hidden; color: white; padding: 30px;
                background:
                    linear-gradient(135deg, rgba(8,17,31,0.98), rgba(35,50,77,0.95)),
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
            .quick-card {
                margin-top: 24px; padding: 26px; background: rgba(255,255,255,0.92);
                backdrop-filter: blur(16px);
            }
            .quick-actions { display: grid; gap: 12px; }
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
            @keyframes pageLightFlow {
                0%, 38% { opacity: 0.32; transform: translateX(-28%) translateY(0) rotate(0.001deg); }
                56% { opacity: 0.7; }
                82%, 100% { opacity: 0.18; transform: translateX(24%) translateY(-18px) rotate(0.001deg); }
            }
            @media (max-width: 860px) { .dashboard-grid { grid-template-columns: 1fr; } }
            @media (max-width: 640px) {
                .navbar { padding: 14px 18px; align-items: flex-start; gap: 12px; flex-direction: column; }
                .navbar-menu { width: 100%; justify-content: space-between; }
                .container { margin: 22px auto; padding: 0 16px; }
                .profile-card, .wallet-card { padding: 22px; }
                .profile-hero { padding: 26px; }
                .profile-identity { align-items: flex-start; flex-direction: column; }
                .info-row { grid-template-columns: 1fr; gap: 4px; }
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
            <section class="profile-hero">
                <div class="profile-identity">
                    <div class="avatar">👤</div>
                    <div>
                        <h2>Xin chào, ${displayName}</h2>
                        <p>Quản lý thông tin cá nhân, ví điện tử và các thao tác thuê xe của bạn tại một nơi.</p>
                    </div>
                </div>
            </section>

            <div class="dashboard-grid">
                <div>
                    <div class="profile-card">
                        <h2 class="card-title">👤 Thông Tin Cá Nhân</h2>
                        <div class="user-info">
                            <div class="info-row"><strong>Tên</strong><span>${displayName}</span></div>
                            <div class="info-row"><strong>Email</strong><span>${displayEmail}</span></div>
                            <div class="info-row"><strong>Số điện thoại</strong><span>${empty profileUser.phone ? 'Chưa cập nhật' : profileUser.phone}</span></div>
                            <div class="info-row"><strong>Vai trò</strong><span>${empty profileUser.role ? 'CUSTOMER' : profileUser.role.value}</span></div>
                            <div class="info-row"><strong>Trạng thái</strong><span>${empty profileUser.status ? 'ACTIVE' : profileUser.status}</span></div>
                        </div>
                    </div>
                </div>

                <div>
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

                    <div class="quick-card">
                        <h2 class="card-title">⚡ Thao tác nhanh</h2>
                        <div class="quick-actions">
                            <a class="primary-btn" href="${pageContext.request.contextPath}?action=home">Tìm xe để thuê</a>
                            <a class="secondary-btn" href="${pageContext.request.contextPath}?action=wallet">Xem lịch sử ví</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
