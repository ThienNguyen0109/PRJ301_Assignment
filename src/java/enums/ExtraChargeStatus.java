package enums;

public enum ExtraChargeStatus {
    UNPAID("UNPAID"),
    PENDING("PENDING"),
    PAID("PAID"),
    CANCELLED("CANCELLED");

    private final String value;

    ExtraChargeStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
