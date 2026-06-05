package controllers;

import daos.WalletDAO;
import daos.IWalletDAO;
import daos.WalletTransactionDAO;
import models.Wallet;
import models.WalletTransaction;
import models.TransactionType;
import services.VNPayService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for handling VNPay callback
 * URL Pattern: /vnpay-callback
 */
@WebServlet(name = "VNPayCallbackServlet", urlPatterns = {"/vnpay-callback"})
public class VNPayCallbackServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(VNPayCallbackServlet.class.getName());
    private IWalletDAO walletDAO = new WalletDAO();
    private WalletTransactionDAO transactionDAO = new WalletTransactionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);
        
        try {
            // Get all VNPay response parameters
            Map<String, String> vnpParams = new TreeMap<>();
            Enumeration<String> paramNames = request.getParameterNames();
            String secureHash = "";
            
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (paramName.equals("vnp_SecureHash")) {
                    secureHash = request.getParameter(paramName);
                } else if (paramName.equals("vnp_SecureHashType")) {
                    continue;
                } else {
                    vnpParams.put(paramName, request.getParameter(paramName));
                }
            }

            // Verify secure hash
            if (!VNPayService.verifySecureHash(secureHash, vnpParams)) {
                LOGGER.log(Level.WARNING, "Invalid secure hash from VNPay");
                response.sendRedirect(request.getContextPath() + "?page=wallet&error=invalid_hash");
                return;
            }

            String responseCode = request.getParameter("vnp_ResponseCode");
            String orderId = request.getParameter("vnp_TxnRef");
            String transactionNo = request.getParameter("vnp_TransactionNo");
            String bankCode = request.getParameter("vnp_BankCode");

            // Get session data
            String topupOrderId = (String) session.getAttribute("topupOrderId");
            Long topupAmount = (Long) session.getAttribute("topupAmount");
            
            // Get user from session
            Object userObj = session.getAttribute("user");
            if (userObj == null) {
                LOGGER.log(Level.WARNING, "User not found in session for order: " + orderId);
                response.sendRedirect(request.getContextPath() + "?page=login");
                return;
            }

            // Verify order ID matches
            if (topupOrderId == null || !orderId.equals(topupOrderId)) {
                LOGGER.log(Level.WARNING, "Order ID mismatch: " + orderId + " vs " + topupOrderId);
                response.sendRedirect(request.getContextPath() + "?page=wallet&error=order_mismatch");
                return;
            }

            if ("00".equals(responseCode)) {
                // Payment successful
                models.Account user = (models.Account) userObj;
                Wallet wallet = walletDAO.getWalletByAccountId(user.getAccountId());
                if (wallet != null && topupAmount != null) {
                    double newBalance = wallet.getBalance() + topupAmount;
                    
                    // Update wallet balance
                    if (walletDAO.updateWalletBalance(wallet.getWalletId(), newBalance)) {
                        // Create transaction record
                        WalletTransaction transaction = new WalletTransaction(
                            wallet.getWalletId(),
                            (double) topupAmount,
                            TransactionType.TOPUP,
                            "Nạp tiền qua VNPay - Mã giao dịch: " + transactionNo
                        );
                        
                        if (transactionDAO.createTransaction(transaction)) {
                            session.setAttribute("topupSuccess", true);
                            session.setAttribute("topupSuccessAmount", topupAmount);
                            session.removeAttribute("topupOrderId");
                            session.removeAttribute("topupAmount");
                            
                            LOGGER.log(Level.INFO, "Topup successful - Order: " + orderId + ", Amount: " + topupAmount);
                            response.sendRedirect(request.getContextPath() + "?page=wallet&success=topup");
                            return;
                        }
                    }
                }
                
                // If balance update failed
                LOGGER.log(Level.SEVERE, "Failed to update wallet balance for order: " + orderId);
                response.sendRedirect(request.getContextPath() + "?page=wallet&error=update_failed");
                
            } else {
                // Payment failed
                String transactionStatus = request.getParameter("vnp_TransactionStatus");
                LOGGER.log(Level.WARNING, "Payment failed - Order: " + orderId + 
                          ", ResponseCode: " + responseCode + 
                          ", TransactionStatus: " + transactionStatus);
                
                session.removeAttribute("topupOrderId");
                session.removeAttribute("topupAmount");
                
                response.sendRedirect(request.getContextPath() + "?page=wallet&error=payment_failed");
            }

        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error processing VNPay callback: " + ex.getMessage(), ex);
            response.sendRedirect(request.getContextPath() + "?page=wallet&error=system_error");
        }
    }

    @Override
    public String getServletInfo() {
        return "VNPay Callback Servlet for handling payment responses";
    }
}
