<%--
    Document   : wallet
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
        <title><fmt:message key="wallet.title"/></title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer.css">
    </head>
    <body class="customer-page wallet-page">
        <c:set var="navName" value="${empty sessionScope.user.fullName ? 'User' : sessionScope.user.fullName}" />
        <c:set var="navInitial" value="${fn:substring(navName, 0, 1)}" />

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
                        <a class="active" href="${pageContext.request.contextPath}?action=wallet"><fmt:message key="nav.wallet"/></a>
                        <a href="${pageContext.request.contextPath}?action=profile"><fmt:message key="nav.profile"/></a>
                    </div>
                </details>
                <%@ include file="/WEB-INF/jspf/customer-language-switch.jspf" %>
                <a class="logout-link" href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.logout"/></a>
            </div>
        </nav>

        <main class="customer-container">
            <section class="wallet-grid">
                <div class="wallet-balance-card">
                    <span class="kicker"><fmt:message key="wallet.kicker"/></span>
                    <p class="muted"><fmt:message key="wallet.currentBalance"/></p>
                    <div class="wallet-balance" data-realtime-wallet-balance>
                        <c:choose>
                            <c:when test="${not empty wallet}"><fmt:formatNumber value="${wallet.balance}" pattern="#,##0.00" /> VND</c:when>
                            <c:otherwise>0 VND</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="hero-actions">
                        <a class="btn-gold" href="#topupForm"><fmt:message key="wallet.topup"/></a>
                        <a class="btn-ghost" href="#transactionHistory"><fmt:message key="wallet.history"/></a>
                    </div>
                </div>

                <section class="glass-card" id="topupForm" style="margin-top:0;">
                    <div class="section-head">
                        <div>
                            <span class="kicker">Top Up</span>
                            <h2><fmt:message key="wallet.topupTitle"/></h2>
                            <p><fmt:message key="wallet.topupCopy"/></p>
                        </div>
                    </div>

                    <c:if test="${topupSuccess}">
                        <div class="alert success"><fmt:message key="wallet.topupSuccess"/> <fmt:formatNumber value="${topupSuccessAmount}" pattern="#,##0" /> VND <fmt:message key="wallet.toWallet"/></div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="alert error"><c:out value="${error}"/></div>
                    </c:if>

                    <c:if test="${not empty paymentError}">
                        <div class="alert error">
                            <c:choose>
                                <c:when test="${paymentError eq 'payment_failed'}"><fmt:message key="wallet.paymentFailed"/></c:when>
                                <c:when test="${paymentError eq 'invalid_hash'}"><fmt:message key="wallet.invalidHash"/></c:when>
                                <c:when test="${paymentError eq 'order_mismatch'}"><fmt:message key="wallet.orderMismatch"/></c:when>
                                <c:when test="${paymentError eq 'update_failed'}"><fmt:message key="wallet.updateFailed"/></c:when>
                                <c:when test="${paymentError eq 'system_error'}"><fmt:message key="wallet.systemError"/></c:when>
                                <c:otherwise><fmt:message key="wallet.cannotProcess"/></c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/topup" method="POST">
                        <div class="field">
                            <label><fmt:message key="wallet.quickAmount"/></label>
                            <div class="preset-grid">
                                <button type="button" class="preset-btn" onclick="setAmount(100000)">100.000</button>
                                <button type="button" class="preset-btn" onclick="setAmount(200000)">200.000</button>
                                <button type="button" class="preset-btn" onclick="setAmount(500000)">500.000</button>
                                <button type="button" class="preset-btn" onclick="setAmount(1000000)">1.000.000</button>
                            </div>
                        </div>

                        <div class="field">
                            <label for="amountDisplay"><fmt:message key="wallet.customAmount"/></label>
                            <input class="wallet-input money-display-input" type="text" id="amountDisplay" inputmode="numeric" value="${amount}" placeholder="10,000 - 10,000,000"
                                   data-money-target="amount" data-money-min="10000" data-money-max="10000000" required>
                            <input type="hidden" id="amount" name="amount" value="${amount}">
                        </div>

                        <button type="submit" class="btn-gold" style="width:100%; margin-top:14px;"><fmt:message key="wallet.payVnpay"/></button>
                    </form>
                </section>
            </section>

            <section class="glass-card" id="transactionHistory">
                <div class="section-head">
                    <div>
                        <span class="kicker">Transaction History</span>
                        <h2><fmt:message key="wallet.history"/></h2>
                        <p><fmt:message key="wallet.historyCopy"/></p>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty transactions}">
                        <div class="table-tools">
                            <input id="transactionSearch" type="search" placeholder="Search transaction or description">
                            <select id="transactionTypeFilter">
                                <option value="ALL">All Types</option>
                                <option value="TOPUP">TOPUP</option>
                                <option value="PAYMENT">PAYMENT</option>
                                <option value="REFUND">REFUND</option>
                            </select>
                        </div>
                        <div class="table-wrap">
                            <table class="customer-table" id="transactionTable">
                                <thead>
                                    <tr>
                                        <th>Transaction ID</th>
                                        <th><fmt:message key="wallet.type"/></th>
                                        <th><fmt:message key="wallet.amount"/></th>
                                        <th><fmt:message key="wallet.description"/></th>
                                        <th><fmt:message key="wallet.createdAt"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="trans" items="${transactions}">
                                        <c:set var="typeValue" value="${trans.type.value}"/>
                                        <c:set var="typeClass" value="type-topup"/>
                                        <c:if test="${typeValue eq 'PAYMENT'}"><c:set var="typeClass" value="type-payment"/></c:if>
                                        <c:if test="${typeValue eq 'REFUND'}"><c:set var="typeClass" value="type-refund"/></c:if>
                                        <tr data-type="${typeValue}" data-search="${fn:toLowerCase(empty trans.transactionId ? '' : trans.transactionId)} ${fn:toLowerCase(empty trans.description ? '' : trans.description)} ${fn:toLowerCase(typeValue)}">
                                            <td><span class="code-text"><c:choose><c:when test="${not empty trans.transactionId}"><c:out value="${fn:substring(trans.transactionId, 0, 8)}"/>...</c:when><c:otherwise>-</c:otherwise></c:choose></span></td>
                                            <td><span class="type-badge ${typeClass}"><c:out value="${typeValue}"/></span></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${typeValue eq 'PAYMENT'}"><span class="amount-negative">-<fmt:formatNumber value="${trans.amount}" pattern="#,##0.00" /> VND</span></c:when>
                                                    <c:otherwise><span class="amount-positive">+<fmt:formatNumber value="${trans.amount}" pattern="#,##0.00" /> VND</span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td><c:out value="${trans.description}"/></td>
                                            <td><c:out value="${trans.createdAt}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="pagination-bar"><span id="transactionCount"></span><div class="pagination-actions" id="transactionPagination"></div></div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state"><fmt:message key="wallet.noTransactions"/></div>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="glass-card">
                <div class="section-head">
                    <div>
                        <span class="kicker">Wallet Information</span>
                        <h2><fmt:message key="wallet.infoTitle"/></h2>
                    </div>
                </div>
                <div class="why-grid">
                    <article class="benefit-card"><h3>TOPUP</h3><p><fmt:message key="wallet.topupInfo"/></p></article>
                    <article class="benefit-card"><h3>PAYMENT</h3><p><fmt:message key="wallet.paymentInfo"/></p></article>
                    <article class="benefit-card"><h3>REFUND</h3><p><fmt:message key="wallet.refundInfo"/></p></article>
                    <article class="benefit-card"><h3>Bảo mật</h3><p><fmt:message key="wallet.securityInfo"/></p></article>
                </div>
            </section>

            <footer class="footer-card">
                <div><strong>E-Vehicle Rental System</strong><br>Ví điện tử cho đặt xe nhanh và rõ ràng.</div>
                <div>Liên hệ<br>hotro@evehicle.vn</div>
                <div>Hỗ trợ<br>TOPUP, PAYMENT, REFUND</div>
                <div>© 2026 E-Vehicle Rental System</div>
            </footer>
        </main>

        <script src="${pageContext.request.contextPath}/assets/js/money-input.js"></script>
        <script>
            function setAmount(amount) {
                var display = document.getElementById('amountDisplay');
                var hidden = document.getElementById('amount');
                if (display && hidden) {
                    hidden.value = amount;
                    display.value = window.formatMoneyInputValue ? window.formatMoneyInputValue(amount) : amount;
                    if (window.syncMoneyInput) {
                        window.syncMoneyInput(display);
                    }
                }
            }

            (function () {
                const table = document.getElementById('transactionTable');
                if (!table) return;
                const rows = Array.from(table.querySelectorAll('tbody tr'));
                const search = document.getElementById('transactionSearch');
                const typeFilter = document.getElementById('transactionTypeFilter');
                const pagination = document.getElementById('transactionPagination');
                const count = document.getElementById('transactionCount');
                const pageSize = 8;
                let page = 1;
                let filtered = rows.slice();

                function applyFilter() {
                    const keyword = (search.value || '').toLowerCase().trim();
                    const type = typeFilter.value;
                    filtered = rows.filter(row => {
                        const matchType = type === 'ALL' || row.dataset.type === type;
                        const matchText = !keyword || (row.dataset.search || '').indexOf(keyword) >= 0;
                        return matchType && matchText;
                    });
                    page = 1;
                    render();
                }

                function render() {
                    const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize));
                    page = Math.min(page, totalPages);
                    rows.forEach(row => row.style.display = 'none');
                    filtered.slice((page - 1) * pageSize, page * pageSize).forEach(row => row.style.display = '');
                    count.textContent = filtered.length ? `Hiển thị ${(page - 1) * pageSize + 1} - ${Math.min(page * pageSize, filtered.length)} / ${filtered.length} giao dịch` : 'Không có giao dịch phù hợp';
                    pagination.innerHTML = '';
                    const previous = document.createElement('button');
                    previous.type = 'button';
                    previous.className = 'page-link' + (page <= 1 ? ' disabled' : '');
                    previous.textContent = 'Trước';
                    previous.disabled = page <= 1;
                    previous.onclick = function () { if (page > 1) { page--; render(); } };
                    pagination.appendChild(previous);

                    for (let i = 1; i <= totalPages; i++) {
                        const button = document.createElement('button');
                        button.type = 'button';
                        button.className = 'page-link' + (i === page ? ' active' : '');
                        button.textContent = i;
                        button.onclick = function () { page = i; render(); };
                        pagination.appendChild(button);
                    }

                    const next = document.createElement('button');
                    next.type = 'button';
                    next.className = 'page-link' + (page >= totalPages ? ' disabled' : '');
                    next.textContent = 'Sau';
                    next.disabled = page >= totalPages;
                    next.onclick = function () { if (page < totalPages) { page++; render(); } };
                    pagination.appendChild(next);
                }

                search.addEventListener('input', applyFilter);
                typeFilter.addEventListener('change', applyFilter);
                render();
            })();
        </script>
        <%@ include file="/WEB-INF/jspf/realtime-client.jspf" %>
    </body>
</html>
