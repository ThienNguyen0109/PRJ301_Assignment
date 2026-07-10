<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html><head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicles - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head><body class="admin-body"><div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main"><%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %><div class="admin-content">
        <c:if test="${not empty adminSuccess}"><div class="admin-message success"><c:out value="${adminSuccess}"/></div></c:if>
        <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>
        <section class="admin-panel"><div class="admin-panel-header">
            <div><h2>Vehicle Inventory</h2><p>Manage each vehicle's model, station, battery, color, and availability.</p></div>
            <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-vehicle-form">Add Vehicle</a>
        </div>
        <form class="form-row" action="${pageContext.request.contextPath}/" method="GET">
            <input type="hidden" name="action" value="admin-vehicles">
            <input type="search" name="keyword" value="${keyword}" placeholder="License plate, model, or station">
            <select name="stationId"><option value="ALL">All Stations</option><c:forEach var="station" items="${stations}"><option value="${station.stationId}" ${selectedStationId eq station.stationId ? 'selected' : ''}><c:out value="${station.name}"/></option></c:forEach></select>
            <select name="categoryId"><option value="ALL">All Categories</option><c:forEach var="category" items="${categories}"><option value="${category.categoryId}" ${selectedCategoryId eq category.categoryId ? 'selected' : ''}><c:out value="${category.name}"/></option></c:forEach></select>
            <select name="status"><option value="ALL">All Statuses</option><c:forEach var="item" items="${vehicleStatuses}"><option value="${item}"${selectedStatus == item.name() ? 'selected' : ''}>${item}</option></c:forEach></select>
            <button class="admin-button" type="submit">Filter</button>
        </form>
        <div class="admin-table-wrap"><table class="admin-table"><thead><tr>
            <th>License Plate</th><th>Model</th><th>Category</th><th>Station</th><th>Battery</th><th>Color</th><th>Status</th><th>Actions</th>
        </tr></thead><tbody>
        <c:forEach var="vehicle" items="${vehicles}"><tr>
            <td><strong><c:out value="${vehicle.licensePlate}"/></strong></td><td><c:out value="${vehicle.modelName}"/></td>
            <td><c:out value="${vehicle.categoryName}"/></td><td><c:out value="${vehicle.stationName}"/></td>
            <td><c:out value="${vehicle.batteryLevel}"/>%</td><td><c:out value="${empty vehicle.color ? '-' : vehicle.color}"/></td>
            <td><span class="status-chip"><c:out value="${vehicle.status}"/></span></td>
            <td><div class="inline-actions">
                <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-detail&id=${vehicle.vehicleId}">View</a>
                <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-form&id=${vehicle.vehicleId}">Edit</a>
                <form class="inline-form" action="${pageContext.request.contextPath}/admin/vehicles/delete" method="POST" onsubmit="return confirm('Delete this vehicle? This is allowed only when it has no rental history.');">
                    <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}"><button class="danger-button" type="submit">Delete</button>
                </form>
            </div></td>
        </tr></c:forEach>
        <c:if test="${empty vehicles}"><tr><td colspan="8">No vehicles found.</td></tr></c:if>
        </tbody></table></div>
        <%@ include file="/WEB-INF/jspf/admin-pagination.jspf" %>
        </section>
    </div></main>
</div></body></html>
