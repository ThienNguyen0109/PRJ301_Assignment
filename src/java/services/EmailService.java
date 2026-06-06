package services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
            Message message = createMessage(recipientEmail, subject, body);
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
        String body = buildOTPEmailBody(otp);

        try {
            Message message = createMessage(email, subject, body);
            Transport.send(message);

            LOGGER.log(Level.INFO, "OTP Email sent successfully to: " + email);
            return true;
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Error sending OTP email to " + email + ": " + ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * Send password reset OTP email
     * @param email Recipient email
     * @param otp OTP code
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendPasswordResetOTPEmail(String email, String otp) {
        String subject = "Đặt Lại Mật Khẩu - E-Vehicle Rental";
        String body = buildPasswordResetEmailBody(otp);

        try {
            Message message = createMessage(email, subject, body);
            Transport.send(message);

            LOGGER.log(Level.INFO, "Password reset OTP email sent successfully to: " + email);
            return true;
        } catch (MessagingException ex) {
            LOGGER.log(Level.SEVERE, "Error sending password reset OTP email to " + email + ": " + ex.getMessage(), ex);
            return false;
        }
    }

    private static Message createMessage(String recipientEmail, String subject, String body) throws MessagingException {
        Session session = Session.getInstance(getMailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("=?UTF-8?B?" + Base64.getEncoder().encodeToString(subject.getBytes(StandardCharsets.UTF_8)) + "?=");
        message.setContent(body, "text/html; charset=UTF-8");
        return message;
    }

    private static Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.connectiontimeout", "5000");
        properties.put("mail.smtp.timeout", "5000");
        return properties;
    }

    private static String buildOTPEmailBody(String otp) {
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "<meta charset=\"UTF-8\">\n" +
               "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "<style>\n" +
               "body { margin: 0; padding: 0; background: #08111f; font-family: Arial, Helvetica, sans-serif; color: #172033; }\n" +
               ".outer { width: 100%; padding: 34px 0; background: linear-gradient(135deg, #08111f 0%, #111a2c 48%, #f4f0e8 48%, #f8f6f2 100%); }\n" +
               ".container { width: 92%; max-width: 620px; margin: 0 auto; border-radius: 10px; overflow: hidden; border: 1px solid rgba(218,183,99,0.35); box-shadow: 0 24px 70px rgba(8,17,31,0.28); }\n" +
               ".header { background: linear-gradient(135deg, #0f1c31 0%, #2f3d5b 100%); color: white; padding: 30px 34px; }\n" +
               ".brand { margin: 0; font-size: 24px; font-weight: 800; }\n" +
               ".header-title { margin: 28px 0 8px; font-size: 30px; line-height: 1.2; font-weight: 800; }\n" +
               ".header-copy { margin: 0; color: rgba(255,255,255,0.78); line-height: 1.7; font-size: 14px; }\n" +
               ".content { background: rgba(255,255,255,0.96); padding: 30px 34px; }\n" +
               ".content p { margin: 0 0 15px; line-height: 1.7; color: #566070; font-size: 15px; }\n" +
               ".otp-box { margin: 24px 0; padding: 22px; text-align: center; border-radius: 9px; background: linear-gradient(180deg, #ffffff 0%, #f7f4ee 100%); border: 1px solid rgba(218,183,99,0.48); }\n" +
               ".otp-label { margin: 0 0 10px; color: #8f621b; font-size: 13px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; }\n" +
               ".otp-code { color: #111827; font-size: 38px; font-weight: 800; letter-spacing: 8px; font-family: 'Courier New', monospace; }\n" +
               ".timer { margin: 18px 0; padding: 13px 15px; border-radius: 8px; color: #09111f; background: #f8df9d; border: 1px solid rgba(218,183,99,0.55); text-align: center; font-size: 14px; }\n" +
               ".steps { margin: 16px 0 0; padding-left: 20px; color: #566070; line-height: 1.8; font-size: 14px; }\n" +
               ".security { margin-top: 22px; padding: 14px 16px; border-radius: 8px; background: #f8fafc; border: 1px solid rgba(17,24,39,0.08); color: #566070; font-size: 14px; line-height: 1.6; }\n" +
               ".footer { background: #0f1c31; padding: 18px 28px; text-align: center; font-size: 12px; color: rgba(255,255,255,0.66); }\n" +
               "</style>\n" +
               "</head>\n" +
               "<body>\n" +
               "<div class=\"outer\">\n" +
               "<div class=\"container\">\n" +
               "<div class=\"header\">\n" +
               "<p class=\"brand\">🚗 E-Vehicle Rental</p>\n" +
               "<h1 class=\"header-title\">Xác minh email của bạn</h1>\n" +
               "<p class=\"header-copy\">Hoàn tất đăng ký để bắt đầu quản lý ví và trải nghiệm dịch vụ thuê xe điện cao cấp.</p>\n" +
               "</div>\n" +
               "<div class=\"content\">\n" +
               "<p>Xin chào,</p>\n" +
               "<p>Cảm ơn bạn đã đăng ký tài khoản <strong>E-Vehicle Rental</strong>. Vui lòng nhập mã OTP dưới đây trên trang xác minh email.</p>\n" +
               "<div class=\"otp-box\">\n" +
               "<div class=\"otp-label\">Mã xác minh</div>\n" +
               "<div class=\"otp-code\">" + otp + "</div>\n" +
               "</div>\n" +
               "<div class=\"timer\"><strong>Lưu ý:</strong> Mã OTP sẽ hết hạn sau 5 phút.</div>\n" +
               "<p><strong>Hướng dẫn nhanh:</strong></p>\n" +
               "<ol class=\"steps\">\n" +
               "<li>Sao chép mã OTP phía trên.</li>\n" +
               "<li>Quay lại trang xác minh email.</li>\n" +
               "<li>Dán mã OTP và nhấn nút <strong>Xác Minh</strong>.</li>\n" +
               "</ol>\n" +
               "<div class=\"security\"><strong>Bảo mật:</strong> Không chia sẻ mã OTP này với bất kỳ ai. E-Vehicle Rental sẽ không bao giờ yêu cầu bạn cung cấp OTP qua email hoặc tin nhắn.</div>\n" +
               "</div>\n" +
               "<div class=\"footer\">© 2026 E-Vehicle Rental System. Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email.</div>\n" +
               "</div>\n" +
               "</div>\n" +
               "</body>\n" +
               "</html>";
    }

    private static String buildPasswordResetEmailBody(String otp) {
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "<meta charset=\"UTF-8\">\n" +
               "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "<style>\n" +
               "body { margin: 0; padding: 0; background: #08111f; font-family: Arial, Helvetica, sans-serif; color: #172033; }\n" +
               ".outer { width: 100%; padding: 34px 0; background: linear-gradient(135deg, #08111f 0%, #111a2c 48%, #f4f0e8 48%, #f8f6f2 100%); }\n" +
               ".container { width: 92%; max-width: 620px; margin: 0 auto; border-radius: 10px; overflow: hidden; border: 1px solid rgba(218,183,99,0.35); box-shadow: 0 24px 70px rgba(8,17,31,0.28); }\n" +
               ".header { background: linear-gradient(135deg, #0f1c31 0%, #2f3d5b 100%); color: white; padding: 30px 34px; }\n" +
               ".brand { margin: 0; font-size: 24px; font-weight: 800; }\n" +
               ".header-title { margin: 28px 0 8px; font-size: 30px; line-height: 1.2; font-weight: 800; }\n" +
               ".header-copy { margin: 0; color: rgba(255,255,255,0.78); line-height: 1.7; font-size: 14px; }\n" +
               ".content { background: rgba(255,255,255,0.96); padding: 30px 34px; }\n" +
               ".content p { margin: 0 0 15px; line-height: 1.7; color: #566070; font-size: 15px; }\n" +
               ".otp-box { margin: 24px 0; padding: 22px; text-align: center; border-radius: 9px; background: linear-gradient(180deg, #ffffff 0%, #f7f4ee 100%); border: 1px solid rgba(218,183,99,0.48); }\n" +
               ".otp-label { margin: 0 0 10px; color: #8f621b; font-size: 13px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; }\n" +
               ".otp-code { color: #111827; font-size: 38px; font-weight: 800; letter-spacing: 8px; font-family: 'Courier New', monospace; }\n" +
               ".timer { margin: 18px 0; padding: 13px 15px; border-radius: 8px; color: #09111f; background: #f8df9d; border: 1px solid rgba(218,183,99,0.55); text-align: center; font-size: 14px; }\n" +
               ".security { margin-top: 22px; padding: 14px 16px; border-radius: 8px; background: #f8fafc; border: 1px solid rgba(17,24,39,0.08); color: #566070; font-size: 14px; line-height: 1.6; }\n" +
               ".footer { background: #0f1c31; padding: 18px 28px; text-align: center; font-size: 12px; color: rgba(255,255,255,0.66); }\n" +
               "</style>\n" +
               "</head>\n" +
               "<body>\n" +
               "<div class=\"outer\">\n" +
               "<div class=\"container\">\n" +
               "<div class=\"header\">\n" +
               "<p class=\"brand\">🚗 E-Vehicle Rental</p>\n" +
               "<h1 class=\"header-title\">Đặt lại mật khẩu</h1>\n" +
               "<p class=\"header-copy\">Sử dụng mã OTP dưới đây để xác minh yêu cầu đổi mật khẩu của bạn.</p>\n" +
               "</div>\n" +
               "<div class=\"content\">\n" +
               "<p>Xin chào,</p>\n" +
               "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản <strong>E-Vehicle Rental</strong>.</p>\n" +
               "<div class=\"otp-box\">\n" +
               "<div class=\"otp-label\">Mã đặt lại mật khẩu</div>\n" +
               "<div class=\"otp-code\">" + otp + "</div>\n" +
               "</div>\n" +
               "<div class=\"timer\"><strong>Lưu ý:</strong> Mã OTP sẽ hết hạn sau 5 phút.</div>\n" +
               "<div class=\"security\"><strong>Bảo mật:</strong> Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này và không chia sẻ mã OTP với bất kỳ ai.</div>\n" +
               "</div>\n" +
               "<div class=\"footer\">© 2026 E-Vehicle Rental System. Email này được gửi tự động.</div>\n" +
               "</div>\n" +
               "</div>\n" +
               "</body>\n" +
               "</html>";
    }
}
