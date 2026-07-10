<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html><head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Discounts - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head><body class="admin-body"><div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main"><%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %><div class="admin-content">
        <c:if test="${not empty adminSuccess}"><div class="admin-message success"><c:out value="${adminSuccess}"/></div></c:if>
        <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>
        <section class="admin-panel"><div class="admin-panel-header">
            <div><h2>Discount Management</h2><p>Create promotion codes and manage their availability.</p></div>
            <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-discount-form">Add Discount</a>
        </div>
        <form class="form-row" action="${pageContext.request.contextPath}/" method="GET">
            <input type="hidden" name="action" value="admin-discounts">
            <input type="search" name="keyword" value="${keyword}" placeholder="Search discount code">
            <select name="status">
                <option value="ALL">All Statuses</option>
                <option value="ACTIVE" ${selectedStatus eq 'ACTIVE' ? 'selected' : ''}>Active</option>
                <option value="OUT_OF_STOCK" ${selectedStatus eq 'OUT_OF_STOCK' ? 'selected' : ''}>Out of stock</option>
                <option value="EXPIRED" ${selectedStatus eq 'EXPIRED' ? 'selected' : ''}>Expired</option>
            </select><button class="admin-button" type="submit">Filter</button>
        </form>
        <div class="admin-table-wrap"><table class="admin-table"><thead><tr>
            <th>Code</th><th>Discount</th><th>Quantity</th><th>Expires At</th><th>Status</th><th>Actions</th>
        </tr></thead><tbody>
        <c:forEach var="discount" items="${discounts}"><tr>
            <td><strong><c:out value="${discount.code}"/></strong></td>
            <td><c:out value="${discount.discountPercent}"/>%</td><td><c:out value="${discount.quantity}"/></td>
            <td><fmt:formatDate value="${discount.expiredAt}" pattern="dd/MM/yyyy HH:mm"/></td>
            <td><span class="status-chip"><c:choose>
                <c:when test="${discount.expiredAt le now}">EXPIRED</c:when>
                <c:when test="${discount.quantity le 0}">OUT_OF_STOCK</c:when>
                <c:otherwise>ACTIVE</c:otherwise>
            </c:choose></span></td>
            <td><div class="inline-actions">
                <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-discount-detail&id=${discount.discountId}">View</a>
                <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-discount-form&id=${discount.discountId}">Edit</a>
                <form class="inline-form" action="${pageContext.request.contextPath}/admin/discounts/delete" method="POST" onsubmit="return confirm('Delete this discount? This is allowed only when it has not been used.');">
                    <input type="hidden" name="discountId" value="${discount.discountId}"><button class="danger-button" type="submit">Delete</button>
                </form>
            </div></td>
        </tr></c:forEach>
        <c:if test="${empty discounts}"><tr><td colspan="6">No discounts found.</td></tr></c:if>
        </tbody></table></div>
        <%@ include file="/WEB-INF/jspf/admin-pagination.jspf" %>
        </section>
    </div></main>
</div></body></html>
