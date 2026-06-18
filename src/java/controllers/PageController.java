package controllers;

import daos.IVehicleSearchDAO;
import daos.VehicleSearchDAO;
import daos.WalletDAO;
import daos.WalletTransactionDAO;
import dto.BookingDetail;
import dto.BookingQuote;
import dto.VehicleSearchResult;
import enums.Role;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import models.Vehicle;
import models.Wallet;
import models.WalletTransaction;
import services.BookingService;

@WebServlet(name = "PageController", urlPatterns = {
    "/page/verify-otp",
    "/page/reset-password",
    "/page/vehicle-options",
    "/page/vehicle-detail",
    "/page/booking",
    "/page/booking-detail",
    "/page/wallet",
    "/page/profile",
    "/page/dashboard"
})
public class PageController extends HttpServlet {
    private IVehicleSearchDAO vehicleSearchDAO = new VehicleSearchDAO();
    private WalletDAO walletDAO = new WalletDAO();
    private WalletTransactionDAO walletTransactionDAO = new WalletTransactionDAO();
    private BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String path = request.getServletPath();
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = session != null && session.getAttribute("user") != null;

        if ("/page/verify-otp".equals(path)) {
            if (session != null && session.getAttribute("otp") != null) {
                forward(request, response, "/verify-otp.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "?action=register");
            }
            return;
        }

        if ("/page/reset-password".equals(path)) {
            HttpSession resetSession = request.getSession();
            if (resetSession.getAttribute("resetStep") == null) {
                resetSession.setAttribute("resetStep", "email");
            }
            forward(request, response, "/reset-password.jsp");
            return;
        }

        if (!isLoggedIn) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return;
        }

        if ("/page/vehicle-options".equals(path)) {
            prepareVehicleOptionsPage(request);
            forward(request, response, "/vehicle-options.jsp");
        } else if ("/page/vehicle-detail".equals(path)) {
            prepareVehicleDetailPage(request);
            forward(request, response, "/vehicle-detail.jsp");
        } else if ("/page/booking".equals(path)) {
            prepareBookingPage(request, session);
            forward(request, response, "/booking.jsp");
        } else if ("/page/booking-detail".equals(path)) {
            prepareBookingDetailPage(request, session);
            forward(request, response, "/booking-detail.jsp");
        } else if ("/page/wallet".equals(path)) {
            prepareWalletPage(request, session);
            forward(request, response, "/wallet.jsp");
        } else if ("/page/profile".equals(path)) {
            prepareProfilePage(request, session);
            forward(request, response, "/profile.jsp");
        } else if ("/page/dashboard".equals(path)) {
            Account user = (Account) session.getAttribute("user");
            if (user == null || user.getRole() != Role.ADMIN) {
                response.sendRedirect(request.getContextPath() + "?action=home");
                return;
            }
            forward(request, response, "/dashboard.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "?action=home");
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(jsp);
        dispatcher.forward(request, response);
    }

    private void prepareVehicleOptionsPage(HttpServletRequest request) {
        String stationId = request.getParameter("stationId");
        String categoryId = request.getParameter("categoryId");
        String modelId = request.getParameter("modelId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        request.setAttribute("stationId", stationId);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("modelId", modelId);
        request.setAttribute("startDate", startDateStr);
        request.setAttribute("endDate", endDateStr);

        try {
            if (isBlank(stationId) || isBlank(modelId) || isBlank(startDateStr) || isBlank(endDateStr)) {
                request.setAttribute("vehicleOptionsError", "Thiếu thông tin tìm kiếm xe.");
                return;
            }

            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            if (endDate.before(startDate)) {
                request.setAttribute("vehicleOptionsError", "Ngày kết thúc phải sau ngày bắt đầu.");
                return;
            }

            List<Vehicle> vehicles = vehicleSearchDAO.getAvailableVehiclesByModel(stationId, modelId, startDate, endDate);
            request.setAttribute("availableVehicles", vehicles);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("vehicleOptionsError", "Ngày thuê không hợp lệ.");
        }
    }

    private void prepareVehicleDetailPage(HttpServletRequest request) {
        String stationId = request.getParameter("stationId");
        String modelId = request.getParameter("modelId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String action = request.getParameter("detailAction");
        if (action == null) {
            action = request.getParameter("actionType");
        }

        request.setAttribute("stationId", stationId);
        request.setAttribute("modelId", modelId);
        request.setAttribute("startDate", startDateStr);
        request.setAttribute("endDate", endDateStr);

        if (isBlank(stationId) || isBlank(modelId)) {
            request.setAttribute("vehicleDetailError", "Thiếu thông tin mẫu xe hoặc trạm.");
            return;
        }

        VehicleSearchResult vehicleInfo = vehicleSearchDAO.getAvailableVehicleModelAtStation(modelId, stationId);
        request.setAttribute("vehicleInfo", vehicleInfo);
        if (vehicleInfo == null) {
            request.setAttribute("vehicleDetailError", "Mẫu xe này hiện không còn sẵn tại trạm đã chọn.");
            return;
        }

        if (!"check".equals(action)) {
            return;
        }

        request.setAttribute("detailSearchPerformed", true);
        try {
            if (isBlank(startDateStr) || isBlank(endDateStr)) {
                request.setAttribute("vehicleDetailError", "Vui lòng chọn ngày bắt đầu và ngày kết thúc.");
                return;
            }

            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            if (endDate.before(startDate)) {
                request.setAttribute("vehicleDetailError", "Ngày kết thúc phải sau ngày bắt đầu.");
                return;
            }

            List<Vehicle> vehicles = vehicleSearchDAO.getAvailableVehiclesByModel(stationId, modelId, startDate, endDate);
            request.setAttribute("availableVehicles", vehicles);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("vehicleDetailError", "Ngày thuê không hợp lệ.");
        }
    }

    private void prepareBookingPage(HttpServletRequest request, HttpSession session) {
        String vehicleId = request.getParameter("vehicleId");
        String stationId = request.getParameter("stationId");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String discountCode = request.getParameter("discountCode");

        request.setAttribute("vehicleId", vehicleId);
        request.setAttribute("stationId", stationId);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("discountCode", discountCode);

        String paymentError = null;
        if (session != null && session.getAttribute("bookingError") != null) {
            paymentError = (String) session.getAttribute("bookingError");
            session.removeAttribute("bookingError");
        }

        try {
            Account user = (Account) session.getAttribute("user");
            BookingQuote quote = bookingService.createQuote(
                    user.getAccountId(),
                    vehicleId,
                    Date.valueOf(startDate),
                    Date.valueOf(endDate),
                    discountCode);
            request.setAttribute("bookingQuote", quote);
            request.setAttribute("bookingError", paymentError);
        } catch (Exception ex) {
            if (!isBlank(discountCode) && BookingService.INVALID_DISCOUNT_MESSAGE.equals(ex.getMessage())) {
                prepareBookingPageWithoutDiscount(request, session, vehicleId, startDate, endDate, discountCode);
                request.setAttribute("bookingError", paymentError);
                return;
            }
            request.setAttribute("bookingError", firstNonBlank(paymentError, ex.getMessage()));
        }
    }

    private void prepareBookingPageWithoutDiscount(HttpServletRequest request, HttpSession session, String vehicleId,
            String startDate, String endDate, String discountCode) {
        try {
            Account user = (Account) session.getAttribute("user");
            BookingQuote quote = bookingService.createQuote(
                    user.getAccountId(),
                    vehicleId,
                    Date.valueOf(startDate),
                    Date.valueOf(endDate),
                    null);
            quote.setDiscountCode(discountCode);
            request.setAttribute("bookingQuote", quote);
            request.setAttribute("discountError", BookingService.INVALID_DISCOUNT_MESSAGE);
        } catch (Exception fallbackEx) {
            request.setAttribute("bookingError", fallbackEx.getMessage());
        }
    }

    private void prepareBookingDetailPage(HttpServletRequest request, HttpSession session) {
        BookingDetail detail = (BookingDetail) session.getAttribute("bookingDetail");
        request.setAttribute("bookingDetail", detail);
    }

    private void prepareProfilePage(HttpServletRequest request, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        request.setAttribute("profileUser", user);
        request.setAttribute("displayName", firstNonBlank((String) session.getAttribute("userName"), user.getFullName()));
        request.setAttribute("displayEmail", firstNonBlank((String) session.getAttribute("userEmail"), user.getEmail()));
        request.setAttribute("wallet", walletDAO.getWalletByAccountId(user.getAccountId()));
    }

    private void prepareWalletPage(HttpServletRequest request, HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        Wallet wallet = walletDAO.getWalletByAccountId(user.getAccountId());
        List<WalletTransaction> transactions = walletTransactionDAO.getTransactionsByWalletId(
                wallet != null ? wallet.getWalletId() : "");

        Boolean topupSuccess = (Boolean) session.getAttribute("topupSuccess");
        Long topupAmount = (Long) session.getAttribute("topupSuccessAmount");
        if (Boolean.TRUE.equals(topupSuccess)) {
            session.removeAttribute("topupSuccess");
            session.removeAttribute("topupSuccessAmount");
        }

        request.setAttribute("wallet", wallet);
        request.setAttribute("transactions", transactions);
        request.setAttribute("topupSuccess", topupSuccess);
        request.setAttribute("topupSuccessAmount", topupAmount);
        request.setAttribute("paymentError", request.getParameter("error"));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String firstNonBlank(String preferred, String fallback) {
        return isBlank(preferred) ? fallback : preferred;
    }
}
