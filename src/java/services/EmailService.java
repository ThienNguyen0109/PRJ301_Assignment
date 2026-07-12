package services;

import dto.BookingDetail;
import dto.BookingQuote;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import models.Account;

/**
 * Service for sending emails via Gmail SMTP.
 */
public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "thien.nmt1972004@gmail.com";
    private static final String SENDER_PASSWORD = "mefk ralp ymuo lcjr";
    private static final String TIMEOUT_MS = "30000";

    public static boolean sendEmail(String recipientEmail, String subject, String body) {
        try {
            Message message = createMessage(recipientEmail, subject, body);
            Transport.send(message);

            LOGGER.log(Level.INFO, "Email sent successfully to: " + recipientEmail);
            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error sending email to " + recipientEmail + ": " + ex.getMessage(), ex);
            return false;
        }
    }

    public static boolean sendOTPEmail(String email, String otp) {
        return sendEmail(email, "Xác minh email - E-Vehicle Rental", buildOTPEmailBody(otp));
    }

    public static boolean sendPasswordResetOTPEmail(String email, String otp) {
        return sendEmail(email, "Đặt lại mật khẩu - E-Vehicle Rental", buildPasswordResetEmailBody(otp));
    }

    public static boolean sendBookingConfirmationEmail(String email, Account customer, BookingDetail detail) {
        return sendEmail(email, "Xác nhận booking - E-Vehicle Rental",
                buildBookingConfirmationEmailBody(customer, detail));
    }

    private static Message createMessage(String recipientEmail, String subject, String body) throws Exception {
        Session session = Session.getInstance(getMailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(SENDER_EMAIL, "E-Vehicle Rental", "UTF-8"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject, "UTF-8");
        message.setContent(body, "text/html; charset=UTF-8");
        message.saveChanges();
        return message;
    }

    private static Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.trust", SMTP_HOST);
        properties.put("mail.smtp.connectiontimeout", TIMEOUT_MS);
        properties.put("mail.smtp.timeout", TIMEOUT_MS);
        properties.put("mail.smtp.writetimeout", TIMEOUT_MS);
        properties.put("mail.smtp.quitwait", "false");
        properties.put("mail.mime.charset", "UTF-8");
        return properties;
    }

    private static String buildBookingConfirmationEmailBody(Account customer, BookingDetail detail) {
        BookingQuote quote = detail.getQuote();
        DecimalFormat moneyFormat = new DecimalFormat("#,##0");
        String phone = customer.getPhone() != null && !customer.getPhone().trim().isEmpty()
                ? customer.getPhone()
                : "Chưa cập nhật";

        String rows =
                row("Mã booking", detail.getRentalId()) +
                row("Khách hàng", customer.getFullName()) +
                row("Email", customer.getEmail()) +
                row("Số điện thoại", phone) +
                row("Xe", quote.getVehicleModelName() + " - " + quote.getLicensePlate()) +
                row("Trạm nhận xe", quote.getStationName()) +
                row("Địa chỉ nhận xe", quote.getStationAddress()) +
                row("Thời gian thuê", quote.getStartDate() + " đến " + quote.getEndDate()) +
                row("Số ngày", String.valueOf(quote.getTotalDays())) +
                row("Phương thức thanh toán", detail.getPaymentMethod().getValue()) +
                totalRow("Tổng tiền", moneyFormat.format(quote.getFinalAmount()) + " VND");

        return buildShell(
                "E-Vehicle Rental",
                "Booking đã được xác nhận",
                "Cảm ơn bạn đã đặt xe. Thông tin đơn booking của bạn ở bên dưới.",
                rows,
                "Bạn vui lòng kiểm tra lại thông tin trước khi đến trạm nhận xe.");
    }

    private static String buildOTPEmailBody(String otp) {
        String content =
                "<p>Xin chào,</p>" +
                "<p>Cảm ơn bạn đã đăng ký tài khoản <strong>E-Vehicle Rental</strong>. Vui lòng nhập mã OTP dưới đây trên trang xác minh email.</p>" +
                otpBox("Mã xác minh", otp) +
                note("Mã OTP sẽ hết hạn sau 5 phút.") +
                "<p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email.</p>";

        return buildShell(
                "E-Vehicle Rental",
                "Xác minh email của bạn",
                "Hoàn tất đăng ký để bắt đầu sử dụng dịch vụ thuê xe điện.",
                content,
                "Không chia sẻ mã OTP này với bất kỳ ai.");
    }

    private static String buildPasswordResetEmailBody(String otp) {
        String content =
                "<p>Xin chào,</p>" +
                "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản <strong>E-Vehicle Rental</strong>.</p>" +
                otpBox("Mã đặt lại mật khẩu", otp) +
                note("Mã OTP sẽ hết hạn sau 5 phút.") +
                "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>";

        return buildShell(
                "E-Vehicle Rental",
                "Đặt lại mật khẩu",
                "Sử dụng mã OTP bên dưới để xác minh yêu cầu đổi mật khẩu.",
                content,
                "Email này được gửi tự động từ E-Vehicle Rental.");
    }

    private static String buildShell(String brand, String title, String subtitle, String content, String footerNote) {
        return "<!DOCTYPE html>" +
                "<html><head><meta charset=\"UTF-8\"><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">" +
                "<style>" +
                "body{margin:0;padding:0;background:#08111f;font-family:Arial,Helvetica,sans-serif;color:#172033}" +
                ".outer{width:100%;padding:34px 0;background:linear-gradient(135deg,#08111f 0%,#111a2c 48%,#f4f0e8 48%,#f8f6f2 100%)}" +
                ".box{width:92%;max-width:680px;margin:0 auto;border-radius:10px;overflow:hidden;border:1px solid rgba(218,183,99,.35);box-shadow:0 24px 70px rgba(8,17,31,.28)}" +
                ".header{background:#0f1c31;color:white;padding:28px 34px}.brand{font-size:24px;font-weight:800;margin:0}.title{font-size:30px;margin:24px 0 8px;line-height:1.2}" +
                ".subtitle{margin:0;color:rgba(255,255,255,.78);line-height:1.7;font-size:14px}.content{background:rgba(255,255,255,.96);padding:30px 34px}" +
                ".content p{margin:0 0 15px;line-height:1.7;color:#566070;font-size:15px}.row{border-bottom:1px solid rgba(17,24,39,.08);padding:12px 0;color:#566070}" +
                ".label{display:inline-block;width:190px;color:#566070}.value{color:#111827;font-weight:800}.total{font-size:20px;color:#b47a1f;font-weight:800}" +
                ".otp{margin:24px 0;padding:22px;text-align:center;border-radius:9px;background:linear-gradient(180deg,#fff 0%,#f7f4ee 100%);border:1px solid rgba(218,183,99,.48)}" +
                ".otp-label{margin:0 0 10px;color:#8f621b;font-size:13px;font-weight:800;text-transform:uppercase;letter-spacing:1px}.otp-code{color:#111827;font-size:38px;font-weight:800;letter-spacing:8px;font-family:'Courier New',monospace}" +
                ".note{margin:18px 0;padding:13px 15px;border-radius:8px;color:#09111f;background:#f8df9d;border:1px solid rgba(218,183,99,.55);text-align:center;font-size:14px}" +
                ".footer{background:#0f1c31;color:rgba(255,255,255,.66);padding:18px;text-align:center;font-size:12px}" +
                "</style></head><body><div class=\"outer\"><div class=\"box\">" +
                "<div class=\"header\"><p class=\"brand\">" + escapeHtml(brand) + "</p><h1 class=\"title\">" + escapeHtml(title) + "</h1><p class=\"subtitle\">" + escapeHtml(subtitle) + "</p></div>" +
                "<div class=\"content\">" + content + "</div>" +
                "<div class=\"footer\">&copy; 2026 E-Vehicle Rental System. " + escapeHtml(footerNote) + "</div>" +
                "</div></div></body></html>";
    }

    private static String row(String label, String value) {
        return "<div class=\"row\"><span class=\"label\">" + escapeHtml(label) + "</span><span class=\"value\">" + safe(value) + "</span></div>";
    }

    private static String totalRow(String label, String value) {
        return "<div class=\"row\"><span class=\"label\">" + escapeHtml(label) + "</span><span class=\"value total\">" + safe(value) + "</span></div>";
    }

    private static String otpBox(String label, String otp) {
        return "<div class=\"otp\"><div class=\"otp-label\">" + escapeHtml(label) + "</div><div class=\"otp-code\">" + escapeHtml(otp) + "</div></div>";
    }

    private static String note(String message) {
        return "<div class=\"note\"><strong>Lưu ý:</strong> " + escapeHtml(message) + "</div>";
    }

    private static String safe(String value) {
        return value == null ? "" : escapeHtml(value);
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
