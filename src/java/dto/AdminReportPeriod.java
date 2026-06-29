package dto;

import java.sql.Timestamp;

public class AdminReportPeriod {
    private final String period;
    private final Timestamp start;
    private final Timestamp endExclusive;
    private final int year;

    public AdminReportPeriod(String period, Timestamp start, Timestamp endExclusive, int year) {
        this.period = period;
        this.start = start;
        this.endExclusive = endExclusive;
        this.year = year;
    }

    public String getPeriod() {
        return period;
    }

    public Timestamp getStart() {
        return start;
    }

    public Timestamp getEndExclusive() {
        return endExclusive;
    }

    public int getYear() {
        return year;
    }
}
