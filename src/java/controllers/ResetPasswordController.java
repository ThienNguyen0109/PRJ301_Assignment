package controllers;

import daos.AccountDAO;
import daos.IAccountDAO;
import models.Account;
import services.EmailService;
import services.OTPService;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for handling password reset form submissions
 * URL Pattern: /reset-password
 */
@WebServlet(name = "ResetPasswordController", urlPatterns = {"/reset-password"})
public class ResetPasswordController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ResetPasswordController.class.getName());
    private IAccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "?action=reset-password");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        try {
            if ("requestOtp".equals(action)) {
                handleRequestOtp(request, response, session);
                return;
            }

            if ("verifyOtp".equals(action)) {
                handleVerifyOtp(request, response, session);
                return;
            }

            if ("updatePassword".equals(action)) {
                handleUpdatePassword(request, response, session);
                return;
            }

            session.setAttribute("resetStep", "email");
            response.sendRedirect(request.getContextPath() + "?action=reset-password");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error during password reset: " + ex.getMessage(), ex);
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại.");
            forward(request, response);
        }
    }

    private void handleRequestOtp(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email không được để trống");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        Account account = accountDAO.getAccountByEmail(email.trim());
        if (account == null) {
            request.setAttribute("error", "Email không tồn tại trong hệ thống");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        if (!"ACTIVE".equals(account.getStatus())) {
            request.setAttribute("error", "Tài khoản này đang không hoạt động");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        String otp = OTPService.generateOTP();
        boolean emailSent = EmailService.sendPasswordResetOTPEmail(email.trim(), otp);
        if (!emailSent) {
            request.setAttribute("error", "Lỗi khi gửi mã OTP. Vui lòng thử lại.");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        session.setAttribute("resetEmail", email.trim());
        session.setAttribute("resetOtp", otp);
        session.setAttribute("resetOtpCreationTime", System.currentTimeMillis());
        session.setAttribute("resetVerified", false);
        session.setAttribute("resetStep", "otp");

        request.setAttribute("success", "Mã OTP đã được gửi tới email của bạn.");
        forward(request, response);
    }

    private void handleVerifyOtp(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        String enteredOtp = request.getParameter("otp");
        String storedOtp = (String) session.getAttribute("resetOtp");
        Long creationTime = (Long) session.getAttribute("resetOtpCreationTime");

        if (storedOtp == null || creationTime == null || session.getAttribute("resetEmail") == null) {
            request.setAttribute("error", "Phiên đặt lại mật khẩu đã hết hạn. Vui lòng nhập email lại.");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        if (enteredOtp == null || enteredOtp.trim().isEmpty()) {
            request.setAttribute("error", "Mã OTP không được để trống");
            session.setAttribute("resetStep", "otp");
            forward(request, response);
            return;
        }

        if (!OTPService.validateOTP(storedOtp, enteredOtp.trim(), creationTime)) {
            request.setAttribute("error", "Mã OTP không đúng hoặc đã hết hạn");
            session.setAttribute("resetStep", "otp");
            forward(request, response);
            return;
        }

        session.setAttribute("resetVerified", true);
        session.setAttribute("resetStep", "password");
        request.setAttribute("success", "Xác minh OTP thành công. Vui lòng nhập mật khẩu mới.");
        forward(request, response);
    }

    private void handleUpdatePassword(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        Boolean resetVerified = (Boolean) session.getAttribute("resetVerified");
        String email = (String) session.getAttribute("resetEmail");

        if (email == null || resetVerified == null || !resetVerified) {
            request.setAttribute("error", "Vui lòng xác minh OTP trước khi đổi mật khẩu.");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            request.setAttribute("error", "Mật khẩu mới không được để trống");
            session.setAttribute("resetStep", "password");
            forward(request, response);
            return;
        }

        if (newPassword.length() < 6) {
            request.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự");
            session.setAttribute("resetStep", "password");
            forward(request, response);
            return;
        }

        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Xác nhận mật khẩu không trùng khớp");
            session.setAttribute("resetStep", "password");
            forward(request, response);
            return;
        }

        if (!accountDAO.updatePasswordByEmail(email, newPassword)) {
            request.setAttribute("error", "Không thể cập nhật mật khẩu. Vui lòng thử lại.");
            session.setAttribute("resetStep", "password");
            forward(request, response);
            return;
        }

        clearResetSession(session);
        session.setAttribute("registrationSuccess", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
        response.sendRedirect(request.getContextPath() + "?action=login");
    }

    private void clearResetSession(HttpSession session) {
        session.removeAttribute("resetStep");
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetOtp");
        session.removeAttribute("resetOtpCreationTime");
        session.removeAttribute("resetVerified");
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("reset-password.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Reset Password Servlet for password recovery";
    }
}

