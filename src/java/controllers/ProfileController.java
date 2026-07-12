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
        if (phone != null) {
            phone = phone.trim();
        }

        if (isBlank(phone)) {
            session.setAttribute("profileError", "Vui lòng nhập số điện thoại.");
            response.sendRedirect(request.getContextPath() + "?action=profile");
            return;
        }

        if (!phone.matches("\\d{10,11}")) {
            session.setAttribute("profileError", "Số điện thoại phải gồm 10 đến 11 chữ số.");
            response.sendRedirect(request.getContextPath() + "?action=profile");
            return;
        }

        boolean updated = accountDAO.updatePhone(user.getAccountId(), phone);
        if (!updated) {
            session.setAttribute("profileError", "Không thể cập nhật số điện thoại. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "?action=profile");
            return;
        }

        user.setPhone(phone);
        session.setAttribute("user", user);
        session.setAttribute("profileSuccess", "Đã cập nhật số điện thoại thành công.");
        response.sendRedirect(request.getContextPath() + "?action=profile");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
