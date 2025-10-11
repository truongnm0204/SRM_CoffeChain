/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Inventory;

import com.google.gson.Gson;
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
import model.Ingredient;
import model.PurchaseOrder;
import model.PurchaseOrderDetail;
import model.Supplier;

/**
 *
 * @author DELL
 */
@WebServlet(name = "update", urlPatterns = {"/purchase-orders/update"})
public class poupdate extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int poId = Integer.parseInt(request.getParameter("id"));
            PoDAO poDAO = new PoDAO();
            SupplierDAO supplierDAO = new SupplierDAO(); // Khởi tạo SupplierDAO
            List<Ingredient> ingredientList = IngredientDAO.getAllActiveIngredients();
            String ingredientsJson = new Gson().toJson(ingredientList);

            // Lấy thông tin chung của PO
            PurchaseOrder po = poDAO.findPurchaseOrderById(poId);

            if (po != null) {
                // Lấy danh sách chi tiết
                List<PurchaseOrderDetail> details = poDAO.findDetailsByPoId(poId);
                po.setItems(details);

                // Lấy danh sách nhà cung cấp để hiển thị dropdown (khi ở chế độ edit)
                List<Supplier> supplierList = supplierDAO.getAllSuppliers();

                // Gửi tất cả dữ liệu qua cho JSP
                request.setAttribute("po", po);
                request.setAttribute("supplierList", supplierList);
                request.setAttribute("ingredientList", ingredientList); // Vẫn giữ lại nếu cần dùng ở đâu khác
                request.setAttribute("ingredientsJson", ingredientsJson);

                request.getRequestDispatcher("/view/Inventory/po-details-update.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Purchase Order not found.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Purchase Order ID.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // --- 1. Lấy dữ liệu chung từ form ---
            int poId = Integer.parseInt(request.getParameter("poId"));
            int supplierId = Integer.parseInt(request.getParameter("supplierId"));

            String expectedDateStr = request.getParameter("expectedDate");
            Date expectedDate = null;
            if (expectedDateStr != null && !expectedDateStr.isEmpty()) {
                expectedDate = Date.valueOf(expectedDateStr);
            }

            // --- 2. Xử lý tất cả các item (cũ và mới) một cách nhất quán ---
            List<PurchaseOrderDetail> updatedDetails = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;

            // Lấy tất cả tên của các tham số được gửi lên
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();

                // Nếu một tham số có tên bắt đầu bằng "quantity_", đó là một item cần xử lý
                if (paramName.startsWith("quantity_")) {
                    // Trích xuất ingredientId từ tên tham số (ví dụ: từ "quantity_1" -> "1")
                    int ingredientId = Integer.parseInt(paramName.substring(9));

                    // Lấy giá trị quantity và price tương ứng
                    String quantityParam = request.getParameter(paramName); // "quantity_" + ingredientId
                    String priceParam = request.getParameter("price_" + ingredientId);

                    if (quantityParam != null && !quantityParam.isEmpty() && priceParam != null && !priceParam.isEmpty()) {
                        PurchaseOrderDetail detail = new PurchaseOrderDetail();
                        detail.setIngredientID(ingredientId);

                        BigDecimal qty = new BigDecimal(quantityParam);
                        BigDecimal price = new BigDecimal(priceParam);

                        detail.setQuantity(qty);
                        detail.setPricePerUnit(price);

                        updatedDetails.add(detail);
                        totalAmount = totalAmount.add(qty.multiply(price));
                    }
                }
            }

            // --- Các bước còn lại giữ nguyên ---
            if (updatedDetails.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/purchase-orders/update?id=" + poId + "&error=empty");
                return;
            }

            PurchaseOrder poToUpdate = new PurchaseOrder();
            poToUpdate.setPurchaseOrderID(poId);
            poToUpdate.setSupplierID(supplierId);
            poToUpdate.setExpectedDeliveryDate(expectedDate);
            poToUpdate.setTotalAmount(totalAmount);

            PoDAO poDAO = new PoDAO();
            boolean success = poDAO.updatePurchaseOrder(poToUpdate, updatedDetails);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/purchase-orders/list?update=success");
            } else {
                response.sendRedirect(request.getContextPath() + "/purchase-orders/update?id=" + poId + "&error=failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/purchase-orders/list?error=true");
        }
    }

}
