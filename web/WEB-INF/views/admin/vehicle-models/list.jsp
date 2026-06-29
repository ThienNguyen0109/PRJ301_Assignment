<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicle Models - Admin</title>
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
                        <h2>Vehicle Model Management</h2>
                        <p>Manage model name, brand, category, seat count, price, and description.</p>
                    </div>
                    <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-vehicle-model-form">Add Model</a>
                </div>
                <form class="form-row" action="${pageContext.request.contextPath}/" method="GET">
                    <input type="hidden" name="action" value="admin-vehicle-models">
                    <input type="search" name="keyword" value="${keyword}" placeholder="Search model or brand">
                    <select name="categoryId">
                        <option value="ALL">All Categories</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.categoryId}" ${selectedCategoryId eq category.categoryId ? 'selected' : ''}>
                                <c:out value="${category.name}"/>
                            </option>
                        </c:forEach>
                    </select>
                    <select disabled><option>All Status</option></select>
                    <button class="admin-button" type="submit">Filter</button>
                </form>
                <div class="admin-table-wrap">
                    <table class="admin-table">
                        <thead>
                        <tr>
                            <th>Model</th>
                            <th>Brand</th>
                            <th>Category</th>
                            <th>Seats</th>
                            <th>Price/Day</th>
                            <th>Vehicles</th>
                            <th>Images</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="model" items="${models}">
                            <tr>
                                <td><strong><c:out value="${model.name}"/></strong></td>
                                <td><c:out value="${empty model.brand ? '-' : model.brand}"/></td>
                                <td><c:out value="${model.categoryName}"/></td>
                                <td><c:out value="${model.seatCount}"/></td>
                                <td><fmt:formatNumber value="${model.pricePerDay}" pattern="#,##0"/> VND</td>
                                <td><span class="status-chip"><c:out value="${model.vehicleCount}"/></span></td>
                                <td><span class="status-chip"><c:out value="${model.imageCount}"/></span></td>
                                <td>
                                    <div class="inline-actions">
                                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-model-detail&id=${model.modelId}">View</a>
                                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-model-form&id=${model.modelId}">Edit</a>
                                        <form class="inline-form" action="${pageContext.request.contextPath}/admin/vehicle-models/delete" method="POST" onsubmit="return confirm('Delete this vehicle model?');">
                                            <input type="hidden" name="modelId" value="${model.modelId}">
                                            <button class="danger-button" type="submit">Delete</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty models}">
                            <tr><td colspan="8">No vehicle models found.</td></tr>
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
