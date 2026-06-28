package controllers;

import enums.Role;
import enums.VehicleModelImageType;
import java.io.IOException;
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
import services.AdminVehicleModelImageService;
import services.AdminVehicleModelService;

@WebServlet(name = "AdminController", urlPatterns = {
    "/admin/dashboard",
    "/admin/financial-reports",
    "/admin/station-performance",
    "/admin/model-performance",
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
    "/admin/discounts",
    "/admin/rental-discounts",
    "/admin/rentals",
    "/admin/rental-status-history",
    "/admin/payments",
    "/admin/extra-charges",
    "/admin/incidents",
    "/admin/maintenance",
    "/admin/wallets",
    "/admin/reviews",
    "/admin/profile"
})
public class AdminController extends HttpServlet {
    private final AdminAccountService accountService = new AdminAccountService();
    private final AdminVehicleModelService vehicleModelService = new AdminVehicleModelService();
    private final AdminVehicleModelImageService vehicleModelImageService = new AdminVehicleModelImageService();

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

        AdminPage page = resolvePage(request.getServletPath());
        request.setAttribute("adminAccount", admin);
        request.setAttribute("activeModule", page.activeModule);
        request.setAttribute("adminPageTitle", page.title);
        request.setAttribute("adminPageSubtitle", page.subtitle);
        request.setAttribute("adminPageBadge", page.badge);
        request.setAttribute("adminStats", page.stats);
        request.setAttribute("adminColumns", page.columns);
        request.setAttribute("adminRows", page.rows);
        request.setAttribute("adminPrimaryAction", page.primaryAction);
        request.setAttribute("adminSearchPlaceholder", page.searchPlaceholder);
        request.setAttribute("adminChartMode", page.chartMode);
        request.setAttribute("adminCurrentAction", actionForPath(request.getServletPath()));
        request.setAttribute("reportPeriod", paramOrDefault(request, "period", "month"));
        request.setAttribute("reportStartDate", paramOrDefault(request, "startDate", ""));
        request.setAttribute("reportEndDate", paramOrDefault(request, "endDate", ""));
        request.setAttribute("reportMonth", paramOrDefault(request, "month", "2026-06"));
        request.setAttribute("reportQuarter", paramOrDefault(request, "quarter", "2"));
        request.setAttribute("reportYear", paramOrDefault(request, "year", "2026"));

        String jsp = "dashboard".equals(page.activeModule)
                ? "/WEB-INF/views/admin/dashboard.jsp"
                : "/WEB-INF/views/admin/module.jsp";
        request.getRequestDispatcher(jsp).forward(request, response);
    }

    private boolean handleCrudGet(String path, HttpServletRequest request, HttpServletResponse response, Account admin)
            throws ServletException, IOException {
        if ("/admin/accounts".equals(path)) {
            configureAdminShell(request, admin, "accounts", "Accounts", "CRUD", "Search name, email, phone");
            request.setAttribute("accounts", accountService.search(
                    request.getParameter("keyword"),
                    request.getParameter("role"),
                    request.getParameter("status")));
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
        if ("/admin/vehicle-models".equals(path)) {
            configureAdminShell(request, admin, "vehicle-models", "Vehicle Models", "CRUD", "Search model or category");
            request.setAttribute("models", vehicleModelService.search(request.getParameter("keyword"), request.getParameter("categoryId")));
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
            request.setAttribute("images", vehicleModelImageService.search(
                    request.getParameter("keyword"),
                    request.getParameter("modelId"),
                    request.getParameter("imageType")));
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
        return false;
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
        if (session == null) return;
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

    private String actionForPath(String path) {
        if ("/admin/financial-reports".equals(path)) return "admin-financial-reports";
        if ("/admin/station-performance".equals(path)) return "admin-station-performance";
        if ("/admin/model-performance".equals(path)) return "admin-model-performance";
        if ("/admin/accounts".equals(path)) return "admin-accounts";
        if ("/admin/stations".equals(path)) return "admin-stations";
        if ("/admin/categories".equals(path)) return "admin-categories";
        if ("/admin/vehicle-models".equals(path)) return "admin-vehicle-models";
        if ("/admin/vehicle-model-images".equals(path)) return "admin-vehicle-model-images";
        if ("/admin/vehicles".equals(path)) return "admin-vehicles";
        if ("/admin/discounts".equals(path)) return "admin-discounts";
        if ("/admin/rental-discounts".equals(path)) return "admin-rental-discounts";
        if ("/admin/rentals".equals(path)) return "admin-rentals";
        if ("/admin/rental-status-history".equals(path)) return "admin-rental-status-history";
        if ("/admin/payments".equals(path)) return "admin-payments";
        if ("/admin/extra-charges".equals(path)) return "admin-extra-charges";
        if ("/admin/incidents".equals(path)) return "admin-incidents";
        if ("/admin/maintenance".equals(path)) return "admin-maintenance";
        if ("/admin/wallets".equals(path)) return "admin-wallets";
        if ("/admin/reviews".equals(path)) return "admin-reviews";
        if ("/admin/profile".equals(path)) return "admin-profile";
        return "admin-dashboard";
    }

    private AdminPage resolvePage(String path) {
        if ("/admin/financial-reports".equals(path)) {
            return page("financial", "Financial Reports", "Monitor revenue, payment health, wallet topups, and extra charges.",
                    "Finance", "Export Report", "Search payment, rental, or customer",
                    stats("Total Revenue", "128.6M", "Booking Revenue", "94.2M", "Extra Charges", "8.4M", "Pending Payments", "12"),
                    columns("Metric", "Payment Method", "Status", "Amount"),
                    rows(
                            row("Booking Revenue", "Wallet + VNPay", "SUCCESS", "94,200,000 VND"),
                            row("Late Fees", "Cash + VNPay", "SUCCESS", "5,600,000 VND"),
                            row("Damage Fees", "Cash + VNPay", "PENDING", "2,800,000 VND")
                    ), "financial");
        }
        if ("/admin/station-performance".equals(path)) {
            return page("station-performance", "Station Performance", "Compare station utilization, vehicle availability, and revenue.",
                    "Performance", "View Heatmap", "Search station",
                    stats("Top Station", "Quan 1", "Available Vehicles", "36", "Rented Vehicles", "14", "Maintenance", "5"),
                    columns("Station", "Available", "Rented", "Revenue"),
                    rows(row("Tram Quan 1", "12", "6", "32,400,000 VND"),
                            row("Tram Tan Binh", "9", "4", "21,800,000 VND"),
                            row("Tram Thu Duc", "15", "4", "18,600,000 VND")), "station");
        }
        if ("/admin/model-performance".equals(path)) {
            return page("model-performance", "Model Performance", "Track model demand, revenue, incidents, and maintenance frequency.",
                    "Performance", "Compare Models", "Search model",
                    stats("Most Booked", "VF e34", "Model Groups", "18", "Incidents", "7", "Avg Utilization", "68%"),
                    columns("Model", "Bookings", "Revenue", "Incidents"),
                    rows(row("VinFast VF e34", "42", "56,200,000 VND", "2"),
                            row("Tesla Model 3", "31", "44,500,000 VND", "1"),
                            row("Yadea iGo", "26", "8,300,000 VND", "0")), "model");
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
        return page("dashboard", "Admin Dashboard", "Control finance, operations, inventory, and platform data from one workspace.",
                "Overview", "New Report", "Search system data",
                stats("Total Revenue", "128.6M", "Active Rentals", "23", "Available Vehicles", "36", "Pending Charges", "5"),
                columns("Module", "Signal", "Priority", "Status"),
                rows(row("Financial Reports", "Revenue and payment health", "P1", "Ready for wiring"),
                        row("Station Performance", "Station utilization", "P1", "UI Ready"),
                        row("Model Performance", "Fleet demand by model", "P1", "UI Ready")));
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

    private AdminPage page(String activeModule, String title, String subtitle, String badge,
            String primaryAction, String searchPlaceholder, List<AdminStat> stats,
            List<String> columns, List<List<String>> rows, String chartMode) {
        return new AdminPage(activeModule, title, subtitle, badge, primaryAction,
                searchPlaceholder, stats, columns, rows, chartMode);
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

        public String getLabel() { return label; }
        public String getValue() { return value; }
    }

    public static class AdminPage {
        private final String activeModule;
        private final String title;
        private final String subtitle;
        private final String badge;
        private final String primaryAction;
        private final String searchPlaceholder;
        private final List<AdminStat> stats;
        private final List<String> columns;
        private final List<List<String>> rows;
        private final String chartMode;

        public AdminPage(String activeModule, String title, String subtitle, String badge,
                String primaryAction, String searchPlaceholder, List<AdminStat> stats,
                List<String> columns, List<List<String>> rows, String chartMode) {
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
        }
    }
}
