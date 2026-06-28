<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty image ? 'Create Model Image' : 'Edit Model Image'} - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content">
            <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>
            <section class="admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2>${empty image ? 'Create Model Image' : 'Edit Model Image'}</h2>
                        <p>Upload vehicle images. Files are stored under assets/images/vehicles.</p>
                    </div>
                    <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-model-images">Back</a>
                </div>
                <form class="admin-form" action="${pageContext.request.contextPath}/admin/vehicle-model-images/save" method="POST" enctype="multipart/form-data">
                    <input type="hidden" name="imageId" value="${image.imageId}">
                    <input type="hidden" name="currentImageUrl" value="${image.imageUrl}">
                    <div class="admin-form-grid">
                        <div class="admin-field">
                            <label>Vehicle Model</label>
                            <select name="modelId" required>
                                <option value="">Select model</option>
                                <c:forEach var="model" items="${models}">
                                    <option value="${model.modelId}" ${image.modelId eq model.modelId ? 'selected' : ''}>
                                        <c:out value="${model.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="admin-field">
                            <label>Image Type</label>
                            <select name="imageType" required>
                                <c:forEach var="type" items="${imageTypes}">
                                    <option value="${type.value}" ${image.imageType.value eq type.value ? 'selected' : ''}>${type.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="admin-field full">
                            <label>Image File</label>
                            <input type="file" name="imageFile" accept="image/jpeg,image/png,image/webp,image/gif" ${empty image ? 'required' : ''}>
                            <p class="admin-help-text">
                                <c:choose>
                                    <c:when test="${empty image}">
                                        Choose an image file to create this vehicle model image.
                                    </c:when>
                                    <c:otherwise>
                                        Leave empty to keep the current image.
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                        <c:if test="${not empty image.imageUrl}">
                            <div class="admin-detail-item full">
                                <span>Current Image</span>
                                <img class="admin-preview-image" src="${pageContext.request.contextPath}/${image.imageUrl}" alt="Vehicle model image">
                                <p><c:out value="${image.imageUrl}"/></p>
                            </div>
                        </c:if>
                    </div>
                    <div class="admin-form-actions">
                        <button class="admin-button" type="submit">Save Image</button>
                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-model-images">Cancel</a>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
