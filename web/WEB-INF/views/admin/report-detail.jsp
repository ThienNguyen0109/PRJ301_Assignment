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
            <section class="admin-section admin-grid">
                <c:forEach var="stat" items="${adminStats}">
                    <article class="admin-card">
                        <div class="admin-card-label"><c:out value="${stat.label}"/></div>
                        <div class="admin-card-value"><c:out value="${stat.value}"/></div>
                        <div class="admin-card-foot">Detail metric</div>
                    </article>
                </c:forEach>
            </section>

            <section class="admin-section admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2><c:out value="${adminPageTitle}"/></h2>
                        <p>Detailed records for the selected report row.</p>
                    </div>
                    <a class="admin-button light" href="${pageContext.request.contextPath}?action=${adminBackAction}">Back</a>
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
                                                <c:when test="${status.index eq 2 || status.index eq 3}">
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
                <%@ include file="/WEB-INF/jspf/admin-pagination.jspf" %>
            </section>
        </div>
    </main>
</div>
</body>
</html>
