package controllers;

import daos.CategoryDAO;
import daos.ICategoryDAO;
import daos.IStationDAO;
import daos.IVehicleSearchDAO;
import daos.StationDAO;
import daos.VehicleSearchDAO;
import dto.VehicleSearchResult;
import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "HomeController", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {
    private static final int HOME_PAGE_SIZE = 9;

    private IStationDAO stationDAO = new StationDAO();
    private ICategoryDAO categoryDAO = new CategoryDAO();
    private IVehicleSearchDAO vehicleSearchDAO = new VehicleSearchDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        HttpSession session = request.getSession(false);
        boolean isLoggedIn = session != null && session.getAttribute("user") != null;
        if (!isLoggedIn) {
            response.sendRedirect(request.getContextPath() + "?action=login");
            return;
        }

        prepareHomePage(request);
        forward(request, response, "/home.jsp");
    }

    private void prepareHomePage(HttpServletRequest request) {
        request.setAttribute("stations", stationDAO.getAllStations());
        request.setAttribute("categories", categoryDAO.getAllCategories());

        String action = request.getParameter("action");
        String searchAction = request.getParameter("searchAction");
        if (!"search".equals(action) && !"search".equals(searchAction)) {
            paginate(request, "featuredVehicles", vehicleSearchDAO.getFeaturedAvailableVehicleModels(200));
            return;
        }

        String stationId = request.getParameter("stationId");
        String categoryId = request.getParameter("categoryId");
        if (isBlank(stationId) && isBlank(categoryId)) {
            paginate(request, "featuredVehicles", vehicleSearchDAO.getFeaturedAvailableVehicleModels(200));
            return;
        }

        request.setAttribute("selectedStationId", stationId);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("searchPerformed", true);
        paginate(request, "vehicleSearchResults", vehicleSearchDAO.searchAvailableVehicleModels(stationId, categoryId));
    }

    private void paginate(HttpServletRequest request, String attributeName, List<VehicleSearchResult> allItems) {
        int currentPage = parsePositiveInt(request.getParameter("listPage"), 1);
        int totalItems = allItems == null ? 0 : allItems.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) HOME_PAGE_SIZE));
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int fromIndex = Math.min((currentPage - 1) * HOME_PAGE_SIZE, totalItems);
        int toIndex = Math.min(fromIndex + HOME_PAGE_SIZE, totalItems);
        List<VehicleSearchResult> pageItems = totalItems == 0
                ? java.util.Collections.emptyList()
                : allItems.subList(fromIndex, toIndex);

        request.setAttribute(attributeName, pageItems);
        request.setAttribute("listCurrentPage", currentPage);
        request.setAttribute("listTotalPages", totalPages);
        request.setAttribute("listTotalItems", totalItems);
        request.setAttribute("listPageSize", HOME_PAGE_SIZE);
        request.setAttribute("listStartItem", totalItems == 0 ? 0 : fromIndex + 1);
        request.setAttribute("listEndItem", toIndex);
        request.setAttribute("hasPreviousPage", currentPage > 1);
        request.setAttribute("hasNextPage", currentPage < totalPages);
        request.setAttribute("previousPage", currentPage - 1);
        request.setAttribute("nextPage", currentPage + 1);
    }

    private int parsePositiveInt(String value, int defaultValue) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(jsp);
        dispatcher.forward(request, response);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
