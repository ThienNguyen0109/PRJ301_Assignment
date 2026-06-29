<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accounts - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content">
            <c:if test="${not empty adminSuccess}"><div class="admin-message success"><c:out value="${adminSuccess}"/></div></c:if>
            <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>

            <section class="admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2>Account Management</h2>
                        <p>Create staff/admin/customer accounts, lock users, and review account information.</p>
                    </div>
                    <a class="admin-button" href="${pageContext.request.contextPath}?action=admin-account-form">Add Account</a>
                </div>

                <form class="form-row" action="${pageContext.request.contextPath}/" method="GET">
                    <input type="hidden" name="action" value="admin-accounts">
                    <input type="search" name="keyword" value="${keyword}" placeholder="Search name, email, phone">
                    <select name="role">
                        <option value="ALL">All Roles</option>
                        <c:forEach var="role" items="${roles}">
                            <option value="${role.value}" ${selectedRole eq role.value ? 'selected' : ''}>${role.value}</option>
                        </c:forEach>
                    </select>
                    <select name="status">
                        <option value="ALL" ${selectedStatus eq 'ALL' ? 'selected' : ''}>All Status</option>
                        <option value="ACTIVE" ${selectedStatus eq 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                        <option value="INACTIVE" ${selectedStatus eq 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                        <option value="LOCKED" ${selectedStatus eq 'LOCKED' ? 'selected' : ''}>LOCKED</option>
                    </select>
                    <button class="admin-button" type="submit">Filter</button>
                </form>

                <div class="admin-table-wrap">
                    <table class="admin-table">
                        <thead>
                        <tr>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Role</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="account" items="${accounts}">
                            <tr>
                                <td><strong><c:out value="${account.fullName}"/></strong></td>
                                <td><c:out value="${account.email}"/></td>
                                <td><c:out value="${empty account.phone ? '-' : account.phone}"/></td>
                                <td><span class="status-chip"><c:out value="${account.role.value}"/></span></td>
                                <td><span class="status-chip"><c:out value="${account.status}"/></span></td>
                                <td><fmt:formatDate value="${account.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td>
                                    <div class="inline-actions">
                                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-account-detail&id=${account.accountId}">View</a>
                                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-account-form&id=${account.accountId}">Edit</a>
                                        <form class="inline-form" action="${pageContext.request.contextPath}/admin/accounts/status" method="POST">
                                            <input type="hidden" name="accountId" value="${account.accountId}">
                                            <input type="hidden" name="status" value="${account.status eq 'ACTIVE' ? 'LOCKED' : 'ACTIVE'}">
                                            <button class="danger-button" type="submit">${account.status eq 'ACTIVE' ? 'Lock' : 'Unlock'}</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty accounts}">
                            <tr><td colspan="7">No accounts found.</td></tr>
                        </c:if>
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
