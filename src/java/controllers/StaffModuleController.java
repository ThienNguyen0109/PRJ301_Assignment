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

@WebServlet(name = "StaffModuleController", urlPatterns = {
    "/staff/return", "/staff/maintenance", "/staff/incidents", "/staff/profile"
})
public class StaffModuleController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Account staff = requireStaff(request, response);
        if (staff == null) return;

        String path = request.getServletPath();
        if ("/staff/return".equals(path)) {
            configure(request, "return", "Return Management",
                    "Receive rented vehicles and record their return condition.");
        } else if ("/staff/maintenance".equals(path)) {
            configure(request, "maintenance", "Maintenance Management",
                    "Track vehicles under maintenance and complete maintenance work.");
        } else if ("/staff/incidents".equals(path)) {
            configure(request, "incident", "Incident Management",
                    "Review incidents reported during vehicle rental and return.");
        } else {
            configure(request, "profile", "Staff Profile",
                    "Review your staff account and assigned access level.");
            request.setAttribute("staffAccount", staff);
        }
        request.getRequestDispatcher("/WEB-INF/views/staff/module.jsp").forward(request, response);
    }

    private void configure(HttpServletRequest request, String module, String title, String description) {
        request.setAttribute("activeModule", module);
        request.setAttribute("staffPageTitle", title);
        request.setAttribute("moduleDescription", description);
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
