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
@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ResetPasswordServlet.class.getName());
    private IAccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "?page=reset-password");
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
            response.sendRedirect(request.getContextPath() + "?page=reset-password");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error during password reset: " + ex.getMessage(), ex);
            request.setAttribute("error", "CÃ³ lá»—i xáº£y ra. Vui lÃ²ng thá»­ láº¡i.");
            forward(request, response);
        }
    }

    private void handleRequestOtp(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        Account account = accountDAO.getAccountByEmail(email.trim());
        if (account == null) {
            request.setAttribute("error", "Email khÃ´ng tá»“n táº¡i trong há»‡ thá»‘ng");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        if (!"ACTIVE".equals(account.getStatus())) {
            request.setAttribute("error", "TÃ i khoáº£n nÃ y Ä‘ang khÃ´ng hoáº¡t Ä‘á»™ng");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        String otp = OTPService.generateOTP();
        boolean emailSent = EmailService.sendPasswordResetOTPEmail(email.trim(), otp);
        if (!emailSent) {
            request.setAttribute("error", "Lá»—i khi gá»­i mÃ£ OTP. Vui lÃ²ng thá»­ láº¡i.");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        session.setAttribute("resetEmail", email.trim());
        session.setAttribute("resetOtp", otp);
        session.setAttribute("resetOtpCreationTime", System.currentTimeMillis());
        session.setAttribute("resetVerified", false);
        session.setAttribute("resetStep", "otp");

        request.setAttribute("success", "MÃ£ OTP Ä‘Ã£ Ä‘Æ°á»£c gá»­i tá»›i email cá»§a báº¡n.");
        forward(request, response);
    }

    private void handleVerifyOtp(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        String enteredOtp = request.getParameter("otp");
        String storedOtp = (String) session.getAttribute("resetOtp");
        Long creationTime = (Long) session.getAttribute("resetOtpCreationTime");

        if (storedOtp == null || creationTime == null || session.getAttribute("resetEmail") == null) {
            request.setAttribute("error", "PhiÃªn Ä‘áº·t láº¡i máº­t kháº©u Ä‘Ã£ háº¿t háº¡n. Vui lÃ²ng nháº­p email láº¡i.");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        if (enteredOtp == null || enteredOtp.trim().isEmpty()) {
            request.setAttribute("error", "MÃ£ OTP khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
            session.setAttribute("resetStep", "otp");
            forward(request, response);
            return;
        }

        if (!OTPService.validateOTP(storedOtp, enteredOtp.trim(), creationTime)) {
            request.setAttribute("error", "MÃ£ OTP khÃ´ng Ä‘Ãºng hoáº·c Ä‘Ã£ háº¿t háº¡n");
            session.setAttribute("resetStep", "otp");
            forward(request, response);
            return;
        }

        session.setAttribute("resetVerified", true);
        session.setAttribute("resetStep", "password");
        request.setAttribute("success", "XÃ¡c minh OTP thÃ nh cÃ´ng. Vui lÃ²ng nháº­p máº­t kháº©u má»›i.");
        forward(request, response);
    }

    private void handleUpdatePassword(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
        Boolean resetVerified = (Boolean) session.getAttribute("resetVerified");
        String email = (String) session.getAttribute("resetEmail");

        if (email == null || resetVerified == null || !resetVerified) {
            request.setAttribute("error", "Vui lÃ²ng xÃ¡c minh OTP trÆ°á»›c khi Ä‘á»•i máº­t kháº©u.");
            session.setAttribute("resetStep", "email");
            forward(request, response);
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (newPassword == null || newPassword.isEmpty()) {
            request.setAttribute("error", "Máº­t kháº©u má»›i khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng");
            session.setAttribute("resetStep", "password");
            forward(request, response);
            return;
        }

        if (newPassword.length() < 6) {
            request.setAttribute("error", "Máº­t kháº©u má»›i pháº£i cÃ³ Ã­t nháº¥t 6 kÃ½ tá»±");
            session.setAttribute("resetStep", "password");
            forward(request, response);
            return;
        }

        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "XÃ¡c nháº­n máº­t kháº©u khÃ´ng trÃ¹ng khá»›p");
            session.setAttribute("resetStep", "password");
            forward(request, response);
            return;
        }

        if (!accountDAO.updatePasswordByEmail(email, newPassword)) {
            request.setAttribute("error", "KhÃ´ng thá»ƒ cáº­p nháº­t máº­t kháº©u. Vui lÃ²ng thá»­ láº¡i.");
            session.setAttribute("resetStep", "password");
            forward(request, response);
            return;
        }

        clearResetSession(session);
        session.setAttribute("registrationSuccess", "Äáº·t láº¡i máº­t kháº©u thÃ nh cÃ´ng! Vui lÃ²ng Ä‘Äƒng nháº­p.");
        response.sendRedirect(request.getContextPath() + "?page=login");
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

