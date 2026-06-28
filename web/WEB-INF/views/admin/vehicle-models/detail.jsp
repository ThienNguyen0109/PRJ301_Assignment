<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicle Model Detail - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content">
            <section class="admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2>Vehicle Model Detail</h2>
                        <p>Read-only model information.</p>
                    </div>
                    <div class="inline-actions">
                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-models">Back</a>
                        <c:if test="${not empty model}">
                            <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-vehicle-model-form&id=${model.modelId}">Edit</a>
                        </c:if>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty model}">
                        <div class="admin-detail-grid">
                            <div class="admin-detail-item"><span>Model Name</span><strong><c:out value="${model.name}"/></strong></div>
                            <div class="admin-detail-item"><span>Brand</span><strong><c:out value="${empty model.brand ? '-' : model.brand}"/></strong></div>
                            <div class="admin-detail-item"><span>Category ID</span><strong><c:out value="${model.categoryId}"/></strong></div>
                            <div class="admin-detail-item"><span>Seat Count</span><strong><c:out value="${model.seatCount}"/></strong></div>
                            <div class="admin-detail-item"><span>Price Per Day</span><strong><fmt:formatNumber value="${model.pricePerDay}" pattern="#,##0"/> VND</strong></div>
                            <div class="admin-detail-item full"><span>Description</span><p><c:out value="${empty model.description ? '-' : model.description}"/></p></div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="admin-detail-grid"><div class="admin-detail-item full"><strong>Vehicle model not found.</strong></div></div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</div>
</body>
</html>
