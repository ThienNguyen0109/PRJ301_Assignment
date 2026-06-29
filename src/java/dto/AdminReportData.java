package dto;

import java.util.Collections;
import java.util.List;

public class AdminReportData {
    private final List<AdminReportMetric> stats;
    private final List<String> columns;
    private final List<List<String>> rows;
    private final List<AdminChartItem> primaryChartItems;
    private final List<AdminChartItem> secondaryChartItems;

    public AdminReportData(List<AdminReportMetric> stats, List<String> columns, List<List<String>> rows,
            List<AdminChartItem> primaryChartItems, List<AdminChartItem> secondaryChartItems) {
        this.stats = stats == null ? Collections.emptyList() : stats;
        this.columns = columns == null ? Collections.emptyList() : columns;
        this.rows = rows == null ? Collections.emptyList() : rows;
        this.primaryChartItems = primaryChartItems == null ? Collections.emptyList() : primaryChartItems;
        this.secondaryChartItems = secondaryChartItems == null ? Collections.emptyList() : secondaryChartItems;
    }

    public List<AdminReportMetric> getStats() {
        return stats;
    }

    public List<String> getColumns() {
        return columns;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public List<AdminChartItem> getPrimaryChartItems() {
        return primaryChartItems;
    }

    public List<AdminChartItem> getSecondaryChartItems() {
        return secondaryChartItems;
    }
}
