package controllers;

import dto.ReturnRentalDTO;
import enums.Role;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import services.ReturnService;

@WebServlet(name = "ReturnManagementController", urlPatterns = {"/staff/return"})
public class ReturnManagementController extends HttpServlet {
    private final ReturnService returnService = new ReturnService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8");
        if (requireStaff(request, response) == null) return;
        request.setAttribute("activeModule", "return"); request.setAttribute("staffPageTitle", "Return Management");
        moveFlash(request, "returnSuccess"); moveFlash(request, "returnError");

        String query = trim(request.getParameter("query"));
        String endDateValue = trim(request.getParameter("endDate"));
        try {
            Date endDate = endDateValue.isEmpty() ? null : Date.valueOf(endDateValue);
            List<ReturnRentalDTO> rentals = returnService.searchRentedRentals(query, endDate);
            request.setAttribute("rentedRentals", rentals);
            request.setAttribute("searchQuery", query); request.setAttribute("endDate", endDateValue);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("returnError", "Ngày trả xe không hợp lệ.");
        }
        request.getRequestDispatcher("/WEB-INF/views/staff/return-management.jsp").forward(request, response);
    }

    private void moveFlash(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(); Object value = session.getAttribute(name);
        if (value != null) { request.setAttribute(name, value); session.removeAttribute(name); }
    }
    private Account requireStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof Account)) { response.sendRedirect(request.getContextPath()+"?action=login"); return null; }
        Account user=(Account)session.getAttribute("user"); if(user.getRole()!=Role.STAFF){response.sendError(403);return null;} return user;
    }
    private String trim(String value) { return value == null ? "" : value.trim(); }
}
