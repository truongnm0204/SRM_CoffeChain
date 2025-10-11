/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.Inventory;

import dao.PoDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.PurchaseOrder;

/**
 *
 * @author DELL
 */
@WebServlet(name = "polist", urlPatterns = {"/purchase-orders/list"})
public class polist extends HttpServlet {

    private static final int PAGE_SIZE = 10; // Số lượng bản ghi mỗi trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PoDAO poDAO = new PoDAO();

        // Lấy các tham số cho việc tìm kiếm, lọc, và phân trang từ URL
        String searchTerm = request.getParameter("search");
        String status = request.getParameter("status");
        int currentPage = 1;
        try {
            // Lấy số trang từ parameter 'page', nếu không có thì mặc định là 1
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                currentPage = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            currentPage = 1; // Nếu param 'page' không phải là số, quay về trang 1
        }

        // Gọi DAO để lấy danh sách PO theo các tiêu chí
        List<PurchaseOrder> poList = poDAO.searchAndFilterPurchaseOrders(searchTerm, status, currentPage, PAGE_SIZE);

        // Gọi DAO để đếm tổng số bản ghi khớp với tiêu chí (dùng cho phân trang)
        int totalRecords = poDAO.getPurchaseOrderCount(searchTerm, status);

        // Tính toán tổng số trang
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

        // Đặt các thuộc tính vào request để gửi cho file JSP
        request.setAttribute("poList", poList);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("searchTerm", searchTerm); // Gửi lại để giữ giá trị trên ô search
        request.setAttribute("status", status);         // Gửi lại để biết tab status nào đang active

        // Chuyển hướng đến file JSP để hiển thị giao diện
        request.getRequestDispatcher("/view/Inventory/po-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Xử lý các request POST (ví dụ: tạo, cập nhật PO) ở đây
        doGet(request, response); // Tạm thời gọi lại doGet
    }
}
