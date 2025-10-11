/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Inventory;

import dao.IngredientDAO;
import dao.SettingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import model.Ingredient;
import model.IngredientCategory;
import model.Setting;

/**
 *
 * @author DELL
 */
@WebServlet(name = "ingredientdetail", urlPatterns = {"/ingredients/detail"})
public class ingredientdetail extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int ingredientId = Integer.parseInt(request.getParameter("id"));

            // Khởi tạo các DAO
            IngredientDAO ingredientDAO = new IngredientDAO();
            IngredientDAO categoryDAO = new IngredientDAO();
            SettingDAO settingDAO = new SettingDAO();

            // Lấy thông tin chi tiết của ingredient cần sửa
            Ingredient ingredient = ingredientDAO.findIngredientById(ingredientId);

            // Lấy danh sách cho các dropdown
            List<IngredientCategory> categoryList = categoryDAO.getAllCategories();
            List<Setting> unitList = settingDAO.getSettingsByType("UNIT");

            if (ingredient != null) {
                // Gửi tất cả dữ liệu cần thiết qua JSP
                request.setAttribute("ingredient", ingredient);
                request.setAttribute("categoryList", categoryList);
                request.setAttribute("unitList", unitList);
                request.getRequestDispatcher("/view/Inventory/ingredient-detail.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ingredient not found.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Ingredient ID.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy dữ liệu từ form submit
            int id = Integer.parseInt(request.getParameter("ingredientId"));
            String name = request.getParameter("ingredientName");
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            int unitId = Integer.parseInt(request.getParameter("unitSettingId"));
            BigDecimal minStock = new BigDecimal(request.getParameter("minStock"));
            String description = request.getParameter("description");

            // Tạo đối tượng Ingredient với thông tin đã cập nhật
            Ingredient ingredientToUpdate = new Ingredient();
            ingredientToUpdate.setIngredientID(id);
            ingredientToUpdate.setIngredientName(name);
            ingredientToUpdate.setCategoryID(categoryId);
            ingredientToUpdate.setUnitSettingID(unitId);
            ingredientToUpdate.setMinStock(minStock);
            ingredientToUpdate.setDescription(description);

            // Gọi DAO để thực hiện update
            IngredientDAO ingredientDAO = new IngredientDAO();
            boolean success = ingredientDAO.updateIngredient(ingredientToUpdate);

            // Chuyển hướng về trang danh sách
            if (success) {
                response.sendRedirect(request.getContextPath() + "/ingredients/list?update=success");
            } else {
                response.sendRedirect(request.getContextPath() + "/ingredients/list/update?id=" + id + "&update=failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/inventory/ingredients?update=error");
        }
    }

}
