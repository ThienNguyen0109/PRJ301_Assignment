package controllers;

import daos.AccountDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;

@WebServlet(name = "ProfileController", urlPatterns = {"/profile/update-phone"})
public class ProfileController extends HttpServlet {
    private final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return;
        }

        Account user = (Account) session.getAttribute("user");
        String phone = request.getParameter("phone");
        String returnTo = request.getParameter("returnTo");
        if (phone != null) {
            phone = phone.trim();
        }

        if (isBlank(phone)) {
            redirectPhoneUpdateError(request, response, session, returnTo, "Vui lòng nhập số điện thoại.");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            redirectPhoneUpdateError(request, response, session, returnTo, "Số điện thoại phải gồm đúng 10 chữ số.");
            return;
        }

        boolean updated = accountDAO.updatePhone(user.getAccountId(), phone);
        if (!updated) {
            redirectPhoneUpdateError(request, response, session, returnTo, "Không thể cập nhật số điện thoại. Vui lòng thử lại.");
            return;
        }

        user.setPhone(phone);
        session.setAttribute("user", user);
        if (isSafeBookingReturn(request, returnTo)) {
            session.setAttribute("bookingPhoneSuccess", "Đã cập nhật số điện thoại. Bạn có thể tiếp tục thanh toán booking.");
            response.sendRedirect(returnTo);
            return;
        }
        session.setAttribute("profileSuccess", "Đã cập nhật số điện thoại thành công.");
        response.sendRedirect(request.getContextPath() + "?action=profile");
    }

    private void redirectPhoneUpdateError(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, String returnTo, String message) throws IOException {
        if (isSafeBookingReturn(request, returnTo)) {
            session.setAttribute("bookingPhoneRequired", true);
            session.setAttribute("bookingPhoneError", message);
            response.sendRedirect(returnTo);
            return;
        }
        session.setAttribute("profileError", message);
        response.sendRedirect(request.getContextPath() + "?action=profile");
    }

    private boolean isSafeBookingReturn(HttpServletRequest request, String returnTo) {
        if (isBlank(returnTo)) {
            return false;
        }
        String contextPath = request.getContextPath();
        return returnTo.startsWith(contextPath + "?action=booking")
                || returnTo.startsWith(contextPath + "/?action=booking")
                || returnTo.startsWith("?action=booking")
                || returnTo.startsWith("/?action=booking");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
