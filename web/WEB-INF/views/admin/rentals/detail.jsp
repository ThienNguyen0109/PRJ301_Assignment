<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html><head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rental Detail - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head><body class="admin-body"><div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main"><%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %><div class="admin-content">
        <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>
        <section class="admin-panel"><div class="admin-panel-header"><div><h2>Rental Detail</h2><p>Read-only transaction, payment, and status history.</p></div><div class="inline-actions"><a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-rentals">Back</a><c:if test="${rental.canCancel}"><form class="inline-form" action="${pageContext.request.contextPath}/admin/rentals/cancel" method="POST" onsubmit="return confirm('Cancel this booked rental?');"><input type="hidden" name="rentalId" value="${rental.rentalId}"><button class="danger-button" type="submit">Cancel Booking</button></form></c:if></div></div>
        <c:choose><c:when test="${not empty rental}"><div class="admin-detail-grid">
            <div class="admin-detail-item"><span>Status</span><strong><c:out value="${rental.status}"/></strong></div>
            <div class="admin-detail-item"><span>Total Amount</span><strong><fmt:formatNumber value="${rental.totalAmount}" pattern="#,##0"/> VND</strong></div>
            <div class="admin-detail-item"><span>Customer</span><strong><c:out value="${rental.customerName}"/></strong></div>
            <div class="admin-detail-item"><span>Contact</span><strong><c:out value="${rental.customerEmail}"/> · <c:out value="${rental.customerPhone}"/></strong></div>
            <div class="admin-detail-item"><span>Vehicle</span><strong><c:out value="${rental.vehicleModelName}"/> · <c:out value="${rental.licensePlate}"/></strong></div>
            <div class="admin-detail-item"><span>Vehicle Status</span><strong><c:out value="${rental.vehicleStatus}"/></strong></div>
            <div class="admin-detail-item"><span>Pickup Station</span><strong><c:out value="${rental.stationName}"/></strong></div>
            <div class="admin-detail-item"><span>Rental Dates</span><strong><fmt:formatDate value="${rental.startDate}" pattern="dd/MM/yyyy"/> – <fmt:formatDate value="${rental.endDate}" pattern="dd/MM/yyyy"/></strong></div>
            <div class="admin-detail-item"><span>Actual Return</span><strong><fmt:formatDate value="${rental.actualReturnDate}" pattern="dd/MM/yyyy"/></strong></div>
            <div class="admin-detail-item"><span>Total Days / Late Fee</span><strong><c:out value="${rental.totalDays}"/> / <fmt:formatNumber value="${rental.lateFee}" pattern="#,##0"/> VND</strong></div>
            <div class="admin-detail-item full"><span>Rental ID</span><strong><c:out value="${rental.rentalId}"/></strong></div>
        </div>
        <h3>Payments</h3><div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>Method</th><th>Type</th><th>Amount</th><th>Status</th><th>Transaction</th><th>Date</th></tr></thead><tbody><c:forEach var="payment" items="${payments}"><tr><td><c:out value="${payment.paymentMethod}"/></td><td><c:out value="${payment.paymentType}"/></td><td><fmt:formatNumber value="${payment.amount}" pattern="#,##0"/> VND</td><td><c:out value="${payment.status}"/></td><td><c:out value="${payment.transactionCode}"/></td><td><fmt:formatDate value="${payment.paymentDate}" pattern="dd/MM/yyyy HH:mm"/></td></tr></c:forEach><c:if test="${empty payments}"><tr><td colspan="6">No payments found.</td></tr></c:if></tbody></table></div>
        <h3>Status History</h3><div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>Status</th><th>Changed At</th></tr></thead><tbody><c:forEach var="history" items="${rentalHistory}"><tr><td><c:out value="${history.status}"/></td><td><fmt:formatDate value="${history.changedAt}" pattern="dd/MM/yyyy HH:mm"/></td></tr></c:forEach><c:if test="${empty rentalHistory}"><tr><td colspan="2">No status history found.</td></tr></c:if></tbody></table></div>
        </c:when><c:otherwise><div class="admin-detail-grid"><div class="admin-detail-item full"><strong>Rental not found.</strong></div></div></c:otherwise></c:choose>
        </section>
    </div></main>
</div></body></html>
