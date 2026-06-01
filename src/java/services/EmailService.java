package services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for sending emails via Gmail SMTP
 */
public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());
    
    // Gmail SMTP configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "thien.nmt1972004@gmail.com"; // TODO: Change to your Gmail
    private static final String SENDER_PASSWORD = "mefk ralp ymuo lcjr"; // TODO: Change to your App Password

    /**
     * Send email with OTP
     * @param recipientEmail Recipient email address
     * @param subject Email subject
     * @param body Email body
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendEmail(String recipientEmail, String subject, String body) {
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.starttls.required", "true");
            properties.put("mail.smtp.connectiontimeout", "5000");
            properties.put("mail.smtp.timeout", "5000");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            
            // Set subject with UTF-8 encoding
            message.setSubject("=?UTF-8?B?" + java.util.Base64.getEncoder().encodeToString(subject.getBytes(java.nio.charset.StandardCharsets.UTF_8)) + "?=");
            
            // Set content with UTF-8 encoding
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);

            LOGGER.log(Level.INFO, "Email sent successfully to: " + recipientEmail);
            return true;

        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Error sending email to " + recipientEmail + ": " + ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * Send OTP verification email
     * @param email Recipient email
     * @param otp OTP code
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendOTPEmail(String email, String otp) {
        String subject = "Xác Minh Email - E-Vehicle Rental";
        String body = "<!DOCTYPE html>\n" +
                      "<html>\n" +
                      "<head>\n" +
                      "<meta charset=\"UTF-8\">\n" +
                      "<style>\n" +
                      "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }\n" +
                      ".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; border-radius: 5px; }\n" +
                      ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }\n" +
                      ".header h1 { margin: 0; }\n" +
                      ".content { background: white; padding: 20px; }\n" +
                      ".otp-box { background-color: #f0f4ff; border-left: 4px solid #667eea; padding: 15px; margin: 20px 0; }\n" +
                      ".otp-code { font-size: 32px; font-weight: bold; color: #667eea; text-align: center; letter-spacing: 5px; font-family: monospace; }\n" +
                      ".timer { background-color: #fff3cd; color: #856404; padding: 10px; border-radius: 3px; margin: 15px 0; text-align: center; }\n" +
                      ".footer { background-color: #f5f5f5; padding: 15px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 5px 5px; }\n" +
                      "</style>\n" +
                      "</head>\n" +
                      "<body>\n" +
                      "<div class=\"container\">\n" +
                      "<div class=\"header\">\n" +
                      "<h1>🔐 Xác Minh Email</h1>\n" +
                      "</div>\n" +
                      "<div class=\"content\">\n" +
                      "<p>Xin chào,</p>\n" +
                      "<p>Cảm ơn bạn đã đăng ký tài khoản <strong>E-Vehicle Rental</strong>. Để hoàn tất quá trình đăng ký, vui lòng nhập mã OTP dưới đây:</p>\n" +
                      "<div class=\"otp-box\">\n" +
                      "<p style=\"margin: 0 0 10px 0;\">Mã OTP của bạn:</p>\n" +
                      "<div class=\"otp-code\">" + otp + "</div>\n" +
                      "</div>\n" +
                      "<div class=\"timer\">\n" +
                      "⏱️ <strong>Lưu ý:</strong> Mã OTP sẽ hết hạn sau 5 phút\n" +
                      "</div>\n" +
                      "<p><strong>Hướng dẫn:</strong></p>\n" +
                      "<ol>\n" +
                      "<li>Sao chép mã OTP phía trên</li>\n" +
                      "<li>Quay lại trang xác minh email</li>\n" +
                      "<li>Dán mã OTP vào ô nhập liệu</li>\n" +
                      "<li>Nhấn nút \"Xác Minh\" để hoàn tất</li>\n" +
                      "</ol>\n" +
                      "<p><strong>Lưu ý bảo mật:</strong> Không chia sẻ mã OTP này với bất kỳ ai. Chúng tôi sẽ không bao giờ yêu cầu bạn cung cấp mã OTP qua email hoặc tin nhắn.</p>\n" +
                      "</div>\n" +
                      "<div class=\"footer\">\n" +
                      "<p>© 2026 E-Vehicle Rental System. All rights reserved.</p>\n" +
                      "<p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>\n" +
                      "</div>\n" +
                      "</div>\n" +
                      "</body>\n" +
                      "</html>";

        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.starttls.required", "true");
            properties.put("mail.smtp.connectiontimeout", "5000");
            properties.put("mail.smtp.timeout", "5000");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("=?UTF-8?B?" + java.util.Base64.getEncoder().encodeToString(subject.getBytes(java.nio.charset.StandardCharsets.UTF_8)) + "?=");
            
            // Set HTML content with UTF-8 encoding
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);

            LOGGER.log(Level.INFO, "OTP Email sent successfully to: " + email);
            return true;

        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Error sending OTP email to " + email + ": " + ex.getMessage(), ex);
            return false;
        }
    }
}
