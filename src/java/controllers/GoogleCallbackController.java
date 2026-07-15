package controllers;

import enums.Role;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import services.GoogleAuthService;
import utils.RequestUrlUtil;

@WebServlet(name = "GoogleCallbackController", urlPatterns = {"/google-callback"})
public class GoogleCallbackController extends HttpServlet {
    private final GoogleAuthService googleAuthService = new GoogleAuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        String googleError = request.getParameter("error");
        if (googleError != null && !googleError.trim().isEmpty()) {
            session.setAttribute("loginError", "Đăng nhập Google đã bị hủy hoặc thất bại.");
            response.sendRedirect(request.getContextPath() + "?action=login");
            return;
        }

        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String expectedState = (String) session.getAttribute("googleOAuthState");
        session.removeAttribute("googleOAuthState");

        if (isBlank(code) || isBlank(state) || expectedState == null || !expectedState.equals(state)) {
            session.setAttribute("loginError", "Phiên đăng nhập Google không hợp lệ. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "?action=login");
            return;
        }

        try {
            String redirectUri = RequestUrlUtil.buildUrl(request, "/google-callback");
            Account account = googleAuthService.handleCallback(getServletContext(), code, redirectUri);
            session = createFreshLoginSession(request, account);
            if (account != null && account.getRole() == Role.CUSTOMER && isBlank(account.getPhone())) {
                session.setAttribute("showPhoneUpdatePrompt", true);
                response.sendRedirect(request.getContextPath() + "?action=home");
                return;
            }
            response.sendRedirect(request.getContextPath() + getRedirectPageByRole(account));
        } catch (RuntimeException ex) {
            session.setAttribute("loginError", ex.getMessage());
            response.sendRedirect(request.getContextPath() + "?action=login");
        }
    }

    private HttpSession createFreshLoginSession(HttpServletRequest request, Account account) {
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("user", account);
        session.setAttribute("userId", account.getAccountId());
        session.setAttribute("userEmail", account.getEmail());
        session.setAttribute("userRole", account.getRole().getValue());
        session.setAttribute("userName", account.getFullName());
        return session;
    }

    private String getRedirectPageByRole(Account account) {
        if (account != null && account.getRole() == Role.ADMIN) {
            return "?action=admin-dashboard";
        }
        if (account != null && account.getRole() == Role.STAFF) {
            return "?action=staff-dashboard";
        }
        return "?action=home";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
