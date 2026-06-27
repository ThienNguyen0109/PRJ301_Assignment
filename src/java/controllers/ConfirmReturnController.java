package controllers;

import dto.ReturnConfirmationResult;
import enums.IncidentSeverity;
import enums.PaymentMethod;
import enums.Role;
import enums.VehicleCondition;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import services.ReturnService;
import services.VNPayService;

@WebServlet(name = "ConfirmReturnController", urlPatterns = {"/staff/return/confirm"})
public class ConfirmReturnController extends HttpServlet {
    private final ReturnService returnService = new ReturnService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (requireStaff(request, response) == null) {
            return;
        }

        String rentalId = trim(request.getParameter("rentalId"));
        HttpSession session = request.getSession();
        try {
            int battery = Integer.parseInt(trim(request.getParameter("batteryLevel")));
            VehicleCondition condition = VehicleCondition.valueOf(trim(request.getParameter("condition")));
            PaymentMethod extraChargePaymentMethod = PaymentMethod.fromValue(
                    trim(request.getParameter("extraChargePaymentMethod")));
            BigDecimal damageFee = parseMoney(request.getParameter("damageFee"));
            IncidentSeverity severity = condition == VehicleCondition.DAMAGED
                    ? IncidentSeverity.valueOf(trim(request.getParameter("severity")))
                    : null;

            ReturnConfirmationResult result = returnService.confirmReturn(rentalId, battery, condition,
                    extraChargePaymentMethod, damageFee, request.getParameter("notes"),
                    request.getParameter("damageDescription"), severity);

            if (result.isExtraChargeVNPayPending()) {
                session.setAttribute("chargeOrderId", result.getExtraChargeOrderId());
                String returnUrl = request.getScheme() + "://" + request.getServerName() + ":"
                        + request.getServerPort() + request.getContextPath() + "/vnpay-callback";
                String paymentUrl = VNPayService.createPaymentUrl(
                        result.getExtraChargePaymentAmount().longValue(),
                        result.getExtraChargeOrderId(),
                        "Thanh toan phu phi rental " + rentalId,
                        returnUrl,
                        request.getRemoteAddr());
                if (paymentUrl == null) {
                    session.setAttribute("returnError",
                            "Da tao phu phi nhung khong the tao URL VNPay. Vui long thanh toan lai sau.");
                    response.sendRedirect(request.getContextPath()
                            + "?action=staff-return-detail&rentalId=" + URLEncoder.encode(rentalId, "UTF-8"));
                    return;
                }
                response.sendRedirect(paymentUrl);
                return;
            }

            session.setAttribute("returnSuccess", result.isDamaged()
                    ? "Vehicle returned and moved to maintenance successfully."
                    : "Vehicle returned successfully.");
        } catch (NumberFormatException ex) {
            session.setAttribute("returnError", "Battery Level and damage fee must be valid numbers.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            session.setAttribute("returnError", ex.getMessage());
        } catch (RuntimeException ex) {
            session.setAttribute("returnError", "Cannot process return. Transaction has been rolled back.");
        }
        response.sendRedirect(request.getContextPath()
                + "?action=staff-return-detail&rentalId=" + URLEncoder.encode(rentalId, "UTF-8"));
    }

    private Account requireStaff(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof Account)) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return null;
        }
        Account user = (Account) session.getAttribute("user");
        if (user.getRole() != Role.STAFF) {
            response.sendError(403);
            return null;
        }
        return user;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal parseMoney(String value) {
        String normalized = trim(value).replace(",", "");
        return normalized.isEmpty() ? BigDecimal.ZERO : new BigDecimal(normalized);
    }
}
