package controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import seeders.DataSeeder;

/**
 * Runs database seed data when the web application starts.
 */
@WebServlet(name = "DataSeedController", urlPatterns = {"/__seed"}, loadOnStartup = 1)
public class DataSeedController extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        DataSeeder.seed();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
