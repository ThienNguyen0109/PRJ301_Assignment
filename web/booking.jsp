<%--
    Document   : booking
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
        <title>Booking - E-Vehicle Rental</title>
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body {
                min-height: 100vh; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; color: #172033;
                background: radial-gradient(circle at 12% 8%, rgba(205,164,82,0.16), transparent 28%),
                            radial-gradient(circle at 88% 18%, rgba(58,191,184,0.14), transparent 30%),
                            linear-gradient(135deg, #08111f 0%, #111a2c 38%, #f4f0e8 38%, #f8f6f2 100%);
                background-attachment: fixed;
            }
            .navbar {
                color: white; padding: 18px 38px; display: flex; justify-content: space-between; align-items: center;
                background: rgba(9,17,31,0.9); border-bottom: 1px solid rgba(218,183,99,0.32);
                box-shadow: 0 18px 45px rgba(5,10,18,0.24);
            }
            .navbar h1 { font-size: 25px; font-weight: 800; }
            .navbar-menu { display: flex; gap: 12px; align-items: center; }
            .navbar a { color: white; text-decoration: none; padding: 10px 16px; border-radius: 7px; font-weight: 600; }
            .navbar a:hover { color: #f0d28a; background: rgba(255,255,255,0.08); }
            .logout-btn { background: linear-gradient(135deg, #d14f54 0%, #f28b61 100%); }
            .container { max-width: 860px; margin: 34px auto; padding: 0 28px; }
            .panel {
                padding: 34px; background: rgba(255,255,255,0.92); border-radius: 8px;
                border: 1px solid rgba(218,183,99,0.2); box-shadow: 0 22px 60px rgba(8,17,31,0.14);
            }
            .section-title {
                color: #111827; margin-bottom: 18px; padding-bottom: 13px;
                border-bottom: 1px solid rgba(17,24,39,0.12); font-size: 27px; font-weight: 800;
            }
            .booking-line { color: #566070; margin-bottom: 12px; font-size: 15px; }
            .booking-line strong { color: #111827; display: inline-block; min-width: 110px; }
            .primary-btn {
                display: inline-block; margin-top: 18px; padding: 12px 18px; color: #09111f;
                border: 1px solid rgba(218,183,99,0.55); border-radius: 7px;
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                text-decoration: none; font-weight: 800;
            }
        </style>
    </head>
    <body>
        <div class="navbar">
            <h1>🚗 E-Vehicle Rental System</h1>
            <div class="navbar-menu">
                <a href="${pageContext.request.contextPath}?page=home">Trang Chủ</a>
                <a href="${pageContext.request.contextPath}?page=profile">Profile</a>
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
            </div>
        </div>
        <div class="container">
            <div class="panel">
                <h2 class="section-title">📅 Booking</h2>
                <div class="booking-line"><strong>Vehicle ID:</strong> ${vehicleId}</div>
                <div class="booking-line"><strong>Station ID:</strong> ${stationId}</div>
                <div class="booking-line"><strong>Start Date:</strong> ${startDate}</div>
                <div class="booking-line"><strong>End Date:</strong> ${endDate}</div>
                <a class="primary-btn" href="${pageContext.request.contextPath}?page=home">Quay về trang chủ</a>
            </div>
        </div>
    </body>
</html>
