package controllers;

import daos.StaffDashboardDAO;
import daos.WalletDAO;
import dto.AdminChartItem;
import dto.AdminReportData;
import dto.AdminReportMetric;
import dto.AdminReportPeriod;
import dto.StaffActivityDTO;
import dto.StaffDashboardDTO;
import enums.Role;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import models.Wallet;
import services.AdminReportService;

@WebServlet(name = "RealtimeDataController", urlPatterns = {"/api/realtime-data"})
public class RealtimeDataController extends HttpServlet {
    private final WalletDAO walletDAO = new WalletDAO();
    private final StaffDashboardDAO staffDashboardDAO = new StaffDashboardDAO();
    private final AdminReportService adminReportService = new AdminReportService();
    private final DecimalFormat moneyFormat = new DecimalFormat("#,##0.00");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        Account user = currentUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"unauthorized\"}");
            return;
        }

        String type = request.getParameter("type");
        if ("wallet".equals(type)) {
            writeWallet(response, user);
            return;
        }
        if ("staff-dashboard".equals(type)) {
            if (user.getRole() != Role.STAFF) {
                forbidden(response);
                return;
            }
            writeStaffDashboard(response);
            return;
        }
        if ("admin-dashboard".equals(type)) {
            if (user.getRole() != Role.ADMIN) {
                forbidden(response);
                return;
            }
            writeAdminReport(response, adminReportService.dashboard(), false);
            return;
        }
        if ("admin-report".equals(type)) {
            if (user.getRole() != Role.ADMIN) {
                forbidden(response);
                return;
            }
            writeAdminReport(response, resolveAdminReport(request), true);
            return;
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\":\"unknown_type\"}");
    }

    private void writeWallet(HttpServletResponse response, Account user) throws IOException {
        Wallet wallet = walletDAO.getWalletByAccountId(user.getAccountId());
        double balance = wallet == null || wallet.getBalance() == null ? 0.0 : wallet.getBalance();
        response.getWriter().write("{\"balance\":" + balance
                + ",\"balanceText\":\"" + json(moneyFormat.format(balance) + " VND") + "\"}");
    }

    private void writeStaffDashboard(HttpServletResponse response) throws IOException {
        StaffDashboardDTO dashboard = staffDashboardDAO.loadDashboard();
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"waitingForPickup\":").append(dashboard.getWaitingForPickup()).append(",");
        json.append("\"currentlyRented\":").append(dashboard.getCurrentlyRented()).append(",");
        json.append("\"waitingForReturn\":").append(dashboard.getWaitingForReturn()).append(",");
        json.append("\"underMaintenance\":").append(dashboard.getUnderMaintenance()).append(",");
        json.append("\"recentActivities\":[");
        List<StaffActivityDTO> activities = dashboard.getRecentActivities();
        for (int i = 0; i < activities.size(); i++) {
            StaffActivityDTO activity = activities.get(i);
            if (i > 0) json.append(",");
            json.append("{")
                    .append("\"rentalId\":\"").append(json(activity.getRentalId())).append("\",")
                    .append("\"customer\":\"").append(json(activity.getCustomer())).append("\",")
                    .append("\"vehicle\":\"").append(json(activity.getVehicle())).append("\",")
                    .append("\"action\":\"").append(json(activity.getAction())).append("\",")
                    .append("\"time\":\"").append(json(formatTime(activity.getTime()))).append("\"")
                    .append("}");
        }
        json.append("]}");
        response.getWriter().write(json.toString());
    }

    private void writeAdminReport(HttpServletResponse response, AdminReportData data, boolean includeCharts)
            throws IOException {
        StringBuilder json = new StringBuilder();
        json.append("{");
        appendStats(json, data.getStats());
        json.append(",");
        appendRows(json, data.getRows());
        if (includeCharts) {
            json.append(",");
            appendChart(json, "primaryChart", data.getPrimaryChartItems());
            json.append(",");
            appendChart(json, "secondaryChart", data.getSecondaryChartItems());
        }
        json.append("}");
        response.getWriter().write(json.toString());
    }

    private AdminReportData resolveAdminReport(HttpServletRequest request) {
        AdminReportPeriod period = buildReportPeriod(request);
        String report = request.getParameter("report");
        if ("station".equals(report)) {
            return adminReportService.stationPerformance(period);
        }
        if ("model".equals(report)) {
            return adminReportService.modelPerformance(period);
        }
        return adminReportService.financial(period);
    }

    private AdminReportPeriod buildReportPeriod(HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        String selectedPeriod = value(request.getParameter("period"), "month");
        String selectedMonth = value(request.getParameter("month"), YearMonth.from(today).toString());
        String selectedQuarter = value(request.getParameter("quarter"), String.valueOf(((today.getMonthValue() - 1) / 3) + 1));
        String selectedYear = value(request.getParameter("year"), String.valueOf(today.getYear()));
        String selectedStartDate = value(request.getParameter("startDate"), today.withDayOfMonth(1).toString());
        String selectedEndDate = value(request.getParameter("endDate"), today.toString());

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
                start = LocalDate.of(reportYear, (quarter - 1) * 3 + 1, 1);
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
            reportYear = today.getYear();
            start = month.atDay(1);
            endExclusive = month.plusMonths(1).atDay(1);
        }
        return new AdminReportPeriod(selectedPeriod, Timestamp.valueOf(start.atStartOfDay()),
                Timestamp.valueOf(endExclusive.atStartOfDay()), reportYear);
    }

    private void appendStats(StringBuilder json, List<AdminReportMetric> stats) {
        json.append("\"stats\":[");
        for (int i = 0; i < stats.size(); i++) {
            AdminReportMetric stat = stats.get(i);
            if (i > 0) json.append(",");
            json.append("{\"label\":\"").append(json(stat.getLabel())).append("\",")
                    .append("\"value\":\"").append(json(stat.getValue())).append("\"}");
        }
        json.append("]");
    }

    private void appendRows(StringBuilder json, List<List<String>> rows) {
        json.append("\"rows\":[");
        for (int i = 0; i < rows.size(); i++) {
            if (i > 0) json.append(",");
            json.append("[");
            List<String> row = rows.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (j > 0) json.append(",");
                json.append("\"").append(json(row.get(j))).append("\"");
            }
            json.append("]");
        }
        json.append("]");
    }

    private void appendChart(StringBuilder json, String name, List<AdminChartItem> items) {
        json.append("\"").append(name).append("\":[");
        for (int i = 0; i < items.size(); i++) {
            AdminChartItem item = items.get(i);
            if (i > 0) json.append(",");
            json.append("{\"label\":\"").append(json(item.getLabel())).append("\",")
                    .append("\"value\":\"").append(json(item.getValue())).append("\",")
                    .append("\"percent\":").append(item.getPercent()).append(",")
                    .append("\"secondaryPercent\":").append(item.getSecondaryPercent()).append(",")
                    .append("\"tertiaryPercent\":").append(item.getTertiaryPercent()).append("}");
        }
        json.append("]");
    }

    private Account currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof Account)) {
            return null;
        }
        return (Account) session.getAttribute("user");
    }

    private void forbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{\"error\":\"forbidden\"}");
    }

    private String value(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }

    private String formatTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(timestamp);
    }

    private String json(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder result = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"': result.append("\\\""); break;
                case '\\': result.append("\\\\"); break;
                case '\n': result.append("\\n"); break;
                case '\r': result.append("\\r"); break;
                case '\t': result.append("\\t"); break;
                default:
                    if (ch < 32) {
                        result.append(String.format("\\u%04x", (int) ch));
                    } else {
                        result.append(ch);
                    }
            }
        }
        return result.toString();
    }
}
