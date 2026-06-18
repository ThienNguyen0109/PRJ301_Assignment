package controllers;

import controllers.RegisterController.RegistrationData;
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
import services.OTPService;
import services.RegistrationService;

/**
 * Controller for verifying OTP during registration.
 */
@WebServlet(name = "VerifyOTPController", urlPatterns = {"/verify-otp"})
public class VerifyOTPController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(VerifyOTPController.class.getName());

    private RegistrationService registrationService = new RegistrationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("otp") == null) {
            response.sendRedirect(request.getContextPath() + "?action=register");
            return;
        }

        forward(request, response);
    }

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
                error = "Phiên xác minh đã hết hạn. Vui lòng đăng ký lại.";
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
                            response.sendRedirect(request.getContextPath() + "?action=login");
                            return;
                        }

                        error = (String) result.get("message");
                    }
                }
            }
        } catch (Exception ex) {
            error = "Có lỗi xảy ra trong quá trình xác minh";
            LOGGER.log(Level.SEVERE, "Error during OTP verification: " + ex.getMessage(), ex);
        }

        request.setAttribute("error", error);
        forward(request, response);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("verify-otp.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "OTP Verification Controller for handling registration confirmation";
    }
}
