package controllers;

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
 * Servlet for handling user logout
 * URL Pattern: /logout
 */
@WebServlet(name = "LogoutController", urlPatterns = {"/logout"})
public class LogoutController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(LogoutController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            String userEmail = (String) session.getAttribute("userEmail");
            LOGGER.log(Level.INFO, "User logged out: " + userEmail);
            
            // Invalidate the session
            session.invalidate();
        }
        
        // Redirect to login page through MainController routing
        response.sendRedirect(request.getContextPath() + "?action=login");
    }

    @Override
    public String getServletInfo() {
        return "Logout Servlet for handling user logout";
    }
}

