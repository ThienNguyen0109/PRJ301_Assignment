<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Station Detail - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content"><section class="admin-panel">
            <div class="admin-panel-header">
                <div><h2>Station Detail</h2><p>Read-only station information.</p></div>
                <div class="inline-actions"><a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-stations">Back</a><c:if test="${not empty station}"><a class="admin-button" href="${pageContext.request.contextPath}?action=admin-station-form&id=${station.stationId}">Edit</a></c:if></div>
            </div>
            <c:choose><c:when test="${not empty station}"><div class="admin-detail-grid">
                <div class="admin-detail-item"><span>Station Name</span><strong><c:out value="${station.name}"/></strong></div>
                <div class="admin-detail-item"><span>Contact Number</span><strong><c:out value="${station.contactNumber}"/></strong></div>
                <div class="admin-detail-item full"><span>Address</span><strong><c:out value="${station.address}"/></strong></div>
                <div class="admin-detail-item full"><span>Station ID</span><strong><c:out value="${station.stationId}"/></strong></div>
            </div></c:when><c:otherwise><div class="admin-detail-grid"><div class="admin-detail-item full"><strong>Station not found.</strong></div></div></c:otherwise></c:choose>
        </section></div>
    </main>
</div>
</body>
</html>
