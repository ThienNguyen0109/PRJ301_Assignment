<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicle Model Images - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content">
            <c:if test="${not empty adminSuccess}"><div class="admin-message success"><c:out value="${adminSuccess}"/></div></c:if>
            <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>
            <section class="admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2>Vehicle Model Image Management</h2>
                        <p>Manage model image paths and image types used by customer-facing vehicle cards.</p>
                    </div>
                    <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-vehicle-model-image-form">Add Image</a>
                </div>
                <form class="form-row" action="${pageContext.request.contextPath}/" method="GET">
                    <input type="hidden" name="action" value="admin-vehicle-model-images">
                    <input type="search" name="keyword" value="${keyword}" placeholder="Search model or image URL">
                    <select name="modelId">
                        <option value="ALL">All Models</option>
                        <c:forEach var="model" items="${models}">
                            <option value="${model.modelId}" ${selectedModelId eq model.modelId ? 'selected' : ''}>
                                <c:out value="${model.name}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <select name="imageType">
                        <option value="ALL">All Types</option>
                        <c:forEach var="type" items="${imageTypes}">
                            <option value="${type.value}" ${selectedImageType eq type.value ? 'selected' : ''}>${type.value}</option>
                        </c:forEach>
                    </select>
                    <button class="admin-button" type="submit">Filter</button>
                </form>
                <div class="admin-table-wrap">
                    <table class="admin-table">
                        <thead>
                        <tr>
                            <th>Preview</th>
                            <th>Model</th>
                            <th>Type</th>
                            <th>Image URL</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="image" items="${images}">
                            <tr>
                                <td><img class="admin-thumb" src="${pageContext.request.contextPath}/${image.imageUrl}" alt="${image.modelName}"></td>
                                <td><strong><c:out value="${image.modelName}"/></strong></td>
                                <td><span class="status-chip"><c:out value="${image.imageType.value}"/></span></td>
                                <td><c:out value="${image.imageUrl}"/></td>
                                <td>
                                    <div class="inline-actions">
                                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-model-image-detail&id=${image.imageId}">View</a>
                                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-model-image-form&id=${image.imageId}">Edit</a>
                                        <form class="inline-form" action="${pageContext.request.contextPath}/admin/vehicle-model-images/delete" method="POST" onsubmit="return confirm('Delete this image record?');">
                                            <input type="hidden" name="imageId" value="${image.imageId}">
                                            <button class="danger-button" type="submit">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty images}">
                            <tr><td colspan="5">No vehicle model images found.</td></tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
                <%@ include file="/WEB-INF/jspf/admin-pagination.jspf" %>
            </section>
        </div>
    </main>
</div>
</body>
</html>
