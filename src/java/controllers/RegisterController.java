package controllers;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import services.EmailService;
import services.OTPService;
import services.RegistrationService;

/**
 * Controller for handling user registration.
 */
@WebServlet(name = "RegisterController", urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());

    private RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        response.sendRedirect(request.getContextPath() + "?action=register");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String phone = request.getParameter("phone");
        String error = "";

        try {
            if (isBlank(fullName)) {
                error = "Họ và tên không được để trống";
            } else if (isBlank(email)) {
                error = "Email không được để trống";
            } else if (password == null || password.isEmpty()) {
                error = "Mật khẩu không được để trống";
            } else if (confirmPassword == null || confirmPassword.isEmpty()) {
                error = "Xác nhận mật khẩu không được để trống";
            } else if (!password.equals(confirmPassword)) {
                error = "Mật khẩu không trùng khớp";
            } else {
                Map<String, Object> validationResult = registrationService.validateRegistrationData(
                        fullName, email, password, phone);

                if (!(Boolean) validationResult.get("valid")) {
                    error = (String) validationResult.get("message");
                } else {
                    String otp = OTPService.generateOTP();
                    boolean emailSent = EmailService.sendOTPEmail(email.trim(), otp);

                    if (!emailSent) {
                        error = "Lỗi khi gửi mã OTP. Vui lòng thử lại.";
                    } else {
                        HttpSession session = request.getSession();
                        session.setAttribute("otp", otp);
                        session.setAttribute("otpCreationTime", System.currentTimeMillis());
                        session.setAttribute("registrationData",
                                new RegistrationData(fullName, email, password, phone));

                        LOGGER.log(Level.INFO, "OTP sent to email: " + email);
                        response.sendRedirect(request.getContextPath() + "?action=verify-otp");
                        return;
                    }
                }
            }
        } catch (Exception ex) {
            error = "Có lỗi xảy ra trong quá trình đăng ký";
            LOGGER.log(Level.SEVERE, "Error during registration: " + ex.getMessage(), ex);
        }

        request.setAttribute("error", error);
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);
        request.setAttribute("phone", phone);

        RequestDispatcher dispatcher = request.getRequestDispatcher("register.jsp");
        dispatcher.forward(request, response);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String getServletInfo() {
        return "Registration Controller for handling user registration";
    }

    public static class RegistrationData {
        public String fullName;
        public String email;
        public String password;
        public String phone;

        public RegistrationData(String fullName, String email, String password, String phone) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.phone = phone;
        }
    }
}
