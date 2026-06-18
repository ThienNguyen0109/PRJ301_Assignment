<%--
    Document   : vehicle-options
    Created on : June 8, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chọn Xe - E-Vehicle Rental</title>
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
                box-shadow: 0 12px 28px rgba(209, 79, 84, 0.28);
            }
            .container { max-width: 1080px; margin: 34px auto; padding: 0 28px; }
            .panel {
                position: relative; overflow: hidden; padding: 34px; margin-bottom: 24px;
                background: rgba(255,255,255,0.92); border-radius: 8px;
                border: 1px solid rgba(218, 183, 99, 0.2);
                box-shadow: 0 22px 60px rgba(8,17,31,0.14);
            }
            .panel::before {
                content: ""; position: absolute; inset: 0; pointer-events: none;
                background: linear-gradient(135deg, rgba(218,183,99,0.16), transparent 32%, rgba(58,191,184,0.09));
            }
            .panel > * { position: relative; }
            .section-title {
                color: #111827; margin-bottom: 18px; padding-bottom: 13px;
                border-bottom: 1px solid rgba(17,24,39,0.12); font-size: 27px; font-weight: 800;
            }
            .summary { color: #566070; margin-bottom: 24px; line-height: 1.7; }
            .vehicle-list { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 18px; }
            .vehicle-card {
                padding: 20px; background: linear-gradient(180deg, #ffffff 0%, #f7f4ee 100%);
                border: 1px solid rgba(17,24,39,0.1); border-radius: 8px;
                box-shadow: 0 12px 26px rgba(8,17,31,0.08);
            }
            .vehicle-card h3 { color: #172033; margin-bottom: 12px; font-size: 20px; }
            .meta-line { color: #566070; margin-bottom: 9px; font-size: 14px; }
            .choose-btn, .back-btn {
                display: inline-block; margin-top: 12px; padding: 11px 16px; border-radius: 7px;
                text-decoration: none; font-weight: 800; text-align: center; transition: transform 0.25s, box-shadow 0.25s;
            }
            .choose-btn {
                color: #09111f; border: 1px solid rgba(218,183,99,0.55);
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                box-shadow: 0 10px 24px rgba(180,122,31,0.18);
            }
            .back-btn { color: #111827; background: #ffffff; box-shadow: 0 10px 24px rgba(8,17,31,0.12); }
            .choose-btn:hover, .back-btn:hover { transform: translateY(-2px); box-shadow: 0 14px 30px rgba(8,17,31,0.18); }
            .error-message, .empty-state {
                color: #7f1d1d; background-color: #fee2e2; border: 1px solid #fecaca;
                border-radius: 7px; padding: 12px 14px; margin-bottom: 18px; line-height: 1.5;
            }
            .empty-state { color: #566070; background: rgba(255,255,255,0.7); border-color: rgba(17,24,39,0.08); text-align: center; }
            @media (max-width: 900px) { .vehicle-list { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
            @media (max-width: 640px) {
                .navbar { padding: 14px 18px; align-items: flex-start; gap: 12px; flex-direction: column; }
                .navbar-menu { width: 100%; justify-content: space-between; }
                .container { margin: 22px auto; padding: 0 16px; }
                .panel { padding: 22px; }
                .vehicle-list { grid-template-columns: 1fr; }
            }
        </style>
    </head>
    <body>
        <div class="navbar">
            <h1>🚗 E-Vehicle Rental System</h1>
            <div class="navbar-menu">
                <a href="${pageContext.request.contextPath}?action=home" class="active">Trang Chủ</a>
                <a href="${pageContext.request.contextPath}?action=profile">Profile</a>
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
            </div>
        </div>

        <div class="container">
            <div class="panel">
                <h2 class="section-title">🚘 Chọn xe cụ thể</h2>
                <p class="summary">Thời gian thuê: <strong>${startDate}</strong> đến <strong>${endDate}</strong></p>

                <c:if test="${not empty vehicleOptionsError}">
                    <div class="error-message">${vehicleOptionsError}</div>
                </c:if>

                <c:choose>
                    <c:when test="${not empty availableVehicles}">
                        <div class="vehicle-list">
                            <c:forEach var="vehicle" items="${availableVehicles}">
                                <div class="vehicle-card">
                                    <h3>
                                        <c:choose>
                                            <c:when test="${not empty vehicle.licensePlate}">${vehicle.licensePlate}</c:when>
                                            <c:otherwise>Chưa có biển số</c:otherwise>
                                        </c:choose>
                                    </h3>
                                    <div class="meta-line">Battery Level: <strong>${empty vehicle.batteryLevel ? 0 : vehicle.batteryLevel}%</strong></div>
                                    <div class="meta-line">Color: <strong>${empty vehicle.color ? 'N/A' : vehicle.color}</strong></div>
                                    <c:url var="bookingUrl" value="/">
                                        <c:param name="action" value="booking" />
                                        <c:param name="vehicleId" value="${vehicle.vehicleId}" />
                                        <c:param name="stationId" value="${stationId}" />
                                        <c:param name="startDate" value="${startDate}" />
                                        <c:param name="endDate" value="${endDate}" />
                                    </c:url>
                                    <a class="choose-btn" href="${bookingUrl}">Chọn xe này</a>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:when test="${empty vehicleOptionsError}">
                        <div class="empty-state">Không còn xe cụ thể nào phù hợp với lựa chọn này.</div>
                    </c:when>
                </c:choose>

                <c:url var="backToSearchUrl" value="/">
                    <c:param name="action" value="home" />
                    <c:param name="action" value="search" />
                    <c:param name="stationId" value="${stationId}" />
                    <c:param name="categoryId" value="${categoryId}" />
                    <c:param name="startDate" value="${startDate}" />
                    <c:param name="endDate" value="${endDate}" />
                </c:url>
                <a class="back-btn" href="${backToSearchUrl}">Quay lại tìm kiếm</a>
            </div>
        </div>
    </body>
</html>
