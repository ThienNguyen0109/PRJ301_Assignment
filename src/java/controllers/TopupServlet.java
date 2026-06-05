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
@WebServlet(name = "TopupServlet", urlPatterns = {"/topup"})
public class TopupServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TopupServlet.class.getName());
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
                error = "Vui lòng đăng nhập trước";
                request.setAttribute("error", error);
                response.sendRedirect(request.getContextPath() + "?page=login");
                return;
            }

            String amountStr = request.getParameter("amount");
            
            if (amountStr == null || amountStr.trim().isEmpty()) {
                error = "Vui lòng nhập số tiền";
            } else {
                long amount = Long.parseLong(amountStr);
                
                // Validate amount
                if (!VNPayService.isValidAmount(amount)) {
                    error = "Số tiền nạp phải từ 10,000 đến 100,000,000 VND";
                } else {
                    // Get user and wallet info
                    Account user = (Account) session.getAttribute("user");
                    String accountId = user.getAccountId();
                    
                    // Get wallet
                    Wallet wallet = walletDAO.getWalletByAccountId(accountId);
                    if (wallet == null) {
                        error = "Không tìm thấy ví của bạn";
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
                            error = "Lỗi tạo URL thanh toán. Vui lòng thử lại";
                        }
                    }
                }
            }
        } catch (NumberFormatException ex) {
            error = "Số tiền không hợp lệ";
            LOGGER.log(Level.WARNING, "Invalid amount format: " + ex.getMessage());
        } catch (Exception ex) {
            error = "Có lỗi xảy ra. Vui lòng thử lại";
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
