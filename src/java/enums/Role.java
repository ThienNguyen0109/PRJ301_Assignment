package enums;

/**
 * Enum for Account roles
 */
public enum Role {
    CUSTOMER("CUSTOMER"),
    STAFF("STAFF"),
    ADMIN("ADMIN");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Get Role from String value
     */
    public static Role fromValue(String value) {
        if (value == null) {
            return CUSTOMER; // Default role
        }
        for (Role role : Role.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        return CUSTOMER; // Default if not found
    }

    @Override
    public String toString() {
        return value;
    }
}

