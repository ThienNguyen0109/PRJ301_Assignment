package controllers;

import dto.PickupRentalDTO;
import enums.Role;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import services.PickupService;

@WebServlet(name = "PickupController", urlPatterns = {"/staff/pickup"})
public class PickupController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(PickupController.class.getName());
    private final PickupService pickupService = new PickupService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);
        Account staff = requireStaff(request, response);
        if (staff == null) {
            return;
        }

        request.setAttribute("activeModule", "pickup");
        request.setAttribute("staffPageTitle", "Pickup Management");

        HttpSession session = request.getSession();
        moveFlashMessage(session, request, "pickupSuccess");
        moveFlashMessage(session, request, "pickupError");

        String query = trim(request.getParameter("query"));
        String rentalId = trim(request.getParameter("rentalId"));
        String pickupDateValue = trim(request.getParameter("pickupDate"));
        try {
            Date pickupDate = pickupDateValue.isEmpty() ? null : Date.valueOf(pickupDateValue);
            List<PickupRentalDTO> rentals = pickupService.searchBookedRentals(query, pickupDate);
            request.setAttribute("bookedRentals", rentals);
            request.setAttribute("searchQuery", query);
            request.setAttribute("pickupDate", pickupDateValue);
            if (!rentalId.isEmpty()) {
                PickupRentalDTO detail = pickupService.findRentalDetail(rentalId);
                if (detail == null) {
                    request.setAttribute("pickupError", "Không tìm thấy booking đã chọn.");
                } else {
                    request.setAttribute("selectedRental", detail);
                }
            }
        } catch (IllegalArgumentException ex) {
            request.setAttribute("pickupError", "Ngày nhận xe không hợp lệ.");
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Could not load pickup management", ex);
            request.setAttribute("pickupError", "Không thể tải dữ liệu giao xe. Vui lòng thử lại.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/staff/pickup-dashboard.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configureEncoding(request, response);
        if (requireStaff(request, response) == null) {
            return;
        }

        String action = trim(request.getParameter("pickupAction"));
        String rentalId = trim(request.getParameter("rentalId"));
        HttpSession session = request.getSession();
        try {
            if ("confirm".equals(action)) {
                pickupService.confirmPickup(rentalId);
                session.setAttribute("pickupSuccess", "Vehicle delivered successfully.");
            } else if ("no-show".equals(action)) {
                pickupService.markNoShow(rentalId);
                session.setAttribute("pickupSuccess", "Rental marked as NO_SHOW successfully.");
            } else {
                throw new IllegalArgumentException("Thao tác giao xe không hợp lệ.");
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            session.setAttribute("pickupError", ex.getMessage());
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Pickup transaction failed for rental " + rentalId, ex);
            session.setAttribute("pickupError", "Không thể xử lý booking. Dữ liệu đã được rollback.");
        }

        String redirect = request.getContextPath() + "?action=staff-pickup";
        if (!rentalId.isEmpty()) {
            redirect += "&rentalId=" + URLEncoder.encode(rentalId, "UTF-8");
        }
        response.sendRedirect(redirect);
    }

    private Account requireStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof Account)) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return null;
        }
        Account user = (Account) session.getAttribute("user");
        if (user.getRole() != Role.STAFF) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Bạn không có quyền truy cập khu vực dành cho nhân viên.");
            return null;
        }
        return user;
    }

    private void moveFlashMessage(HttpSession session, HttpServletRequest request, String name) {
        Object value = session.getAttribute(name);
        if (value != null) {
            request.setAttribute(name, value);
            session.removeAttribute(name);
        }
    }

    private void configureEncoding(HttpServletRequest request, HttpServletResponse response)
            throws java.io.UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
