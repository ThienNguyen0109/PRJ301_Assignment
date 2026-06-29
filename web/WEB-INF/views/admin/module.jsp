<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${adminPageTitle}"/> - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
    <div class="admin-shell">
        <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
        <main class="admin-main">
            <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
            <div class="admin-content">
                <section class="admin-hero" style="min-height:220px">
                    <div class="admin-hero-content">
                        <span class="admin-badge"><c:out value="${adminPageBadge}"/></span>
                        <h1><c:out value="${adminPageTitle}"/></h1>
                        <p><c:out value="${adminPageSubtitle}"/></p>
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

                <c:if test="${adminChartMode ne 'table'}">
                    <section class="admin-section report-filter-card">
                        <form class="report-filter-form" action="${pageContext.request.contextPath}/" method="GET">
                            <input type="hidden" name="action" value="${adminCurrentAction}">
                            <div class="filter-group">
                                <label>View By</label>
                                <select id="reportPeriod" name="period" onchange="toggleReportFilter()">
                                    <option value="custom" ${reportPeriod eq 'custom' ? 'selected' : ''}>Custom Date</option>
                                    <option value="month" ${reportPeriod eq 'month' ? 'selected' : ''}>Month</option>
                                    <option value="quarter" ${reportPeriod eq 'quarter' ? 'selected' : ''}>Quarter</option>
                                    <option value="year" ${reportPeriod eq 'year' ? 'selected' : ''}>Year</option>
                                </select>
                            </div>
                            <div class="filter-group report-filter-panel" data-period-panel="custom">
                                <label>Start Date</label>
                                <input type="date" name="startDate" value="${reportStartDate}">
                            </div>
                            <div class="filter-group report-filter-panel" data-period-panel="custom">
                                <label>End Date</label>
                                <input type="date" name="endDate" value="${reportEndDate}">
                            </div>
                            <div class="filter-group report-filter-panel" data-period-panel="month">
                                <label>Month</label>
                                <input type="month" name="month" value="${reportMonth}">
                            </div>
                            <div class="filter-group report-filter-panel" data-period-panel="quarter">
                                <label>Quarter</label>
                                <select name="quarter">
                                    <option value="1" ${reportQuarter eq '1' ? 'selected' : ''}>Q1</option>
                                    <option value="2" ${reportQuarter eq '2' ? 'selected' : ''}>Q2</option>
                                    <option value="3" ${reportQuarter eq '3' ? 'selected' : ''}>Q3</option>
                                    <option value="4" ${reportQuarter eq '4' ? 'selected' : ''}>Q4</option>
                                </select>
                            </div>
                            <div class="filter-group report-filter-panel" data-period-panel="quarter year">
                                <label>Year</label>
                                <select name="year">
                                    <option value="2024" ${reportYear eq '2024' ? 'selected' : ''}>2024</option>
                                    <option value="2025" ${reportYear eq '2025' ? 'selected' : ''}>2025</option>
                                    <option value="2026" ${reportYear eq '2026' ? 'selected' : ''}>2026</option>
                                    <option value="2027" ${reportYear eq '2027' ? 'selected' : ''}>2027</option>
                                </select>
                            </div>
                            <button class="admin-button" type="submit">Apply</button>
                        </form>
                    </section>

                    <section class="admin-section chart-layout">
                        <c:choose>
                            <c:when test="${adminChartMode eq 'financial'}">
                                <div class="chart-card chart-wide">
                                    <div class="chart-head">
                                        <div>
                                            <h2>
                                                <c:choose>
                                                    <c:when test="${reportPeriod eq 'custom'}">Daily Revenue Trend</c:when>
                                                    <c:when test="${reportPeriod eq 'month'}">Daily Revenue Trend</c:when>
                                                    <c:when test="${reportPeriod eq 'quarter'}">Quarterly Revenue Trend</c:when>
                                                    <c:otherwise>Year Revenue Trend</c:otherwise>
                                                </c:choose>
                                            </h2>
                                            <p>Booking revenue, extra charges, and wallet topups.</p>
                                        </div>
                                        <span class="status-chip">
                                            <c:choose>
                                                <c:when test="${reportPeriod eq 'custom'}">
                                                    <c:out value="${reportStartDate}"/> - <c:out value="${reportEndDate}"/>
                                                </c:when>
                                                <c:when test="${reportPeriod eq 'month'}">
                                                    <c:out value="${reportMonth}"/>
                                                </c:when>
                                                <c:when test="${reportPeriod eq 'quarter'}">
                                                    <c:out value="${reportYear}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${reportYear}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </span>
                                    </div>
                                    <div class="bar-chart">
                                        <c:forEach var="item" items="${adminChartPrimary}" varStatus="status">
                                            <div class="bar-item" title="${item.value}">
                                                <span class="bar ${status.index gt 8 ? 'hot' : (status.index gt 5 ? 'accent' : '')}" style="height:${item.percent}%"></span>
                                                <strong><c:out value="${item.label}"/></strong>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="chart-card">
                                    <div class="chart-head">
                                        <div>
                                            <h2>Payment Mix</h2>
                                            <p>Current successful payment split.</p>
                                        </div>
                                    </div>
                                    <c:forEach var="item" items="${adminChartSecondary}" varStatus="status">
                                        <c:if test="${status.first}">
                                            <div class="donut-chart finance-donut"><span><c:out value="${item.percent}"/>%</span></div>
                                        </c:if>
                                    </c:forEach>
                                    <div class="chart-legend">
                                        <c:forEach var="item" items="${adminChartSecondary}" varStatus="status">
                                            <div>
                                                <i style="background:${status.index == 0 ? '#2563eb' : (status.index == 1 ? '#06b6d4' : '#f59e0b')}"></i>
                                                <c:out value="${item.label}"/> - <c:out value="${item.percent}"/>% (<c:out value="${item.value}"/>)
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:when>
                            <c:when test="${adminChartMode eq 'station'}">
                                <div class="chart-card chart-wide">
                                    <div class="chart-head">
                                        <div>
                                            <h2>Station Utilization</h2>
                                            <p>Available, rented, and maintenance load by station.</p>
                                        </div>
                                        <span class="status-chip">Live Fleet</span>
                                    </div>
                                    <div class="stack-chart">
                                        <c:forEach var="item" items="${adminChartPrimary}">
                                            <div class="stack-row">
                                                <label><c:out value="${item.label}"/></label>
                                                <div class="stack-track">
                                                    <span class="stack available" style="width:${item.percent}%"></span>
                                                    <span class="stack rented" style="width:${item.secondaryPercent}%"></span>
                                                    <span class="stack maintenance" style="width:${item.tertiaryPercent}%"></span>
                                                </div>
                                                <strong><c:out value="${item.value}"/></strong>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <div class="chart-legend horizontal">
                                        <div><i style="background:#22c55e"></i>Available</div>
                                        <div><i style="background:#f59e0b"></i>Rented</div>
                                        <div><i style="background:#8b5cf6"></i>Maintenance</div>
                                    </div>
                                </div>
                                <div class="chart-card">
                                    <div class="chart-head">
                                        <div>
                                            <h2>Revenue Share</h2>
                                            <p>Top station contribution.</p>
                                        </div>
                                    </div>
                                    <c:forEach var="item" items="${adminChartSecondary}" varStatus="status">
                                        <c:if test="${status.first}">
                                            <div class="donut-chart station-donut"><span><c:out value="${item.percent}"/>%</span></div>
                                        </c:if>
                                    </c:forEach>
                                    <div class="chart-legend">
                                        <c:forEach var="item" items="${adminChartSecondary}" varStatus="status">
                                            <div>
                                                <i style="background:${status.index == 0 ? '#2563eb' : (status.index == 1 ? '#06b6d4' : '#f59e0b')}"></i>
                                                <c:out value="${item.label}"/> - <c:out value="${item.percent}"/>% (<c:out value="${item.value}"/>)
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:when>
                            <c:when test="${adminChartMode eq 'model'}">
                                <div class="chart-card chart-wide">
                                    <div class="chart-head">
                                        <div>
                                            <h2>Top Model Bookings</h2>
                                            <p>Booking volume comparison by vehicle model.</p>
                                        </div>
                                        <span class="status-chip">Demand</span>
                                    </div>
                                    <div class="horizontal-bars">
                                        <c:forEach var="item" items="${adminChartPrimary}">
                                            <div class="hbar-row">
                                                <label><c:out value="${item.label}"/></label>
                                                <span><i style="width:${item.percent}%"></i></span>
                                                <strong><c:out value="${item.value}"/></strong>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                                <div class="chart-card">
                                    <div class="chart-head">
                                        <div>
                                            <h2>Incident Ratio</h2>
                                            <p>Incidents against completed rentals.</p>
                                        </div>
                                    </div>
                                    <c:forEach var="item" items="${adminChartSecondary}" varStatus="status">
                                        <c:if test="${status.first}">
                                            <div class="donut-chart model-donut"><span><c:out value="${item.percent}"/>%</span></div>
                                        </c:if>
                                    </c:forEach>
                                    <div class="chart-legend">
                                        <c:forEach var="item" items="${adminChartSecondary}" varStatus="status">
                                            <div>
                                                <i style="background:${status.index == 0 ? '#ef4444' : '#22c55e'}"></i>
                                                <c:out value="${item.label}"/> - <c:out value="${item.value}"/>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </c:when>
                        </c:choose>
                    </section>
                </c:if>

                <section class="admin-section admin-workspace">
                    <div class="admin-panel">
                        <div class="admin-panel-header">
                            <div>
                                <h2><c:out value="${adminPageTitle}"/></h2>
                                <p>Data below is loaded from the current database for the selected period.</p>
                            </div>
                            <a class="admin-button" href="#"><c:out value="${adminPrimaryAction}"/></a>
                        </div>

                        <div class="form-row">
                            <input type="search" placeholder="${adminSearchPlaceholder}">
                            <select aria-label="Status filter">
                                <option>All Status</option>
                                <option>Active</option>
                                <option>Pending</option>
                                <option>Completed</option>
                            </select>
                            <select aria-label="Priority filter">
                                <option>All Priority</option>
                                <option>P1</option>
                                <option>P2</option>
                                <option>P3</option>
                            </select>
                            <button class="admin-button" type="button">Filter</button>
                        </div>

                        <div class="admin-table-wrap">
                            <table class="admin-table">
                                <thead>
                                    <tr>
                                        <c:forEach var="column" items="${adminColumns}">
                                            <th><c:out value="${column}"/></th>
                                        </c:forEach>
                                        <th>Actions</th>
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
                                            <td>
                                                <a class="admin-button light" href="#">View</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <aside class="admin-tools">
                        <div class="tool-card">
                            <h3>Report Source</h3>
                            <p>Metrics are calculated from Payment, Rental, Vehicle, Station, Vehicle Model, Incident, and Extra Charge data.</p>
                        </div>
                        <div class="tool-card">
                            <h3>Access Guard</h3>
                            <p>This page is forwarded through AdminController and only allows session users with ADMIN role.</p>
                        </div>
                        <div class="tool-card">
                            <h3>Recommended Pattern</h3>
                            <p>Keep calculations in service/DAO and pass DTOs into this JSP. Do not put business logic in the view.</p>
                        </div>
                    </aside>
                </section>
            </div>
        </main>
    </div>
    <script>
        function toggleReportFilter() {
            var period = document.getElementById('reportPeriod');
            if (!period) return;
            var value = period.value;
            var panels = document.querySelectorAll('[data-period-panel]');
            panels.forEach(function(panel) {
                var modes = panel.getAttribute('data-period-panel').split(' ');
                panel.hidden = modes.indexOf(value) === -1;
            });
        }
        toggleReportFilter();
    </script>
</body>
</html>
