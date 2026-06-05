package controllers;

import services.OTPService;
import services.RegistrationService;
import controllers.RegisterServlet.RegistrationData;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for verifying OTP during registration
 * URL Pattern: /verify-otp
 */
@WebServlet(name = "VerifyOTPServlet", urlPatterns = {"/verify-otp"})
public class VerifyOTPServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(VerifyOTPServlet.class.getName());
    private RegistrationService registrationService = new RegistrationService();

    /**
     * Display OTP verification page (GET request)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("otp") == null) {
            response.sendRedirect(request.getContextPath() + "/register");
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("verify-otp.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Handle OTP verification (POST request)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String enteredOTP = request.getParameter("otp");
        String error = "";

        try {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("otp") == null) {
                error = "Session hết hạn. Vui lòng đăng ký lại.";
            } else if (enteredOTP == null || enteredOTP.trim().isEmpty()) {
                error = "Mã OTP không được để trống";
            } else {
                String storedOTP = (String) session.getAttribute("otp");
                Long otpCreationTime = (Long) session.getAttribute("otpCreationTime");

                if (!OTPService.validateOTP(storedOTP, enteredOTP.trim(), otpCreationTime)) {
                    error = "Mã OTP không đúng hoặc đã hết hạn";
                } else {
                    RegistrationData regData = (RegistrationData) session.getAttribute("registrationData");

                    if (regData == null) {
                        error = "Dữ liệu đăng ký bị mất. Vui lòng đăng ký lại.";
                    } else {
                        Map<String, Object> result = registrationService.registerAccount(
                            regData.fullName, regData.email, regData.password, regData.phone);

                        if ((Boolean) result.get("success")) {
                            session.removeAttribute("otp");
                            session.removeAttribute("otpCreationTime");
                            session.removeAttribute("registrationData");

                            LOGGER.log(Level.INFO, "User registered successfully: " + regData.email);

                            request.getSession().setAttribute("registrationSuccess",
                                "Đăng ký thành công! Vui lòng đăng nhập.");
                            response.sendRedirect(request.getContextPath() + "?page=login");
                            return;
                        } else {
                            error = (String) result.get("message");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            error = "Có lỗi xảy ra trong quá trình xác minh";
            LOGGER.log(Level.SEVERE, "Error during OTP verification: " + ex.getMessage(), ex);
        }

        request.setAttribute("error", error);
        RequestDispatcher dispatcher = request.getRequestDispatcher("verify-otp.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "OTP Verification Servlet for handling registration confirmation";
    }
}
