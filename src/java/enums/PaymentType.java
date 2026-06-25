package enums;

public enum PaymentType {
    BOOKING("BOOKING"),
    LATE_FEE("LATE_FEE");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentType fromValue(String value) {
        if (value == null) {
            return BOOKING;
        }
        for (PaymentType type : PaymentType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return BOOKING;
    }

    @Override
    public String toString() {
        return value;
    }
}
