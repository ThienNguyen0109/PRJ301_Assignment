<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pickup Management - E-Vehicle Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body class="staff-body">
    <%@ include file="/WEB-INF/jspf/staff-sidebar.jspf" %>
    <div class="staff-main">
        <%@ include file="/WEB-INF/jspf/staff-topbar.jspf" %>
        <main class="staff-content">
            <div class="content-header"><h1>Pickup Management</h1><p>Handle customers arriving at the station to receive booked vehicles.</p></div>
            <c:if test="${not empty pickupSuccess}"><div class="message message-success">${pickupSuccess}</div></c:if>
            <c:if test="${not empty pickupError}"><div class="message message-error">${pickupError}</div></c:if>

            <section class="staff-card" style="margin-bottom:20px">
                <div class="card-body">
                    <form class="toolbar" action="${pageContext.request.contextPath}/" method="GET">
                        <input type="hidden" name="action" value="staff-pickup">
                        <input class="form-control" type="search" name="query" value="${searchQuery}" placeholder="Rental ID, phone number or customer name">
                        <input class="form-control" type="date" name="pickupDate" value="${pickupDate}" aria-label="Pickup date">
                        <button class="btn btn-primary" type="submit">Search</button>
                    </form>
                </div>
            </section>

            <div class="two-column">
                <section class="staff-card">
                    <div class="card-header"><h2>Rentals Waiting For Pickup</h2><span class="badge badge-booked">BOOKED</span></div>
                    <div class="table-responsive">
                        <table class="staff-table">
                            <thead><tr><th>Rental ID</th><th>Customer</th><th>Phone</th><th>Vehicle</th><th>Pickup Date</th><th>Status</th><th>Actions</th></tr></thead>
                            <tbody>
                                <c:forEach var="rental" items="${bookedRentals}">
                                    <c:url var="detailUrl" value="/"><c:param name="action" value="staff-pickup"/><c:param name="rentalId" value="${rental.rentalId}"/><c:param name="query" value="${searchQuery}"/><c:param name="pickupDate" value="${pickupDate}"/></c:url>
                                    <tr>
                                        <td><strong>${rental.rentalId}</strong></td><td>${rental.customerName}</td><td>${empty rental.phone ? '-' : rental.phone}</td>
                                        <td>${rental.vehicleModel}<br><small>${rental.licensePlate}</small></td><td>${rental.startDate}</td><td><span class="badge badge-booked">${rental.status.value}</span></td>
                                        <td><div class="action-group">
                                            <a class="btn btn-light btn-sm" href="${detailUrl}">View Detail</a>
                                            <button class="btn btn-primary btn-sm" type="button" onclick="openPickupModal('confirm','${rental.rentalId}')">Confirm</button>
                                            <button class="btn btn-warning btn-sm" type="button" onclick="openPickupModal('no-show','${rental.rentalId}')">No Show</button>
                                        </div></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty bookedRentals}"><tr><td colspan="7" class="empty-state">No BOOKED rentals match the current filters.</td></tr></c:if>
                            </tbody>
                        </table>
                    </div>
                </section>

                <aside class="staff-card sticky-card">
                    <div class="card-header"><h2>Pickup Detail</h2></div>
                    <div class="card-body">
                        <c:choose><c:when test="${not empty selectedRental}">
                            <h3 style="margin-bottom:13px">Customer Information</h3>
                            <dl class="detail-list">
                                <div class="detail-row"><dt>Full Name</dt><dd>${selectedRental.customerName}</dd></div>
                                <div class="detail-row"><dt>Email</dt><dd>${selectedRental.email}</dd></div>
                                <div class="detail-row"><dt>Phone</dt><dd>${empty selectedRental.phone ? 'Not updated' : selectedRental.phone}</dd></div>
                            </dl>
                            <h3 style="margin:20px 0 13px">Rental Information</h3>
                            <dl class="detail-list">
                                <div class="detail-row"><dt>Rental ID</dt><dd>${selectedRental.rentalId}</dd></div>
                                <div class="detail-row"><dt>Rental Period</dt><dd>${selectedRental.startDate} - ${selectedRental.endDate}</dd></div>
                                <div class="detail-row"><dt>Pickup Station</dt><dd>${selectedRental.stationName}</dd></div>
                            </dl>
                            <h3 style="margin:20px 0 13px">Vehicle Information</h3>
                            <dl class="detail-list">
                                <div class="detail-row"><dt>Model</dt><dd>${selectedRental.vehicleModel}</dd></div>
                                <div class="detail-row"><dt>License Plate</dt><dd>${selectedRental.licensePlate}</dd></div>
                                <div class="detail-row"><dt>Battery Level</dt><dd>${selectedRental.batteryLevel}%</dd></div>
                                <div class="detail-row"><dt>Status</dt><dd><span class="badge badge-rented">${selectedRental.vehicleStatus.value}</span></dd></div>
                            </dl>
                            <c:if test="${selectedRental.status.value eq 'BOOKED'}"><div class="detail-actions">
                                <button class="btn btn-primary" type="button" onclick="openPickupModal('confirm','${selectedRental.rentalId}')">Confirm Pickup</button>
                                <button class="btn btn-warning" type="button" onclick="openPickupModal('no-show','${selectedRental.rentalId}')">Mark No Show</button>
                            </div></c:if>
                        </c:when><c:otherwise><div class="empty-state">Select a rental to view pickup details.</div></c:otherwise></c:choose>
                    </div>
                </aside>
            </div>
        </main>
    </div>

    <div class="modal-backdrop" id="pickupModal" role="dialog" aria-modal="true" aria-labelledby="modalTitle">
        <div class="confirm-modal">
            <div class="modal-header"><h3 id="modalTitle">Confirm action</h3><button class="btn btn-light btn-sm" type="button" onclick="closePickupModal()">Close</button></div>
            <div class="modal-body" id="modalMessage"></div>
            <div class="modal-footer"><button class="btn btn-light" type="button" onclick="closePickupModal()">Cancel</button>
                <form action="${pageContext.request.contextPath}/staff/pickup" method="POST">
                    <input type="hidden" name="pickupAction" id="modalAction"><input type="hidden" name="rentalId" id="modalRentalId">
                    <button class="btn btn-primary" id="modalConfirmButton" type="submit">Confirm</button>
                </form>
            </div>
        </div>
    </div>
    <script>
        function openPickupModal(action, rentalId) {
            var noShow = action === 'no-show';
            document.getElementById('modalAction').value = action;
            document.getElementById('modalRentalId').value = rentalId;
            document.getElementById('modalTitle').textContent = noShow ? 'Mark Rental As No Show' : 'Confirm Vehicle Pickup';
            document.getElementById('modalMessage').textContent = noShow
                    ? 'Customer did not arrive to pick up the vehicle. Are you sure?'
                    : 'Confirm that the vehicle has been delivered to the customer?';
            var button = document.getElementById('modalConfirmButton');
            button.textContent = noShow ? 'Mark No Show' : 'Confirm Pickup';
            button.className = noShow ? 'btn btn-danger' : 'btn btn-primary';
            document.getElementById('pickupModal').classList.add('open');
        }
        function closePickupModal() { document.getElementById('pickupModal').classList.remove('open'); }
        document.getElementById('pickupModal').addEventListener('click', function (event) { if (event.target === this) closePickupModal(); });
    </script>
</body>
</html>
