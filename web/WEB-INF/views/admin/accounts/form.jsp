<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty account ? 'Create Account' : 'Edit Account'} - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
</head>
<body class="admin-body">
<div class="admin-shell">
    <%@ include file="/WEB-INF/jspf/admin-sidebar.jspf" %>
    <main class="admin-main">
        <%@ include file="/WEB-INF/jspf/admin-topbar.jspf" %>
        <div class="admin-content">
            <c:if test="${not empty adminError}"><div class="admin-message error"><c:out value="${adminError}"/></div></c:if>
            <section class="admin-panel">
                <div class="admin-panel-header">
                    <div>
                        <h2>${empty account ? 'Create Account' : 'Edit Account'}</h2>
                        <p>Manage login profile, role, and account status.</p>
                    </div>
                    <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-accounts">Back</a>
                </div>
                <form class="admin-form" action="${pageContext.request.contextPath}/admin/accounts/save" method="POST">
                    <input type="hidden" name="accountId" value="${account.accountId}">
                    <div class="admin-form-grid">
                        <div class="admin-field">
                            <label>Full Name</label>
                            <input type="text" name="fullName" value="${account.fullName}" required>
                        </div>
                        <div class="admin-field">
                            <label>Email</label>
                            <input type="email" name="email" value="${account.email}" ${empty account ? 'required' : 'readonly'}>
                        </div>
                        <div class="admin-field">
                            <label>Phone</label>
                            <input type="text" name="phone" value="${account.phone}">
                        </div>
                        <div class="admin-field">
                            <label>Role</label>
                            <select name="role" required>
                                <c:forEach var="role" items="${roles}">
                                    <option value="${role.value}" ${account.role.value eq role.value ? 'selected' : ''}>${role.value}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="admin-field">
                            <label>Status</label>
                            <select name="status" required>
                                <option value="ACTIVE" ${account.status eq 'ACTIVE' || empty account ? 'selected' : ''}>ACTIVE</option>
                                <option value="INACTIVE" ${account.status eq 'INACTIVE' ? 'selected' : ''}>INACTIVE</option>
                                <option value="LOCKED" ${account.status eq 'LOCKED' ? 'selected' : ''}>LOCKED</option>
                            </select>
                        </div>
                        <c:if test="${empty account}">
                            <div class="admin-field">
                                <label>Password</label>
                                <input type="password" name="password" required>
                            </div>
                            <div class="admin-field">
                                <label>Confirm Password</label>
                                <input type="password" name="confirmPassword" required>
                            </div>
                        </c:if>
                    </div>
                    <div class="admin-form-actions">
                        <button class="admin-button" type="submit">Save Account</button>
                        <a class="admin-button light" href="${pageContext.request.contextPath}?action=admin-accounts">Cancel</a>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
