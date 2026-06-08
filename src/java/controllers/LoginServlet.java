package controllers;

import daos.AccountDAO;
import daos.IAccountDAO;
import models.Account;
import enums.Role;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet for handling user login
 * URL Pattern: /login
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    private IAccountDAO accountDAO = new AccountDAO();

    /**
     * Display login page (GET request)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            Account loggedInUser = (Account) session.getAttribute("user");
            response.sendRedirect(request.getContextPath() + getRedirectPageByRole(loggedInUser));
            return;
        }
        
        response.sendRedirect(request.getContextPath() + "?page=login");
    }

    /**
     * Handle login submission (POST request)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set request encoding to UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String error = "";

        try {
            // Validate input
            if (email == null || email.trim().isEmpty()) {
                error = "Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng";
            } else if (password == null || password.trim().isEmpty()) {
                error = "Máº­t kháº©u khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng";
            } else {
                // Attempt to authenticate user
                Account account = accountDAO.getAccountByEmailAndPassword(email.trim(), password);

                if (account != null) {
                    // Check if account status is ACTIVE
                    if ("ACTIVE".equals(account.getStatus())) {
                        // Login successful - create session
                        HttpSession session = request.getSession();
                        session.setAttribute("user", account);
                        session.setAttribute("userId", account.getAccountId());
                        session.setAttribute("userEmail", account.getEmail());
                        session.setAttribute("userRole", account.getRole().getValue());
                        session.setAttribute("userName", account.getFullName());
                        
                        LOGGER.log(Level.INFO, "User logged in: " + email);
                        
                        response.sendRedirect(request.getContextPath() + getRedirectPageByRole(account));
                        return;
                    } else {
                        // Account is inactive
                        error = "TÃ i khoáº£n cá»§a báº¡n Ä‘Ã£ bá»‹ vÃ´ hiá»‡u hÃ³a";
                    }
                } else {
                    // Invalid credentials
                    error = "Email hoáº·c máº­t kháº©u khÃ´ng chÃ­nh xÃ¡c";
                    LOGGER.log(Level.WARNING, "Failed login attempt for email: " + email);
                }
            }
        } catch (Exception ex) {
            error = "CÃ³ lá»—i xáº£y ra trong quÃ¡ trÃ¬nh Ä‘Äƒng nháº­p";
            LOGGER.log(Level.SEVERE, "Error during login: " + ex.getMessage(), ex);
        }

        // Forward back to login page with error message
        request.setAttribute("error", error);
        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Login Servlet for handling user authentication";
    }

    private String getRedirectPageByRole(Account account) {
        if (account != null && account.getRole() == Role.ADMIN) {
            return "?page=dashboard";
        }
        return "?page=home";
    }
}

