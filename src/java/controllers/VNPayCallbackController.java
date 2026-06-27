package controllers;

import daos.IWalletDAO;
import daos.WalletDAO;
import daos.WalletTransactionDAO;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import dto.BookingDetail;
import dto.BookingQuote;
import enums.TransactionType;
import models.Wallet;
import models.WalletTransaction;
import services.BookingService;
import services.EmailService;
import services.ReturnService;
import services.VNPayService;

/**
 * Servlet for handling VNPay callback.
 */
@WebServlet(name = "VNPayCallbackController", urlPatterns = {"/vnpay-callback"})
public class VNPayCallbackController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(VNPayCallbackController.class.getName());
    private IWalletDAO walletDAO = new WalletDAO();
    private WalletTransactionDAO transactionDAO = new WalletTransactionDAO();
    private BookingService bookingService = new BookingService();
    private ReturnService returnService = new ReturnService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);

        try {
            Map<String, String> vnpParams = new TreeMap<>();
            Enumeration<String> paramNames = request.getParameterNames();
            String secureHash = "";

            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if ("vnp_SecureHash".equals(paramName)) {
                    secureHash = request.getParameter(paramName);
                } else if (!"vnp_SecureHashType".equals(paramName)) {
                    vnpParams.put(paramName, request.getParameter(paramName));
                }
            }

            if (!VNPayService.verifySecureHash(secureHash, vnpParams)) {
                LOGGER.log(Level.WARNING, "Invalid secure hash from VNPay");
                response.sendRedirect(request.getContextPath() + "?action=wallet&error=invalid_hash");
                return;
            }

            if (session == null || session.getAttribute("user") == null) {
                response.sendRedirect(request.getContextPath() + "?action=login");
                return;
            }

            String responseCode = request.getParameter("vnp_ResponseCode");
            String orderId = request.getParameter("vnp_TxnRef");
            String transactionNo = request.getParameter("vnp_TransactionNo");

            if (orderId != null && orderId.startsWith("BOOK")) {
                handleBookingCallback(request, response, session, responseCode, orderId, transactionNo);
                return;
            }

            if (orderId != null && (orderId.startsWith("CHARGE_") || orderId.startsWith("VNPAY_LATE_"))) {
                handleLateFeeCallback(request, response, session, responseCode, orderId, transactionNo);
                return;
            }

            handleTopupCallback(request, response, session, responseCode, orderId, transactionNo);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing VNPay callback: " + ex.getMessage(), ex);
            response.sendRedirect(request.getContextPath() + "?action=wallet&error=system_error");
        }
    }

    private void handleLateFeeCallback(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            String responseCode, String orderId, String transactionNo) throws IOException {
        String sessionOrderId = (String) session.getAttribute("chargeOrderId");
        if (sessionOrderId == null) {
            sessionOrderId = (String) session.getAttribute("lateFeeOrderId");
        }
        if (sessionOrderId != null && !orderId.equals(sessionOrderId)) {
            response.sendRedirect(request.getContextPath() + "?action=staff-return&returnError=late_fee_order_mismatch");
            return;
        }

        if ("00".equals(responseCode)) {
            returnService.completeLateFeeVNPayPayment(orderId, transactionNo);
            session.setAttribute("returnSuccess", "Vehicle returned successfully. Extra charge has been paid via VNPay.");
        } else {
            returnService.failLateFeeVNPayPayment(orderId, transactionNo);
            session.setAttribute("returnError", "Vehicle returned, but extra charge VNPay payment failed.");
        }
        session.removeAttribute("chargeOrderId");
        session.removeAttribute("lateFeeOrderId");
        response.sendRedirect(request.getContextPath() + "?action=staff-return");
    }

    private void handleBookingCallback(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            String responseCode, String orderId, String transactionNo) throws IOException {
        String bookingOrderId = (String) session.getAttribute("bookingOrderId");
        BookingQuote quote = (BookingQuote) session.getAttribute("bookingQuote");
        Account customer = (Account) session.getAttribute("user");

        if (bookingOrderId == null || quote == null || !orderId.equals(bookingOrderId)) {
            response.sendRedirect(request.getContextPath() + "?action=home&bookingError=order_mismatch");
            return;
        }

        if ("00".equals(responseCode)) {
            try {
                BookingDetail detail = bookingService.completeVNPayBooking(customer, quote, orderId, transactionNo);
                EmailService.sendBookingConfirmationEmail(customer.getEmail(), customer, detail);
                session.setAttribute("bookingDetail", detail);
                clearBookingSession(session);
                response.sendRedirect(request.getContextPath() + "?action=booking-detail");
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Could not complete VNPay booking", ex);
                clearBookingSession(session);
                response.sendRedirect(request.getContextPath() + "?action=home&bookingError=booking_failed");
            }
            return;
        }

        bookingService.failPendingVNPayPayment(orderId, transactionNo);
        String retryUrl = buildBookingRetryUrl(request, quote, "Thanh toÃ¡n VNPay tháº¥t báº¡i. Vui lÃ²ng thá»­ láº¡i.");
        clearBookingSession(session);
        response.sendRedirect(retryUrl);
    }

    private void handleTopupCallback(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            String responseCode, String orderId, String transactionNo) throws IOException {
        String topupOrderId = (String) session.getAttribute("topupOrderId");
        Long topupAmount = (Long) session.getAttribute("topupAmount");
        Account user = (Account) session.getAttribute("user");

        if (topupOrderId == null || !orderId.equals(topupOrderId)) {
            LOGGER.log(Level.WARNING, "Topup order ID mismatch: " + orderId + " vs " + topupOrderId);
            response.sendRedirect(request.getContextPath() + "?action=wallet&error=order_mismatch");
            return;
        }

        if ("00".equals(responseCode)) {
            Wallet wallet = walletDAO.getWalletByAccountId(user.getAccountId());
            if (wallet != null && topupAmount != null) {
                double newBalance = wallet.getBalance() + topupAmount;
                if (walletDAO.updateWalletBalance(wallet.getWalletId(), newBalance)) {
                    WalletTransaction transaction = new WalletTransaction(
                            wallet.getWalletId(),
                            (double) topupAmount,
                            TransactionType.TOPUP,
                            "Náº¡p tiá»n qua VNPay - MÃ£ giao dá»‹ch: " + transactionNo);

                    if (transactionDAO.createTransaction(transaction)) {
                        session.setAttribute("topupSuccess", true);
                        session.setAttribute("topupSuccessAmount", topupAmount);
                        session.removeAttribute("topupOrderId");
                        session.removeAttribute("topupAmount");
                        response.sendRedirect(request.getContextPath() + "?action=wallet&success=topup");
                        return;
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "?action=wallet&error=update_failed");
            return;
        }

        session.removeAttribute("topupOrderId");
        session.removeAttribute("topupAmount");
        response.sendRedirect(request.getContextPath() + "?action=wallet&error=payment_failed");
    }

    private void clearBookingSession(HttpSession session) {
        session.removeAttribute("bookingOrderId");
        session.removeAttribute("bookingQuote");
    }

    private String buildBookingRetryUrl(HttpServletRequest request, BookingQuote quote, String error) throws IOException {
        return request.getContextPath() + "?action=booking"
                + "&vehicleId=" + encode(quote.getVehicleId())
                + "&stationId=" + encode(quote.getStationId())
                + "&startDate=" + encode(String.valueOf(quote.getStartDate()))
                + "&endDate=" + encode(String.valueOf(quote.getEndDate()))
                + "&discountCode=" + encode(quote.getDiscountCode())
                + "&error=" + encode(error);
    }

    private String encode(String value) throws IOException {
        return java.net.URLEncoder.encode(value == null ? "" : value, "UTF-8");
    }
}

