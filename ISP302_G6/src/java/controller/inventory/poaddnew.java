/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Inventory;

import dao.IngredientDAO;
import dao.PoDAO;
import dao.SupplierDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import model.PurchaseOrder;
import model.PurchaseOrderDetail;
import model.User;
import java.sql.Timestamp;

/**
 *
 * @author DELL
 */
@WebServlet(name = "addnewpo", urlPatterns = {"/purchase-orders/create"})
public class poaddnew extends HttpServlet {
    // Xử lý GET: Hiển thị form

    @Override

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy danh sách nhà cung cấp và nguyên vật liệu từ DAO
        SupplierDAO supplierDAO = new SupplierDAO();

        IngredientDAO ingredientDAO = new IngredientDAO();

        request.setAttribute("supplierList", supplierDAO.getAllSuppliers());

        request.setAttribute("ingredientList", ingredientDAO.getAllIngredients());

        request.getRequestDispatcher("/view/Inventory/po-add-new.jsp").forward(request, response);

    }

    // Xử lý POST: Lưu dữ liệu từ form
    @Override

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            // 1. Lấy thông tin chung từ form
            int supplierId = Integer.parseInt(request.getParameter("supplierId"));

            Date expectedDate = Date.valueOf(request.getParameter("expectedDate"));

            User loggedInUser = (User) request.getSession().getAttribute("user");

            // 2. Lấy danh sách các mặt hàng từ form
            String[] ingredientIds = request.getParameterValues("ingredientId");

            String[] quantities = request.getParameterValues("quantity");

            String[] prices = request.getParameterValues("pricePerUnit");

            List<PurchaseOrderDetail> details = new ArrayList<>();

            BigDecimal totalAmount = BigDecimal.ZERO;

            if (ingredientIds != null) {

                for (int i = 0; i < ingredientIds.length; i++) {

                    PurchaseOrderDetail detail = new PurchaseOrderDetail();

                    detail.setIngredientID(Integer.parseInt(ingredientIds[i]));

                    BigDecimal qty = new BigDecimal(quantities[i]);

                    BigDecimal price = new BigDecimal(prices[i]);

                    detail.setQuantity(qty);

                    detail.setPricePerUnit(price);

                    details.add(detail);

                    totalAmount = totalAmount.add(qty.multiply(price)); // Tính tổng tiền

                }

            }

            // 3. Tạo đối tượng PurchaseOrder
            PurchaseOrder po = new PurchaseOrder();

            po.setSupplierID(supplierId);

            po.setExpectedDeliveryDate(expectedDate);

            po.setTotalAmount(totalAmount);

            po.setCreatedBy(loggedInUser.getUserID());

            po.setOrderDate(new Timestamp(System.currentTimeMillis()));

            po.setStatus("Pending"); // Trạng thái mặc định

            // 4. Gọi DAO để lưu vào DB
            PoDAO poDAO = new PoDAO();

            boolean success = poDAO.createPurchaseOrder(po, details);

            if (success) {

                response.sendRedirect(request.getContextPath() + "/purchase-orders/list?status=success");

            } else {

                response.sendRedirect(request.getContextPath() + "/purchase-orders/create?status=failed");

            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(request.getContextPath() + "/purchase-orders/create?status=error");

        }

    }
}
