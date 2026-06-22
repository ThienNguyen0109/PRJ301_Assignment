<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${staffPageTitle} - E-Vehicle Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body class="staff-body">
    <%@ include file="/WEB-INF/jspf/staff-sidebar.jspf" %>
    <div class="staff-main">
        <%@ include file="/WEB-INF/jspf/staff-topbar.jspf" %>
        <main class="staff-content">
            <div class="content-header"><h1>${staffPageTitle}</h1><p>${moduleDescription}</p></div>

            <c:choose>
                <c:when test="${activeModule eq 'return'}">
                    <section class="staff-card">
                        <div class="card-header"><h2>Rented Vehicles Waiting For Return</h2><span class="badge badge-rented">RENTED</span></div>
                        <div class="card-body"><div class="toolbar"><input class="form-control" placeholder="Rental ID, customer or vehicle"><input class="form-control" type="date"><button class="btn btn-primary">Search</button></div></div>
                        <div class="table-responsive"><table class="staff-table"><thead><tr><th>Rental ID</th><th>Customer</th><th>Vehicle</th><th>Return Date</th><th>Status</th><th>Actions</th></tr></thead>
                            <tbody><tr><td colspan="6"><div class="module-placeholder"><div class="placeholder-icon">R</div><h2>Return workflow ready for implementation</h2><p>The next phase will connect RENTED rentals, return condition forms, damage fields and incident creation.</p></div></td></tr></tbody></table></div>
                    </section>
                </c:when>

                <c:when test="${activeModule eq 'incident'}">
                    <section class="staff-card">
                        <div class="card-header"><h2>Incident Reports</h2></div>
                        <div class="card-body"><div class="toolbar"><input class="form-control" placeholder="Incident ID, rental ID or license plate"><select class="form-select"><option>All severity</option><option>LOW</option><option>MEDIUM</option><option>HIGH</option></select><button class="btn btn-primary">Filter</button></div></div>
                        <div class="table-responsive"><table class="staff-table"><thead><tr><th>Incident ID</th><th>Vehicle</th><th>Rental ID</th><th>Severity</th><th>Created Date</th><th>Actions</th></tr></thead>
                            <tbody><tr><td colspan="6"><div class="module-placeholder"><div class="placeholder-icon">I</div><h2>Incident module ready for implementation</h2><p>The layout already supports severity filters, incident detail and rental/vehicle references.</p></div></td></tr></tbody></table></div>
                    </section>
                </c:when>

                <c:when test="${activeModule eq 'maintenance'}">
                    <section class="staff-card">
                        <div class="card-header"><h2>Vehicles Under Maintenance</h2><span class="badge badge-maintenance">MAINTENANCE</span></div>
                        <div class="card-body"><div class="toolbar"><input class="form-control" placeholder="Vehicle model or license plate"><select class="form-select"><option>All status</option><option>PENDING</option><option>COMPLETED</option></select><button class="btn btn-primary">Filter</button></div></div>
                        <div class="table-responsive"><table class="staff-table"><thead><tr><th>Vehicle</th><th>License Plate</th><th>Description</th><th>Maintenance Date</th><th>Status</th><th>Actions</th></tr></thead>
                            <tbody><tr><td colspan="6"><div class="module-placeholder"><div class="placeholder-icon">M</div><h2>Maintenance module ready for implementation</h2><p>The next phase will load MAINTENANCE vehicles and support detail and Mark Completed actions.</p></div></td></tr></tbody></table></div>
                    </section>
                </c:when>

                <c:otherwise>
                    <div class="two-column">
                        <section class="staff-card"><div class="card-header"><h2>Account Information</h2></div><div class="card-body"><dl class="detail-list">
                            <div class="detail-row"><dt>Full Name</dt><dd>${staffAccount.fullName}</dd></div><div class="detail-row"><dt>Email</dt><dd>${staffAccount.email}</dd></div>
                            <div class="detail-row"><dt>Phone</dt><dd>${empty staffAccount.phone ? 'Not updated' : staffAccount.phone}</dd></div><div class="detail-row"><dt>Role</dt><dd><span class="badge badge-booked">${staffAccount.role.value}</span></dd></div>
                            <div class="detail-row"><dt>Status</dt><dd>${staffAccount.status}</dd></div>
                        </dl></div></section>
                        <section class="staff-card"><div class="card-header"><h2>Staff Permissions</h2></div><div class="card-body"><div class="module-placeholder"><div class="placeholder-icon">U</div><h2>Operations Access</h2><p>This account can access pickup, return, maintenance and incident modules. Customer and Admin roles are blocked from Staff URLs.</p></div></div></section>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</body>
</html>
