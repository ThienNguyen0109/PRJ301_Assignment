<%--
    Document   : booking-detail
    Created on : June 8, 2026
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Booking Detail - E-Vehicle Rental</title>
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
            .logout-btn { background: linear-gradient(135deg, #d14f54 0%, #f28b61 100%); }
            .container { max-width: 880px; margin: 34px auto; padding: 0 28px; }
            .panel {
                position: relative; overflow: hidden; padding: 34px; background: rgba(255,255,255,0.92);
                border-radius: 8px; border: 1px solid rgba(218,183,99,0.2);
                box-shadow: 0 22px 60px rgba(8,17,31,0.14);
            }
            .section-title {
                color: #111827; margin-bottom: 18px; padding-bottom: 13px;
                border-bottom: 1px solid rgba(17,24,39,0.12); font-size: 27px; font-weight: 800;
            }
            .success {
                margin-bottom: 18px; padding: 14px 16px; border-radius: 8px;
                color: #14532d; background: #dcfce7; border: 1px solid #bbf7d0; font-weight: 800;
            }
            .line {
                display: flex; justify-content: space-between; gap: 20px; padding: 12px 0;
                border-bottom: 1px solid rgba(17,24,39,0.08); color: #566070;
            }
            .line strong { color: #111827; text-align: right; }
            .line.total strong { color: #b47a1f; font-size: 22px; }
            .primary-btn {
                display: inline-block; margin-top: 20px; padding: 12px 16px; color: #09111f;
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
                <a href="${pageContext.request.contextPath}?action=home">Trang Chủ</a>
                <a href="${pageContext.request.contextPath}?action=profile">Profile</a>
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
            </div>
        </div>

        <div class="container">
            <div class="panel">
                <h2 class="section-title">✅ Booking Detail</h2>
                <c:choose>
                    <c:when test="${not empty bookingDetail}">
                        <div class="success">Booking thành công. Email xác nhận đã được gửi tới tài khoản của bạn.</div>
                        <div class="line"><span>Mã booking</span><strong>${bookingDetail.rentalId}</strong></div>
                        <div class="line"><span>Mã payment</span><strong>${bookingDetail.paymentId}</strong></div>
                        <div class="line"><span>Xe</span><strong>${bookingDetail.quote.vehicleModelName}</strong></div>
                        <div class="line"><span>Biển số</span><strong>${bookingDetail.quote.licensePlate}</strong></div>
                        <div class="line"><span>Trạm nhận xe</span><strong>${bookingDetail.quote.stationName}</strong></div>
                        <div class="line"><span>Ngày thuê</span><strong>${bookingDetail.quote.startDate} đến ${bookingDetail.quote.endDate}</strong></div>
                        <div class="line"><span>Total Days</span><strong>${bookingDetail.quote.totalDays}</strong></div>
                        <div class="line"><span>Payment Method</span><strong>${bookingDetail.paymentMethod.value}</strong></div>
                        <div class="line"><span>Payment Status</span><strong>${bookingDetail.paymentStatus.value}</strong></div>
                        <div class="line total"><span>Final Amount</span><strong><fmt:formatNumber value="${bookingDetail.quote.finalAmount}" pattern="#,##0" /> VND</strong></div>
                    </c:when>
                    <c:otherwise>
                        <div class="line"><span>Không tìm thấy thông tin booking vừa tạo.</span><strong></strong></div>
                    </c:otherwise>
                </c:choose>
                <a class="primary-btn" href="${pageContext.request.contextPath}?action=home">Quay về Home</a>
            </div>
        </div>
        <%@ include file="/WEB-INF/jspf/realtime-client.jspf" %>
    </body>
</html>
