package controllers;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling home page
 * URL Pattern: /
 */
@WebServlet(name = "HomeServlet", urlPatterns = {""})
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // User is logged in, redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard.jsp");
            return;
        }
        
        // User is not logged in, redirect to login page
        response.sendRedirect(request.getContextPath() + "/login");
    }

    @Override
    public String getServletInfo() {
        return "Home Servlet for handling index page and redirects";
    }
}
