<%--
    Document   : vehicle-detail
    Created on : June 8, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ include file="/WEB-INF/jspf/customer-i18n.jspf" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết xe - E-Vehicle Rental</title>
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body {
                min-height: 100vh;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                color: #172033;
                background:
                    radial-gradient(circle at 12% 8%, rgba(205,164,82,0.16), transparent 28%),
                    radial-gradient(circle at 88% 18%, rgba(58,191,184,0.14), transparent 30%),
                    linear-gradient(135deg, #08111f 0%, #111a2c 38%, #f4f0e8 38%, #f8f6f2 100%);
                background-attachment: fixed;
            }
            .navbar {
                position: sticky; top: 0; z-index: 10; color: white; padding: 18px 38px;
                display: flex; justify-content: space-between; align-items: center;
                background: rgba(9,17,31,0.9);
                border-bottom: 1px solid rgba(218,183,99,0.32);
                box-shadow: 0 18px 45px rgba(5,10,18,0.24);
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
            .container { max-width: 1180px; margin: 34px auto; padding: 0 28px; }
            .panel {
                position: relative; overflow: hidden; padding: 34px; margin-bottom: 24px;
                background: rgba(255,255,255,0.92); border-radius: 8px;
                border: 1px solid rgba(218,183,99,0.2);
                box-shadow: 0 22px 60px rgba(8,17,31,0.14);
            }
            .panel::before {
                content: ""; position: absolute; inset: 0; pointer-events: none;
                background: linear-gradient(135deg, rgba(218,183,99,0.16), transparent 32%, rgba(58,191,184,0.09));
            }
            .panel > * { position: relative; }
            .detail-layout { display: grid; grid-template-columns: 420px minmax(0, 1fr); gap: 28px; align-items: stretch; }
            .vehicle-photo {
                min-height: 280px; border-radius: 8px; overflow: hidden; background: #ffffff;
                display: flex; align-items: center; justify-content: center;
                color: #f8df9d; font-size: 56px; font-weight: 800;
                box-shadow: inset 0 0 0 1px rgba(218,183,99,0.18);
            }
            .vehicle-photo img { width: 100%; height: 100%; object-fit: contain; display: block; padding: 16px; }
            .section-title {
                color: #111827; margin-bottom: 18px; padding-bottom: 13px;
                border-bottom: 1px solid rgba(17,24,39,0.12); font-size: 27px; font-weight: 800;
            }
            .meta-line { color: #566070; font-size: 15px; margin-bottom: 10px; line-height: 1.55; }
            .meta-line strong { color: #111827; }
            .availability-note {
                color: #7d8794; font-size: 13px; line-height: 1.55; margin: 12px 0 16px;
                padding: 11px 13px; border-radius: 7px; background: rgba(248,250,252,0.78);
                border: 1px solid rgba(17,24,39,0.08);
            }
            .model-description {
                color: #3f4b5d; font-size: 15px; line-height: 1.7; margin: 14px 0 16px;
                padding: 14px 16px; border-radius: 8px; background: rgba(255,255,255,0.68);
                border: 1px solid rgba(17,24,39,0.08);
            }
            .price { color: #b47a1f; font-size: 25px; font-weight: 800; margin: 16px 0 22px; }
            .date-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; margin-bottom: 16px; }
            label { display: block; color: #111827; font-weight: 700; margin-bottom: 8px; font-size: 14px; }
            input[type="date"] {
                width: 100%; min-height: 44px; padding: 11px 12px;
                border: 1px solid rgba(17,24,39,0.14); border-radius: 7px;
                background: rgba(255,255,255,0.84); color: #172033; font-size: 14px;
            }
            input[type="date"]:focus {
                outline: none; border-color: rgba(214,169,78,0.85);
                box-shadow: 0 0 0 4px rgba(214,169,78,0.16); background: #ffffff;
            }
            .primary-btn, .secondary-btn {
                display: inline-block; padding: 12px 16px; border-radius: 7px;
                font-weight: 800; text-decoration: none; text-align: center; cursor: pointer;
                transition: transform 0.25s, box-shadow 0.25s;
            }
            .primary-btn {
                color: #09111f; border: 1px solid rgba(218,183,99,0.55);
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                box-shadow: 0 10px 24px rgba(180,122,31,0.18);
            }
            .secondary-btn { color: #111827; background: #ffffff; box-shadow: 0 10px 24px rgba(8,17,31,0.12); }
            .primary-btn:hover, .secondary-btn:hover { transform: translateY(-2px); box-shadow: 0 14px 30px rgba(8,17,31,0.18); }
            .error-message {
                color: #7f1d1d; background-color: #fee2e2; border: 1px solid #fecaca;
                border-radius: 7px; padding: 12px 14px; margin-bottom: 18px; line-height: 1.5;
            }
            .vehicle-list { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 18px; }
            .vehicle-card {
                padding: 20px; background: linear-gradient(180deg, #ffffff 0%, #f7f4ee 100%);
                border: 1px solid rgba(17,24,39,0.1); border-radius: 8px;
                box-shadow: 0 12px 26px rgba(8,17,31,0.08);
            }
            .vehicle-card h3 { color: #172033; margin-bottom: 12px; font-size: 20px; }
            .empty-state { color: #566070; text-align: center; padding: 28px; }
            .actions { display: flex; gap: 12px; flex-wrap: wrap; align-items: center; }
            .language-switch { display: inline-flex; align-items: center; gap: 4px; padding: 4px; border-radius: 999px; background: rgba(255,255,255,0.08); border: 1px solid rgba(255,255,255,0.14); }
            .language-switch a { min-width: 34px; padding: 7px 9px; text-align: center; font-size: 12px; font-weight: 900; }
            .language-switch a.active { color: #09111f; background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%); }
            @media (max-width: 960px) {
                .detail-layout, .vehicle-list { grid-template-columns: 1fr; }
                .vehicle-photo { min-height: 220px; }
            }
            .language-switch { display: inline-flex; align-items: center; gap: 4px; padding: 4px; border-radius: 999px; background: rgba(255,255,255,0.08); border: 1px solid rgba(255,255,255,0.14); }
            .language-switch a { min-width: 34px; padding: 7px 9px; text-align: center; font-size: 12px; font-weight: 900; }
            .language-switch a.active { color: #09111f; background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%); }
            @media (max-width: 640px) {
                .navbar { padding: 14px 18px; align-items: flex-start; gap: 12px; flex-direction: column; }
                .navbar-menu { width: 100%; justify-content: space-between; }
                .container { margin: 22px auto; padding: 0 16px; }
                .panel { padding: 22px; }
                .date-grid { grid-template-columns: 1fr; }
            }
        </style>
    </head>
    <body>
        <div class="navbar">
            <h1>🚗 E-Vehicle Rental System</h1>
            <div class="navbar-menu">
                <a href="${pageContext.request.contextPath}?action=home" class="active"><fmt:message key="nav.home"/></a>
                <a href="${pageContext.request.contextPath}?action=profile"><fmt:message key="nav.profile"/></a>
                <%@ include file="/WEB-INF/jspf/customer-language-switch.jspf" %>
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn"><fmt:message key="nav.logout"/></a>
            </div>
        </div>

        <div class="container">
            <div class="panel">
                <c:if test="${not empty vehicleDetailError}">
                    <div class="error-message">${vehicleDetailError}</div>
                </c:if>

                <c:if test="${not empty vehicleInfo}">
                    <div class="detail-layout">
                        <div class="vehicle-photo">
                            <c:choose>
                                <c:when test="${not empty vehicleInfo.thumbnailImage}">
                                    <c:set var="vehicleImageUrl" value="${vehicleInfo.thumbnailImage}" />
                                    <c:if test="${not fn:startsWith(vehicleImageUrl, 'http://') and not fn:startsWith(vehicleImageUrl, 'https://')}">
                                        <c:set var="vehicleImageUrl" value="${pageContext.request.contextPath}/${vehicleImageUrl}" />
                                    </c:if>
                                    <img src="${vehicleImageUrl}" alt="${vehicleInfo.modelName}">
                                </c:when>
                                <c:otherwise>EV</c:otherwise>
                            </c:choose>
                        </div>

                        <div>
                            <h2 class="section-title">${vehicleInfo.modelName}</h2>
                            <div class="meta-line"><strong>Thương hiệu:</strong> ${empty vehicleInfo.brand ? 'Chưa cập nhật' : vehicleInfo.brand}</div>
                            <div class="meta-line"><strong>Số ghế:</strong> ${empty vehicleInfo.seatCount ? 0 : vehicleInfo.seatCount}</div>
                            <c:if test="${not empty vehicleInfo.description}">
                                <div class="model-description">${vehicleInfo.description}</div>
                            </c:if>
                            <div class="meta-line"><strong>Trạm:</strong> ${vehicleInfo.stationName}</div>
                            <div class="meta-line"><strong>Địa chỉ:</strong> ${empty vehicleInfo.stationAddress ? 'Chưa cập nhật địa chỉ trạm' : vehicleInfo.stationAddress}</div>
                            <div class="meta-line"><strong>Số xe tại trạm:</strong> ${vehicleInfo.remaining}</div>
                            <div class="availability-note">Con số này chưa áp dụng lịch thuê. Vui lòng chọn ngày để hệ thống kiểm tra xe trống theo đúng khoảng thời gian.</div>
                            <div class="price"><fmt:formatNumber value="${vehicleInfo.pricePerDay}" pattern="#,##0" /> VND/ngày</div>

                            <form action="${pageContext.request.contextPath}/" method="GET">
                                <input type="hidden" name="action" value="vehicle-detail">
                                <input type="hidden" name="detailAction" value="check">
                                <input type="hidden" name="modelId" value="${modelId}">
                                <input type="hidden" name="stationId" value="${stationId}">
                                <div class="date-grid">
                                    <div>
                                        <label for="startDate">Ngày bắt đầu</label>
                                        <input type="date" id="startDate" name="startDate" value="${startDate}" required>
                                    </div>
                                    <div>
                                        <label for="endDate">Ngày kết thúc</label>
                                        <input type="date" id="endDate" name="endDate" value="${endDate}" required>
                                    </div>
                                </div>
                                <div class="actions">
                                    <button type="submit" class="primary-btn">Kiểm tra xe trống theo ngày</button>
                                    <a class="secondary-btn" href="${pageContext.request.contextPath}?action=home">Quay lại Home</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:if>
            </div>

            <c:if test="${detailSearchPerformed}">
                <div class="panel">
                    <h2 class="section-title">Xe trống theo thời gian đã chọn</h2>
                    <c:choose>
                        <c:when test="${not empty availableVehicles}">
                            <div class="vehicle-list">
                                <c:forEach var="vehicle" items="${availableVehicles}">
                                    <div class="vehicle-card">
                                        <h3>${empty vehicle.licensePlate ? 'Chưa có biển số' : vehicle.licensePlate}</h3>
                                        <div class="meta-line">Battery Level: <strong>${empty vehicle.batteryLevel ? 0 : vehicle.batteryLevel}%</strong></div>
                                        <div class="meta-line">Color: <strong>${empty vehicle.color ? 'N/A' : vehicle.color}</strong></div>
                                        <c:url var="bookingUrl" value="/">
                                            <c:param name="action" value="booking" />
                                            <c:param name="vehicleId" value="${vehicle.vehicleId}" />
                                            <c:param name="stationId" value="${stationId}" />
                                            <c:param name="startDate" value="${startDate}" />
                                            <c:param name="endDate" value="${endDate}" />
                                        </c:url>
                                        <a class="primary-btn" href="${bookingUrl}">Đặt xe này</a>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:when test="${empty vehicleDetailError}">
                            <div class="empty-state">Không còn xe trống cho khoảng ngày đã chọn.</div>
                        </c:when>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </body>
</html>
