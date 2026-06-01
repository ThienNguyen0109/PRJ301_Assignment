package services;

import java.util.Random;
import java.util.logging.Logger;

/**
 * Service for OTP (One-Time Password) generation and validation
 */
public class OTPService {
    private static final Logger LOGGER = Logger.getLogger(OTPService.class.getName());
    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALIDITY_DURATION = 5 * 60 * 1000; // 5 minutes in milliseconds

    /**
     * Generate random OTP of 6 digits
     * @return OTP string
     */
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit number
        return String.valueOf(otp);
    }

    /**
     * Validate OTP with expiry time
     * @param storedOTP OTP stored in session
     * @param enteredOTP OTP entered by user
     * @param creationTime Time when OTP was created
     * @return true if OTP is valid and not expired, false otherwise
     */
    public static boolean validateOTP(String storedOTP, String enteredOTP, long creationTime) {
        if (storedOTP == null || enteredOTP == null) {
            return false;
        }

        // Check if OTP has expired
        long currentTime = System.currentTimeMillis();
        if (currentTime - creationTime > OTP_VALIDITY_DURATION) {
            LOGGER.log(java.util.logging.Level.WARNING, "OTP expired");
            return false;
        }

        // Check if entered OTP matches stored OTP
        return storedOTP.equals(enteredOTP);
    }
}
