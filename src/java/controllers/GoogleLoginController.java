package controllers;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import services.GoogleAuthService;
import utils.RequestUrlUtil;

@WebServlet(name = "GoogleLoginController", urlPatterns = {"/google-login"})
public class GoogleLoginController extends HttpServlet {
    private final GoogleAuthService googleAuthService = new GoogleAuthService();
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            HttpSession session = request.getSession();
            String state = generateState();
            session.setAttribute("googleOAuthState", state);

            String redirectUri = RequestUrlUtil.buildUrl(request, "/google-callback");
            String authorizationUrl = googleAuthService.buildAuthorizationUrl(
                    getServletContext(), redirectUri, state);
            response.sendRedirect(authorizationUrl);
        } catch (RuntimeException ex) {
            request.getSession().setAttribute("loginError", ex.getMessage());
            response.sendRedirect(request.getContextPath() + "?action=login");
        }
    }

    private String generateState() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
