package filters;

import enums.Role;
import models.Account;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Filter to check user session
 * Redirects to login if session is not valid
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/dashboard/*", "/profile/*", "/admin/*", "/staff/*"})
public class AuthenticationFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        // Check if user is logged in
        if (session == null || session.getAttribute("user") == null) {
            LOGGER.log(Level.WARNING, "Unauthorized access attempt to: " + httpRequest.getRequestURI());
            httpResponse.sendRedirect(httpRequest.getContextPath() + "?action=login");
            return;
        }

        if (httpRequest.getRequestURI().startsWith(httpRequest.getContextPath() + "/staff/")) {
            Account user = (Account) session.getAttribute("user");
            if (user.getRole() != Role.STAFF) {
                LOGGER.log(Level.WARNING, "Forbidden staff access attempt by: " + user.getEmail());
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Bạn không có quyền truy cập khu vực dành cho nhân viên.");
                return;
            }
        }

        // User is authenticated, continue
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}

