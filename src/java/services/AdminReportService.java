package services;

import daos.AdminReportDAO;
import dto.AdminChartItem;
import dto.AdminReportData;
import dto.AdminReportMetric;
import dto.AdminReportPeriod;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utils.JPAUtil;

public class AdminReportService {
    private static final String[] MONTH_LABELS = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private final AdminReportDAO reportDAO = new AdminReportDAO();
    private final DecimalFormat numberFormat = new DecimalFormat("#,##0");

    public AdminReportData dashboard() {
        return JPAUtil.execute(em -> {
            Object[] totals = reportDAO.dashboardTotals(em);
            List<AdminReportMetric> stats = Arrays.asList(
                    metric("Total Revenue", moneyShort(decimal(totals[0]))),
                    metric("Active Rentals", number(totals[1])),
                    metric("Available Vehicles", number(totals[2])),
                    metric("Pending Charges", number(totals[3])));

            List<List<String>> rows = new ArrayList<>();
            for (Object[] row : reportDAO.recentDashboardRows(em)) {
                rows.add(Arrays.asList(shortId(row[0]), text(row[1]), text(row[2]), text(row[3])));
            }
            if (rows.isEmpty()) {
                rows.add(Arrays.asList("N/A", "No rentals yet", "N/A", "EMPTY"));
            }
            return new AdminReportData(stats,
                    Arrays.asList("Rental ID", "Customer", "Vehicle", "Status"),
                    rows, null, null);
        });
    }

    public AdminReportData financial(AdminReportPeriod period) {
        return JPAUtil.execute(em -> {
            Object[] totals = reportDAO.financialTotals(em, period.getStart(), period.getEndExclusive());
            BigDecimal totalRevenue = decimal(totals[0]);
            BigDecimal bookingRevenue = decimal(totals[1]);
            BigDecimal chargeRevenue = decimal(totals[2]);
            List<AdminReportMetric> stats = Arrays.asList(
                    metric("Total Revenue", moneyShort(totalRevenue)),
                    metric("Booking Revenue", moneyShort(bookingRevenue)),
                    metric("Extra Charges", moneyShort(chargeRevenue)),
                    metric("Pending Payments", number(totals[3])));

            List<List<String>> rows = new ArrayList<>();
            for (Object[] row : reportDAO.financialRows(em, period.getStart(), period.getEndExclusive())) {
                String type = text(row[0]);
                rows.add(Arrays.asList(revenueCategoryLabel(type), revenueCategoryDescription(type),
                        money(decimal(row[1])), money(decimal(row[2])), money(decimal(row[3])),
                        "?action=admin-financial-detail&" + exactPeriodParams(period)
                        + "&paymentType=" + encode(type)));
            }
            if (rows.isEmpty()) {
                rows.add(Arrays.asList("No revenue yet", "No payments were created in this period.", "0 VND", "0 VND", "0 VND"));
            }

            return new AdminReportData(stats,
                    Arrays.asList("Revenue Category", "Meaning", "Collected", "Pending", "Failed"),
                    rows,
                    revenueBars(em, period),
                    paymentMix(reportDAO.paymentMix(em, period.getStart(), period.getEndExclusive())));
        });
    }

    public AdminReportData stationPerformance(AdminReportPeriod period) {
        return JPAUtil.execute(em -> {
            Object[] fleet = reportDAO.fleetStatusTotals(em);
            Object[] topStation = reportDAO.topStationRevenue(em, period.getStart(), period.getEndExclusive());
            List<AdminReportMetric> stats = Arrays.asList(
                    metric("Top Station", text(topStation[0])),
                    metric("Available Vehicles", number(fleet[0])),
                    metric("Rented Vehicles", number(fleet[1])),
                    metric("Maintenance", number(fleet[2])));

            List<List<String>> rows = new ArrayList<>();
            List<AdminChartItem> stacks = new ArrayList<>();
            List<AdminChartItem> revenueShare = new ArrayList<>();
            BigDecimal totalRevenue = BigDecimal.ZERO;
            List<Object[]> stationRows = reportDAO.stationRows(em, period.getStart(), period.getEndExclusive());
            for (Object[] row : stationRows) {
                totalRevenue = totalRevenue.add(decimal(row[5]));
            }
            for (Object[] row : stationRows) {
                String stationId = text(row[0]);
                String stationName = text(row[1]);
                int available = integer(row[2]);
                int rented = integer(row[3]);
                int maintenance = integer(row[4]);
                int total = Math.max(1, available + rented + maintenance);
                BigDecimal revenue = decimal(row[5]);
                rows.add(Arrays.asList(stationName, number(available), number(rented), money(revenue),
                        "?action=admin-station-performance-detail&" + exactPeriodParams(period)
                        + "&stationId=" + encode(stationId)));
                stacks.add(new AdminChartItem(stationName, utilizationLabel(rented, total),
                        percent(available, total), percent(rented, total), percent(maintenance, total)));
                revenueShare.add(new AdminChartItem(stationName, moneyShort(revenue),
                        percent(revenue, totalRevenue)));
            }
            if (rows.isEmpty()) {
                rows.add(Arrays.asList("N/A", "0", "0", "0 VND"));
            }
            revenueShare.sort(Comparator.comparing(AdminChartItem::getPercent).reversed());
            return new AdminReportData(stats,
                    Arrays.asList("Station", "Available", "Rented", "Revenue"),
                    rows, stacks, revenueShare);
        });
    }

    public AdminReportData modelPerformance(AdminReportPeriod period) {
        return JPAUtil.execute(em -> {
            Object[] totals = reportDAO.modelTotals(em, period.getStart(), period.getEndExclusive());
            Object[] mostBooked = reportDAO.mostBookedModel(em, period.getStart(), period.getEndExclusive());
            int rentedVehicles = integer(totals[3]);
            int totalVehicles = Math.max(1, integer(totals[4]));
            List<AdminReportMetric> stats = Arrays.asList(
                    metric("Most Booked", text(mostBooked[0])),
                    metric("Model Groups", number(totals[0])),
                    metric("Incidents", number(totals[1])),
                    metric("Avg Utilization", percentLabel(rentedVehicles, totalVehicles)));

            List<Object[]> modelRows = reportDAO.modelRows(em, period.getStart(), period.getEndExclusive());
            modelRows.sort((a, b) -> Integer.compare(integer(b[2]), integer(a[2])));

            List<List<String>> rows = new ArrayList<>();
            List<AdminChartItem> bookingBars = new ArrayList<>();
            int maxBookings = 1;
            int totalBookings = 0;
            int totalIncidents = 0;
            for (Object[] row : modelRows) {
                maxBookings = Math.max(maxBookings, integer(row[2]));
                totalBookings += integer(row[2]);
                totalIncidents += integer(row[4]);
            }
            for (Object[] row : modelRows) {
                String modelId = text(row[0]);
                String modelName = text(row[1]);
                rows.add(Arrays.asList(modelName, number(row[2]), money(decimal(row[3])), number(row[4]),
                        "?action=admin-model-performance-detail&" + exactPeriodParams(period)
                        + "&modelId=" + encode(modelId)));
                bookingBars.add(new AdminChartItem(modelName, number(row[2]), percent(integer(row[2]), maxBookings)));
            }
            if (rows.isEmpty()) {
                rows.add(Arrays.asList("N/A", "0", "0 VND", "0"));
            }

            List<AdminChartItem> incidentRatio = Arrays.asList(
                    new AdminChartItem("Incidents", number(totalIncidents), percent(totalIncidents, Math.max(1, totalBookings))),
                    new AdminChartItem("Healthy rentals", number(Math.max(0, totalBookings - totalIncidents)),
                            100 - percent(totalIncidents, Math.max(1, totalBookings))));
            return new AdminReportData(stats,
                    Arrays.asList("Model", "Bookings", "Revenue", "Incidents"),
                    rows, bookingBars, incidentRatio);
        });
    }

    public AdminReportData financialDetail(AdminReportPeriod period, String paymentMethod, String paymentType, String status) {
        return JPAUtil.execute(em -> {
            List<List<String>> rows = new ArrayList<>();
            List<Object[]> detailRows = safe(paymentMethod).equals("N/A") || safe(status).equals("N/A")
                    ? reportDAO.financialDetailRowsByType(em, period.getStart(), period.getEndExclusive(), paymentType)
                    : reportDAO.financialDetailRows(em, period.getStart(), period.getEndExclusive(),
                            paymentMethod, paymentType, status);
            for (Object[] row : detailRows) {
                rows.add(Arrays.asList(shortId(row[0]), text(row[1]), text(row[2]) + " / " + text(row[3]),
                        text(row[4]), money(decimal(row[5])), text(row[6])));
            }
            if (rows.isEmpty()) {
                rows.add(Arrays.asList("N/A", "No payment records", "-", "-", "0 VND", "-"));
            }
            return new AdminReportData(
                    Arrays.asList(metric("Revenue Category", revenueCategoryLabel(paymentType)),
                            metric("Payment Type", safe(paymentType)),
                            metric("Period", periodLabel(period)), metric("Records", number(rows.size()))),
                    Arrays.asList("Payment ID", "Customer", "Method / Type", "Status", "Amount", "Payment Date"),
                    rows, null, null);
        });
    }

    public AdminReportData stationDetail(AdminReportPeriod period, String stationId) {
        return JPAUtil.execute(em -> {
            String stationName = text(reportDAO.stationInfo(em, stationId));
            List<List<String>> rows = new ArrayList<>();
            for (Object[] row : reportDAO.stationDetailRows(em, stationId, period.getStart(), period.getEndExclusive())) {
                rows.add(Arrays.asList(text(row[0]), text(row[1]), text(row[2]), number(row[3]),
                        number(row[4]), money(decimal(row[5]))));
            }
            if (rows.isEmpty()) {
                rows.add(Arrays.asList("N/A", "No vehicles", "-", "0", "0", "0 VND"));
            }
            return new AdminReportData(
                    Arrays.asList(metric("Station", stationName), metric("Vehicles", number(rows.size())),
                            metric("Period", periodLabel(period)), metric("Scope", "Fleet")),
                    Arrays.asList("License Plate", "Model", "Status", "Battery", "Bookings", "Revenue"),
                    rows, null, null);
        });
    }

    public AdminReportData modelDetail(AdminReportPeriod period, String modelId) {
        return JPAUtil.execute(em -> {
            String modelName = text(reportDAO.modelInfo(em, modelId));
            List<List<String>> rows = new ArrayList<>();
            for (Object[] row : reportDAO.modelDetailRows(em, modelId, period.getStart(), period.getEndExclusive())) {
                rows.add(Arrays.asList(text(row[0]), text(row[1]), text(row[2]), number(row[3]),
                        number(row[4]), money(decimal(row[5]))));
            }
            if (rows.isEmpty()) {
                rows.add(Arrays.asList("N/A", "No vehicles", "-", "0", "0", "0 VND"));
            }
            return new AdminReportData(
                    Arrays.asList(metric("Model", modelName), metric("Vehicles", number(rows.size())),
                            metric("Period", periodLabel(period)), metric("Scope", "Fleet")),
                    Arrays.asList("License Plate", "Station", "Status", "Battery", "Bookings", "Revenue"),
                    rows, null, null);
        });
    }

    private List<AdminChartItem> monthlyBars(List<Object[]> rows) {
        Map<Integer, BigDecimal> amountByMonth = new HashMap<>();
        BigDecimal max = BigDecimal.ONE;
        for (Object[] row : rows) {
            BigDecimal amount = decimal(row[1]);
            amountByMonth.put(integer(row[0]), amount);
            if (amount.compareTo(max) > 0) {
                max = amount;
            }
        }

        List<AdminChartItem> items = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            BigDecimal amount = amountByMonth.getOrDefault(i, BigDecimal.ZERO);
            items.add(new AdminChartItem(MONTH_LABELS[i - 1], moneyShort(amount), chartPercent(amount, max)));
        }
        return items;
    }

    private List<AdminChartItem> revenueBars(javax.persistence.EntityManager em, AdminReportPeriod period) {
        if ("custom".equals(period.getPeriod()) || "month".equals(period.getPeriod())) {
            return dailyBars(reportDAO.dailyRevenue(em, period.getStart(), period.getEndExclusive()),
                    toLocalDate(period.getStart()),
                    toLocalDate(period.getEndExclusive()).minusDays(1));
        }
        if ("quarter".equals(period.getPeriod())) {
            return quarterlyBars(reportDAO.quarterlyRevenue(em, period.getYear()));
        }
        return monthlyBars(reportDAO.monthlyRevenue(em, period.getYear()));
    }

    private List<AdminChartItem> dailyBars(List<Object[]> rows, LocalDate start, LocalDate end) {
        Map<LocalDate, BigDecimal> amountByDate = new HashMap<>();
        BigDecimal max = BigDecimal.ONE;
        for (Object[] row : rows) {
            LocalDate date = toLocalDate(row[0]);
            BigDecimal amount = decimal(row[1]);
            amountByDate.put(date, amount);
            if (amount.compareTo(max) > 0) {
                max = amount;
            }
        }

        List<AdminChartItem> items = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            BigDecimal amount = amountByDate.getOrDefault(current, BigDecimal.ZERO);
            items.add(new AdminChartItem(current.getDayOfMonth() + "/" + current.getMonthValue(),
                    moneyShort(amount), chartPercent(amount, max)));
            current = current.plusDays(1);
        }
        return items;
    }

    private List<AdminChartItem> quarterlyBars(List<Object[]> rows) {
        Map<Integer, BigDecimal> amountByQuarter = new HashMap<>();
        BigDecimal max = BigDecimal.ONE;
        for (Object[] row : rows) {
            BigDecimal amount = decimal(row[1]);
            amountByQuarter.put(integer(row[0]), amount);
            if (amount.compareTo(max) > 0) {
                max = amount;
            }
        }

        List<AdminChartItem> items = new ArrayList<>();
        for (int quarter = 1; quarter <= 4; quarter++) {
            BigDecimal amount = amountByQuarter.getOrDefault(quarter, BigDecimal.ZERO);
            items.add(new AdminChartItem("Q" + quarter, moneyShort(amount), chartPercent(amount, max)));
        }
        return items;
    }

    private List<AdminChartItem> paymentMix(List<Object[]> rows) {
        BigDecimal total = BigDecimal.ZERO;
        for (Object[] row : rows) {
            total = total.add(decimal(row[1]));
        }
        List<AdminChartItem> items = new ArrayList<>();
        for (Object[] row : rows) {
            BigDecimal amount = decimal(row[1]);
            items.add(new AdminChartItem(text(row[0]), moneyShort(amount), percent(amount, total)));
        }
        if (items.isEmpty()) {
            items.add(new AdminChartItem("No successful payments", "0 VND", 0));
        }
        return items;
    }

    private AdminReportMetric metric(String label, String value) {
        return new AdminReportMetric(label, value);
    }

    private String utilizationLabel(int rented, int total) {
        return percentLabel(rented, total);
    }

    private String percentLabel(int value, int total) {
        return percent(value, total) + "%";
    }

    private int percent(int value, int total) {
        if (total <= 0) {
            return 0;
        }
        return Math.max(0, Math.min(100, (int) Math.round(value * 100.0 / total)));
    }

    private int percent(BigDecimal value, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return value.multiply(BigDecimal.valueOf(100))
                .divide(total, 0, RoundingMode.HALF_UP)
                .min(BigDecimal.valueOf(100))
                .max(BigDecimal.ZERO)
                .intValue();
    }

    private int chartPercent(BigDecimal value, BigDecimal total) {
        int percent = percent(value, total);
        return value.compareTo(BigDecimal.ZERO) > 0 ? Math.max(10, percent) : 0;
    }

    private BigDecimal decimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }
        return new BigDecimal(String.valueOf(value));
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime().toLocalDate();
        }
        return LocalDate.parse(String.valueOf(value).substring(0, 10));
    }

    private int integer(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private String text(Object value) {
        return value == null ? "N/A" : String.valueOf(value);
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "N/A" : value.trim();
    }

    private String revenueCategoryLabel(String paymentType) {
        String type = safe(paymentType);
        if ("BOOKING".equals(type)) {
            return "Booking Revenue";
        }
        if ("LATE_FEE".equals(type)) {
            return "Late Return Fees";
        }
        if ("DAMAGE_FEE".equals(type)) {
            return "Damage Fees";
        }
        if ("CLEANING_FEE".equals(type)) {
            return "Cleaning Fees";
        }
        if ("LOST_ACCESSORY".equals(type)) {
            return "Lost Accessory Fees";
        }
        if ("OTHER".equals(type)) {
            return "Other Charges";
        }
        return type;
    }

    private String revenueCategoryDescription(String paymentType) {
        String type = safe(paymentType);
        if ("BOOKING".equals(type)) {
            return "Money paid for vehicle rental bookings.";
        }
        if ("LATE_FEE".equals(type)) {
            return "Extra fees when customers return vehicles late.";
        }
        if ("DAMAGE_FEE".equals(type)) {
            return "Compensation fees for vehicle damage incidents.";
        }
        if ("CLEANING_FEE".equals(type)) {
            return "Cleaning fees charged after vehicle return.";
        }
        if ("LOST_ACCESSORY".equals(type)) {
            return "Fees for missing accessories or equipment.";
        }
        if ("OTHER".equals(type)) {
            return "Other extra charges collected from rentals.";
        }
        return "Payment group recorded by the system.";
    }

    private String number(Object value) {
        if (value instanceof Number) {
            return numberFormat.format(((Number) value).longValue());
        }
        return String.valueOf(value);
    }

    private String number(int value) {
        return numberFormat.format(value);
    }

    private String money(BigDecimal value) {
        return numberFormat.format(value.setScale(0, RoundingMode.HALF_UP)) + " VND";
    }

    private String moneyShort(BigDecimal value) {
        return money(value);
    }

    private String shortId(Object value) {
        String id = text(value);
        return id.length() <= 8 ? id : id.substring(0, 8).toUpperCase();
    }

    private String exactPeriodParams(AdminReportPeriod period) {
        LocalDate start = toLocalDate(period.getStart());
        LocalDate end = toLocalDate(period.getEndExclusive()).minusDays(1);
        return "period=custom&startDate=" + start + "&endDate=" + end;
    }

    private String periodLabel(AdminReportPeriod period) {
        LocalDate start = toLocalDate(period.getStart());
        LocalDate end = toLocalDate(period.getEndExclusive()).minusDays(1);
        return start + " to " + end;
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (Exception ex) {
            return "";
        }
    }
}
