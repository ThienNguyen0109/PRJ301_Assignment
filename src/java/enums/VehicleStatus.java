package enums;

/**
 * Enum for vehicle statuses
 */
public enum VehicleStatus {
    AVAILABLE("AVAILABLE"),
    RENTED("RENTED"),
    MAINTENANCE("MAINTENANCE");

    private final String value;

    VehicleStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static VehicleStatus fromValue(String value) {
        if (value == null) {
            return AVAILABLE;
        }
        for (VehicleStatus status : VehicleStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return AVAILABLE;
    }

    @Override
    public String toString() {
        return value;
    }
}

