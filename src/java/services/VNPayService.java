package services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for VNPay payment gateway integration
 */
public class VNPayService {
    private static final Logger LOGGER = Logger.getLogger(VNPayService.class.getName());
    
    // VNPay Configuration - TODO: Move these values to environment/config before production
    private static final String VNP_SANDBOX_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String VNP_TMN_CODE = "DOBN8JJT"; // Replace with your TMN Code
    private static final String VNP_HASH_SECRET = "S99H6PLLLNHIIFUURTEE6W41OHU568OT"; // Replace with your Hash Secret
    
    /**
     * Generate VNPay payment URL
     * @param amount Amount in VND
     * @param orderId Order ID (unique)
     * @param orderInfo Order description
     * @param returnUrl Return URL after payment
     * @param ipAddress Client IP address
     * @return VNPay payment URL
     */
    public static String createPaymentUrl(long amount, String orderId, String orderInfo, 
                                          String returnUrl, String ipAddress) {
        try {
            Map<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Amount", String.valueOf(amount * 100)); // Amount in cents
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_CreateDate", getCurrentDateTime());
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_IpAddr", ipAddress);
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_OrderInfo", orderInfo);
            vnpParams.put("vnp_OrderType", "topup");
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_TmnCode", VNP_TMN_CODE);
            vnpParams.put("vnp_TxnRef", orderId);
            vnpParams.put("vnp_Version", "2.1.0");
            
            // VNPay signs the alphabetically sorted, URL-encoded parameter string.
            String queryString = buildHashData(vnpParams);
            String secureHash = hmacSHA512(VNP_HASH_SECRET, queryString);
            
            String paymentUrl = VNP_SANDBOX_URL + "?" + queryString + "&vnp_SecureHash=" + secureHash;
            
            LOGGER.log(Level.INFO, "VNPay payment URL created for order: " + orderId);
            return paymentUrl;
            
        } catch (UnsupportedEncodingException ex) {
            LOGGER.log(Level.SEVERE, "Error encoding VNPay URL: " + ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Get current datetime in VNPay format
     */
    private static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        return dateFormat.format(new Date());
    }
    
    /**
     * Build hash/query data string from parameters.
     */
    public static String buildHashData(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            if (!first) {
                sb.append("&");
            }
            sb.append(encode(entry.getKey())).append("=").append(encode(entry.getValue()));
            first = false;
        }
        return sb.toString();
    }

    private static String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    }
    
    /**
     * Calculate HMAC SHA512 secure hash
     */
    public static String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error calculating HMAC SHA512: " + ex.getMessage(), ex);
            return null;
        }
    }
    
    /**
     * Verify VNPay response secure hash
     */
    public static boolean verifySecureHash(String securehash, Map<String, String> params) {
        try {
            String calculatedHash = hmacSHA512(VNP_HASH_SECRET, buildHashData(new TreeMap<>(params)));
            return securehash != null && securehash.equalsIgnoreCase(calculatedHash);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.log(Level.SEVERE, "Error verifying VNPay hash: " + ex.getMessage(), ex);
            return false;
        }
    }
    
    /**
     * Validate transaction amount (in VND)
     */
    public static boolean isValidAmount(long amount) {
        // Minimum: 10,000 VND, Maximum: 100,000,000 VND
        return amount >= 10000 && amount <= 100000000;
    }
}
