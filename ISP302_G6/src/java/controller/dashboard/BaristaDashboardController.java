package controller.dashboard;

import dao.OrderDAO;
import dao.ProductDAO;
import model.Order;
import model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller cho Barista Dashboard
 */
@WebServlet(name = "BaristaDashboardController", urlPatterns = {"/dashboard/barista"})
public class BaristaDashboardController extends HttpServlet {
    
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Kiểm tra đăng nhập
        if (session == null || session.getAttribute("isLoggedIn") == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        String role = (String) session.getAttribute("role");
        String userId = (String) session.getAttribute("userId");
        
        try {
            // Lấy thống kê đơn hàng
            List<Order> allOrders = orderDAO.getAllOrders();
            
            // Đếm số đơn theo trạng thái
            long totalOrders = allOrders.size();
            long pendingOrders = allOrders.stream()
                    .filter(o -> "Pending".equals(o.getStatus()))
                    .count();
            long processingOrders = allOrders.stream()
                    .filter(o -> "Processing".equals(o.getStatus()))
                    .count();
            long completedOrders = allOrders.stream()
                    .filter(o -> "Completed".equals(o.getStatus()))
                    .count();
            
            // Tính tổng doanh thu từ đơn hoàn thành
            double totalRevenue = allOrders.stream()
                    .filter(o -> "Completed".equals(o.getStatus()))
                    .mapToDouble(Order::getTotalAmount)
                    .sum();
            
            // Lấy 5 đơn hàng gần nhất
            List<Order> recentOrders = allOrders.size() > 5 
                    ? allOrders.subList(0, 5) 
                    : allOrders;
            
            // Lấy danh sách sản phẩm
            List<Product> products = productDAO.getAllProducts();
            
            // Set attributes
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("pendingOrders", pendingOrders);
            request.setAttribute("processingOrders", processingOrders);
            request.setAttribute("completedOrders", completedOrders);
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("recentOrders", recentOrders);
            request.setAttribute("products", products);
            request.setAttribute("userRole", role);
            request.setAttribute("userName", session.getAttribute("username"));
            
            // Forward đến view tương ứng với role
            if ("Barista".equalsIgnoreCase(role)) {
                request.getRequestDispatcher("/view/dashboard/barista-dashboard.jsp").forward(request, response);
            } else {
                // Có thể tạo dashboard khác cho các role khác
                request.getRequestDispatcher("/view/trangchu/index.jsp").forward(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải dashboard: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }
}

