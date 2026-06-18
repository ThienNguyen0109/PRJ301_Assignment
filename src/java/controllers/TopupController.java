package controllers;

import daos.WalletDAO;
import daos.IWalletDAO;
import models.Wallet;
import models.Account;
import services.VNPayService;
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
 * Servlet for handling wallet top-up form submission
 * URL Pattern: /topup
 */
@WebServlet(name = "TopupController", urlPatterns = {"/topup"})
public class TopupController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TopupController.class.getName());
    private IWalletDAO walletDAO = new WalletDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);
        String error = "";

        try {
            // Check if user is logged in
            if (session == null || session.getAttribute("user") == null) {
                error = "Vui lÃ²ng Ä‘Äƒng nháº­p trÆ°á»›c";
                request.setAttribute("error", error);
                response.sendRedirect(request.getContextPath() + "?action=login");
                return;
            }

            String amountStr = request.getParameter("amount");
            
            if (amountStr == null || amountStr.trim().isEmpty()) {
                error = "Vui lÃ²ng nháº­p sá»‘ tiá»n";
            } else {
                long amount = Long.parseLong(amountStr);
                
                // Validate amount
                if (!VNPayService.isValidAmount(amount)) {
                    error = "Sá»‘ tiá»n náº¡p pháº£i tá»« 10,000 Ä‘áº¿n 100,000,000 VND";
                } else {
                    // Get user and wallet info
                    Account user = (Account) session.getAttribute("user");
                    String accountId = user.getAccountId();
                    
                    // Get wallet
                    Wallet wallet = walletDAO.getWalletByAccountId(accountId);
                    if (wallet == null) {
                        error = "KhÃ´ng tÃ¬m tháº¥y vÃ­ cá»§a báº¡n";
                    } else {
                        // Generate order ID
                        String orderId = "TOPUP" + System.currentTimeMillis();
                        
                        // Create VNPay payment URL
                        String returnUrl = request.getScheme() + "://" + request.getServerName() 
                            + ":" + request.getServerPort() + request.getContextPath() + "/vnpay-callback";
                        
                        String paymentUrl = VNPayService.createPaymentUrl(
                            amount,
                            orderId,
                            "Nap tien vao vi - " + user.getEmail(),
                            returnUrl,
                            getClientIP(request)
                        );
                        
                        if (paymentUrl != null) {
                            // Save order ID and amount to session for callback verification
                            session.setAttribute("topupOrderId", orderId);
                            session.setAttribute("topupAmount", amount);
                            
                            LOGGER.log(Level.INFO, "VNPay payment URL created for user: " + user.getEmail());
                            
                            // Redirect to VNPay
                            response.sendRedirect(paymentUrl);
                            return;
                        } else {
                            error = "Lá»—i táº¡o URL thanh toÃ¡n. Vui lÃ²ng thá»­ láº¡i";
                        }
                    }
                }
            }
        } catch (NumberFormatException ex) {
            error = "Sá»‘ tiá»n khÃ´ng há»£p lá»‡";
            LOGGER.log(Level.WARNING, "Invalid amount format: " + ex.getMessage());
        } catch (Exception ex) {
            error = "CÃ³ lá»—i xáº£y ra. Vui lÃ²ng thá»­ láº¡i";
            LOGGER.log(Level.SEVERE, "Error during topup: " + ex.getMessage(), ex);
        }

        // If error, redirect back to wallet page
        request.setAttribute("error", error);
        request.setAttribute("amount", request.getParameter("amount"));
        RequestDispatcher dispatcher = request.getRequestDispatcher("wallet.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Get client IP address
     */
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

    @Override
    public String getServletInfo() {
        return "Top-up Servlet for wallet recharge";
    }
}

