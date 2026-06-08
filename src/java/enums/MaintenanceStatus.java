package enums;

/**
 * Enum for vehicle maintenance statuses
 */
public enum MaintenanceStatus {
    PENDING("PENDING"),
    COMPLETED("COMPLETED");

    private final String value;

    MaintenanceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MaintenanceStatus fromValue(String value) {
        if (value == null) {
            return PENDING;
        }
        for (MaintenanceStatus status : MaintenanceStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return PENDING;
    }

    @Override
    public String toString() {
        return value;
    }
}

