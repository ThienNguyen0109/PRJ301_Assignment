package controllers;

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
                EmailService.sendBookingConfirmationEmail(customer.getEmail(), customer, detail);
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

    private String getUserMessage(Exception ex) {
        String message = ex.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Không thể xử lý thanh toán. Vui lòng thử lại.";
        }
        return message;
    }
}
