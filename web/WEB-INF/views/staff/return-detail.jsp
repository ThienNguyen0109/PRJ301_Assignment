<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Return Detail - E-Vehicle Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff.css">
</head>
<body class="staff-body">
    <%@ include file="/WEB-INF/jspf/staff-sidebar.jspf" %>
    <div class="staff-main">
        <%@ include file="/WEB-INF/jspf/staff-topbar.jspf" %>
        <main class="staff-content">
            <div class="content-header"><h1>Return Detail</h1><p>Review rental information and record the vehicle condition.</p></div>
            <c:if test="${not empty returnSuccess}"><div class="message message-success"><c:out value="${returnSuccess}"/></div></c:if>
            <c:if test="${not empty returnError}"><div class="message message-error"><c:out value="${returnError}"/></div></c:if>
            <c:choose>
                <c:when test="${not empty returnRental}">
                    <div class="two-column">
                        <section class="staff-card">
                            <div class="card-header"><h2>Rental Overview</h2><span class="badge badge-rented"><c:out value="${returnRental.rentalStatus.value}"/></span></div>
                            <div class="card-body">
                                <h3>Customer Information</h3>
                                <dl class="detail-list" style="margin-top:12px">
                                    <div class="detail-row"><dt>Full Name</dt><dd><c:out value="${returnRental.customerName}"/></dd></div>
                                    <div class="detail-row"><dt>Email</dt><dd><c:out value="${returnRental.email}"/></dd></div>
                                    <div class="detail-row"><dt>Phone</dt><dd><c:out value="${empty returnRental.phone ? 'Not updated' : returnRental.phone}"/></dd></div>
                                </dl>
                                <h3 style="margin:20px 0 12px">Rental Information</h3>
                                <dl class="detail-list">
                                    <div class="detail-row"><dt>Rental ID</dt><dd><c:out value="${returnRental.rentalId}"/></dd></div>
                                    <div class="detail-row"><dt>Rental Period</dt><dd><c:out value="${returnRental.startDate}"/> - <c:out value="${returnRental.endDate}"/></dd></div>
                                    <div class="detail-row"><dt>Total Amount</dt><dd><fmt:formatNumber value="${returnRental.totalAmount}" pattern="#,##0"/> VND</dd></div>
                                    <div class="detail-row"><dt>Price Per Day</dt><dd><fmt:formatNumber value="${returnRental.pricePerDay}" pattern="#,##0"/> VND</dd></div>
                                    <div class="detail-row"><dt>Pickup Station</dt><dd><c:out value="${returnRental.stationName}"/></dd></div>
                                </dl>
                                <c:if test="${returnRental.late}">
                                    <div class="message message-error" style="margin-top:18px">
                                        Vehicle returned <strong>${returnRental.estimatedLateDays}</strong> day(s) late.
                                        Late fee: <strong><fmt:formatNumber value="${returnRental.estimatedLateFee}" pattern="#,##0"/> VND</strong>.
                                    </div>
                                </c:if>
                                <h3 style="margin:20px 0 12px">Vehicle Information</h3>
                                <dl class="detail-list">
                                    <div class="detail-row"><dt>Model</dt><dd><c:out value="${returnRental.vehicleModel}"/></dd></div>
                                    <div class="detail-row"><dt>License Plate</dt><dd><c:out value="${returnRental.licensePlate}"/></dd></div>
                                    <div class="detail-row"><dt>Current Battery</dt><dd><c:out value="${returnRental.batteryLevel}"/>%</dd></div>
                                    <div class="detail-row"><dt>Vehicle Status</dt><dd><span class="badge badge-rented"><c:out value="${returnRental.vehicleStatus.value}"/></span></dd></div>
                                </dl>
                            </div>
                        </section>

                        <section class="staff-card sticky-card">
                            <div class="card-header"><h2>Vehicle Condition</h2></div>
                            <div class="card-body">
                                <c:choose><c:when test="${returnRental.rentalStatus.value eq 'RENTED' && returnRental.vehicleStatus.value eq 'RENTED'}">
                                    <form action="${pageContext.request.contextPath}/staff/return/confirm" method="POST" id="returnForm">
                                        <input type="hidden" name="rentalId" value="<c:out value='${returnRental.rentalId}'/>">
                                        <label for="batteryLevel"><strong>Battery Level</strong></label>
                                        <input class="form-control" id="batteryLevel" name="batteryLevel" type="number" min="0" max="100" value="${returnRental.batteryLevel}" required style="margin:8px 0 16px">
                                        <label for="condition"><strong>Exterior Condition</strong></label>
                                        <select class="form-select" id="condition" name="condition" onchange="toggleDamageFields()" required style="margin:8px 0 16px">
                                            <option value="NORMAL">NORMAL</option><option value="DAMAGED">DAMAGED</option>
                                        </select>
                                        <label for="notes"><strong>Notes</strong></label>
                                        <textarea class="form-textarea" id="notes" name="notes" placeholder="Inspection notes" style="margin:8px 0 16px"></textarea>
                                        <div id="damageFields" hidden>
                                            <label for="damageDescription"><strong>Damage Description</strong></label>
                                            <textarea class="form-textarea" id="damageDescription" name="damageDescription" placeholder="Describe the damage" style="margin:8px 0 16px"></textarea>
                                            <label for="severity"><strong>Severity</strong></label>
                                            <select class="form-select" id="severity" name="severity" style="margin:8px 0 16px">
                                                <option value="LOW">LOW</option><option value="MEDIUM">MEDIUM</option><option value="HIGH">HIGH</option>
                                            </select>
                                            <label for="damageFee"><strong>Damage Fee (VND)</strong></label>
                                            <input class="form-control" id="damageFee" name="damageFee" type="number" min="0" step="1000" value="0" style="margin:8px 0 16px">
                                        </div>
                                        <div id="extraChargePaymentBlock" style="margin-bottom:16px">
                                            <label for="extraChargePaymentMethod"><strong>Extra Charge Payment</strong></label>
                                            <select class="form-select" id="extraChargePaymentMethod" name="extraChargePaymentMethod" style="margin:8px 0 8px">
                                                <option value="CASH">CASH - collected by staff</option>
                                                <option value="VNPAY">VNPAY - customer pays online</option>
                                            </select>
                                            <div class="muted-text">Applies to late fee and damage fee if any.</div>
                                        </div>
                                        <button class="btn btn-primary" type="button" onclick="openReturnModal()">Confirm Return</button>
                                    </form>
                                </c:when><c:otherwise><div class="empty-state">This rental or vehicle is no longer eligible for return confirmation.</div></c:otherwise></c:choose>
                            </div>
                        </section>
                    </div>
                </c:when>
                <c:otherwise><section class="staff-card"><div class="empty-state"><a class="btn btn-light" href="${pageContext.request.contextPath}?action=staff-return">Back to Return Management</a></div></section></c:otherwise>
            </c:choose>
        </main>
    </div>
    <div class="modal-backdrop" id="returnModal" role="dialog" aria-modal="true">
        <div class="confirm-modal"><div class="modal-header"><h3>Confirm Vehicle Return</h3></div>
            <div class="modal-body">
                Confirm the inspection details and complete this rental?
                <c:if test="${not empty returnRental && returnRental.late}">
                    <div class="message message-error" style="margin-top:14px">
                        This return is ${returnRental.estimatedLateDays} day(s) late.
                        Late fee to collect after return:
                        <strong><fmt:formatNumber value="${returnRental.estimatedLateFee}" pattern="#,##0"/> VND</strong>.
                    </div>
                </c:if>
                <div class="message message-info" style="margin-top:14px">
                    If the vehicle is damaged, the damage fee will be stored as an extra charge and paid by the selected payment method.
                </div>
            </div>
            <div class="modal-footer"><button class="btn btn-light" type="button" onclick="closeReturnModal()">Cancel</button><button class="btn btn-primary" type="button" onclick="document.getElementById('returnForm').submit()">Confirm Return</button></div>
        </div>
    </div>
    <script>
        function toggleDamageFields() {
            var damaged = document.getElementById('condition').value === 'DAMAGED';
            var fields = document.getElementById('damageFields');
            fields.hidden = !damaged;
            document.getElementById('damageDescription').required = damaged;
            document.getElementById('severity').required = damaged;
            document.getElementById('damageFee').required = damaged;
        }
        function openReturnModal() {
            var form = document.getElementById('returnForm');
            if (form.reportValidity()) document.getElementById('returnModal').classList.add('open');
        }
        function closeReturnModal() { document.getElementById('returnModal').classList.remove('open'); }
    </script>
</body>
</html>
