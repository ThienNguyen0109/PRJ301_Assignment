<%--
    Document   : home
    Created on : June 5, 2026
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
        <title><fmt:message key="home.title"/></title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer.css">
    </head>
    <body class="customer-page">
        <c:set var="navName" value="${empty sessionScope.user.fullName ? 'User' : sessionScope.user.fullName}" />
        <c:set var="navInitial" value="${fn:substring(navName, 0, 1)}" />
        <c:set var="showPhoneUpdatePrompt" value="${sessionScope.showPhoneUpdatePrompt}" />
        <c:remove var="showPhoneUpdatePrompt" scope="session" />

        <nav class="customer-navbar">
            <a class="brand-link" href="${pageContext.request.contextPath}?action=home">
                <span class="brand-logo">
                    <img src="${pageContext.request.contextPath}/assets/images/logo/logo.png" alt="E-Vehicle Rental">
                </span>
            </a>
            <div class="customer-menu">
                <a class="active" href="${pageContext.request.contextPath}?action=home"><fmt:message key="nav.home"/></a>
                <details class="nav-account-menu">
                    <summary class="nav-user">
                        <span class="nav-avatar"><c:out value="${navInitial}"/></span>
                        <span><c:out value="${navName}"/></span>
                        <span class="nav-caret"></span>
                    </summary>
                    <div class="nav-dropdown">
                        <a href="${pageContext.request.contextPath}?action=profile#rental-history"><fmt:message key="nav.myBookings"/></a>
                        <a href="${pageContext.request.contextPath}?action=wallet"><fmt:message key="nav.wallet"/></a>
                        <a href="${pageContext.request.contextPath}?action=profile"><fmt:message key="nav.profile"/></a>
                    </div>
                </details>
                <%@ include file="/WEB-INF/jspf/customer-language-switch.jspf" %>
                <a class="logout-link" href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout"/></a>
            </div>
        </nav>

        <main class="customer-container home-container">
            <section class="hero-section">
                <div class="hero-video-layer" aria-hidden="true">
                    <video autoplay muted loop playsinline>
                        <source src="${pageContext.request.contextPath}/assets/video/istockphoto-902026438-640_adpp_is.mp4" type="video/mp4">
                    </video>
                </div>
                <div class="hero-content">
                    <span class="kicker hero-badge"><fmt:message key="home.heroKicker"/></span>
                    <h1 class="hero-title"><fmt:message key="home.heroTitle"/> <span class="highlight"><fmt:message key="home.heroHighlight"/></span></h1>
                    <p class="hero-copy hero-description"><fmt:message key="home.heroCopy"/></p>
                    <div class="hero-actions">
                        <a class="btn-gold" href="#search-section"><fmt:message key="home.findNow"/></a>
                        <a class="btn-ghost" href="#vehicle-list"><fmt:message key="home.viewStations"/></a>
                    </div>
                    <div class="hero-stats">
                        <div class="stat-tile"><strong>${empty stations ? 0 : fn:length(stations)}</strong><span><fmt:message key="home.stationCount"/></span></div>
                        <div class="stat-tile"><strong>${empty categories ? 0 : fn:length(categories)}</strong><span><fmt:message key="home.categoryCount"/></span></div>
                        <div class="stat-tile"><strong>24/7</strong><span><fmt:message key="home.support"/></span></div>
                    </div>
                </div>
            </section>

            <section id="search-section" class="glass-card search-panel">
                <div class="section-head">
                    <div>
                        <span class="kicker"><fmt:message key="home.searchKicker"/></span>
                        <h2><fmt:message key="home.searchTitle"/></h2>
                        <p><fmt:message key="home.searchCopy"/></p>
                    </div>
                </div>

                <c:if test="${not empty searchError}">
                    <div class="alert error"><c:out value="${searchError}"/></div>
                </c:if>

                <form class="search-form" action="${pageContext.request.contextPath}/" method="GET">
                    <input type="hidden" name="action" value="search">
                    <div class="field">
                        <label for="stationId">Station</label>
                        <div class="input-wrap">
                            <span class="input-icon">ST</span>
                            <select id="stationId" name="stationId">
                                <option value=""><fmt:message key="home.allStations"/></option>
                                <c:forEach var="station" items="${stations}">
                                    <option value="${station.stationId}" ${station.stationId eq selectedStationId ? 'selected' : ''}>
                                        <c:out value="${station.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="field">
                        <label for="categoryId">Category</label>
                        <div class="input-wrap">
                            <span class="input-icon">CT</span>
                            <select id="categoryId" name="categoryId">
                                <option value=""><fmt:message key="home.allCategories"/></option>
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}" ${category.categoryId eq selectedCategoryId ? 'selected' : ''}>
                                        <c:out value="${category.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <button type="submit" class="btn-gold">Search</button>
                </form>

                <div class="quick-categories">
                    <c:forEach var="category" items="${categories}">
                        <c:url var="categoryUrl" value="/">
                            <c:param name="action" value="search"/>
                            <c:if test="${not empty selectedStationId}">
                                <c:param name="stationId" value="${selectedStationId}"/>
                            </c:if>
                            <c:param name="categoryId" value="${category.categoryId}"/>
                        </c:url>
                        <a class="category-pill" href="${categoryUrl}"><c:out value="${category.name}"/></a>
                    </c:forEach>
                    <c:if test="${searchPerformed}">
                        <a class="category-pill" href="${pageContext.request.contextPath}?action=home"><fmt:message key="home.clearFilter"/></a>
                    </c:if>
                </div>
            </section>

            <c:if test="${not searchPerformed and not empty featuredVehicles}">
                <section id="vehicle-list" class="glass-card vehicle-section">
                    <div class="section-head">
                        <div>
                            <span class="kicker"><fmt:message key="home.availableKicker"/></span>
                            <h2><fmt:message key="home.availableTitle"/></h2>
                            <p><fmt:message key="home.availableCopy"/></p>
                        </div>
                    </div>
                    <div class="vehicle-grid">
                        <c:forEach var="vehicle" items="${featuredVehicles}">
                            <c:set var="cardItem" value="${vehicle}" scope="request"/>
                            <%@ include file="WEB-INF/jspf/customer-vehicle-card.jspf" %>
                        </c:forEach>
                    </div>
                    <c:set var="paginationMode" value="featured" />
                    <%@ include file="WEB-INF/jspf/home-pagination.jspf" %>
                </section>
            </c:if>

            <c:if test="${searchPerformed}">
                <section id="vehicle-list" class="glass-card vehicle-section">
                    <div class="section-head">
                        <div>
                            <span class="kicker"><fmt:message key="home.resultsKicker"/></span>
                            <h2><fmt:message key="home.resultsTitle"/></h2>
                            <p>
                                <c:choose>
                                    <c:when test="${empty selectedStationId and empty selectedCategoryId}">
                                        <span class="filter-chip"><fmt:message key="home.allAvailable"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="station" items="${stations}">
                                            <c:if test="${station.stationId eq selectedStationId}"><span class="filter-chip"><c:out value="${station.name}"/></span></c:if>
                                        </c:forEach>
                                        <c:forEach var="category" items="${categories}">
                                            <c:if test="${category.categoryId eq selectedCategoryId}"><span class="filter-chip"><c:out value="${category.name}"/></span></c:if>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${not empty vehicleSearchResults}">
                            <div class="vehicle-grid">
                                <c:forEach var="result" items="${vehicleSearchResults}">
                                    <c:set var="cardItem" value="${result}" scope="request"/>
                                    <%@ include file="WEB-INF/jspf/customer-vehicle-card.jspf" %>
                                </c:forEach>
                            </div>
                            <c:set var="paginationMode" value="search" />
                            <%@ include file="WEB-INF/jspf/home-pagination.jspf" %>
                        </c:when>
                        <c:when test="${empty searchError}">
                            <div class="empty-state"><fmt:message key="home.noResults"/></div>
                        </c:when>
                    </c:choose>
                </section>
            </c:if>

            <section class="glass-card benefit-section">
                <div class="section-head">
                    <div>
                        <span class="kicker"><fmt:message key="home.whyKicker"/></span>
                        <h2><fmt:message key="home.whyTitle"/></h2>
                    </div>
                </div>
                <div class="why-grid">
                    <article class="benefit-card"><h3><fmt:message key="home.benefitStationsTitle"/></h3><p><fmt:message key="home.benefitStationsCopy"/></p></article>
                    <article class="benefit-card"><h3><fmt:message key="home.benefitPaymentTitle"/></h3><p><fmt:message key="home.benefitPaymentCopy"/></p></article>
                    <article class="benefit-card"><h3><fmt:message key="home.benefitEcoTitle"/></h3><p><fmt:message key="home.benefitEcoCopy"/></p></article>
                    <article class="benefit-card"><h3><fmt:message key="home.benefitSimpleTitle"/></h3><p><fmt:message key="home.benefitSimpleCopy"/></p></article>
                </div>
            </section>

            <footer class="footer-card">
                <div><strong>E-Vehicle Rental System</strong><br>Premium electric mobility for students and city riders.</div>
                <div><fmt:message key="footer.contact"/><br>hotro@evehicle.vn</div>
                <div><fmt:message key="footer.support"/><br>Wallet, VNPay, Booking</div>
                <div>© 2026 E-Vehicle Rental System</div>
            </footer>
        </main>

        <c:if test="${showPhoneUpdatePrompt}">
            <div class="customer-modal-backdrop" id="phoneUpdateModal" role="dialog" aria-modal="true" aria-labelledby="phoneUpdateTitle">
                <div class="customer-modal">
                    <button class="modal-close" type="button" aria-label="Đóng thông báo" onclick="closePhoneUpdateModal()">x</button>
                    <span class="kicker"><fmt:message key="home.phonePromptKicker"/></span>
                    <h2 id="phoneUpdateTitle"><fmt:message key="home.phonePromptTitle"/></h2>
                    <p><fmt:message key="home.phonePromptCopy"/></p>
                    <div class="modal-actions">
                        <a class="btn-gold" href="${pageContext.request.contextPath}?action=profile#phone-update"><fmt:message key="home.updateNow"/></a>
                        <button class="btn-ghost" type="button" onclick="closePhoneUpdateModal()"><fmt:message key="home.later"/></button>
                    </div>
                </div>
            </div>
            <script>
                function closePhoneUpdateModal() {
                    var modal = document.getElementById('phoneUpdateModal');
                    if (modal) {
                        modal.classList.add('is-hidden');
                    }
                }
            </script>
        </c:if>

        <%@ include file="/WEB-INF/jspf/realtime-client.jspf" %>
    </body>
</html>
