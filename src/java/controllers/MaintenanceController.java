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
import services.MaintenanceService;

@WebServlet(name = "MaintenanceController", urlPatterns = {"/staff/maintenance"})
public class MaintenanceController extends HttpServlet {
    private final MaintenanceService maintenanceService = new MaintenanceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (requireStaff(request, response) == null) return;
        moveFlash(request, "maintenanceSuccess");
        moveFlash(request, "maintenanceError");
        String query = trim(request.getParameter("query"));
        request.setAttribute("maintenanceVehicles", maintenanceService.findPendingMaintenance(query));
        request.setAttribute("searchQuery", query);
        request.setAttribute("activeModule", "maintenance");
        request.setAttribute("staffPageTitle", "Maintenance Management");
        request.getRequestDispatcher("/WEB-INF/views/staff/maintenance-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (requireStaff(request, response) == null) return;
        HttpSession session = request.getSession();
        try {
            maintenanceService.markCompleted(request.getParameter("maintenanceId"));
            session.setAttribute("maintenanceSuccess", "Vehicle is available for rental again.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            session.setAttribute("maintenanceError", ex.getMessage());
        } catch (RuntimeException ex) {
            session.setAttribute("maintenanceError", "Không thể hoàn tất bảo trì. Giao dịch đã được hoàn tác.");
        }
        response.sendRedirect(request.getContextPath() + "?action=staff-maintenance");
    }

    private void moveFlash(HttpServletRequest request, String name) {
        HttpSession session = request.getSession();
        Object value = session.getAttribute(name);
        if (value != null) { request.setAttribute(name, value); session.removeAttribute(name); }
    }

    private Account requireStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof Account)) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return null;
        }
        Account user = (Account) session.getAttribute("user");
        if (user.getRole() != Role.STAFF) { response.sendError(HttpServletResponse.SC_FORBIDDEN); return null; }
        return user;
    }

    private String trim(String value) { return value == null ? "" : value.trim(); }
}
