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
@WebServlet(name = "addIngredient", urlPatterns = {"/ingredients/add"})
public class addIngredient extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method. Chịu trách nhiệm hiển thị trang
     * form "Add New Ingredient".
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Khởi tạo các DAO cần thiết
        IngredientDAO categoryDAO = new IngredientDAO();
        SettingDAO settingDAO = new SettingDAO();

        // 2. Lấy danh sách các category và unit từ database
        List<IngredientCategory> categoryList = categoryDAO.getAllCategories();
        List<Setting> unitList = settingDAO.getSettingsByType("UNIT");

        // 3. Đặt các danh sách này vào request scope để JSP có thể truy cập
        request.setAttribute("categoryList", categoryList);
        request.setAttribute("unitList", unitList);

        // 4. Chuyển hướng đến trang JSP
        request.getRequestDispatcher("/view/Inventory/ingredients-add.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method. Chịu trách nhiệm xử lý dữ liệu
     * khi người dùng nhấn nút "Save Ingredient".
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Lấy dữ liệu từ form
            String name = request.getParameter("ingredient_name");
            int categoryId = Integer.parseInt(request.getParameter("category_id"));
            int unitId = Integer.parseInt(request.getParameter("unit_setting_id"));
            BigDecimal minStock = new BigDecimal(request.getParameter("min_stock"));
            String description = request.getParameter("description");

            // 2. Tạo đối tượng Ingredient mới
            Ingredient newIngredient = new Ingredient();
            newIngredient.setIngredientName(name);
            newIngredient.setCategoryID(categoryId);
            newIngredient.setUnitSettingID(unitId);
            newIngredient.setMinStock(minStock);
            newIngredient.setDescription(description);
            newIngredient.setCurrentStock(BigDecimal.ZERO); // Tồn kho ban đầu là 0

            // 3. Gọi DAO để lưu vào database
            IngredientDAO ingredientDAO = new IngredientDAO();
            boolean success = ingredientDAO.addIngredient(newIngredient);

            // 4. Chuyển hướng người dùng
            if (success) {
                // Nếu thành công, chuyển về trang danh sách với thông báo
                response.sendRedirect(request.getContextPath() + "/ingredients/list?add=success");
            } else {
                // Nếu thất bại, quay lại form với thông báo lỗi
                request.setAttribute("error", "Failed to add new ingredient. Please try again.");
                doGet(request, response); // Gọi lại doGet để tải lại danh sách cho form
            }

        } catch (Exception e) {
            // Bắt các lỗi khác như NumberFormatException
            e.printStackTrace();
            request.setAttribute("error", "An error occurred: " + e.getMessage());
            doGet(request, response);
        }
    }

}
