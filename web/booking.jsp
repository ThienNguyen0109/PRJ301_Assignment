<%--
    Document   : booking
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
        <title>Thanh toán booking - E-Vehicle Rental</title>
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
            .container { max-width: 980px; margin: 34px auto; padding: 0 28px; }
            .panel {
                position: relative; overflow: hidden; padding: 34px; background: rgba(255,255,255,0.92);
                border-radius: 8px; border: 1px solid rgba(218,183,99,0.2);
                box-shadow: 0 22px 60px rgba(8,17,31,0.14); margin-bottom: 24px;
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
            .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; align-items: start; }
            .info-line, .amount-line {
                display: flex; justify-content: space-between; gap: 20px; padding: 12px 0;
                border-bottom: 1px solid rgba(17,24,39,0.08); color: #566070;
            }
            .info-line strong, .amount-line strong { color: #111827; text-align: right; }
            .amount-line.final strong { color: #b47a1f; font-size: 22px; }
            .error-message {
                color: #7f1d1d; background-color: #fee2e2; border: 1px solid #fecaca;
                border-radius: 7px; padding: 12px 14px; margin-bottom: 18px; line-height: 1.5;
            }
            .field-error {
                margin-top: 8px; color: #b42318; font-size: 13px; font-weight: 700; line-height: 1.4;
            }
            .field-success {
                margin-top: 8px; color: #087443; font-size: 13px; font-weight: 700; line-height: 1.4;
            }
            label { display: block; color: #111827; font-weight: 700; margin-bottom: 8px; font-size: 14px; }
            input[type="text"] {
                width: 100%; min-height: 44px; padding: 11px 12px; border: 1px solid rgba(17,24,39,0.14);
                border-radius: 7px; background: rgba(255,255,255,0.84); color: #172033; font-size: 14px;
            }
            input.has-error { border-color: #f04438; box-shadow: 0 0 0 3px rgba(240,68,56,0.12); }
            .method-list { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin: 18px 0; }
            .method {
                padding: 14px; border-radius: 8px; border: 1px solid rgba(17,24,39,0.12);
                background: rgba(255,255,255,0.74); cursor: pointer; font-weight: 800;
            }
            .method input { margin-right: 8px; }
            .primary-btn, .secondary-btn {
                display: inline-block; border: 0; border-radius: 7px; padding: 12px 16px;
                font-weight: 800; text-decoration: none; cursor: pointer;
            }
            .primary-btn {
                color: #09111f; border: 1px solid rgba(218,183,99,0.55);
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                box-shadow: 0 10px 24px rgba(180,122,31,0.18);
            }
            .secondary-btn { color: #111827; background: #ffffff; box-shadow: 0 10px 24px rgba(8,17,31,0.12); }
            .actions { display: flex; gap: 12px; flex-wrap: wrap; margin-top: 14px; }
            @media (max-width: 760px) {
                .navbar { padding: 14px 18px; align-items: flex-start; gap: 12px; flex-direction: column; }
                .navbar-menu { width: 100%; justify-content: space-between; }
                .container { margin: 22px auto; padding: 0 16px; }
                .panel { padding: 22px; }
                .grid, .method-list { grid-template-columns: 1fr; }
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
                <h2 class="section-title">🧾 Thanh toán booking</h2>

                <c:if test="${not empty bookingError}">
                    <div class="error-message">${bookingError}</div>
                </c:if>

                <c:if test="${not empty bookingQuote}">
                    <div class="grid">
                        <div>
                            <div class="info-line"><span>Xe</span><strong>${bookingQuote.vehicleModelName}</strong></div>
                            <div class="info-line"><span>Biển số</span><strong>${empty bookingQuote.licensePlate ? 'Chưa có biển số' : bookingQuote.licensePlate}</strong></div>
                            <div class="info-line"><span>Trạm nhận xe</span><strong>${bookingQuote.stationName}</strong></div>
                            <div class="info-line"><span>Ngày bắt đầu</span><strong>${bookingQuote.startDate}</strong></div>
                            <div class="info-line"><span>Ngày kết thúc</span><strong>${bookingQuote.endDate}</strong></div>
                            <div class="info-line"><span>Số ngày thuê</span><strong>${bookingQuote.totalDays}</strong></div>
                        </div>

                        <div>
                            <form action="${pageContext.request.contextPath}/" method="GET">
                                <input type="hidden" name="page" value="booking">
                                <input type="hidden" name="vehicleId" value="${vehicleId}">
                                <input type="hidden" name="stationId" value="${stationId}">
                                <input type="hidden" name="startDate" value="${startDate}">
                                <input type="hidden" name="endDate" value="${endDate}">
                                <label for="discountCode">Mã giảm giá</label>
                                <input type="text" id="discountCode" name="discountCode" value="${discountCode}" class="${not empty discountError ? 'has-error' : ''}" placeholder="Nhập mã giảm giá nếu có">
                                <c:if test="${not empty discountError}">
                                    <div class="field-error">${discountError}</div>
                                </c:if>
                                <c:if test="${empty discountError and not empty bookingQuote.discountCode}">
                                    <div class="field-success">Đã áp dụng mã ${bookingQuote.discountCode}</div>
                                </c:if>
                                <div class="actions">
                                    <button class="secondary-btn" type="submit">Áp dụng mã</button>
                                </div>
                            </form>

                            <div style="margin-top: 18px;">
                                <div class="amount-line"><span>Tạm tính</span><strong><fmt:formatNumber value="${bookingQuote.originalAmount}" pattern="#,##0" /> VND</strong></div>
                                <div class="amount-line"><span>Giảm giá</span><strong>-<fmt:formatNumber value="${bookingQuote.discountAmount}" pattern="#,##0" /> VND</strong></div>
                                <div class="amount-line final"><span>Tổng thanh toán</span><strong><fmt:formatNumber value="${bookingQuote.finalAmount}" pattern="#,##0" /> VND</strong></div>
                            </div>

                            <form action="${pageContext.request.contextPath}/booking" method="POST">
                                <input type="hidden" name="vehicleId" value="${vehicleId}">
                                <input type="hidden" name="stationId" value="${stationId}">
                                <input type="hidden" name="startDate" value="${startDate}">
                                <input type="hidden" name="endDate" value="${endDate}">
                                <input type="hidden" name="discountCode" value="${empty discountError ? discountCode : ''}">

                                <div class="method-list">
                                    <label class="method"><input type="radio" name="paymentMethod" value="WALLET" checked> WALLET</label>
                                    <label class="method"><input type="radio" name="paymentMethod" value="VNPAY"> VNPAY</label>
                                </div>

                                <div class="actions">
                                    <button class="primary-btn" type="submit">Xác nhận thanh toán</button>
                                    <a class="secondary-btn" href="${pageContext.request.contextPath}?page=home">Quay lại</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </body>
</html>
