package enums;

/**
 * Enum for incident severity levels
 */
public enum IncidentSeverity {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH");

    private final String value;

    IncidentSeverity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static IncidentSeverity fromValue(String value) {
        if (value == null) {
            return LOW;
        }
        for (IncidentSeverity severity : IncidentSeverity.values()) {
            if (severity.value.equals(value)) {
                return severity;
            }
        }
        return LOW;
    }

    @Override
    public String toString() {
        return value;
    }
}

