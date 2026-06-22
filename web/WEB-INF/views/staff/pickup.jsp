<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Pickup Management - E-Vehicle Rental</title>
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body {
                min-height: 100vh; color: #172033;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background:
                    radial-gradient(circle at 12% 8%, rgba(248,223,157,0.24), transparent 28%),
                    radial-gradient(circle at 88% 12%, rgba(58,191,184,0.12), transparent 30%),
                    linear-gradient(180deg, #eef1ef, #f8f6f1 42%, #edf3f0);
            }
            .navbar {
                position: sticky; top: 0; z-index: 20; padding: 17px 36px;
                display: flex; align-items: center; justify-content: space-between;
                color: #fff; background: rgba(7,16,29,0.94);
                border-bottom: 1px solid rgba(248,223,157,0.24); backdrop-filter: blur(18px);
                box-shadow: 0 18px 45px rgba(5,10,18,0.22);
            }
            .brand { font-size: 24px; font-weight: 900; }
            .nav-actions { display: flex; align-items: center; gap: 12px; }
            .staff-badge { color: #f8df9d; font-size: 14px; font-weight: 800; }
            .logout-btn {
                padding: 10px 15px; border-radius: 7px; color: #fff; text-decoration: none; font-weight: 800;
                background: linear-gradient(135deg, #d14f54, #f28b61);
            }
            .container { max-width: 1240px; margin: 32px auto 56px; padding: 0 24px; }
            .hero {
                position: relative; overflow: hidden; margin-bottom: 22px; padding: 32px;
                border-radius: 8px; color: #fff;
                background: linear-gradient(115deg, #08111f, #182942 72%, #25434a);
                border: 1px solid rgba(248,223,157,0.22); box-shadow: 0 26px 70px rgba(8,17,31,0.2);
            }
            .hero::after {
                content: ""; position: absolute; width: 260px; height: 260px; right: -80px; top: -110px;
                border-radius: 50%; border: 1px solid rgba(248,223,157,0.28);
            }
            .hero h1 { position: relative; font-size: 34px; margin-bottom: 8px; }
            .hero p { position: relative; color: rgba(248,250,252,0.72); line-height: 1.6; }
            .search-panel, .list-panel, .detail-panel {
                background: rgba(255,255,255,0.9); border: 1px solid rgba(15,23,42,0.08);
                border-radius: 8px; box-shadow: 0 20px 54px rgba(8,17,31,0.1); backdrop-filter: blur(16px);
            }
            .search-panel { padding: 22px; margin-bottom: 22px; }
            .search-form { display: grid; grid-template-columns: minmax(0, 1fr) auto; gap: 12px; }
            .search-input {
                width: 100%; min-height: 46px; padding: 12px 14px; border-radius: 7px;
                border: 1px solid rgba(15,23,42,0.14); background: #fff; font-size: 14px;
            }
            .search-input:focus { outline: none; border-color: #d6a94e; box-shadow: 0 0 0 4px rgba(214,169,78,0.15); }
            .primary-btn, .danger-btn {
                border: 0; border-radius: 7px; padding: 12px 17px; cursor: pointer;
                font-weight: 900; text-decoration: none; text-align: center;
            }
            .primary-btn { color: #09111f; background: linear-gradient(135deg, #f8df9d, #d6a94e); }
            .danger-btn { color: #fff; background: linear-gradient(135deg, #b91c1c, #ef644f); }
            .layout { display: grid; grid-template-columns: minmax(0, 1.25fr) minmax(360px, 0.75fr); gap: 22px; align-items: start; }
            .list-panel, .detail-panel { padding: 24px; }
            .detail-panel { position: sticky; top: 96px; }
            .panel-title { margin-bottom: 18px; padding-bottom: 13px; border-bottom: 1px solid rgba(15,23,42,0.1); font-size: 23px; }
            .table-wrap { overflow-x: auto; }
            table { width: 100%; border-collapse: collapse; min-width: 680px; }
            th { padding: 12px; color: #f8df9d; background: #0b1728; text-align: left; font-size: 13px; }
            td { padding: 13px 12px; border-bottom: 1px solid rgba(15,23,42,0.08); color: #566070; font-size: 14px; }
            tbody tr:hover td { background: rgba(248,223,157,0.13); }
            .status {
                display: inline-flex; padding: 6px 9px; border-radius: 999px;
                color: #8a5b0a; background: #fff3cd; border: 1px solid #f2d98d; font-size: 12px; font-weight: 900;
            }
            .view-link { color: #9a650e; font-weight: 900; text-decoration: none; }
            .detail-grid { display: grid; gap: 10px; }
            .detail-row {
                display: grid; grid-template-columns: 130px minmax(0, 1fr); gap: 10px;
                padding: 11px 12px; border-radius: 7px; background: #f8fafc; border: 1px solid rgba(15,23,42,0.07);
            }
            .detail-row strong { color: #111827; }
            .detail-actions { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-top: 18px; }
            .message { margin-bottom: 18px; padding: 13px 15px; border-radius: 7px; font-weight: 700; }
            .success { color: #14532d; background: #dcfce7; border: 1px solid #bbf7d0; }
            .error { color: #7f1d1d; background: #fee2e2; border: 1px solid #fecaca; }
            .empty { padding: 38px 16px; text-align: center; color: #7d8794; }
            @media (max-width: 920px) { .layout { grid-template-columns: 1fr; } .detail-panel { position: static; } }
            @media (max-width: 620px) {
                .navbar { padding: 14px 16px; align-items: flex-start; gap: 10px; flex-direction: column; }
                .search-form, .detail-actions { grid-template-columns: 1fr; }
                .container { padding: 0 14px; }
                .detail-row { grid-template-columns: 1fr; gap: 4px; }
            }
        </style>
    </head>
    <body>
        <header class="navbar">
            <div class="brand">🚗 E-Vehicle Staff</div>
            <div class="nav-actions">
                <span class="staff-badge">STAFF · ${sessionScope.userName}</span>
                <a class="logout-btn" href="${pageContext.request.contextPath}/logout">Logout</a>
            </div>
        </header>

        <main class="container">
            <section class="hero">
                <h1>Pickup Management</h1>
                <p>Tìm booking, xác minh thông tin và bàn giao xe cho khách hàng tại trạm.</p>
            </section>

            <c:if test="${not empty pickupSuccess}"><div class="message success">${pickupSuccess}</div></c:if>
            <c:if test="${not empty pickupError}"><div class="message error">${pickupError}</div></c:if>

            <section class="search-panel">
                <form class="search-form" action="${pageContext.request.contextPath}/" method="GET">
                    <input type="hidden" name="action" value="staff-pickup">
                    <input class="search-input" type="search" name="query" value="${searchQuery}"
                           placeholder="Nhập Booking ID, email hoặc số điện thoại">
                    <button class="primary-btn" type="submit">Tìm booking</button>
                </form>
            </section>

            <div class="layout">
                <section class="list-panel">
                    <h2 class="panel-title">Booking đang chờ nhận xe</h2>
                    <c:choose>
                        <c:when test="${not empty bookedRentals}">
                            <div class="table-wrap">
                                <table>
                                    <thead><tr><th>Rental ID</th><th>Customer</th><th>Vehicle</th><th>Status</th><th></th></tr></thead>
                                    <tbody>
                                        <c:forEach var="rental" items="${bookedRentals}">
                                            <tr>
                                                <td>${rental.rentalId}</td>
                                                <td>${rental.customerName}</td>
                                                <td>${rental.vehicleModel}<br><small>${rental.licensePlate}</small></td>
                                                <td><span class="status">${rental.status.value}</span></td>
                                                <td><a class="view-link" href="${pageContext.request.contextPath}?action=staff-pickup&amp;rentalId=${rental.rentalId}&amp;query=${searchQuery}">Xem</a></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise><div class="empty">Không có booking BOOKED phù hợp.</div></c:otherwise>
                    </c:choose>
                </section>

                <aside class="detail-panel">
                    <h2 class="panel-title">Chi tiết booking</h2>
                    <c:choose>
                        <c:when test="${not empty selectedRental}">
                            <div class="detail-grid">
                                <div class="detail-row"><strong>Customer</strong><span>${selectedRental.customerName}</span></div>
                                <div class="detail-row"><strong>Email</strong><span>${selectedRental.email}</span></div>
                                <div class="detail-row"><strong>Phone</strong><span>${empty selectedRental.phone ? 'Chưa cập nhật' : selectedRental.phone}</span></div>
                                <div class="detail-row"><strong>Vehicle</strong><span>${selectedRental.vehicleModel}</span></div>
                                <div class="detail-row"><strong>License</strong><span>${selectedRental.licensePlate}</span></div>
                                <div class="detail-row"><strong>Battery</strong><span>${selectedRental.batteryLevel}%</span></div>
                                <div class="detail-row"><strong>Rental period</strong><span>${selectedRental.startDate} - ${selectedRental.endDate}</span></div>
                                <div class="detail-row"><strong>Pickup station</strong><span>${selectedRental.stationName}</span></div>
                                <div class="detail-row"><strong>Rental status</strong><span>${selectedRental.status.value}</span></div>
                                <div class="detail-row"><strong>Vehicle status</strong><span>${selectedRental.vehicleStatus.value}</span></div>
                            </div>
                            <c:if test="${selectedRental.status.value eq 'BOOKED'}">
                                <div class="detail-actions">
                                    <form action="${pageContext.request.contextPath}/staff/pickup" method="POST">
                                        <input type="hidden" name="pickupAction" value="confirm">
                                        <input type="hidden" name="rentalId" value="${selectedRental.rentalId}">
                                        <button class="primary-btn" type="submit">Confirm Pickup</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/staff/pickup" method="POST"
                                          onsubmit="return confirm('Customer did not arrive to pick up the vehicle.\n\nAre you sure?');">
                                        <input type="hidden" name="pickupAction" value="no-show">
                                        <input type="hidden" name="rentalId" value="${selectedRental.rentalId}">
                                        <button class="danger-btn" type="submit">Mark No Show</button>
                                    </form>
                                </div>
                            </c:if>
                        </c:when>
                        <c:otherwise><div class="empty">Chọn một booking để xem chi tiết.</div></c:otherwise>
                    </c:choose>
                </aside>
            </div>
        </main>
    </body>
</html>
