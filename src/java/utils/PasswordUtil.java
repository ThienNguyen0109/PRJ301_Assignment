package utils;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private static final int BCRYPT_LOG_ROUNDS = 12;

    private PasswordUtil() {
    }

    public static String hashPassword(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Password must not be null.");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    public static boolean verifyPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null || storedPassword.trim().isEmpty()) {
            return false;
        }
        if (isBCryptHash(storedPassword)) {
            try {
                return BCrypt.checkpw(rawPassword, storedPassword);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }

        // Legacy fallback for accounts created before BCrypt migration.
        return rawPassword.equals(storedPassword);
    }

    public static boolean isBCryptHash(String value) {
        return value != null
                && (value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$"));
    }
}
