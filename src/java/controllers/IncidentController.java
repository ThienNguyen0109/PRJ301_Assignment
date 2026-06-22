package controllers;

import dto.IncidentReportDTO;
import enums.IncidentSeverity;
import enums.Role;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import services.IncidentService;

@WebServlet(name = "IncidentController", urlPatterns = {"/staff/incidents"})
public class IncidentController extends HttpServlet {
    private final IncidentService incidentService = new IncidentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (requireStaff(request, response) == null) return;
        IncidentSeverity severity = parseSeverity(request.getParameter("severity"));
        request.setAttribute("incidents", incidentService.findIncidents(severity));
        request.setAttribute("selectedSeverity", severity == null ? "" : severity.name());
        String incidentId = request.getParameter("incidentId");
        if (incidentId != null && !incidentId.trim().isEmpty()) {
            IncidentReportDTO selected = incidentService.findById(incidentId);
            if (selected == null) request.setAttribute("incidentError", "Không tìm thấy báo cáo sự cố.");
            else request.setAttribute("selectedIncident", selected);
        }
        request.setAttribute("activeModule", "incident");
        request.setAttribute("staffPageTitle", "Incident Management");
        request.getRequestDispatcher("/WEB-INF/views/staff/incident-management.jsp").forward(request, response);
    }

    private IncidentSeverity parseSeverity(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try { return IncidentSeverity.valueOf(value.trim()); }
        catch (IllegalArgumentException ex) { return null; }
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
}
