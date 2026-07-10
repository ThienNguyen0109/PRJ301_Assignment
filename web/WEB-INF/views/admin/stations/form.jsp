<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty station ? 'Create Station' : 'Edit Station'} - Admin</title>
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
                    <div><h2>${empty station ? 'Create Station' : 'Edit Station'}</h2><p>Set the station name, address, and contact number.</p></div>
                    <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-stations">Back</a>
                </div>
                <form class="admin-form" action="${pageContext.request.contextPath}/admin/stations/save" method="POST">
                    <input type="hidden" name="stationId" value="${station.stationId}">
                    <div class="admin-form-grid">
                        <div class="admin-field"><label>Station Name</label><input type="text" name="name" value="${station.name}" required maxlength="100"></div>
                        <div class="admin-field"><label>Contact Number</label><input type="text" name="contactNumber" value="${station.contactNumber}" required maxlength="20"></div>
                        <div class="admin-field full"><label>Address</label><textarea name="address" required rows="4"><c:out value="${station.address}"/></textarea></div>
                    </div>
                    <div class="admin-form-actions"><button class="admin-button" type="submit">Save Station</button><a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-stations">Cancel</a></div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
