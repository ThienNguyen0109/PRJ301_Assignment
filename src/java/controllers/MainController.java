package controllers;

import enums.Role;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;

@WebServlet(name = "MainController", urlPatterns = {"", "/main"})
public class MainController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String url = "/login.jsp";
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = request.getParameter("page");
        }

        if (action != null) {
            if (action.equals("login")) {
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute("user") != null) {
                    Account user = (Account) session.getAttribute("user");
                    response.sendRedirect(request.getContextPath() + getRedirectActionByRole(user));
                    return;
                }
                url = "/login.jsp";
            } else if (action.equals("logout")) {
                url = "/logout";
            } else if (action.equals("register")) {
                url = "/register.jsp";
            } else if (action.equals("verify-otp")) {
                url = "/page/verify-otp";
            } else if (action.equals("reset-password")) {
                url = "/page/reset-password";
            } else if (action.equals("home")) {
                url = "/home";
            } else if (action.equals("search")) {
                url = "/home";
            } else if (action.equals("vehicle-options")) {
                url = "/page/vehicle-options";
            } else if (action.equals("vehicle-detail")) {
                url = "/page/vehicle-detail";
            } else if (action.equals("check")) {
                url = "/page/vehicle-detail";
            } else if (action.equals("booking")) {
                url = "/page/booking";
            } else if (action.equals("booking-detail")) {
                url = "/page/booking-detail";
            } else if (action.equals("wallet")) {
                url = "/page/wallet";
            } else if (action.equals("profile")) {
                url = "/page/profile";
            } else if (action.equals("dashboard")) {
                url = "/admin/dashboard";
            } else if (action.equals("admin-dashboard")) {
                url = "/admin/dashboard";
            } else if (action.equals("admin-financial-reports")) {
                url = "/admin/financial-reports";
            } else if (action.equals("admin-station-performance")) {
                url = "/admin/station-performance";
            } else if (action.equals("admin-model-performance")) {
                url = "/admin/model-performance";
            } else if (action.equals("admin-accounts")) {
                url = "/admin/accounts";
            } else if (action.equals("admin-stations")) {
                url = "/admin/stations";
            } else if (action.equals("admin-categories")) {
                url = "/admin/categories";
            } else if (action.equals("admin-vehicle-models")) {
                url = "/admin/vehicle-models";
            } else if (action.equals("admin-vehicle-model-images")) {
                url = "/admin/vehicle-model-images";
            } else if (action.equals("admin-vehicles")) {
                url = "/admin/vehicles";
            } else if (action.equals("admin-discounts")) {
                url = "/admin/discounts";
            } else if (action.equals("admin-rental-discounts")) {
                url = "/admin/rental-discounts";
            } else if (action.equals("admin-rentals")) {
                url = "/admin/rentals";
            } else if (action.equals("admin-rental-status-history")) {
                url = "/admin/rental-status-history";
            } else if (action.equals("admin-payments")) {
                url = "/admin/payments";
            } else if (action.equals("admin-extra-charges")) {
                url = "/admin/extra-charges";
            } else if (action.equals("admin-incidents")) {
                url = "/admin/incidents";
            } else if (action.equals("admin-maintenance")) {
                url = "/admin/maintenance";
            } else if (action.equals("admin-wallets")) {
                url = "/admin/wallets";
            } else if (action.equals("admin-reviews")) {
                url = "/admin/reviews";
            } else if (action.equals("admin-profile")) {
                url = "/admin/profile";
            } else if (action.equals("staff-pickup")) {
                url = "/staff/pickup";
            } else if (action.equals("staff-dashboard")) {
                url = "/staff/dashboard";
            } else if (action.equals("staff-return")) {
                url = "/staff/return";
            } else if (action.equals("staff-return-detail")) {
                url = "/staff/return/detail";
            } else if (action.equals("staff-maintenance")) {
                url = "/staff/maintenance";
            } else if (action.equals("staff-incidents")) {
                url = "/staff/incidents";
            } else if (action.equals("staff-profile")) {
                url = "/staff/profile";
            }
        }

        RequestDispatcher rd = request.getRequestDispatcher(url);
        rd.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private String getRedirectActionByRole(Account account) {
        if (account != null && account.getRole() == Role.ADMIN) {
            return "?action=admin-dashboard";
        }
        if (account != null && account.getRole() == Role.STAFF) {
            return "?action=staff-dashboard";
        }
        return "?action=home";
    }
}
