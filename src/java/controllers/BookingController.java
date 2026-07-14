package controllers;

import daos.AccountDAO;
import dto.BookingDetail;
import dto.BookingQuote;
import enums.PaymentMethod;
import java.io.IOException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import services.BookingService;
import services.EmailService;
import services.VNPayService;
import utils.RequestUrlUtil;

/**
 * Handles booking payment submissions.
 */
@WebServlet(name = "BookingController", urlPatterns = {"/booking"})
public class BookingController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(BookingController.class.getName());
    private BookingService bookingService = new BookingService();
    private AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return;
        }

        Account customer = (Account) session.getAttribute("user");
        customer = refreshCustomerSession(session, customer);
        if (isBlank(customer.getPhone())) {
            session.setAttribute("bookingPhoneRequired", true);
            response.sendRedirect(buildBookingRedirect(request, request.getParameter("vehicleId"),
                    request.getParameter("stationId"),
                    request.getParameter("startDate"),
                    request.getParameter("endDate"),
                    request.getParameter("discountCode")));
            return;
        }

        String vehicleId = request.getParameter("vehicleId");
        String stationId = request.getParameter("stationId");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String discountCode = request.getParameter("discountCode");
        String paymentMethodValue = request.getParameter("paymentMethod");

        try {
            BookingQuote quote = bookingService.createQuote(
                    customer.getAccountId(),
                    vehicleId,
                    Date.valueOf(startDate),
                    Date.valueOf(endDate),
                    discountCode);

            PaymentMethod paymentMethod = PaymentMethod.fromValue(paymentMethodValue);
            if (paymentMethod == PaymentMethod.WALLET) {
                BookingDetail detail = bookingService.payByWallet(customer, quote);
                sendBookingEmailSafely(customer, detail);
                session.setAttribute("bookingDetail", detail);
                response.sendRedirect(request.getContextPath() + "?action=booking-detail");
                return;
            }

            String orderId = "BOOK" + System.currentTimeMillis();
            String returnUrl = RequestUrlUtil.buildUrl(request, "/vnpay-callback");
            String paymentUrl = VNPayService.createPaymentUrl(
                    Math.round(quote.getFinalAmount()),
                    orderId,
                    "Thanh toan booking - " + customer.getEmail(),
                    returnUrl,
                    getClientIP(request));

            if (paymentUrl == null) {
                throw new IllegalStateException("Không thể tạo URL thanh toán VNPay.");
            }

            bookingService.createPendingVNPayPayment(quote, orderId);
            session.setAttribute("bookingOrderId", orderId);
            session.setAttribute("bookingQuote", quote);
            response.sendRedirect(paymentUrl);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Booking payment failed", ex);
            session.setAttribute("bookingError", getUserMessage(ex));
            String redirect = request.getContextPath() + "?action=booking"
                    + "&vehicleId=" + encode(vehicleId)
                    + "&stationId=" + encode(stationId)
                    + "&startDate=" + encode(startDate)
                    + "&endDate=" + encode(endDate)
                    + "&discountCode=" + encode(discountCode);
            response.sendRedirect(redirect);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String encode(String value) throws IOException {
        return java.net.URLEncoder.encode(value == null ? "" : value, "UTF-8");
    }

    private String buildBookingRedirect(HttpServletRequest request, String vehicleId, String stationId,
            String startDate, String endDate, String discountCode) throws IOException {
        return request.getContextPath() + "?action=booking"
                + "&vehicleId=" + encode(vehicleId)
                + "&stationId=" + encode(stationId)
                + "&startDate=" + encode(startDate)
                + "&endDate=" + encode(endDate)
                + "&discountCode=" + encode(discountCode);
    }

    private String getUserMessage(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Không thể xử lý thanh toán. Vui lòng thử lại.";
        }
        return message;
    }

    private Account refreshCustomerSession(HttpSession session, Account customer) {
        if (customer == null || isBlank(customer.getEmail())) {
            return customer;
        }
        Account fresh = accountDAO.getAccountByEmail(customer.getEmail());
        if (fresh != null) {
            session.setAttribute("user", fresh);
            session.setAttribute("userName", fresh.getFullName());
            session.setAttribute("userEmail", fresh.getEmail());
            session.setAttribute("userRole", fresh.getRole().getValue());
            return fresh;
        }
        return customer;
    }

    private void sendBookingEmailSafely(Account customer, BookingDetail detail) {
        try {
            boolean sent = EmailService.sendBookingConfirmationEmail(customer.getEmail(), customer, detail);
            if (!sent) {
                LOGGER.log(Level.WARNING, "Booking was created but confirmation email was not sent.");
            }
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Booking was created but confirmation email failed.", ex);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}


