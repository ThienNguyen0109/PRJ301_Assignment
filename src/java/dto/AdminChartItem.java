package dto;

public class AdminChartItem {
    private final String label;
    private final String value;
    private final int percent;
    private final int secondaryPercent;
    private final int tertiaryPercent;

    public AdminChartItem(String label, String value, int percent) {
        this(label, value, percent, 0, 0);
    }

    public AdminChartItem(String label, String value, int percent, int secondaryPercent, int tertiaryPercent) {
        this.label = label;
        this.value = value;
        this.percent = percent;
        this.secondaryPercent = secondaryPercent;
        this.tertiaryPercent = tertiaryPercent;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public int getPercent() {
        return percent;
    }

    public int getSecondaryPercent() {
        return secondaryPercent;
    }

    public int getTertiaryPercent() {
        return tertiaryPercent;
    }
}
