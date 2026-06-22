<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Return Management - E-Vehicle Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body class="staff-body">
    <%@ include file="/WEB-INF/jspf/staff-sidebar.jspf" %>
    <div class="staff-main">
        <%@ include file="/WEB-INF/jspf/staff-topbar.jspf" %>
        <main class="staff-content">
            <div class="content-header">
                <h1>Return Management</h1>
                <p>Inspect and receive vehicles from active rentals.</p>
            </div>
            <c:if test="${not empty returnSuccess}"><div class="message message-success"><c:out value="${returnSuccess}"/></div></c:if>
            <c:if test="${not empty returnError}"><div class="message message-error"><c:out value="${returnError}"/></div></c:if>

            <section class="staff-card" style="margin-bottom:20px">
                <div class="card-body">
                    <form class="toolbar" action="${pageContext.request.contextPath}/" method="GET">
                        <input type="hidden" name="action" value="staff-return">
                        <input class="form-control" type="search" name="query" value="<c:out value='${searchQuery}'/>" placeholder="Rental ID, customer, phone or license plate">
                        <input class="form-control" type="date" name="endDate" value="<c:out value='${endDate}'/>" aria-label="Return date">
                        <button class="btn btn-primary" type="submit">Search</button>
                    </form>
                </div>
            </section>

            <section class="staff-card">
                <div class="card-header"><h2>Vehicles Waiting For Return</h2><span class="badge badge-rented">RENTED</span></div>
                <div class="table-responsive">
                    <table class="staff-table">
                        <thead><tr><th>Rental ID</th><th>Customer</th><th>Phone</th><th>Vehicle</th><th>Return Date</th><th>Status</th><th>Action</th></tr></thead>
                        <tbody>
                            <c:forEach var="rental" items="${rentedRentals}">
                                <c:url var="detailUrl" value="/"><c:param name="action" value="staff-return-detail"/><c:param name="rentalId" value="${rental.rentalId}"/></c:url>
                                <tr>
                                    <td><strong><c:out value="${rental.rentalId}"/></strong></td>
                                    <td><c:out value="${rental.customerName}"/></td>
                                    <td><c:out value="${empty rental.phone ? '-' : rental.phone}"/></td>
                                    <td><c:out value="${rental.vehicleModel}"/><br><small><c:out value="${rental.licensePlate}"/></small></td>
                                    <td><c:out value="${rental.endDate}"/></td>
                                    <td><span class="badge badge-rented"><c:out value="${rental.rentalStatus.value}"/></span></td>
                                    <td><a class="btn btn-primary btn-sm" href="${detailUrl}">Inspect Return</a></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty rentedRentals}"><tr><td colspan="7" class="empty-state">No RENTED rentals match the current filters.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</body>
</html>
