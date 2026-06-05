package controllers;

import models.Account;
import models.Role;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling home page and main navigation routing
 * URL Pattern: / and /home
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"", "/home"})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        String page = request.getParameter("page");
        
        // Check if user is logged in
        boolean isLoggedIn = session != null && session.getAttribute("user") != null;

        // Determine which page to show
        if (page != null) {
            switch (page) {
                case "register":
                    RequestDispatcher registerDispatcher = request.getRequestDispatcher("register.jsp");
                    registerDispatcher.forward(request, response);
                    return;
                    
                case "verify-otp":
                    // Only allow access to verify-otp if OTP session exists
                    if (session != null && session.getAttribute("otp") != null) {
                        RequestDispatcher verifyDispatcher = request.getRequestDispatcher("verify-otp.jsp");
                        verifyDispatcher.forward(request, response);
                        return;
                    } else {
                        response.sendRedirect(request.getContextPath() + "?page=register");
                        return;
                    }
                    
                case "login":
                    // If already logged in, redirect to dashboard
                    if (isLoggedIn) {
                        Account user = (Account) session.getAttribute("user");
                        response.sendRedirect(request.getContextPath() + getRedirectPageByRole(user));
                        return;
                    }
                    RequestDispatcher loginDispatcher = request.getRequestDispatcher("login.jsp");
                    loginDispatcher.forward(request, response);
                    return;
                    
                case "home":
                    // Only allow access to home if logged in
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    RequestDispatcher homeDispatcher = request.getRequestDispatcher("home.jsp");
                    homeDispatcher.forward(request, response);
                    return;
                    
                case "wallet":
                    // Only allow access to wallet if logged in
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    RequestDispatcher walletDispatcher = request.getRequestDispatcher("wallet.jsp");
                    walletDispatcher.forward(request, response);
                    return;
                    
                case "dashboard":
                    // Only allow access to dashboard if logged in
                    if (!isLoggedIn) {
                        response.sendRedirect(request.getContextPath() + "?page=login");
                        return;
                    }
                    Account dashboardUser = (Account) session.getAttribute("user");
                    if (dashboardUser == null || dashboardUser.getRole() != Role.ADMIN) {
                        response.sendRedirect(request.getContextPath() + "?page=home");
                        return;
                    }
                    RequestDispatcher dashboardDispatcher = request.getRequestDispatcher("dashboard.jsp");
                    dashboardDispatcher.forward(request, response);
                    return;
                    
                default:
                    break;
            }
        }

        // Default routing
        if (isLoggedIn) {
            Account user = (Account) session.getAttribute("user");
            response.sendRedirect(request.getContextPath() + getRedirectPageByRole(user));
        } else {
            // User is not logged in, show login page
            response.sendRedirect(request.getContextPath() + "?page=login");
        }
    }

    @Override
    public String getServletInfo() {
        return "Home Servlet for main navigation routing";
    }

    private String getRedirectPageByRole(Account account) {
        if (account != null && account.getRole() == Role.ADMIN) {
            return "?page=dashboard";
        }
        return "?page=home";
    }
}

