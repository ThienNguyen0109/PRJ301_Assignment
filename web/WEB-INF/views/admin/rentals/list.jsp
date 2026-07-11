<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html><head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rentals - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head><body class="admin-body"><div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main"><%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %><div class="admin-content">
        <c:if test="${not empty adminSuccess}"><div class="admin-message success"><c:out value="${adminSuccess}"/></div></c:if>
        <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>
        <section class="admin-panel"><div class="admin-panel-header">
            <div><h2>Rental Management</h2><p>View rental transactions and cancel bookings before pickup.</p></div>
        </div>
        <form class="form-row" action="${pageContext.request.contextPath}/" method="GET">
            <input type="hidden" name="action" value="admin-rentals">
            <input type="search" name="keyword" value="${keyword}" placeholder="Rental ID, customer, phone, vehicle">
            <select name="stationId"><option value="ALL">All Stations</option><c:forEach var="station" items="${stations}"><option value="${station.stationId}" ${selectedStationId eq station.stationId ? 'selected' : ''}><c:out value="${station.name}"/></option></c:forEach></select>
            <select name="status"><option value="ALL">All Statuses</option><c:forEach var="item" items="${rentalStatuses}"><option value="${item}" ${selectedStatus == item.name() ? 'selected' : ''}>${item}</option></c:forEach></select>
            <input type="date" name="startDate" value="${startDate}" title="Rental start date from">
            <input type="date" name="endDate" value="${endDate}" title="Rental start date to">
            <button class="admin-button" type="submit">Filter</button>
        </form>
        <div class="admin-table-wrap"><table class="admin-table"><thead><tr>
            <th>Rental</th><th>Customer</th><th>Vehicle</th><th>Station</th><th>Dates</th><th>Total</th><th>Status</th><th>Actions</th>
        </tr></thead><tbody>
        <c:forEach var="rental" items="${rentals}"><tr>
            <td><strong><c:out value="${rental.rentalId}"/></strong></td>
            <td><strong><c:out value="${rental.customerName}"/></strong><br><small><c:out value="${rental.customerPhone}"/></small></td>
            <td><c:out value="${rental.vehicleModelName}"/><br><small><c:out value="${rental.licensePlate}"/></small></td>
            <td><c:out value="${rental.stationName}"/></td>
            <td><fmt:formatDate value="${rental.startDate}" pattern="dd/MM/yyyy"/> – <fmt:formatDate value="${rental.endDate}" pattern="dd/MM/yyyy"/></td>
            <td><fmt:formatNumber value="${rental.totalAmount}" pattern="#,##0"/> VND</td>
            <td><span class="status-chip"><c:out value="${rental.status}"/></span></td>
            <td><div class="inline-actions">
                <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-rental-detail&id=${rental.rentalId}">View</a>
                <c:if test="${rental.canCancel}"><form class="inline-form" action="${pageContext.request.contextPath}/admin/rentals/cancel" method="POST" onsubmit="return confirm('Cancel this booked rental? This does not delete its transaction history.');"><input type="hidden" name="rentalId" value="${rental.rentalId}"><button class="danger-button" type="submit">Cancel</button></form></c:if>
            </div></td>
        </tr></c:forEach>
        <c:if test="${empty rentals}"><tr><td colspan="8">No rentals found.</td></tr></c:if>
        </tbody></table></div>
        <%@ include file="/WEB-INF/jspf/admin-pagination.jspf" %>
        </section>
    </div></main>
</div></body></html>
