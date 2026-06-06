package models;

/**
 * Enum for vehicle model image types
 */
public enum VehicleModelImageType {
    FRONT("FRONT"),
    BACK("BACK"),
    INTERIOR("INTERIOR");

    private final String value;

    VehicleModelImageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static VehicleModelImageType fromValue(String value) {
        if (value == null) {
            return FRONT;
        }
        for (VehicleModelImageType type : VehicleModelImageType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return FRONT;
    }

    @Override
    public String toString() {
        return value;
    }
}
