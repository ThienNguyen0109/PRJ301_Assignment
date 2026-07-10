package controllers;

import enums.Role;
import enums.VehicleModelImageType;
import enums.VehicleStatus;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import services.AdminAccountService;
import services.AdminDiscountService;
import services.AdminRentalService;
import services.AdminPaymentService;
import services.AdminWalletService;
import services.AdminReportService;
import services.AdminStationService;
import services.AdminVehicleModelImageService;
import services.AdminVehicleModelService;
import services.AdminVehicleService;

@WebServlet(name = "AdminController", urlPatterns = {
    "/admin/dashboard",
    "/admin/financial-reports",
    "/admin/financial-detail",
    "/admin/station-performance",
    "/admin/station-performance/detail",
    "/admin/model-performance",
    "/admin/model-performance/detail",
    "/admin/accounts",
    "/admin/accounts/form",
    "/admin/accounts/detail",
    "/admin/stations",
    "/admin/categories",
    "/admin/vehicle-models",
    "/admin/vehicle-models/form",
    "/admin/vehicle-models/detail",
    "/admin/vehicle-model-images",
    "/admin/vehicle-model-images/form",
    "/admin/vehicle-model-images/detail",
    "/admin/vehicles",
    "/admin/vehicles/form",
    "/admin/vehicles/detail",
    "/admin/discounts",
    "/admin/discounts/form",
    "/admin/discounts/detail",
    "/admin/rental-discounts",
    "/admin/rentals",
    "/admin/rentals/detail",
    "/admin/rental-status-history",
    "/admin/payments",
    "/admin/payments/detail",
    "/admin/extra-charges",
    "/admin/incidents",
    "/admin/maintenance",
    "/admin/wallets",
    "/admin/wallets/detail",
    "/admin/reviews",
    "/admin/profile",
    "/admin/stations/form",
    "/admin/stations/detail",
})
public class AdminController extends HttpServlet {

    private final AdminAccountService accountService = new AdminAccountService();
    private final AdminVehicleModelService vehicleModelService = new AdminVehicleModelService();
    private final AdminVehicleModelImageService vehicleModelImageService = new AdminVehicleModelImageService();
    private final AdminReportService reportService = new AdminReportService();
    private final AdminStationService stationService = new AdminStationService();
    private final AdminVehicleService vehicleService = new AdminVehicleService();
    private final AdminDiscountService discountService = new AdminDiscountService();
    private final AdminRentalService rentalService = new AdminRentalService();
    private final AdminPaymentService paymentService = new AdminPaymentService();
    private final AdminWalletService walletService = new AdminWalletService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Account admin = requireAdmin(request, response);
        if (admin == null) {
            return;
        }

        String path = request.getServletPath();
        if (handleCrudGet(path, request, response, admin)) {
            return;
        }
        ReportSelection reportSelection = buildReportSelection(request);
        if (handleReportDetail(path, request, response, admin, reportSelection.period)) {
            return;
        }

        AdminPage page = resolvePage(request, reportSelection.period);
        request.setAttribute("adminAccount", admin);
        request.setAttribute("activeModule", page.activeModule);
        request.setAttribute("adminPageTitle", page.title);
        request.setAttribute("adminPageSubtitle", page.subtitle);
        request.setAttribute("adminPageBadge", page.badge);
        request.setAttribute("adminStats", page.stats);
        request.setAttribute("adminColumns", page.columns);
        request.setAttribute("adminRows", paginate(request, page.rows));
        request.setAttribute("adminPrimaryAction", page.primaryAction);
        request.setAttribute("adminSearchPlaceholder", page.searchPlaceholder);
        request.setAttribute("adminChartMode", page.chartMode);
        request.setAttribute("adminChartPrimary", page.primaryChartItems);
        request.setAttribute("adminChartSecondary", page.secondaryChartItems);
        request.setAttribute("adminCurrentAction", actionForPath(request.getServletPath()));
        request.setAttribute("reportPeriod", reportSelection.selectedPeriod);
        request.setAttribute("reportStartDate", reportSelection.startDate);
        request.setAttribute("reportEndDate", reportSelection.endDate);
        request.setAttribute("reportMonth", reportSelection.month);
        request.setAttribute("reportQuarter", reportSelection.quarter);
        request.setAttribute("reportYear", reportSelection.year);

        String jsp = "dashboard".equals(page.activeModule)
                ? "/WEB-INF/views/admin/dashboard.jsp"
                : "/WEB-INF/views/admin/module.jsp";
        request.getRequestDispatcher(jsp).forward(request, response);
    }

    private boolean handleCrudGet(String path, HttpServletRequest request, HttpServletResponse response, Account admin)
            throws ServletException, IOException {
        if ("/admin/accounts".equals(path)) {
            configureAdminShell(request, admin, "accounts", "Accounts", "CRUD", "Search name, email, phone");
            List<models.Account> accounts = accountService.search(
                    request.getParameter("keyword"),
                    request.getParameter("role"),
                    request.getParameter("status"));
            request.setAttribute("accounts", paginate(request, accounts));
            request.setAttribute("roles", Role.values());
            request.setAttribute("keyword", paramOrDefault(request, "keyword", ""));
            request.setAttribute("selectedRole", paramOrDefault(request, "role", "ALL"));
            request.setAttribute("selectedStatus", paramOrDefault(request, "status", "ALL"));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/accounts/list.jsp").forward(request, response);
            return true;
        }
        if ("/admin/accounts/form".equals(path)) {
            configureAdminShell(request, admin, "accounts", isBlank(request.getParameter("id")) ? "Create Account" : "Edit Account", "CRUD", "Search accounts");
            request.setAttribute("account", accountService.findById(request.getParameter("id")));
            request.setAttribute("roles", Role.values());
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/accounts/form.jsp").forward(request, response);
            return true;
        }
        if ("/admin/accounts/detail".equals(path)) {
            configureAdminShell(request, admin, "accounts", "Account Detail", "CRUD", "Search accounts");
            request.setAttribute("account", accountService.findById(request.getParameter("id")));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/accounts/detail.jsp").forward(request, response);
            return true;
        }
        if ("/admin/stations".equals(path)) {
            configureAdminShell(
                    request,
                    admin,
                    "stations",
                    "Stations",
                    "CRUD",
                    "Search station");
            request.setAttribute("stations", paginate(request, stationService.search(request.getParameter("keyword"))));
            request.setAttribute("keyword", paramOrDefault(request, "keyword", ""));
            consumeFlash(request);
            request.getRequestDispatcher(
                    "/WEB-INF/views/admin/stations/list.jsp")
                    .forward(request, response);
            return true;
        }
        if ("/admin/stations/form".equals(path)) {
            configureAdminShell(
                    request,
                    admin,
                    "stations",
                    isBlank(request.getParameter("id"))
                    ? "Create Station"
                    : "Edit Station",
                    "CRUD",
                    "Search station");
            request.setAttribute(
                    "station",
                    stationService.getStationById(
                            request.getParameter("id")));
            consumeFlash(request);
            request.getRequestDispatcher(
                    "/WEB-INF/views/admin/stations/form.jsp")
                    .forward(request, response);
            return true;
        }
        if ("/admin/stations/detail".equals(path)) {
            configureAdminShell(
                    request,
                    admin,
                    "stations",
                    "Station Detail",
                    "CRUD",
                    "Search station");
            request.setAttribute(
                    "station",
                    stationService.getStationById(
                            request.getParameter("id")));
            consumeFlash(request);
            request.getRequestDispatcher(
                    "/WEB-INF/views/admin/stations/detail.jsp")
                    .forward(request, response);
            return true;
        }
        if ("/admin/vehicle-models".equals(path)) {
            configureAdminShell(request, admin, "vehicle-models", "Vehicle Models", "CRUD", "Search model or category");
            List<dto.AdminVehicleModelRow> models = vehicleModelService.search(request.getParameter("keyword"), request.getParameter("categoryId"));
            request.setAttribute("models", paginate(request, models));
            request.setAttribute("categories", vehicleModelService.findAllCategories());
            request.setAttribute("keyword", paramOrDefault(request, "keyword", ""));
            request.setAttribute("selectedCategoryId", paramOrDefault(request, "categoryId", "ALL"));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/vehicle-models/list.jsp").forward(request, response);
            return true;
        }
        if ("/admin/vehicle-models/form".equals(path)) {
            configureAdminShell(request, admin, "vehicle-models", isBlank(request.getParameter("id")) ? "Create Vehicle Model" : "Edit Vehicle Model", "CRUD", "Search models");
            request.setAttribute("model", vehicleModelService.findById(request.getParameter("id")));
            request.setAttribute("categories", vehicleModelService.findAllCategories());
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/vehicle-models/form.jsp").forward(request, response);
            return true;
        }
        if ("/admin/vehicle-models/detail".equals(path)) {
            configureAdminShell(request, admin, "vehicle-models", "Vehicle Model Detail", "CRUD", "Search models");
            request.setAttribute("model", vehicleModelService.findById(request.getParameter("id")));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/vehicle-models/detail.jsp").forward(request, response);
            return true;
        }
        if ("/admin/vehicle-model-images".equals(path)) {
            configureAdminShell(request, admin, "vehicle-model-images", "Vehicle Model Images", "CRUD", "Search model image");
            List<dto.AdminVehicleModelImageRow> images = vehicleModelImageService.search(
                    request.getParameter("keyword"),
                    request.getParameter("modelId"),
                    request.getParameter("imageType"));
            request.setAttribute("images", paginate(request, images));
            request.setAttribute("models", vehicleModelImageService.findAllModels());
            request.setAttribute("imageTypes", VehicleModelImageType.values());
            request.setAttribute("keyword", paramOrDefault(request, "keyword", ""));
            request.setAttribute("selectedModelId", paramOrDefault(request, "modelId", "ALL"));
            request.setAttribute("selectedImageType", paramOrDefault(request, "imageType", "ALL"));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/vehicle-model-images/list.jsp").forward(request, response);
            return true;
        }
        if ("/admin/vehicle-model-images/form".equals(path)) {
            configureAdminShell(request, admin, "vehicle-model-images", isBlank(request.getParameter("id")) ? "Create Model Image" : "Edit Model Image", "CRUD", "Search images");
            request.setAttribute("image", vehicleModelImageService.findById(request.getParameter("id")));
            request.setAttribute("models", vehicleModelImageService.findAllModels());
            request.setAttribute("imageTypes", VehicleModelImageType.values());
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/vehicle-model-images/form.jsp").forward(request, response);
            return true;
        }
        if ("/admin/vehicle-model-images/detail".equals(path)) {
            configureAdminShell(request, admin, "vehicle-model-images", "Model Image Detail", "CRUD", "Search images");
            request.setAttribute("image", vehicleModelImageService.findById(request.getParameter("id")));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/vehicle-model-images/detail.jsp").forward(request, response);
            return true;
        }
        if ("/admin/vehicles".equals(path)) {
            configureAdminShell(request, admin, "vehicles", "Vehicles", "CRUD", "Search license plate, model, station");
            List<dto.AdminVehicleRow> vehicles = vehicleService.search(
                    request.getParameter("keyword"), request.getParameter("stationId"),
                    request.getParameter("categoryId"), request.getParameter("status"));
            request.setAttribute("vehicles", paginate(request, vehicles));
            request.setAttribute("stations", vehicleService.findAllStations());
            request.setAttribute("categories", vehicleService.findAllCategories());
            request.setAttribute("vehicleStatuses", VehicleStatus.values());
            request.setAttribute("keyword", paramOrDefault(request, "keyword", ""));
            request.setAttribute("selectedStationId", paramOrDefault(request, "stationId", "ALL"));
            request.setAttribute("selectedCategoryId", paramOrDefault(request, "categoryId", "ALL"));
            request.setAttribute("selectedStatus", paramOrDefault(request, "status", "ALL"));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/vehicles/list.jsp").forward(request, response);
            return true;
        }
        if ("/admin/vehicles/form".equals(path)) {
            configureAdminShell(request, admin, "vehicles", isBlank(request.getParameter("id")) ? "Create Vehicle" : "Edit Vehicle", "CRUD", "Search vehicles");
            request.setAttribute("vehicle", vehicleService.findById(request.getParameter("id")));
            request.setAttribute("models", vehicleService.findAllModels());
            request.setAttribute("stations", vehicleService.findAllStations());
            request.setAttribute("vehicleStatuses", VehicleStatus.values());
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/vehicles/form.jsp").forward(request, response);
            return true;
        }
        if ("/admin/vehicles/detail".equals(path)) {
            configureAdminShell(request, admin, "vehicles", "Vehicle Detail", "CRUD", "Search vehicles");
            request.setAttribute("vehicle", vehicleService.findById(request.getParameter("id")));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/vehicles/detail.jsp").forward(request, response);
            return true;
        }
        if ("/admin/discounts".equals(path)) {
            configureAdminShell(request, admin, "discounts", "Discounts", "CRUD", "Search discount code");
            List<models.Discount> discounts = discountService.search(request.getParameter("keyword"), request.getParameter("status"));
            request.setAttribute("discounts", paginate(request, discounts));
            request.setAttribute("keyword", paramOrDefault(request, "keyword", ""));
            request.setAttribute("selectedStatus", paramOrDefault(request, "status", "ALL"));
            request.setAttribute("now", new Timestamp(System.currentTimeMillis()));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/discounts/list.jsp").forward(request, response);
            return true;
        }
        if ("/admin/discounts/form".equals(path)) {
            configureAdminShell(request, admin, "discounts", isBlank(request.getParameter("id")) ? "Create Discount" : "Edit Discount", "CRUD", "Search discount code");
            models.Discount discount = discountService.findById(request.getParameter("id"));
            request.setAttribute("discount", discount);
            request.setAttribute("discountHasUsage", discount != null && discountService.hasUsage(discount.getDiscountId()));
            request.setAttribute("expiredAtInput", discount == null || discount.getExpiredAt() == null
                    ? "" : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(discount.getExpiredAt()));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/discounts/form.jsp").forward(request, response);
            return true;
        }
        if ("/admin/discounts/detail".equals(path)) {
            configureAdminShell(request, admin, "discounts", "Discount Detail", "CRUD", "Search discount code");
            models.Discount discount = discountService.findById(request.getParameter("id"));
            request.setAttribute("discount", discount);
            request.setAttribute("discountHasUsage", discount != null && discountService.hasUsage(discount.getDiscountId()));
            request.setAttribute("now", new Timestamp(System.currentTimeMillis()));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/discounts/detail.jsp").forward(request, response);
            return true;
        }
        if ("/admin/rentals".equals(path)) {
            configureAdminShell(request, admin, "rentals", "Rentals", "Transactions", "Search rental, customer, phone, or vehicle");
            List<dto.AdminRentalRow> rentals = rentalService.search(request.getParameter("keyword"), request.getParameter("stationId"),
                    request.getParameter("status"), request.getParameter("startDate"), request.getParameter("endDate"));
            request.setAttribute("rentals", paginate(request, rentals));
            request.setAttribute("stations", rentalService.findAllStations());
            request.setAttribute("rentalStatuses", enums.RentalStatus.values());
            request.setAttribute("keyword", paramOrDefault(request, "keyword", ""));
            request.setAttribute("selectedStationId", paramOrDefault(request, "stationId", "ALL"));
            request.setAttribute("selectedStatus", paramOrDefault(request, "status", "ALL"));
            request.setAttribute("startDate", paramOrDefault(request, "startDate", ""));
            request.setAttribute("endDate", paramOrDefault(request, "endDate", ""));
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/rentals/list.jsp").forward(request, response);
            return true;
        }
        if ("/admin/rentals/detail".equals(path)) {
            configureAdminShell(request, admin, "rentals", "Rental Detail", "Transactions", "Search rentals");
            dto.AdminRentalRow rental = rentalService.findDetail(request.getParameter("id"));
            request.setAttribute("rental", rental);
            if (rental != null) {
                request.setAttribute("payments", rentalService.findPayments(rental.getRentalId()));
                request.setAttribute("rentalHistory", rentalService.findHistory(rental.getRentalId()));
            }
            consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/rentals/detail.jsp").forward(request, response);
            return true;
        }
        if ("/admin/payments".equals(path)) {
            configureAdminShell(request, admin, "payments", "Payments", "Transactions", "Search payment, rental, customer, or transaction");
            List<dto.AdminPaymentRow> payments = paymentService.search(request.getParameter("keyword"), request.getParameter("method"), request.getParameter("type"), request.getParameter("status"));
            request.setAttribute("payments", paginate(request, payments)); request.setAttribute("paymentMethods", enums.PaymentMethod.values());
            request.setAttribute("paymentTypes", enums.PaymentType.values()); request.setAttribute("paymentStatuses", enums.PaymentStatus.values());
            request.setAttribute("keyword", paramOrDefault(request, "keyword", "")); request.setAttribute("selectedMethod", paramOrDefault(request, "method", "ALL"));
            request.setAttribute("selectedType", paramOrDefault(request, "type", "ALL")); request.setAttribute("selectedStatus", paramOrDefault(request, "status", "ALL"));
            consumeFlash(request); request.getRequestDispatcher("/WEB-INF/views/admin/payments/list.jsp").forward(request, response); return true;
        }
        if ("/admin/payments/detail".equals(path)) {
            configureAdminShell(request, admin, "payments", "Payment Detail", "Transactions", "Search payments");
            request.setAttribute("payment", paymentService.findDetail(request.getParameter("id"))); consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/payments/detail.jsp").forward(request, response); return true;
        }
        if ("/admin/wallets".equals(path)) {
            configureAdminShell(request, admin, "wallets", "Wallets", "Transactions", "Search customer name, email, or phone");
            request.setAttribute("wallets", paginate(request, walletService.search(request.getParameter("keyword"))));
            request.setAttribute("keyword", paramOrDefault(request, "keyword", "")); consumeFlash(request);
            request.getRequestDispatcher("/WEB-INF/views/admin/wallets/list.jsp").forward(request, response); return true;
        }
        if ("/admin/wallets/detail".equals(path)) {
            configureAdminShell(request, admin, "wallets", "Wallet Detail", "Transactions", "Search wallets");
            dto.AdminWalletRow wallet = walletService.findDetail(request.getParameter("id")); request.setAttribute("wallet", wallet);
            if (wallet != null) request.setAttribute("walletTransactions", walletService.findTransactions(wallet.getWalletId()));
            consumeFlash(request); request.getRequestDispatcher("/WEB-INF/views/admin/wallets/detail.jsp").forward(request, response); return true;
        }
        return false;
    }

    private boolean handleReportDetail(String path, HttpServletRequest request, HttpServletResponse response,
            Account admin, dto.AdminReportPeriod period) throws ServletException, IOException {
        dto.AdminReportData data;
        String title;
        String activeModule;
        String backAction;

        if ("/admin/financial-detail".equals(path)) {
            activeModule = "financial";
            title = "Financial Detail";
            backAction = "admin-financial-reports";
            data = reportService.financialDetail(period,
                    request.getParameter("paymentMethod"),
                    request.getParameter("paymentType"),
                    request.getParameter("status"));
        } else if ("/admin/station-performance/detail".equals(path)) {
            activeModule = "station-performance";
            title = "Station Performance Detail";
            backAction = "admin-station-performance";
            data = reportService.stationDetail(period, request.getParameter("stationId"));
        } else if ("/admin/model-performance/detail".equals(path)) {
            activeModule = "model-performance";
            title = "Model Performance Detail";
            backAction = "admin-model-performance";
            data = reportService.modelDetail(period, request.getParameter("modelId"));
        } else {
            return false;
        }

        configureAdminShell(request, admin, activeModule, title, "Detail", "Search detail");
        request.setAttribute("adminStats", data.getStats());
        request.setAttribute("adminColumns", data.getColumns());
        request.setAttribute("adminRows", paginate(request, data.getRows()));
        request.setAttribute("adminBackAction", backAction);
        request.getRequestDispatcher("/WEB-INF/views/admin/report-detail.jsp").forward(request, response);
        return true;
    }

    private void configureAdminShell(HttpServletRequest request, Account admin, String activeModule,
            String title, String badge, String searchPlaceholder) {
        request.setAttribute("adminAccount", admin);
        request.setAttribute("activeModule", activeModule);
        request.setAttribute("adminPageTitle", title);
        request.setAttribute("adminPageBadge", badge);
        request.setAttribute("adminSearchPlaceholder", searchPlaceholder);
    }

    private void consumeFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        request.setAttribute("adminSuccess", session.getAttribute("adminSuccess"));
        request.setAttribute("adminError", session.getAttribute("adminError"));
        session.removeAttribute("adminSuccess");
        session.removeAttribute("adminError");
    }

    private Account requireAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof Account)) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return null;
        }
        Account user = (Account) session.getAttribute("user");
        if (user.getRole() != Role.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        return user;
    }

    private String paramOrDefault(HttpServletRequest request, String name, String fallback) {
        String value = request.getParameter(name);
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private <T> List<T> paginate(HttpServletRequest request, List<T> items) {
        List<T> source = items == null ? new ArrayList<T>() : items;
        int totalItems = source.size();
        int pageSize = parseBoundedInt(request.getParameter("pageSize"), 10, 5, 50);
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) pageSize));
        int currentPage = parseBoundedInt(request.getParameter("page"), 1, 1, totalPages);
        int fromIndex = totalItems == 0 ? 0 : (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        request.setAttribute("adminPagination", new AdminPagination(
                currentPage,
                totalPages,
                totalItems,
                pageSize,
                totalItems == 0 ? 0 : fromIndex + 1,
                toIndex,
                paginationUrlPrefix(request, pageSize)));
        return source.subList(fromIndex, toIndex);
    }

    private int parseBoundedInt(String value, int fallback, int min, int max) {
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(min, Math.min(max, parsed));
        } catch (Exception ex) {
            return fallback;
        }
    }

    private String paginationUrlPrefix(HttpServletRequest request, int pageSize) {
        StringBuilder query = new StringBuilder();
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.trim().isEmpty()) {
            for (String pair : queryString.split("&")) {
                if (pair.startsWith("page=") || pair.startsWith("pageSize=") || pair.trim().isEmpty()) {
                    continue;
                }
                if (query.length() > 0) {
                    query.append("&");
                }
                query.append(pair);
            }
        }
        if (query.length() > 0) {
            query.append("&");
        }
        query.append("pageSize=").append(pageSize).append("&page=");
        return request.getContextPath() + "/?" + query.toString();
    }

    private String actionForPath(String path) {
        if ("/admin/financial-reports".equals(path)) {
            return "admin-financial-reports";
        }
        if ("/admin/station-performance".equals(path)) {
            return "admin-station-performance";
        }
        if ("/admin/model-performance".equals(path)) {
            return "admin-model-performance";
        }
        if ("/admin/accounts".equals(path)) {
            return "admin-accounts";
        }
        if ("/admin/stations".equals(path)) {
            return "admin-stations";
        }
        if ("/admin/categories".equals(path)) {
            return "admin-categories";
        }
        if ("/admin/vehicle-models".equals(path)) {
            return "admin-vehicle-models";
        }
        if ("/admin/vehicle-model-images".equals(path)) {
            return "admin-vehicle-model-images";
        }
        if ("/admin/vehicles".equals(path)) {
            return "admin-vehicles";
        }
        if ("/admin/discounts".equals(path)) {
            return "admin-discounts";
        }
        if ("/admin/rental-discounts".equals(path)) {
            return "admin-rental-discounts";
        }
        if ("/admin/rentals".equals(path)) {
            return "admin-rentals";
        }
        if ("/admin/rental-status-history".equals(path)) {
            return "admin-rental-status-history";
        }
        if ("/admin/payments".equals(path)) {
            return "admin-payments";
        }
        if ("/admin/extra-charges".equals(path)) {
            return "admin-extra-charges";
        }
        if ("/admin/incidents".equals(path)) {
            return "admin-incidents";
        }
        if ("/admin/maintenance".equals(path)) {
            return "admin-maintenance";
        }
        if ("/admin/wallets".equals(path)) {
            return "admin-wallets";
        }
        if ("/admin/reviews".equals(path)) {
            return "admin-reviews";
        }
        if ("/admin/profile".equals(path)) {
            return "admin-profile";
        }
        return "admin-dashboard";
    }

    private AdminPage resolvePage(HttpServletRequest request, dto.AdminReportPeriod reportPeriod) {
        String path = request.getServletPath();
        if ("/admin/financial-reports".equals(path)) {
            return reportPage("financial", "Financial Reports", "Monitor revenue, payment health, wallet topups, and extra charges.",
                    "Finance", "Export Report", "Search payment, rental, or customer",
                    reportService.financial(reportPeriod), "financial");
        }
        if ("/admin/station-performance".equals(path)) {
            return reportPage("station-performance", "Station Performance", "Compare station utilization, vehicle availability, and revenue.",
                    "Performance", "View Heatmap", "Search station",
                    reportService.stationPerformance(reportPeriod), "station");
        }
        if ("/admin/model-performance".equals(path)) {
            return reportPage("model-performance", "Model Performance", "Track model demand, revenue, incidents, and maintenance frequency.",
                    "Performance", "Compare Models", "Search model",
                    reportService.modelPerformance(reportPeriod), "model");
        }
        if ("/admin/accounts".equals(path)) {
            return crud("accounts", "Accounts", "Manage customer, staff, and admin accounts.",
                    "Add Account", "Search name, email, phone", "Total Accounts", "126", "Customers", "112",
                    "Staff", "8", "Admins", "3", columns("Full Name", "Email", "Role", "Status"));
        }
        if ("/admin/stations".equals(path)) {
            return crud("stations", "Stations", "Manage pickup and return stations.",
                    "Add Station", "Search station or address", "Stations", "5", "Active", "5",
                    "Vehicles", "50", "Top Station", "Quan 1", columns("Station", "Address", "Vehicles", "Status"));
        }
        if ("/admin/categories".equals(path)) {
            return crud("categories", "Categories", "Manage vehicle categories used by search and models.",
                    "Add Category", "Search category", "Categories", "5", "Models", "18",
                    "Active", "5", "Priority", "P1", columns("Category", "Models", "Vehicles", "Status"));
        }
        if ("/admin/vehicle-models".equals(path)) {
            return crud("vehicle-models", "Vehicle Models", "Manage model profile, price, seats, and category.",
                    "Add Model", "Search model or category", "Models", "18", "Categories", "5",
                    "Avg Price", "520K", "Images", "32", columns("Model", "Category", "Price/Day", "Seats"));
        }
        if ("/admin/vehicle-model-images".equals(path)) {
            return crud("vehicle-model-images", "Vehicle Model Images", "Manage model thumbnails and gallery images.",
                    "Add Image", "Search model image", "Images", "32", "Models", "18",
                    "Missing", "2", "Priority", "P2", columns("Model", "Image Path", "Type", "Status"));
        }
        if ("/admin/vehicles".equals(path)) {
            return crud("vehicles", "Vehicles", "Manage inventory, station assignment, battery, color, and status.",
                    "Add Vehicle", "Search license plate, model, station", "Vehicles", "50", "Available", "36",
                    "Rented", "14", "Maintenance", "5", columns("License Plate", "Model", "Station", "Status"));
        }
        if ("/admin/discounts".equals(path)) {
            return crud("discounts", "Discounts", "Manage promotion codes, date ranges, quantity, and status.",
                    "Add Discount", "Search code or status", "Discounts", "8", "Active", "5",
                    "Expired", "2", "Used", "41", columns("Code", "Type", "Value", "Status"));
        }
        if ("/admin/rental-discounts".equals(path)) {
            return crud("rental-discounts", "Rental Discounts", "View discounts applied to rentals.",
                    "View Usage", "Search rental or discount", "Applied", "41", "Active Codes", "5",
                    "Savings", "12.4M", "Priority", "P4", columns("Rental", "Discount", "Customer", "Amount"));
        }
        if ("/admin/rentals".equals(path)) {
            return crud("rentals", "Rentals", "View rental lifecycle, customer, vehicle, and status.",
                    "Export Rentals", "Search rental, customer, phone", "Rentals", "78", "Booked", "9",
                    "Rented", "14", "Completed", "51", columns("Rental ID", "Customer", "Vehicle", "Status"));
        }
        if ("/admin/rental-status-history".equals(path)) {
            return crud("rental-status-history", "Rental Status History", "Audit all rental status changes.",
                    "Export History", "Search rental id", "History Rows", "162", "Today", "11",
                    "Completed", "51", "No Show", "3", columns("Rental ID", "Status", "Changed At", "Actor"));
        }
        if ("/admin/payments".equals(path)) {
            return crud("payments", "Payments", "View booking payments and extra charge payments.",
                    "Export Payments", "Search payment, rental, transaction", "Payments", "92", "Success", "84",
                    "Pending", "5", "Failed", "3", columns("Payment ID", "Method", "Type", "Status"));
        }
        if ("/admin/extra-charges".equals(path)) {
            return crud("extra-charges", "Extra Charges", "Manage late, damage, cleaning, and other charges.",
                    "Create Charge", "Search rental or customer", "Charges", "16", "Paid", "11",
                    "Pending", "3", "Unpaid", "2", columns("Charge Type", "Rental", "Amount", "Status"));
        }
        if ("/admin/incidents".equals(path)) {
            return crud("incidents", "Incidents", "Review vehicle damage incidents and severity.",
                    "View Incidents", "Search incident, vehicle, rental", "Incidents", "7", "High", "1",
                    "Medium", "3", "Low", "3", columns("Incident ID", "Vehicle", "Severity", "Created"));
        }
        if ("/admin/maintenance".equals(path)) {
            return crud("maintenance", "Maintenance", "Track vehicles under maintenance and completed jobs.",
                    "Add Maintenance", "Search vehicle or status", "Pending", "5", "Completed", "18",
                    "Vehicles", "5", "This Month", "9", columns("Vehicle", "Description", "Date", "Status"));
        }
        if ("/admin/wallets".equals(path)) {
            return crud("wallets", "Wallets", "View customer wallet balance and wallet transaction activity.",
                    "Export Wallets", "Search customer wallet", "Wallets", "118", "Topups", "74",
                    "Payments", "36", "Balance", "86.5M", columns("Customer", "Balance", "Last Transaction", "Status"));
        }
        if ("/admin/reviews".equals(path)) {
            return crud("reviews", "Reviews", "Moderate customer reviews and rating quality.",
                    "Moderate Reviews", "Search customer, vehicle, rating", "Reviews", "24", "Average", "4.6",
                    "Hidden", "0", "Priority", "P4", columns("Customer", "Vehicle", "Rating", "Created"));
        }
        if ("/admin/profile".equals(path)) {
            return page("profile", "Admin Profile", "Review your admin account and access level.",
                    "Account", "Edit Profile", "Search activity",
                    stats("Role", "ADMIN", "Access", "Full", "Security", "Active", "Session", "Online"),
                    columns("Field", "Value", "Scope", "Status"),
                    rows(row("Full Name", "System Admin", "Account", "Active"),
                            row("Role", "ADMIN", "Authorization", "Granted"),
                            row("Dashboard", "All Modules", "Permission", "Enabled")));
        }
        return dashboard();
    }

    private AdminPage dashboard() {
        return reportPage("dashboard", "Admin Dashboard", "Control finance, operations, inventory, and platform data from one workspace.",
                "Overview", "New Report", "Search system data", reportService.dashboard(), "table");
    }

    private AdminPage crud(String module, String title, String subtitle, String action, String search,
            String s1, String v1, String s2, String v2, String s3, String v3, String s4, String v4,
            List<String> columns) {
        return page(module, title, subtitle, "CRUD", action, search,
                stats(s1, v1, s2, v2, s3, v3, s4, v4),
                columns,
                rows(row(columns.get(0), columns.get(1), columns.get(2), "Draft UI"),
                        row("Sample Record", "Pending DAO", "Service Layer", "Ready"),
                        row("Validation", "Business Rules", "Role Guard", "Planned")));
    }

    private AdminPage page(String activeModule, String title, String subtitle, String badge,
            String primaryAction, String searchPlaceholder, List<AdminStat> stats,
            List<String> columns, List<List<String>> rows) {
        return page(activeModule, title, subtitle, badge, primaryAction,
                searchPlaceholder, stats, columns, rows, "table");
    }

    private AdminPage reportPage(String activeModule, String title, String subtitle, String badge,
            String primaryAction, String searchPlaceholder, dto.AdminReportData data, String chartMode) {
        return new AdminPage(activeModule, title, subtitle, badge, primaryAction,
                searchPlaceholder, data.getStats(), data.getColumns(), data.getRows(), chartMode,
                data.getPrimaryChartItems(), data.getSecondaryChartItems());
    }

    private AdminPage page(String activeModule, String title, String subtitle, String badge,
            String primaryAction, String searchPlaceholder, List<AdminStat> stats,
            List<String> columns, List<List<String>> rows, String chartMode) {
        return new AdminPage(activeModule, title, subtitle, badge, primaryAction,
                searchPlaceholder, stats, columns, rows, chartMode, null, null);
    }

    private ReportSelection buildReportSelection(HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        String selectedPeriod = paramOrDefault(request, "period", "month");
        String selectedMonth = paramOrDefault(request, "month", YearMonth.from(today).toString());
        String selectedQuarter = paramOrDefault(request, "quarter", String.valueOf(((today.getMonthValue() - 1) / 3) + 1));
        String selectedYear = paramOrDefault(request, "year", String.valueOf(today.getYear()));
        String selectedStartDate = paramOrDefault(request, "startDate", today.withDayOfMonth(1).toString());
        String selectedEndDate = paramOrDefault(request, "endDate", today.toString());

        LocalDate start;
        LocalDate endExclusive;
        int reportYear;
        try {
            if ("custom".equals(selectedPeriod)) {
                start = LocalDate.parse(selectedStartDate);
                endExclusive = LocalDate.parse(selectedEndDate).plusDays(1);
                reportYear = start.getYear();
            } else if ("quarter".equals(selectedPeriod)) {
                reportYear = Integer.parseInt(selectedYear);
                int quarter = Math.max(1, Math.min(4, Integer.parseInt(selectedQuarter)));
                int startMonth = (quarter - 1) * 3 + 1;
                start = LocalDate.of(reportYear, startMonth, 1);
                endExclusive = start.plusMonths(3);
            } else if ("year".equals(selectedPeriod)) {
                reportYear = Integer.parseInt(selectedYear);
                start = LocalDate.of(reportYear, 1, 1);
                endExclusive = start.plusYears(1);
            } else {
                YearMonth month = YearMonth.parse(selectedMonth);
                selectedPeriod = "month";
                reportYear = month.getYear();
                start = month.atDay(1);
                endExclusive = month.plusMonths(1).atDay(1);
            }
        } catch (RuntimeException ex) {
            selectedPeriod = "month";
            YearMonth month = YearMonth.from(today);
            selectedMonth = month.toString();
            selectedQuarter = String.valueOf(((today.getMonthValue() - 1) / 3) + 1);
            selectedYear = String.valueOf(today.getYear());
            start = month.atDay(1);
            endExclusive = month.plusMonths(1).atDay(1);
            reportYear = today.getYear();
        }

        dto.AdminReportPeriod period = new dto.AdminReportPeriod(selectedPeriod,
                Timestamp.valueOf(start.atStartOfDay()),
                Timestamp.valueOf(endExclusive.atStartOfDay()),
                reportYear);
        return new ReportSelection(period, selectedPeriod, selectedStartDate, selectedEndDate,
                selectedMonth, selectedQuarter, selectedYear);
    }

    private List<AdminStat> stats(String l1, String v1, String l2, String v2, String l3, String v3, String l4, String v4) {
        return Arrays.asList(new AdminStat(l1, v1), new AdminStat(l2, v2), new AdminStat(l3, v3), new AdminStat(l4, v4));
    }

    private List<String> columns(String c1, String c2, String c3, String c4) {
        return Arrays.asList(c1, c2, c3, c4);
    }

    private List<List<String>> rows(List<String> r1, List<String> r2, List<String> r3) {
        return Arrays.asList(r1, r2, r3);
    }

    private List<String> row(String c1, String c2, String c3, String c4) {
        return Arrays.asList(c1, c2, c3, c4);
    }

    public static class AdminStat {

        private final String label;
        private final String value;

        public AdminStat(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }
    }

    public static class AdminPage {

        private final String activeModule;
        private final String title;
        private final String subtitle;
        private final String badge;
        private final String primaryAction;
        private final String searchPlaceholder;
        private final List<?> stats;
        private final List<String> columns;
        private final List<List<String>> rows;
        private final String chartMode;
        private final List<?> primaryChartItems;
        private final List<?> secondaryChartItems;

        public AdminPage(String activeModule, String title, String subtitle, String badge,
                String primaryAction, String searchPlaceholder, List<?> stats,
                List<String> columns, List<List<String>> rows, String chartMode,
                List<?> primaryChartItems, List<?> secondaryChartItems) {
            this.activeModule = activeModule;
            this.title = title;
            this.subtitle = subtitle;
            this.badge = badge;
            this.primaryAction = primaryAction;
            this.searchPlaceholder = searchPlaceholder;
            this.stats = stats;
            this.columns = columns;
            this.rows = rows;
            this.chartMode = chartMode;
            this.primaryChartItems = primaryChartItems;
            this.secondaryChartItems = secondaryChartItems;
        }
    }

    public static class AdminPagination {

        private final int currentPage;
        private final int totalPages;
        private final int totalItems;
        private final int pageSize;
        private final int startItem;
        private final int endItem;
        private final String urlPrefix;

        public AdminPagination(int currentPage, int totalPages, int totalItems, int pageSize,
                int startItem, int endItem, String urlPrefix) {
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
            this.pageSize = pageSize;
            this.startItem = startItem;
            this.endItem = endItem;
            this.urlPrefix = urlPrefix;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getStartItem() {
            return startItem;
        }

        public int getEndItem() {
            return endItem;
        }

        public String getUrlPrefix() {
            return urlPrefix;
        }

        public boolean isHasPrevious() {
            return currentPage > 1;
        }

        public boolean isHasNext() {
            return currentPage < totalPages;
        }
    }

    private static class ReportSelection {

        private final dto.AdminReportPeriod period;
        private final String selectedPeriod;
        private final String startDate;
        private final String endDate;
        private final String month;
        private final String quarter;
        private final String year;

        private ReportSelection(dto.AdminReportPeriod period, String selectedPeriod, String startDate,
                String endDate, String month, String quarter, String year) {
            this.period = period;
            this.selectedPeriod = selectedPeriod;
            this.startDate = startDate;
            this.endDate = endDate;
            this.month = month;
            this.quarter = quarter;
            this.year = year;
        }
    }
}
