package dto;

public class AdminReportMetric {
    private final String label;
    private final String value;

    public AdminReportMetric(String label, String value) {
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
