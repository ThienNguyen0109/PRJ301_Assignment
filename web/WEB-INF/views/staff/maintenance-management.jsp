<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Maintenance Management - E-Vehicle Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body class="staff-body">
    <%@ include file="/WEB-INF/jspf/staff-sidebar.jspf" %>
    <div class="staff-main">
        <%@ include file="/WEB-INF/jspf/staff-topbar.jspf" %>
        <main class="staff-content">
            <div class="content-header"><h1>Maintenance Management</h1><p>Track damaged vehicles and release them after maintenance is completed.</p></div>
            <c:if test="${not empty maintenanceSuccess}"><div class="message message-success"><c:out value="${maintenanceSuccess}"/></div></c:if>
            <c:if test="${not empty maintenanceError}"><div class="message message-error"><c:out value="${maintenanceError}"/></div></c:if>
            <section class="staff-card" style="margin-bottom:20px">
                <div class="card-body">
                    <form class="toolbar" action="${pageContext.request.contextPath}/" method="GET" style="grid-template-columns:minmax(0,1fr) auto">
                        <input type="hidden" name="action" value="staff-maintenance">
                        <input class="form-control" type="search" name="query" value="<c:out value='${searchQuery}'/>" placeholder="Vehicle model, license plate or description">
                        <button class="btn btn-primary" type="submit">Search</button>
                    </form>
                </div>
            </section>
            <section class="staff-card">
                <div class="card-header"><h2>Vehicles Under Maintenance</h2><span class="badge badge-maintenance">MAINTENANCE</span></div>
                <div class="table-responsive">
                    <table class="staff-table">
                        <thead><tr><th>Vehicle</th><th>License Plate</th><th>Description</th><th>Maintenance Date</th><th>Status</th><th>Action</th></tr></thead>
                        <tbody>
                            <c:forEach var="item" items="${maintenanceVehicles}">
                                <tr>
                                    <td><strong><c:out value="${item.vehicleModel}"/></strong></td>
                                    <td><c:out value="${item.licensePlate}"/></td>
                                    <td><c:out value="${item.description}"/></td>
                                    <td><fmt:formatDate value="${item.maintenanceDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td><span class="badge badge-pending"><c:out value="${item.maintenanceStatus.value}"/></span></td>
                                    <td><button class="btn btn-primary btn-sm" type="button" onclick="openMaintenanceModal('${item.maintenanceId}', '${item.licensePlate}')">Mark Completed</button></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty maintenanceVehicles}"><tr><td colspan="6" class="empty-state">No vehicles are currently waiting for maintenance completion.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
    <div class="modal-backdrop" id="maintenanceModal" role="dialog" aria-modal="true">
        <div class="confirm-modal">
            <div class="modal-header"><h3>Complete Maintenance</h3></div>
            <div class="modal-body">Confirm that vehicle <strong id="maintenancePlate"></strong> has passed inspection and can be rented again.</div>
            <div class="modal-footer"><button class="btn btn-light" type="button" onclick="closeMaintenanceModal()">Cancel</button>
                <form action="${pageContext.request.contextPath}/staff/maintenance" method="POST"><input type="hidden" name="maintenanceId" id="maintenanceId"><button class="btn btn-primary" type="submit">Mark Completed</button></form>
            </div>
        </div>
    </div>
    <script>
        function openMaintenanceModal(id, plate) {
            document.getElementById('maintenanceId').value = id;
            document.getElementById('maintenancePlate').textContent = plate;
            document.getElementById('maintenanceModal').classList.add('open');
        }
        function closeMaintenanceModal() { document.getElementById('maintenanceModal').classList.remove('open'); }
    </script>
</body>
</html>
