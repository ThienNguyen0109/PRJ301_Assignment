package controllers;

import dto.ReturnRentalDTO;
import enums.Role;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import models.Account;
import services.ReturnService;

@WebServlet(name = "ReturnDetailController", urlPatterns = {"/staff/return/detail"})
public class ReturnDetailController extends HttpServlet {
    private final ReturnService returnService = new ReturnService();
    @Override protected void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException{
        request.setCharacterEncoding("UTF-8"); response.setCharacterEncoding("UTF-8");
        if(requireStaff(request,response)==null)return;
        request.setAttribute("activeModule","return"); request.setAttribute("staffPageTitle","Return Detail");
        moveFlash(request,"returnSuccess"); moveFlash(request,"returnError");
        String rentalId=request.getParameter("rentalId");
        try { ReturnRentalDTO detail=returnService.findRentalDetail(rentalId); if(detail==null)request.setAttribute("returnError","Không tìm thấy rental."); else request.setAttribute("returnRental",detail); }
        catch(RuntimeException ex){request.setAttribute("returnError","Không thể tải chi tiết rental.");}
        request.getRequestDispatcher("/WEB-INF/views/staff/return-detail.jsp").forward(request,response);
    }
    private void moveFlash(HttpServletRequest request,String name){HttpSession s=request.getSession();Object v=s.getAttribute(name);if(v!=null){request.setAttribute(name,v);s.removeAttribute(name);}}
    private Account requireStaff(HttpServletRequest request,HttpServletResponse response)throws IOException{HttpSession s=request.getSession(false);if(s==null||!(s.getAttribute("user") instanceof Account)){response.sendRedirect(request.getContextPath()+"?action=login");return null;}Account u=(Account)s.getAttribute("user");if(u.getRole()!=Role.STAFF){response.sendError(403);return null;}return u;}
}
