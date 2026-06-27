package enums;

public enum ExtraChargeType {
    LATE_FEE("LATE_FEE"),
    DAMAGE_FEE("DAMAGE_FEE"),
    CLEANING_FEE("CLEANING_FEE"),
    LOST_ACCESSORY("LOST_ACCESSORY"),
    OTHER("OTHER");

    private final String value;

    ExtraChargeType(String value) {
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
