<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - E-Vehicle Rental</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
    <div class="admin-shell">
        <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
        <main class="admin-main">
            <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
            <div class="admin-content">
                <section class="admin-hero">
                    <div class="admin-hero-content">
                        <span class="admin-badge">Executive Control</span>
                        <h1>Operate finance, stations, and fleet performance from one admin workspace.</h1>
                        <p>
                            Live overview from payments, rentals, vehicles, and extra charges.
                            Use the reporting modules to inspect finance, station, and model performance.
                        </p>
                    </div>
                    <div class="admin-hero-panel">
                        <c:forEach var="stat" items="${adminStats}">
                            <div class="hero-metric">
                                <span><c:out value="${stat.label}"/></span>
                                <strong><c:out value="${stat.value}"/></strong>
                            </div>
                        </c:forEach>
                    </div>
                </section>

                <section class="admin-section admin-grid">
                    <c:forEach var="stat" items="${adminStats}">
                        <article class="admin-card">
                            <div class="admin-card-label"><c:out value="${stat.label}"/></div>
                            <div class="admin-card-value"><c:out value="${stat.value}"/></div>
                            <div class="admin-card-foot">Live database metric</div>
                        </article>
                    </c:forEach>
                </section>

                <section class="admin-section admin-workspace">
                    <div class="admin-panel">
                        <div class="admin-panel-header">
                            <div>
                                <h2>Recent Rentals</h2>
                                <p>Latest rental records from the current database.</p>
                            </div>
                            <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-stations">Start CRUD</a>
                        </div>
                        <div class="admin-table-wrap">
                            <table class="admin-table">
                                <thead>
                                    <tr>
                                        <c:forEach var="column" items="${adminColumns}">
                                            <th><c:out value="${column}"/></th>
                                        </c:forEach>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="row" items="${adminRows}">
                                        <tr>
                                            <c:forEach var="cell" items="${row}" varStatus="status">
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${status.last}">
                                                            <span class="status-chip"><c:out value="${cell}"/></span>
                                                        </c:when>
                                                        <c:otherwise><c:out value="${cell}"/></c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </c:forEach>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <aside class="admin-tools">
                        <div class="tool-card">
                            <h3>Financial Reports</h3>
                            <p>Revenue, payment method mix, pending charges, and extra charge monitoring.</p>
                        </div>
                        <div class="tool-card">
                            <h3>Station Performance</h3>
                            <p>Compare availability, rented vehicles, maintenance load, and revenue by station.</p>
                        </div>
                        <div class="tool-card">
                            <h3>CRUD Modules</h3>
                            <p>Accounts, stations, categories, models, vehicles, discounts, rentals, and payments are scaffolded.</p>
                        </div>
                    </aside>
                </section>
            </div>
        </main>
    </div>
</body>
</html>
