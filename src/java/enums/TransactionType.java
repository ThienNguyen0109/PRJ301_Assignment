package enums;

/**
 * Enum for Wallet Transaction types
 */
public enum TransactionType {
    TOPUP("TOPUP"),
    PAYMENT("PAYMENT"),
    REFUND("REFUND");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TransactionType fromValue(String value) {
        if (value == null) {
            return TOPUP;
        }
        for (TransactionType type : TransactionType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return TOPUP;
    }

    @Override
    public String toString() {
        return value;
    }
}

