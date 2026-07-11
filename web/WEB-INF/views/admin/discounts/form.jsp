<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html><head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty discount ? 'Create Discount' : 'Edit Discount'} - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head><body class="admin-body"><div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main"><%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %><div class="admin-content">
        <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>
        <section class="admin-panel"><div class="admin-panel-header">
            <div><h2>${empty discount ? 'Create Discount' : 'Edit Discount'}</h2><p>Codes and percentages cannot change after a discount has been used.</p></div>
            <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-discounts">Back</a>
        </div>
        <form class="admin-form" action="${pageContext.request.contextPath}/admin/discounts/save" method="POST">
            <input type="hidden" name="discountId" value="${discount.discountId}">
            <div class="admin-form-grid">
                <div class="admin-field"><label>Discount Code</label><input type="text" name="code" value="${discount.code}" required maxlength="50" ${discountHasUsage ? 'readonly' : ''}></div>
                <div class="admin-field"><label>Discount Percent (%)</label><input type="number" name="discountPercent" value="${discount.discountPercent}" min="1" max="100" required ${discountHasUsage ? 'readonly' : ''}></div>
                <div class="admin-field"><label>Quantity</label><input type="number" name="quantity" value="${empty discount ? 1 : discount.quantity}" min="0" required></div>
                <div class="admin-field"><label>Expiry Date</label><input type="datetime-local" name="expiredAt" value="${expiredAtInput}" required></div>
            </div>
            <c:if test="${discountHasUsage}"><p class="admin-message error">This discount has been used. Only quantity and expiry date may be updated.</p></c:if>
            <div class="admin-form-actions"><button class="admin-button" type="submit">Save Discount</button><a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-discounts">Cancel</a></div>
        </form>
        </section>
    </div></main>
</div></body></html>
