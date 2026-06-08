<%--
    Document   : home
    Created on : June 5, 2026
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
        <title>Trang Chủ - E-Vehicle Rental</title>
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
            .section-title {
                color: #111827; margin-bottom: 18px; padding-bottom: 13px;
                border-bottom: 1px solid rgba(17,24,39,0.12); font-size: 27px; font-weight: 800;
            }
            .welcome-copy { color: #566070; margin-bottom: 30px; font-size: 16px; line-height: 1.7; }
            .search-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; align-items: end; }
            .form-group label {
                display: block; color: #111827; font-weight: 700; margin-bottom: 8px; font-size: 14px;
            }
            select, input[type="date"] {
                width: 100%; min-height: 44px; padding: 11px 12px;
                border: 1px solid rgba(17,24,39,0.14); border-radius: 7px;
                background: rgba(255,255,255,0.84); color: #172033; font-size: 14px;
            }
            select:focus, input[type="date"]:focus {
                outline: none; border-color: rgba(214,169,78,0.85);
                box-shadow: 0 0 0 4px rgba(214,169,78,0.16); background: #ffffff;
            }
            .search-btn, .choose-btn {
                display: inline-block; padding: 12px 16px; color: #09111f;
                border: 1px solid rgba(218,183,99,0.55); border-radius: 7px; cursor: pointer;
                background: linear-gradient(135deg, #f8df9d 0%, #d6a94e 100%);
                font-weight: 800; text-decoration: none; text-align: center;
                box-shadow: 0 10px 24px rgba(180,122,31,0.18);
                transition: transform 0.25s, box-shadow 0.25s;
            }
            .search-btn:hover, .choose-btn:hover { transform: translateY(-2px); box-shadow: 0 14px 30px rgba(180,122,31,0.28); }
            .error-message {
                color: #7f1d1d; background-color: #fee2e2; border: 1px solid #fecaca;
                border-radius: 7px; padding: 12px 14px; margin-bottom: 18px; line-height: 1.5;
            }
            .result-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 18px; }
            .vehicle-card {
                overflow: hidden; background: linear-gradient(180deg, #ffffff 0%, #f7f4ee 100%);
                border: 1px solid rgba(17,24,39,0.1); border-radius: 8px;
                box-shadow: 0 12px 26px rgba(8,17,31,0.08);
                transition: transform 0.25s, box-shadow 0.25s, border-color 0.25s;
            }
            .vehicle-card:hover {
                transform: translateY(-6px); border-color: rgba(218,183,99,0.58);
                box-shadow: 0 18px 38px rgba(8,17,31,0.16);
            }
            .vehicle-thumb {
                height: 160px; background: #111a2c; display: flex; align-items: center; justify-content: center;
                color: #f8df9d; font-size: 42px; font-weight: 800;
            }
            .vehicle-thumb img { width: 100%; height: 100%; object-fit: cover; display: block; }
            .vehicle-body { padding: 18px; }
            .vehicle-body h3 { color: #172033; margin-bottom: 10px; font-size: 21px; }
            .meta-line { color: #566070; font-size: 14px; margin-bottom: 8px; }
            .price { color: #b47a1f; font-size: 20px; font-weight: 800; margin: 12px 0; }
            .station-badge {
                display: inline-flex; align-items: center; gap: 6px; margin-bottom: 10px;
                padding: 7px 10px; border-radius: 7px; color: #0f1c31;
                background: rgba(248,223,157,0.55); border: 1px solid rgba(218,183,99,0.42);
                font-size: 13px; font-weight: 800;
            }
            .station-address {
                min-height: 38px; color: #7d8794; font-size: 13px; line-height: 1.45; margin-bottom: 10px;
            }
            .empty-state { color: #566070; text-align: center; padding: 28px; }
            @media (max-width: 980px) { .search-grid, .result-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
            @media (max-width: 640px) {
                .navbar { padding: 14px 18px; align-items: flex-start; gap: 12px; flex-direction: column; }
                .navbar-menu { width: 100%; justify-content: space-between; }
                .container { margin: 22px auto; padding: 0 16px; }
                .panel { padding: 22px; }
                .search-grid, .result-grid { grid-template-columns: 1fr; }
            }
        </style>
    </head>
    <body>
        <div class="navbar">
            <h1>🚗 E-Vehicle Rental System</h1>
            <div class="navbar-menu">
                <a href="${pageContext.request.contextPath}?page=home" class="active">Trang Chủ</a>
                <a href="${pageContext.request.contextPath}?page=profile">Profile</a>
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
            </div>
        </div>

        <div class="container">
            <div class="panel">
                <h2 class="section-title">🏠 Tìm xe điện</h2>
                <p class="welcome-copy">
                    Chọn trạm, loại xe và thời gian thuê để xem các mẫu xe còn trống.
                </p>

                <c:if test="${not empty searchError}">
                    <div class="error-message">${searchError}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/" method="GET">
                    <input type="hidden" name="page" value="home">
                    <input type="hidden" name="action" value="search">
                    <div class="search-grid">
                        <div class="form-group">
                            <label for="stationId">Station</label>
                            <select id="stationId" name="stationId" required>
                                <option value="">Chọn trạm</option>
                                <c:forEach var="station" items="${stations}">
                                    <c:choose>
                                        <c:when test="${station.stationId eq selectedStationId}">
                                            <option value="${station.stationId}" selected>${station.name}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${station.stationId}">${station.name}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="categoryId">Category</label>
                            <select id="categoryId" name="categoryId" required>
                                <option value="">Chọn loại xe</option>
                                <c:forEach var="category" items="${categories}">
                                    <c:choose>
                                        <c:when test="${category.categoryId eq selectedCategoryId}">
                                            <option value="${category.categoryId}" selected>${category.name}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${category.categoryId}">${category.name}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="startDate">Start Date</label>
                            <input type="date" id="startDate" name="startDate" value="${selectedStartDate}" required>
                        </div>

                        <div class="form-group">
                            <label for="endDate">End Date</label>
                            <input type="date" id="endDate" name="endDate" value="${selectedEndDate}" required>
                        </div>
                    </div>
                    <div style="margin-top: 16px;">
                        <button type="submit" class="search-btn">Search</button>
                    </div>
                </form>
            </div>

            <c:if test="${not empty featuredVehicles}">
                <div class="panel">
                    <h2 class="section-title">🚘 Xe đang có tại các trạm</h2>
                    <p class="welcome-copy">
                        Một số mẫu xe hiện sẵn sàng cho thuê. Bạn có thể xem nhanh trạm đang có xe trước khi chọn ngày thuê.
                    </p>

                    <div class="result-grid">
                        <c:forEach var="vehicle" items="${featuredVehicles}">
                            <div class="vehicle-card">
                                <div class="vehicle-thumb">
                                    <c:choose>
                                        <c:when test="${not empty vehicle.thumbnailImage}">
                                            <img src="${vehicle.thumbnailImage}" alt="${vehicle.modelName}">
                                        </c:when>
                                        <c:otherwise>EV</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="vehicle-body">
                                    <span class="station-badge">📍 ${vehicle.stationName}</span>
                                    <h3>${vehicle.modelName}</h3>
                                    <div class="station-address">${empty vehicle.stationAddress ? 'Chưa cập nhật địa chỉ trạm' : vehicle.stationAddress}</div>
                                    <div class="meta-line">Remaining: <strong>${vehicle.remaining}</strong></div>
                                    <div class="meta-line">Seat Count: <strong>${empty vehicle.seatCount ? 0 : vehicle.seatCount}</strong></div>
                                    <div class="price">Price/day: <fmt:formatNumber value="${vehicle.pricePerDay}" pattern="#,##0" /> VND</div>
                                    <c:url var="vehicleDetailUrl" value="/">
                                        <c:param name="page" value="vehicle-detail" />
                                        <c:param name="modelId" value="${vehicle.modelId}" />
                                        <c:param name="stationId" value="${vehicle.stationId}" />
                                    </c:url>
                                    <a class="choose-btn" href="${vehicleDetailUrl}">Xem và đặt</a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <c:if test="${searchPerformed}">
                <div class="panel">
                    <h2 class="section-title">📋 Kết quả tìm kiếm</h2>

                    <c:choose>
                        <c:when test="${not empty vehicleSearchResults}">
                            <div class="result-grid">
                                <c:forEach var="result" items="${vehicleSearchResults}">
                                    <div class="vehicle-card">
                                        <div class="vehicle-thumb">
                                            <c:choose>
                                                <c:when test="${not empty result.thumbnailImage}">
                                                    <img src="${result.thumbnailImage}" alt="${result.modelName}">
                                                </c:when>
                                                <c:otherwise>EV</c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="vehicle-body">
                                            <h3>${result.modelName}</h3>
                                            <div class="meta-line">Remaining: <strong>${result.remaining}</strong></div>
                                            <div class="meta-line">Seat Count: <strong>${empty result.seatCount ? 0 : result.seatCount}</strong></div>
                                            <div class="price">Price/day: <fmt:formatNumber value="${result.pricePerDay}" pattern="#,##0" /> VND</div>
                                            <c:url var="vehicleOptionsUrl" value="/">
                                                <c:param name="page" value="vehicle-options" />
                                                <c:param name="modelId" value="${result.modelId}" />
                                                <c:param name="stationId" value="${selectedStationId}" />
                                                <c:param name="categoryId" value="${selectedCategoryId}" />
                                                <c:param name="startDate" value="${selectedStartDate}" />
                                                <c:param name="endDate" value="${selectedEndDate}" />
                                            </c:url>
                                            <a class="choose-btn" href="${vehicleOptionsUrl}">Chọn mẫu xe</a>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:when test="${empty searchError}">
                            <div class="empty-state">Không tìm thấy xe phù hợp trong khoảng thời gian đã chọn.</div>
                        </c:when>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </body>
</html>
