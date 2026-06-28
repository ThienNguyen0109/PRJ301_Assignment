<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Model Image Detail - Admin</title>
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
                        <h2>Model Image Detail</h2>
                        <p>Read-only image record information.</p>
                    </div>
                    <div class="inline-actions">
                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-model-images">Back</a>
                        <c:if test="${not empty image}">
                            <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-vehicle-model-image-form&id=${image.imageId}">Edit</a>
                        </c:if>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${not empty image}">
                        <div class="admin-detail-grid">
                            <div class="admin-detail-item"><span>Image ID</span><strong><c:out value="${image.imageId}"/></strong></div>
                            <div class="admin-detail-item"><span>Model ID</span><strong><c:out value="${image.modelId}"/></strong></div>
                            <div class="admin-detail-item"><span>Image Type</span><strong><c:out value="${image.imageType.value}"/></strong></div>
                            <div class="admin-detail-item full"><span>Image URL</span><strong><c:out value="${image.imageUrl}"/></strong></div>
                            <div class="admin-detail-item full">
                                <span>Preview</span>
                                <p><img class="admin-thumb" style="width:220px;height:150px" src="${pageContext.request.contextPath}/${image.imageUrl}" alt="Preview"></p>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="admin-detail-grid"><div class="admin-detail-item full"><strong>Image not found.</strong></div></div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
    </main>
</div>
</body>
</html>
