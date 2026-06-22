<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Incident Management - E-Vehicle Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body class="staff-body">
    <%@ include file="/WEB-INF/jspf/staff-sidebar.jspf" %>
    <div class="staff-main">
        <%@ include file="/WEB-INF/jspf/staff-topbar.jspf" %>
        <main class="staff-content">
            <div class="content-header"><h1>Incident Management</h1><p>Review damage reports recorded during vehicle returns.</p></div>
            <c:if test="${not empty incidentError}"><div class="message message-error"><c:out value="${incidentError}"/></div></c:if>
            <section class="staff-card" style="margin-bottom:20px">
                <div class="card-body">
                    <form class="toolbar" action="${pageContext.request.contextPath}/" method="GET" style="grid-template-columns:220px auto 1fr">
                        <input type="hidden" name="action" value="staff-incidents">
                        <select class="form-select" name="severity" aria-label="Severity filter">
                            <option value="">All severity levels</option>
                            <option value="LOW" ${selectedSeverity eq 'LOW' ? 'selected' : ''}>LOW</option>
                            <option value="MEDIUM" ${selectedSeverity eq 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
                            <option value="HIGH" ${selectedSeverity eq 'HIGH' ? 'selected' : ''}>HIGH</option>
                        </select>
                        <button class="btn btn-primary" type="submit">Filter</button>
                    </form>
                </div>
            </section>
            <div class="two-column">
                <section class="staff-card">
                    <div class="card-header"><h2>Incident Reports</h2></div>
                    <div class="table-responsive"><table class="staff-table">
                        <thead><tr><th>Incident ID</th><th>Vehicle</th><th>Rental ID</th><th>Severity</th><th>Created</th><th>Action</th></tr></thead>
                        <tbody>
                            <c:forEach var="incident" items="${incidents}">
                                <c:url var="detailUrl" value="/"><c:param name="action" value="staff-incidents"/><c:param name="incidentId" value="${incident.incidentId}"/><c:param name="severity" value="${selectedSeverity}"/></c:url>
                                <tr>
                                    <td><strong><c:out value="${incident.incidentId}"/></strong></td>
                                    <td><c:out value="${incident.vehicleModel}"/><br><small><c:out value="${incident.licensePlate}"/></small></td>
                                    <td><c:out value="${incident.rentalId}"/></td>
                                    <td><span class="badge badge-${incident.severity.value eq 'HIGH' ? 'cancelled' : (incident.severity.value eq 'MEDIUM' ? 'pending' : 'booked')}"><c:out value="${incident.severity.value}"/></span></td>
                                    <td><fmt:formatDate value="${incident.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td><a class="btn btn-light btn-sm" href="${detailUrl}">View Detail</a></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty incidents}"><tr><td colspan="6" class="empty-state">No incident reports match this filter.</td></tr></c:if>
                        </tbody>
                    </table></div>
                </section>
                <aside class="staff-card sticky-card">
                    <div class="card-header"><h2>Incident Detail</h2></div>
                    <div class="card-body">
                        <c:choose><c:when test="${not empty selectedIncident}">
                            <dl class="detail-list">
                                <div class="detail-row"><dt>Incident ID</dt><dd><c:out value="${selectedIncident.incidentId}"/></dd></div>
                                <div class="detail-row"><dt>Rental ID</dt><dd><c:out value="${selectedIncident.rentalId}"/></dd></div>
                                <div class="detail-row"><dt>Vehicle</dt><dd><c:out value="${selectedIncident.vehicleModel}"/> - <c:out value="${selectedIncident.licensePlate}"/></dd></div>
                                <div class="detail-row"><dt>Severity</dt><dd><c:out value="${selectedIncident.severity.value}"/></dd></div>
                                <div class="detail-row"><dt>Created Date</dt><dd><fmt:formatDate value="${selectedIncident.createdAt}" pattern="dd/MM/yyyy HH:mm"/></dd></div>
                                <div class="detail-row"><dt>Description</dt><dd><c:out value="${selectedIncident.description}"/></dd></div>
                            </dl>
                        </c:when><c:otherwise><div class="empty-state">Select an incident to view its details.</div></c:otherwise></c:choose>
                    </div>
                </aside>
            </div>
        </main>
    </div>
</body>
</html>
