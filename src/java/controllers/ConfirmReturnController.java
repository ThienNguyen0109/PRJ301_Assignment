package controllers;

import enums.IncidentSeverity;
import enums.Role;
import enums.VehicleCondition;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import services.ReturnService;

@WebServlet(name="ConfirmReturnController",urlPatterns={"/staff/return/confirm"})
public class ConfirmReturnController extends HttpServlet{
    private final ReturnService returnService=new ReturnService();
    @Override protected void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8");
        if(requireStaff(request,response)==null)return;
        String rentalId=trim(request.getParameter("rentalId")); HttpSession session=request.getSession();
        try{
            int battery=Integer.parseInt(trim(request.getParameter("batteryLevel")));
            VehicleCondition condition=VehicleCondition.valueOf(trim(request.getParameter("condition")));
            IncidentSeverity severity=condition==VehicleCondition.DAMAGED?IncidentSeverity.valueOf(trim(request.getParameter("severity"))):null;
            boolean damaged=returnService.confirmReturn(rentalId,battery,condition,request.getParameter("notes"),request.getParameter("damageDescription"),severity);
            session.setAttribute("returnSuccess",damaged?"Vehicle returned and moved to maintenance successfully.":"Vehicle returned successfully.");
        }catch(NumberFormatException ex){session.setAttribute("returnError","Battery Level phải là số từ 0 đến 100.");}
        catch(IllegalArgumentException|IllegalStateException ex){session.setAttribute("returnError",ex.getMessage());}
        catch(RuntimeException ex){session.setAttribute("returnError","Không thể xử lý trả xe. Transaction đã rollback.");}
        response.sendRedirect(request.getContextPath()+"?action=staff-return-detail&rentalId="+URLEncoder.encode(rentalId,"UTF-8"));
    }
    private Account requireStaff(HttpServletRequest request,HttpServletResponse response)throws IOException{HttpSession s=request.getSession(false);if(s==null||!(s.getAttribute("user") instanceof Account)){response.sendRedirect(request.getContextPath()+"?action=login");return null;}Account u=(Account)s.getAttribute("user");if(u.getRole()!=Role.STAFF){response.sendError(403);return null;}return u;}
    private String trim(String value){return value==null?"":value.trim();}
}
