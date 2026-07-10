<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html><head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicle Detail - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head><body class="admin-body"><div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main"><%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %><div class="admin-content"><section class="admin-panel">
        <div class="admin-panel-header"><div><h2>Vehicle Detail</h2><p>Read-only inventory information.</p></div><div class="inline-actions"><a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicles">Back</a><c:if test="${not empty vehicle}"><a class="admin-button" href="${pageContext.request.contextPath}?action=admin-vehicle-form&id=${vehicle.vehicleId}">Edit</a></c:if></div></div>
        <c:choose><c:when test="${not empty vehicle}"><div class="admin-detail-grid">
            <div class="admin-detail-item"><span>License Plate</span><strong><c:out value="${vehicle.licensePlate}"/></strong></div>
            <div class="admin-detail-item"><span>Status</span><strong><c:out value="${vehicle.status}"/></strong></div>
            <div class="admin-detail-item"><span>Battery Level</span><strong><c:out value="${vehicle.batteryLevel}"/>%</strong></div>
            <div class="admin-detail-item"><span>Color</span><strong><c:out value="${empty vehicle.color ? '-' : vehicle.color}"/></strong></div>
            <div class="admin-detail-item full"><span>Vehicle Model ID</span><strong><c:out value="${vehicle.modelId}"/></strong></div>
            <div class="admin-detail-item full"><span>Station ID</span><strong><c:out value="${vehicle.stationId}"/></strong></div>
            <div class="admin-detail-item full"><span>Vehicle ID</span><strong><c:out value="${vehicle.vehicleId}"/></strong></div>
        </div></c:when><c:otherwise><div class="admin-detail-grid"><div class="admin-detail-item full"><strong>Vehicle not found.</strong></div></div></c:otherwise></c:choose>
    </section></div></main>
</div></body></html>
