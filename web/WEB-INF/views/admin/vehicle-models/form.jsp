<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty model ? 'Create Vehicle Model' : 'Edit Vehicle Model'} - Admin</title>
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
                        <h2>${empty model ? 'Create Vehicle Model' : 'Edit Vehicle Model'}</h2>
                        <p>Configure category, pricing, seats, and customer-facing description.</p>
                    </div>
                    <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-models">Back</a>
                </div>
                <form class="admin-form" action="${pageContext.request.contextPath}/admin/vehicle-models/save" method="POST">
                    <input type="hidden" name="modelId" value="${model.modelId}">
                    <div class="admin-form-grid">
                        <div class="admin-field">
                            <label>Model Name</label>
                            <input type="text" name="name" value="${model.name}" required>
                        </div>
                        <div class="admin-field">
                            <label>Brand</label>
                            <input type="text" name="brand" value="${model.brand}">
                        </div>
                        <div class="admin-field">
                            <label>Category</label>
                            <select name="categoryId" required>
                                <option value="">Select category</option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}" ${model.categoryId eq category.categoryId ? 'selected' : ''}>
                                        <c:out value="${category.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="admin-field">
                            <label>Seat Count</label>
                            <input type="number" name="seatCount" min="1" value="${model.seatCount}" required>
                        </div>
                        <div class="admin-field">
                            <label>Price Per Day</label>
                            <input class="money-display-input" id="pricePerDayDisplay" type="text" inputmode="numeric" value="${model.pricePerDay}" data-money-target="pricePerDay" data-money-min="1000" required>
                            <input type="hidden" id="pricePerDay" name="pricePerDay" value="${model.pricePerDay}">
                        </div>
                        <div class="admin-field full">
                            <label>Description</label>
                            <textarea name="description"><c:out value="${model.description}"/></textarea>
                        </div>
                    </div>
                    <div class="admin-form-actions">
                        <button class="admin-button" type="submit">Save Model</button>
                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-vehicle-models">Cancel</a>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/money-input.js"></script>
</body>
</html>
