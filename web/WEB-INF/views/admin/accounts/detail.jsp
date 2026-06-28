<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Detail - Admin</title>
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
                        <h2>Account Detail</h2>
                        <p>Read-only account information.</p>
                    </div>
                    <div class="inline-actions">
                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-accounts">Back</a>
                        <c:if test="${not empty account}">
                            <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-account-form&id=${account.accountId}">Edit</a>
                        </c:if>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty account}">
                        <div class="admin-detail-grid">
                            <div class="admin-detail-item"><span>Full Name</span><strong><c:out value="${account.fullName}"/></strong></div>
                            <div class="admin-detail-item"><span>Email</span><strong><c:out value="${account.email}"/></strong></div>
                            <div class="admin-detail-item"><span>Phone</span><strong><c:out value="${empty account.phone ? '-' : account.phone}"/></strong></div>
                            <div class="admin-detail-item"><span>Role</span><strong><c:out value="${account.role.value}"/></strong></div>
                            <div class="admin-detail-item"><span>Status</span><strong><c:out value="${account.status}"/></strong></div>
                            <div class="admin-detail-item"><span>Verified</span><strong><c:out value="${account.isVerified}"/></strong></div>
                            <div class="admin-detail-item full"><span>Created At</span><strong><fmt:formatDate value="${account.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/></strong></div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="admin-detail-grid"><div class="admin-detail-item full"><strong>Account not found.</strong></div></div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</div>
</body>
</html>
