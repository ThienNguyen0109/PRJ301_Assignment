<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html><head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Discount Detail - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head><body class="admin-body"><div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main"><%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %><div class="admin-content"><section class="admin-panel">
        <div class="admin-panel-header"><div><h2>Discount Detail</h2><p>Promotion code configuration and usage protection.</p></div><div class="inline-actions"><a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-discounts">Back</a><c:if test="${not empty discount}"><a class="admin-button" href="${pageContext.request.contextPath}?action=admin-discount-form&id=${discount.discountId}">Edit</a></c:if></div></div>
        <c:choose><c:when test="${not empty discount}"><div class="admin-detail-grid">
            <div class="admin-detail-item"><span>Code</span><strong><c:out value="${discount.code}"/></strong></div>
            <div class="admin-detail-item"><span>Discount</span><strong><c:out value="${discount.discountPercent}"/>%</strong></div>
            <div class="admin-detail-item"><span>Quantity Remaining</span><strong><c:out value="${discount.quantity}"/></strong></div>
            <div class="admin-detail-item"><span>Expires At</span><strong><fmt:formatDate value="${discount.expiredAt}" pattern="dd/MM/yyyy HH:mm"/></strong></div>
            <div class="admin-detail-item"><span>Usage</span><strong>${discountHasUsage ? 'Applied to rental(s)' : 'Not used yet'}</strong></div>
            <div class="admin-detail-item full"><span>Discount ID</span><strong><c:out value="${discount.discountId}"/></strong></div>
        </div></c:when><c:otherwise><div class="admin-detail-grid"><div class="admin-detail-item full"><strong>Discount not found.</strong></div></div></c:otherwise></c:choose>
    </section></div></main>
</div></body></html>
