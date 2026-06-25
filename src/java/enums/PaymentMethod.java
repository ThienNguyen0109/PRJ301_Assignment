package enums;

/**
 * Enum for payment methods
 */
public enum PaymentMethod {
    WALLET("WALLET"),
    VNPAY("VNPAY"),
    CASH("CASH");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentMethod fromValue(String value) {
        if (value == null) {
            return WALLET;
        }
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.value.equals(value)) {
                return method;
            }
        }
        return WALLET;
    }

    @Override
    public String toString() {
        return value;
    }
}

