<%--
    Document   : home
    Created on : June 5, 2026
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Trang Chủ - E-Vehicle Rental</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer.css">
    </head>
    <body class="customer-page">
        <c:set var="navName" value="${empty sessionScope.user.fullName ? 'User' : sessionScope.user.fullName}" />
        <c:set var="navInitial" value="${fn:substring(navName, 0, 1)}" />
        <c:set var="showPhoneUpdatePrompt" value="${sessionScope.showPhoneUpdatePrompt}" />
        <c:remove var="showPhoneUpdatePrompt" scope="session" />

        <nav class="customer-navbar">
            <a class="brand-link" href="${pageContext.request.contextPath}?action=home">
                <span class="brand-logo">EV</span>
                <span>E-Vehicle Rental System</span>
            </a>
            <div class="customer-menu">
                <a class="active" href="${pageContext.request.contextPath}?action=home">Trang Chủ</a>
                <a href="${pageContext.request.contextPath}?action=profile#rental-history">Đơn Thuê Của Tôi</a>
                <a href="${pageContext.request.contextPath}?action=wallet">Ví</a>
                <a href="${pageContext.request.contextPath}?action=profile">Profile</a>
                <span class="nav-user"><span class="nav-avatar"><c:out value="${navInitial}"/></span><c:out value="${navName}"/></span>
                <a class="logout-link" href="${pageContext.request.contextPath}/logout">Logout</a>
            </div>
        </nav>

        <main class="customer-container">
            <section class="hero-section">
                <div class="hero-content">
                    <span class="kicker">Premium Electric Mobility</span>
                    <h1 class="hero-title">Thuê xe điện linh hoạt <span>cho mọi hành trình.</span></h1>
                    <p class="hero-copy">Chọn trạm, chọn mẫu xe và đặt xe chỉ trong vài bước. Tận hưởng trải nghiệm thuê xe điện sạch, nhanh và hiện đại.</p>
                    <div class="hero-actions">
                        <a class="btn-gold" href="#search-section">Tìm xe ngay</a>
                        <a class="btn-ghost" href="#vehicle-list">Xem các trạm</a>
                    </div>
                    <div class="hero-stats">
                        <div class="stat-tile"><strong>${empty stations ? 0 : fn:length(stations)}</strong><span>Số trạm</span></div>
                        <div class="stat-tile"><strong>${empty categories ? 0 : fn:length(categories)}</strong><span>Nhóm xe</span></div>
                        <div class="stat-tile"><strong>24/7</strong><span>Hỗ trợ đặt xe</span></div>
                    </div>
                </div>
            </section>

            <section id="search-section" class="glass-card">
                <div class="section-head">
                    <div>
                        <span class="kicker">Search Vehicle</span>
                        <h2>Tìm xe điện</h2>
                        <p>Chọn trạm hoặc loại xe để xem các mẫu xe đang có. Ngày thuê sẽ được kiểm tra ở bước chọn xe.</p>
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
                                <option value="">Tất cả trạm</option>
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
                                <option value="">Tất cả loại xe</option>
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
                        <a class="category-pill" href="${pageContext.request.contextPath}?action=home">Xóa bộ lọc</a>
                    </c:if>
                </div>
            </section>

            <c:if test="${not searchPerformed and not empty featuredVehicles}">
                <section id="vehicle-list" class="glass-card">
                    <div class="section-head">
                        <div>
                            <span class="kicker">Available Models</span>
                            <h2>Mẫu xe tại các trạm</h2>
                            <p>Chọn mẫu xe rồi nhập ngày thuê để hệ thống kiểm tra xe trống chính xác.</p>
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
                <section id="vehicle-list" class="glass-card">
                    <div class="section-head">
                        <div>
                            <span class="kicker">Search Results</span>
                            <h2>Kết quả tìm kiếm</h2>
                            <p>
                                <c:choose>
                                    <c:when test="${empty selectedStationId and empty selectedCategoryId}">
                                        <span class="filter-chip">Tất cả xe có sẵn</span>
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
                            <div class="empty-state">Không tìm thấy mẫu xe phù hợp với bộ lọc đã chọn.</div>
                        </c:when>
                    </c:choose>
                </section>
            </c:if>

            <section class="glass-card">
                <div class="section-head">
                    <div>
                        <span class="kicker">Why Choose Us</span>
                        <h2>Vì sao chọn E-Vehicle Rental?</h2>
                    </div>
                </div>
                <div class="why-grid">
                    <article class="benefit-card"><h3>Nhiều trạm nhận xe</h3><p>Dễ dàng chọn trạm phù hợp với lịch trình di chuyển của bạn.</p></article>
                    <article class="benefit-card"><h3>Wallet hoặc VNPay</h3><p>Thanh toán linh hoạt, theo dõi giao dịch rõ ràng trong ví.</p></article>
                    <article class="benefit-card"><h3>Thân thiện môi trường</h3><p>Các dòng xe điện sạch, tiết kiệm và phù hợp di chuyển đô thị.</p></article>
                    <article class="benefit-card"><h3>Quy trình đơn giản</h3><p>Đặt xe, nhận xe và trả xe theo flow rõ ràng, dễ thao tác.</p></article>
                </div>
            </section>

            <footer class="footer-card">
                <div><strong>E-Vehicle Rental System</strong><br>Premium electric mobility for students and city riders.</div>
                <div>Liên hệ<br>hotro@evehicle.vn</div>
                <div>Hỗ trợ<br>Wallet, VNPay, Booking</div>
                <div>© 2026 E-Vehicle Rental System</div>
            </footer>
        </main>

        <c:if test="${showPhoneUpdatePrompt}">
            <div class="customer-modal-backdrop" id="phoneUpdateModal" role="dialog" aria-modal="true" aria-labelledby="phoneUpdateTitle">
                <div class="customer-modal">
                    <button class="modal-close" type="button" aria-label="Đóng thông báo" onclick="closePhoneUpdateModal()">×</button>
                    <span class="kicker">Complete Profile</span>
                    <h2 id="phoneUpdateTitle">Cập nhật số điện thoại</h2>
                    <p>Bạn đã đăng nhập bằng Google thành công. Hãy cập nhật số điện thoại để staff có thể xác minh khi nhận xe và để quá trình booking diễn ra trọn vẹn.</p>
                    <div class="modal-actions">
                        <a class="btn-gold" href="${pageContext.request.contextPath}?action=profile#phone-update">Cập nhật ngay</a>
                        <button class="btn-ghost" type="button" onclick="closePhoneUpdateModal()">Để sau</button>
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
