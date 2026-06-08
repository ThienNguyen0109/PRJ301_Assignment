package controllers;

import daos.CategoryDAO;
import daos.ICategoryDAO;
import daos.IStationDAO;
import daos.IVehicleSearchDAO;
import daos.StationDAO;
import daos.VehicleSearchDAO;
import daos.WalletDAO;
import daos.WalletTransactionDAO;
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
import dto.BookingDetail;
import dto.BookingQuote;
import enums.Role;
import models.Vehicle;
import dto.VehicleSearchResult;
import models.Wallet;
import models.WalletTransaction;
import services.BookingService;

/**
 * Servlet for handling home page and main navigation routing.
 * URL Pattern: / and /home
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {
    private IStationDAO stationDAO = new StationDAO();
    private ICategoryDAO categoryDAO = new CategoryDAO();
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

        HttpSession session = request.getSession(false);
        String page = request.getParameter("page");
        boolean isLoggedIn = session != null && session.getAttribute("user") != null;

        if (page != null) {
            switch (page) {
                case "register":
                    forward(request, response, "register.jsp");
                    return;

                case "verify-otp":
                    if (session != null && session.getAttribute("otp") != null) {
                        forward(request, response, "verify-otp.jsp");
                    } else {
                        response.sendRedirect(request.getContextPath() + "?page=register");
                    }
                    return;

                case "login":
                    if (isLoggedIn) {
                        Account user = (Account) session.getAttribute("user");
                        response.sendRedirect(request.getContextPath() + getRedirectPageByRole(user));
                        return;
                    }
                    forward(request, response, "login.jsp");
                    return;

                case "reset-password":
                    HttpSession resetSession = request.getSession();
                    if (resetSession.getAttribute("resetStep") == null) {
                        resetSession.setAttribute("resetStep", "email");
                    }
                    forward(request, response, "reset-password.jsp");
                    return;

                case "home":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareHomePage(request);
                    forward(request, response, "home.jsp");
                    return;

                case "vehicle-options":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareVehicleOptionsPage(request);
                    forward(request, response, "vehicle-options.jsp");
                    return;

                case "vehicle-detail":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareVehicleDetailPage(request);
                    forward(request, response, "vehicle-detail.jsp");
                    return;

                case "booking":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareBookingPage(request);
                    forward(request, response, "booking.jsp");
                    return;

                case "booking-detail":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareBookingDetailPage(request, session);
                    forward(request, response, "booking-detail.jsp");
                    return;

                case "wallet":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareWalletPage(request, session);
                    forward(request, response, "wallet.jsp");
                    return;

                case "profile":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareProfilePage(request, session);
                    forward(request, response, "profile.jsp");
                    return;

                case "dashboard":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    Account dashboardUser = (Account) session.getAttribute("user");
                    if (dashboardUser == null || dashboardUser.getRole() != Role.ADMIN) {
                        response.sendRedirect(request.getContextPath() + "?page=home");
                        return;
                    }
                    forward(request, response, "dashboard.jsp");
                    return;

                default:
                    break;
            }
        }

        if (isLoggedIn) {
            Account user = (Account) session.getAttribute("user");
            response.sendRedirect(request.getContextPath() + getRedirectPageByRole(user));
        } else {
            response.sendRedirect(request.getContextPath() + "?page=login");
        }
    }

    @Override
    public String getServletInfo() {
        return "Home Servlet for main navigation routing";
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(jsp);
        dispatcher.forward(request, response);
    }

    private String getRedirectPageByRole(Account account) {
        if (account != null && account.getRole() == Role.ADMIN) {
            return "?page=dashboard";
        }
        return "?page=home";
    }

    private void prepareHomePage(HttpServletRequest request) {
        request.setAttribute("stations", stationDAO.getAllStations());
        request.setAttribute("categories", categoryDAO.getAllCategories());
        request.setAttribute("featuredVehicles", vehicleSearchDAO.getFeaturedAvailableVehicleModels(6));

        String action = request.getParameter("action");
        if (!"search".equals(action)) {
            return;
        }

        String stationId = request.getParameter("stationId");
        String categoryId = request.getParameter("categoryId");

        if (isBlank(stationId) && isBlank(categoryId)) {
            return;
        }

        request.setAttribute("selectedStationId", stationId);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("searchPerformed", true);
        request.setAttribute("vehicleSearchResults", vehicleSearchDAO.searchAvailableVehicleModels(stationId, categoryId));
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
                request.setAttribute("vehicleOptionsError", "Thiáº¿u thÃ´ng tin tÃ¬m kiáº¿m xe.");
                return;
            }

            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            if (!endDate.after(startDate)) {
                request.setAttribute("vehicleOptionsError", "NgÃ y káº¿t thÃºc pháº£i sau ngÃ y báº¯t Ä‘áº§u.");
                return;
            }

            List<Vehicle> vehicles = vehicleSearchDAO.getAvailableVehiclesByModel(stationId, modelId, startDate, endDate);
            request.setAttribute("availableVehicles", vehicles);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("vehicleOptionsError", "NgÃ y thuÃª khÃ´ng há»£p lá»‡.");
        }
    }

    private void prepareVehicleDetailPage(HttpServletRequest request) {
        String stationId = request.getParameter("stationId");
        String modelId = request.getParameter("modelId");
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String action = request.getParameter("action");

        request.setAttribute("stationId", stationId);
        request.setAttribute("modelId", modelId);
        request.setAttribute("startDate", startDateStr);
        request.setAttribute("endDate", endDateStr);

        if (isBlank(stationId) || isBlank(modelId)) {
            request.setAttribute("vehicleDetailError", "Thiáº¿u thÃ´ng tin máº«u xe hoáº·c tráº¡m.");
            return;
        }

        VehicleSearchResult vehicleInfo = vehicleSearchDAO.getAvailableVehicleModelAtStation(modelId, stationId);
        request.setAttribute("vehicleInfo", vehicleInfo);
        if (vehicleInfo == null) {
            request.setAttribute("vehicleDetailError", "Máº«u xe nÃ y hiá»‡n khÃ´ng cÃ²n sáºµn táº¡i tráº¡m Ä‘Ã£ chá»n.");
            return;
        }

        if (!"check".equals(action)) {
            return;
        }

        request.setAttribute("detailSearchPerformed", true);
        try {
            if (isBlank(startDateStr) || isBlank(endDateStr)) {
                request.setAttribute("vehicleDetailError", "Vui lÃ²ng chá»n ngÃ y báº¯t Ä‘áº§u vÃ  ngÃ y káº¿t thÃºc.");
                return;
            }

            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            if (!endDate.after(startDate)) {
                request.setAttribute("vehicleDetailError", "NgÃ y káº¿t thÃºc pháº£i sau ngÃ y báº¯t Ä‘áº§u.");
                return;
            }

            List<Vehicle> vehicles = vehicleSearchDAO.getAvailableVehiclesByModel(stationId, modelId, startDate, endDate);
            request.setAttribute("availableVehicles", vehicles);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("vehicleDetailError", "NgÃ y thuÃª khÃ´ng há»£p lá»‡.");
        }
    }

    private void prepareBookingPage(HttpServletRequest request) {
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
        HttpSession session = request.getSession(false);
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
                prepareBookingPageWithoutDiscount(request, vehicleId, stationId, startDate, endDate, discountCode);
                request.setAttribute("bookingError", paymentError);
                return;
            }
            request.setAttribute("bookingError", firstNonBlank(paymentError, ex.getMessage()));
        }
    }

    private void prepareBookingPageWithoutDiscount(HttpServletRequest request, String vehicleId, String stationId,
            String startDate, String endDate, String discountCode) {
        try {
            HttpSession session = request.getSession(false);
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

