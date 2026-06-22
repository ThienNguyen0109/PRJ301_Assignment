package controllers;

import daos.StaffDashboardDAO;
import enums.Role;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;

@WebServlet(name = "StaffDashboardController", urlPatterns = {"/staff/dashboard"})
public class StaffDashboardController extends HttpServlet {
    private final StaffDashboardDAO dashboardDAO = new StaffDashboardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Account staff = requireStaff(request, response);
        if (staff == null) return;

        request.setAttribute("dashboard", dashboardDAO.loadDashboard());
        request.setAttribute("activeModule", "dashboard");
        request.setAttribute("staffPageTitle", "Dashboard");
        request.getRequestDispatcher("/WEB-INF/views/staff/dashboard.jsp").forward(request, response);
    }

    private Account requireStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof Account)) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return null;
        }
        Account user = (Account) session.getAttribute("user");
        if (user.getRole() != Role.STAFF) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return user;
    }
}
