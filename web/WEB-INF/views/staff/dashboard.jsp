<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Dashboard - E-Vehicle Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body class="staff-body">
    <%@ include file="/WEB-INF/jspf/staff-sidebar.jspf" %>
    <div class="staff-main">
        <%@ include file="/WEB-INF/jspf/staff-topbar.jspf" %>
        <main class="staff-content">
            <div class="content-header"><h1>Operations Overview</h1><p>Monitor pickup, returns, incidents and maintenance workload.</p></div>
            <section class="stats-grid">
                <article class="stat-card stat-blue"><div><div class="label">Vehicles Waiting For Pickup</div><div class="value">${dashboard.waitingForPickup}</div></div><div class="stat-icon">P</div></article>
                <article class="stat-card stat-orange"><div><div class="label">Vehicles Currently Rented</div><div class="value">${dashboard.currentlyRented}</div></div><div class="stat-icon">R</div></article>
                <article class="stat-card stat-green"><div><div class="label">Vehicles Waiting For Return</div><div class="value">${dashboard.waitingForReturn}</div></div><div class="stat-icon">W</div></article>
                <article class="stat-card stat-purple"><div><div class="label">Vehicles Under Maintenance</div><div class="value">${dashboard.underMaintenance}</div></div><div class="stat-icon">M</div></article>
            </section>
            <section class="staff-card">
                <div class="card-header"><h2>Recent Activities</h2><a class="btn btn-light btn-sm" href="${pageContext.request.contextPath}?action=staff-pickup">Open Pickup</a></div>
                <div class="table-responsive">
                    <table class="staff-table">
                        <thead><tr><th>Rental ID</th><th>Customer</th><th>Vehicle</th><th>Action</th><th>Time</th></tr></thead>
                        <tbody>
                            <c:forEach var="activity" items="${dashboard.recentActivities}">
                                <tr><td><strong>${activity.rentalId}</strong></td><td>${activity.customer}</td><td>${activity.vehicle}</td><td>${activity.action}</td><td><fmt:formatDate value="${activity.time}" pattern="dd/MM/yyyy hh:mm a"/></td></tr>
                            </c:forEach>
                            <c:if test="${empty dashboard.recentActivities}"><tr><td colspan="5" class="empty-state">No recent activities.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</body>
</html>
