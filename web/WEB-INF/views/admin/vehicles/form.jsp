<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html><head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty vehicle ? 'Create Vehicle' : 'Edit Vehicle'} - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head><body class="admin-body"><div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main"><%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %><div class="admin-content">
        <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>
        <section class="admin-panel"><div class="admin-panel-header">
            <div><h2>${empty vehicle ? 'Create Vehicle' : 'Edit Vehicle'}</h2><p>RENTED status is controlled by the booking flow.</p></div>
            <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicles">Back</a>
        </div>
        <form class="admin-form" action="${pageContext.request.contextPath}/admin/vehicles/save" method="POST">
            <input type="hidden" name="vehicleId" value="${vehicle.vehicleId}">
            <div class="admin-form-grid">
                <div class="admin-field"><label>License Plate</label><input type="text" name="licensePlate" value="${vehicle.licensePlate}" required maxlength="20"></div>
                <div class="admin-field"><label>Color</label><input type="text" name="color" value="${vehicle.color}" maxlength="50"></div>
                <div class="admin-field"><label>Vehicle Model</label><select name="modelId" required><option value="">Select model</option><c:forEach var="model" items="${models}"><option value="${model.modelId}" ${vehicle.modelId eq model.modelId ? 'selected' : ''}><c:out value="${model.name}"/>${not empty model.brand ? ' - ' : ''}<c:out value="${model.brand}"/></option></c:forEach></select></div>
                <div class="admin-field"><label>Station</label><select name="stationId" required><option value="">Select station</option><c:forEach var="station" items="${stations}"><option value="${station.stationId}" ${vehicle.stationId eq station.stationId ? 'selected' : ''}><c:out value="${station.name}"/></option></c:forEach></select></div>
                <div class="admin-field"><label>Battery Level (%)</label><input type="number" name="batteryLevel" value="${empty vehicle ? 100 : vehicle.batteryLevel}" min="0" max="100" required></div>
                <div class="admin-field"><label>Status</label><select name="status" required><c:forEach var="item" items="${vehicleStatuses}"><option value="${item}" ${(empty vehicle and item eq 'AVAILABLE') or vehicle.status eq item ? 'selected' : ''}>${item}</option></c:forEach></select></div>
            </div>
            <div class="admin-form-actions"><button class="admin-button" type="submit">Save Vehicle</button><a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicles">Cancel</a></div>
        </form>
        </section>
    </div></main>
</div></body></html>
