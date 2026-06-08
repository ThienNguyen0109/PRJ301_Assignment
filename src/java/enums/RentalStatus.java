package enums;

/**
 * Enum for rental statuses
 */
public enum RentalStatus {
    BOOKED("BOOKED"),
    RENTED("RENTED"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    NO_SHOW("NO_SHOW");

    private final String value;

    RentalStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RentalStatus fromValue(String value) {
        if (value == null) {
            return BOOKED;
        }
        for (RentalStatus status : RentalStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return BOOKED;
    }

    @Override
    public String toString() {
        return value;
    }
}

