/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import dao.IngredientDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Ingredient;

/**
 *
 * @author DELL
 */
@WebServlet(urlPatterns = {"/ingredients/list"})
public class ingredientlist extends HttpServlet {

    private static final int PAGE_SIZE = 15;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        IngredientDAO ingredientDAO = new IngredientDAO();
        
        String searchTerm = request.getParameter("search");
        Integer categoryId = null;
        try {
            categoryId = Integer.parseInt(request.getParameter("category"));
        } catch (NumberFormatException e) {
            // Bỏ qua nếu category không phải là số
        }

        int currentPage = 1;
        try {
            currentPage = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            currentPage = 1;
        }

        // Lấy dữ liệu từ DAO
        List<Ingredient> ingredientList = ingredientDAO.searchAndFilterIngredients(searchTerm, categoryId, currentPage, PAGE_SIZE);
        int totalRecords = ingredientDAO.getIngredientCount(searchTerm, categoryId);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

        // Gửi dữ liệu qua JSP
        request.setAttribute("ingredientList", ingredientList);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", currentPage);
        // ...gửi các tham số search, category để giữ lại trên form
        
        request.getRequestDispatcher("/view/Inventory/ingredient-list.jsp").forward(request, response);
    }

}
