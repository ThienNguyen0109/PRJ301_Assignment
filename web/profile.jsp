<%--
    Document   : profile
    Created on : June 8, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ include file="/WEB-INF/jspf/customer-i18n.jspf" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><fmt:message key="profile.title"/></title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer.css">
    </head>
    <body class="customer-page profile-page">
        <c:set var="navName" value="${empty displayName ? (empty sessionScope.user.fullName ? 'User' : sessionScope.user.fullName) : displayName}" />
        <c:set var="navInitial" value="${fn:substring(navName, 0, 1)}" />
        <c:set var="profileSuccessMessage" value="${sessionScope.profileSuccess}" />
        <c:set var="profileErrorMessage" value="${sessionScope.profileError}" />
        <c:remove var="profileSuccess" scope="session" />
        <c:remove var="profileError" scope="session" />

        <nav class="customer-navbar">
            <a class="brand-link" href="${pageContext.request.contextPath}?action=home">
                <span class="brand-logo">
                    <img src="${pageContext.request.contextPath}/assets/images/logo/logo.png" alt="E-Vehicle Rental">
                </span>
            </a>
            <div class="customer-menu">
                <a href="${pageContext.request.contextPath}?action=home"><fmt:message key="nav.home"/></a>
                <details class="nav-account-menu">
                    <summary class="nav-user active">
                        <span class="nav-avatar"><c:out value="${navInitial}"/></span>
                        <span><c:out value="${navName}"/></span>
                        <span class="nav-caret"></span>
                    </summary>
                    <div class="nav-dropdown">
                        <a href="${pageContext.request.contextPath}?action=profile#rental-history"><fmt:message key="nav.myBookings"/></a>
                        <a href="${pageContext.request.contextPath}?action=wallet"><fmt:message key="nav.wallet"/></a>
                        <a class="active" href="${pageContext.request.contextPath}?action=profile"><fmt:message key="nav.profile"/></a>
                    </div>
                </details>
                <%@ include file="/WEB-INF/jspf/customer-language-switch.jspf" %>
                <a class="logout-link" href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout"/></a>
            </div>
        </nav>

        <main class="customer-container profile-container">
            <section class="dark-card profile-hero-card">
                <div class="profile-header">
                    <div class="profile-avatar"><c:out value="${navInitial}"/></div>
                    <div>
                        <span class="kicker"><fmt:message key="profile.kicker"/></span>
                        <h1 class="hero-title profile-title"><c:out value="${displayName}"/></h1>
                        <p class="hero-copy"><c:out value="${displayEmail}"/></p>
                        <div class="badge-row">
                            <span class="status-badge"><c:out value="${empty profileUser.role ? 'CUSTOMER' : profileUser.role.value}"/></span>
                        </div>
                    </div>
                </div>
            </section>

            <section class="summary-grid">
                <article class="summary-tile">
                    <strong><c:out value="${empty totalRentalHistories ? 0 : totalRentalHistories}"/></strong>
                    <span><fmt:message key="profile.totalRentals"/></span>
                </article>
                <article class="summary-tile">
                    <strong data-realtime-wallet-balance>
                        <c:choose>
                            <c:when test="${not empty wallet}"><fmt:formatNumber value="${wallet.balance}" pattern="#,##0"/> VND</c:when>
                            <c:otherwise>0 VND</c:otherwise>
                        </c:choose>
                    </strong>
                    <span><fmt:message key="profile.walletBalance"/></span>
                </article>
                <article class="summary-tile">
                    <strong><c:out value="${empty rentalHistories ? 0 : fn:length(rentalHistories)}"/></strong>
                    <span><fmt:message key="profile.currentPageOrders"/></span>
                </article>
            </section>

            <c:if test="${not empty profileSuccessMessage}">
                <div class="field-success profile-message"><c:out value="${profileSuccessMessage}"/></div>
            </c:if>
            <c:if test="${not empty profileErrorMessage}">
                <div class="error-message profile-message"><c:out value="${profileErrorMessage}"/></div>
            </c:if>

            <c:if test="${empty profileUser.phone}">
                <section class="glass-card phone-update-card" id="phone-update">
                    <div class="section-head">
                        <div>
                            <span class="kicker"><fmt:message key="profile.phoneRequired"/></span>
                            <h2><fmt:message key="profile.updatePhone"/></h2>
                            <p><fmt:message key="profile.phoneCopy"/></p>
                        </div>
                    </div>
                    <form action="${pageContext.request.contextPath}/profile/update-phone" method="POST" class="booking-form phone-update-form">
                        <div class="form-row">
                            <label for="phone">Số điện thoại</label>
                            <input id="phone" type="tel" name="phone" value="${profileUser.phone}" pattern="[0-9]{10}" maxlength="10" inputmode="numeric" autocomplete="tel" placeholder="0123456789" oninput="this.value=this.value.replace(/\D/g,'').slice(0,10)" required>
                        </div>
                        <button class="btn-gold" type="submit"><fmt:message key="profile.updatePhone"/></button>
                    </form>
                </section>
            </c:if>

            <div class="profile-grid profile-main-grid">
                <section class="glass-card">
                    <div class="section-head">
                        <div>
                            <span class="kicker">Personal Information</span>
                            <h2><fmt:message key="profile.personalInfo"/></h2>
                            <p><fmt:message key="profile.personalCopy"/></p>
                        </div>
                    </div>
                    <div class="info-list">
                        <div class="info-row">
                            <span><fmt:message key="profile.fullName"/></span>
                            <strong><c:choose><c:when test="${empty displayName}"><fmt:message key="profile.notUpdated"/></c:when><c:otherwise><c:out value="${displayName}"/></c:otherwise></c:choose></strong>
                        </div>
                        <div class="info-row">
                            <span>Email</span>
                            <strong><c:choose><c:when test="${empty displayEmail}"><fmt:message key="profile.notUpdated"/></c:when><c:otherwise><c:out value="${displayEmail}"/></c:otherwise></c:choose></strong>
                        </div>
                        <div class="info-row">
                            <span><fmt:message key="profile.phone"/></span>
                            <strong><c:choose><c:when test="${empty profileUser.phone}"><fmt:message key="profile.notUpdated"/></c:when><c:otherwise><c:out value="${profileUser.phone}"/></c:otherwise></c:choose></strong>
                        </div>
                        <div class="info-row"><span><fmt:message key="profile.role"/></span><strong><c:out value="${empty profileUser.role ? 'CUSTOMER' : profileUser.role.value}"/></strong></div>
                    </div>
                    <c:if test="${not empty profileUser.phone}">
                        <form action="${pageContext.request.contextPath}/profile/update-phone" method="POST" class="booking-form phone-update-form compact">
                            <div class="form-row">
                                <label for="profilePhone"><fmt:message key="profile.updatePhone"/></label>
                                <input id="profilePhone" type="tel" name="phone" value="${profileUser.phone}" pattern="[0-9]{10}" maxlength="10" inputmode="numeric" autocomplete="tel" oninput="this.value=this.value.replace(/\D/g,'').slice(0,10)" required>
                            </div>
                            <button class="btn-gold" type="submit"><fmt:message key="profile.saveChanges"/></button>
                        </form>
                    </c:if>
                </section>

                <aside class="wallet-balance-card">
                    <span class="kicker">Wallet</span>
                    <p class="muted">Số dư hiện tại</p>
                    <div class="wallet-balance" data-realtime-wallet-balance>
                        <c:choose>
                            <c:when test="${not empty wallet}"><fmt:formatNumber value="${wallet.balance}" pattern="#,##0.00" /> VND</c:when>
                            <c:otherwise>0 VND</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="hero-actions">
                        <a class="btn-gold" href="${pageContext.request.contextPath}?action=wallet"><fmt:message key="profile.manageWallet"/></a>
                        <a class="btn-ghost" href="${pageContext.request.contextPath}?action=home"><fmt:message key="profile.findVehicle"/></a>
                    </div>
                </aside>
            </div>

            <section id="rental-history" class="glass-card">
                <div class="section-head">
                    <div>
                        <span class="kicker">My Bookings</span>
                        <h2><fmt:message key="profile.bookingHistory"/></h2>
                        <p><fmt:message key="profile.bookingCopy"/></p>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty rentalHistories}">
                        <div class="table-wrap">
                            <table class="customer-table">
                                <thead>
                                    <tr>
                                        <th><fmt:message key="profile.rentalId"/></th>
                                        <th><fmt:message key="profile.vehicle"/></th>
                                        <th><fmt:message key="profile.rentalPeriod"/></th>
                                        <th><fmt:message key="profile.actualReturn"/></th>
                                        <th><fmt:message key="profile.totalAmount"/></th>
                                        <th><fmt:message key="profile.extraCharges"/></th>
                                        <th><fmt:message key="profile.status"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="rental" items="${rentalHistories}">
                                        <tr>
                                            <td>
                                                <div class="code-text"><c:out value="${rental.rentalId}"/></div>
                                                <div class="muted"><fmt:formatDate value="${rental.createdAt}" pattern="dd/MM/yyyy HH:mm"/></div>
                                            </td>
                                            <td>
                                                <strong><c:out value="${rental.vehicleModel}"/></strong>
                                                <div class="muted"><c:out value="${rental.licensePlate}"/> · <c:out value="${rental.stationName}"/></div>
                                            </td>
                                            <td><c:out value="${rental.startDate}"/> - <c:out value="${rental.endDate}"/><div class="muted">${rental.totalDays} ngày</div></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty rental.actualReturnDate}"><c:out value="${rental.actualReturnDate}"/></c:when>
                                                    <c:otherwise><fmt:message key="profile.notReturned"/></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td><fmt:formatNumber value="${rental.totalAmount}" pattern="#,##0"/> VND</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${rental.extraChargeTotal gt 0}"><fmt:formatNumber value="${rental.extraChargeTotal}" pattern="#,##0"/> VND</c:when>
                                                    <c:otherwise>0 VND</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:set var="statusClass" value="status-booked"/>
                                                <c:if test="${rental.status.value eq 'RENTED'}"><c:set var="statusClass" value="status-rented"/></c:if>
                                                <c:if test="${rental.status.value eq 'COMPLETED'}"><c:set var="statusClass" value="status-completed"/></c:if>
                                                <c:if test="${rental.status.value eq 'CANCELLED'}"><c:set var="statusClass" value="status-cancelled"/></c:if>
                                                <c:if test="${rental.status.value eq 'NO_SHOW'}"><c:set var="statusClass" value="status-no-show"/></c:if>
                                                <span class="status-badge ${statusClass}"><c:out value="${rental.status.value}"/></span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="pagination-bar">
                            <span>Hiển thị ${rentalStartItem} - ${rentalEndItem} / ${totalRentalHistories} đơn đặt xe</span>
                            <div class="pagination">
                                <c:url var="prevRentalPageUrl" value="/"><c:param name="action" value="profile"/><c:param name="rentalPage" value="${rentalPage - 1}"/></c:url>
                                <a class="page-link ${rentalPage le 1 ? 'disabled' : ''}" href="${prevRentalPageUrl}#rental-history"><fmt:message key="common.previous"/></a>
                                <c:forEach var="pageNo" begin="1" end="${totalRentalPages}">
                                    <c:url var="rentalPageUrl" value="/"><c:param name="action" value="profile"/><c:param name="rentalPage" value="${pageNo}"/></c:url>
                                    <a class="page-link ${pageNo eq rentalPage ? 'active' : ''}" href="${rentalPageUrl}#rental-history">${pageNo}</a>
                                </c:forEach>
                                <c:url var="nextRentalPageUrl" value="/"><c:param name="action" value="profile"/><c:param name="rentalPage" value="${rentalPage + 1}"/></c:url>
                                <a class="page-link ${rentalPage ge totalRentalPages ? 'disabled' : ''}" href="${nextRentalPageUrl}#rental-history"><fmt:message key="common.next"/></a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state"><fmt:message key="profile.noBookings"/></div>
                    </c:otherwise>
                </c:choose>
            </section>

            <footer class="footer-card">
                <div><strong>E-Vehicle Rental System</strong><br>Quản lý hồ sơ, ví và lịch sử thuê xe.</div>
                <div>Liên hệ<br>hotro@evehicle.vn</div>
                <div>Hỗ trợ<br>Profile, Booking, Wallet</div>
                <div>© 2026 E-Vehicle Rental System</div>
            </footer>
        </main>
        <%@ include file="/WEB-INF/jspf/realtime-client.jspf" %>
    </body>
</html>
