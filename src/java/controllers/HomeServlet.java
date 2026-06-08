package controllers;

import daos.CategoryDAO;
import daos.ICategoryDAO;
import daos.IStationDAO;
import daos.IVehicleSearchDAO;
import daos.StationDAO;
import daos.VehicleSearchDAO;
import daos.WalletDAO;
import daos.WalletTransactionDAO;
import java.sql.Date;
import java.util.List;
import models.Account;
import models.Role;
import models.Vehicle;
import models.VehicleSearchResult;
import models.Wallet;
import models.WalletTransaction;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling home page and main navigation routing
 * URL Pattern: / and /home
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {
    private IStationDAO stationDAO = new StationDAO();
    private ICategoryDAO categoryDAO = new CategoryDAO();
    private IVehicleSearchDAO vehicleSearchDAO = new VehicleSearchDAO();
    private WalletDAO walletDAO = new WalletDAO();
    private WalletTransactionDAO walletTransactionDAO = new WalletTransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        String page = request.getParameter("page");
        
        // Check if user is logged in
        boolean isLoggedIn = session != null && session.getAttribute("user") != null;

        // Determine which page to show
        if (page != null) {
            switch (page) {
                case "register":
                    RequestDispatcher registerDispatcher = request.getRequestDispatcher("register.jsp");
                    registerDispatcher.forward(request, response);
                    return;
                    
                case "verify-otp":
                    // Only allow access to verify-otp if OTP session exists
                    if (session != null && session.getAttribute("otp") != null) {
                        RequestDispatcher verifyDispatcher = request.getRequestDispatcher("verify-otp.jsp");
                        verifyDispatcher.forward(request, response);
                        return;
                    } else {
                        response.sendRedirect(request.getContextPath() + "?page=register");
                        return;
                    }
                    
                case "login":
                    // If already logged in, redirect to dashboard
                    if (isLoggedIn) {
                        Account user = (Account) session.getAttribute("user");
                        response.sendRedirect(request.getContextPath() + getRedirectPageByRole(user));
                        return;
                    }
                    RequestDispatcher loginDispatcher = request.getRequestDispatcher("login.jsp");
                    loginDispatcher.forward(request, response);
                    return;

                case "reset-password":
                    HttpSession resetSession = request.getSession();
                    if (resetSession.getAttribute("resetStep") == null) {
                        resetSession.setAttribute("resetStep", "email");
                    }
                    RequestDispatcher resetPasswordDispatcher = request.getRequestDispatcher("reset-password.jsp");
                    resetPasswordDispatcher.forward(request, response);
                    return;
                    
                case "home":
                    // Only allow access to home if logged in
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareHomePage(request);
                    RequestDispatcher homeDispatcher = request.getRequestDispatcher("home.jsp");
                    homeDispatcher.forward(request, response);
                    return;

                case "vehicle-options":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareVehicleOptionsPage(request);
                    RequestDispatcher vehicleOptionsDispatcher = request.getRequestDispatcher("vehicle-options.jsp");
                    vehicleOptionsDispatcher.forward(request, response);
                    return;

                case "vehicle-detail":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareVehicleDetailPage(request);
                    RequestDispatcher vehicleDetailDispatcher = request.getRequestDispatcher("vehicle-detail.jsp");
                    vehicleDetailDispatcher.forward(request, response);
                    return;

                case "booking":
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareBookingPage(request);
                    RequestDispatcher bookingDispatcher = request.getRequestDispatcher("booking.jsp");
                    bookingDispatcher.forward(request, response);
                    return;
                    
                case "wallet":
                    // Only allow access to wallet if logged in
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareWalletPage(request, session);
                    RequestDispatcher walletDispatcher = request.getRequestDispatcher("wallet.jsp");
                    walletDispatcher.forward(request, response);
                    return;

                case "profile":
                    // Only allow access to profile if logged in
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    prepareProfilePage(request, session);
                    RequestDispatcher profileDispatcher = request.getRequestDispatcher("profile.jsp");
                    profileDispatcher.forward(request, response);
                    return;
                    
                case "dashboard":
                    // Only allow access to dashboard if logged in
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    Account dashboardUser = (Account) session.getAttribute("user");
                    if (dashboardUser == null || dashboardUser.getRole() != Role.ADMIN) {
                        response.sendRedirect(request.getContextPath() + "?page=home");
                        return;
                    }
                    RequestDispatcher dashboardDispatcher = request.getRequestDispatcher("dashboard.jsp");
                    dashboardDispatcher.forward(request, response);
                    return;
                    
                default:
                    break;
            }
        }

        // Default routing
        if (isLoggedIn) {
            Account user = (Account) session.getAttribute("user");
            response.sendRedirect(request.getContextPath() + getRedirectPageByRole(user));
        } else {
            // User is not logged in, show login page
            response.sendRedirect(request.getContextPath() + "?page=login");
        }
    }

    @Override
    public String getServletInfo() {
        return "Home Servlet for main navigation routing";
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
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        request.setAttribute("selectedStationId", stationId);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("selectedStartDate", startDateStr);
        request.setAttribute("selectedEndDate", endDateStr);
        request.setAttribute("searchPerformed", true);

        try {
            if (isBlank(stationId) || isBlank(categoryId) || isBlank(startDateStr) || isBlank(endDateStr)) {
                request.setAttribute("searchError", "Vui lòng chọn đầy đủ trạm, loại xe, ngày bắt đầu và ngày kết thúc.");
                return;
            }

            Date startDate = Date.valueOf(startDateStr);
            Date endDate = Date.valueOf(endDateStr);

            if (!endDate.after(startDate)) {
                request.setAttribute("searchError", "Ngày kết thúc phải sau ngày bắt đầu.");
                return;
            }

            List<VehicleSearchResult> searchResults =
                vehicleSearchDAO.searchAvailableVehicleModels(stationId, categoryId, startDate, endDate);
            request.setAttribute("vehicleSearchResults", searchResults);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("searchError", "Ngày thuê không hợp lệ.");
        }
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

            if (!endDate.after(startDate)) {
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
        String action = request.getParameter("action");

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

            if (!endDate.after(startDate)) {
                request.setAttribute("vehicleDetailError", "Ngày kết thúc phải sau ngày bắt đầu.");
                return;
            }

            List<Vehicle> vehicles = vehicleSearchDAO.getAvailableVehiclesByModel(stationId, modelId, startDate, endDate);
            request.setAttribute("availableVehicles", vehicles);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("vehicleDetailError", "Ngày thuê không hợp lệ.");
        }
    }

    private void prepareBookingPage(HttpServletRequest request) {
        request.setAttribute("vehicleId", request.getParameter("vehicleId"));
        request.setAttribute("stationId", request.getParameter("stationId"));
        request.setAttribute("startDate", request.getParameter("startDate"));
        request.setAttribute("endDate", request.getParameter("endDate"));
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

