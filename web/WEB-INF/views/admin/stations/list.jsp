<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Stations - Admin</title>
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
                    <div><h2>Station Management</h2><p>Manage pickup and return locations.</p></div>
                    <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-station-form">Add Station</a>
                </div>
                <form class="form-row" action="${pageContext.request.contextPath}/" method="GET">
                    <input type="hidden" name="action" value="admin-stations">
                    <input type="search" name="keyword" value="${keyword}" placeholder="Search name, address, phone">
                    <button class="admin-button" type="submit">Filter</button>
                </form>
                <div class="admin-table-wrap"><table class="admin-table">
                    <thead><tr><th>Station</th><th>Address</th><th>Contact Number</th><th>Actions</th></tr></thead>
                    <tbody>
                    <c:forEach var="station" items="${stations}">
                        <tr>
                            <td><strong><c:out value="${station.name}"/></strong></td>
                            <td><c:out value="${station.address}"/></td>
                            <td><c:out value="${empty station.contactNumber ? '-' : station.contactNumber}"/></td>
                            <td><div class="inline-actions">
                                <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-station-detail&id=${station.stationId}">View</a>
                                <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-station-form&id=${station.stationId}">Edit</a>
                                <form class="inline-form" action="${pageContext.request.contextPath}/admin/stations/delete" method="POST" onsubmit="return confirm('Delete this station?');">
                                    <input type="hidden" name="stationId" value="${station.stationId}">
                                    <button class="danger-button" type="submit">Delete</button>
                                </form>
                            </div></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty stations}"><tr><td colspan="4">No stations found.</td></tr></c:if>
                    </tbody>
                </table></div>
                <%@ include file="/WEB-INF/jspf/admin-pagination.jspf" %>
            </section>
        </div>
    </main>
</div>
</body>
</html>
