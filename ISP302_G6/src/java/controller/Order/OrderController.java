package controller.order;

import dao.OrderDAO;
import dao.ProductDAO;
import model.Order;
import model.OrderDetail;
import model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller xử lý các chức năng liên quan đến Order
 */
@WebServlet(name = "OrderController", urlPatterns = {"/order/*"})
public class OrderController extends HttpServlet {
    
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
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/list")) {
            // Hiển thị danh sách đơn hàng
            showOrderList(request, response);
        } else if (pathInfo.startsWith("/detail/")) {
            // Hiển thị chi tiết đơn hàng
            showOrderDetail(request, response, pathInfo);
        } else if (pathInfo.equals("/create")) {
            // Hiển thị form tạo đơn hàng mới
            showCreateOrderForm(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo != null && pathInfo.equals("/create")) {
            // Tạo đơn hàng mới
            createOrder(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/update-status/")) {
            // Cập nhật trạng thái đơn hàng
            updateOrderStatus(request, response, pathInfo);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * Hiển thị danh sách đơn hàng
     */
    private void showOrderList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Lấy tất cả đơn hàng
            List<Order> orders = orderDAO.getAllOrders();
            
            request.setAttribute("orders", orders);
            request.setAttribute("pageTitle", "Danh sách đơn hàng");
            request.getRequestDispatcher("/view/order/order-list.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải danh sách đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Hiển thị chi tiết đơn hàng
     */
    private void showOrderDetail(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {
        
        try {
            // Lấy order ID từ URL
            String orderIdStr = pathInfo.substring("/detail/".length());
            int orderId = Integer.parseInt(orderIdStr);
            
            // Lấy thông tin đơn hàng
            Order order = orderDAO.getOrderById(orderId);
            
            if (order == null) {
                request.setAttribute("error", "Không tìm thấy đơn hàng #" + orderId);
                request.getRequestDispatcher("/view/error.jsp").forward(request, response);
                return;
            }
            
            request.setAttribute("order", order);
            request.setAttribute("pageTitle", "Chi tiết đơn hàng #" + orderId);
            request.getRequestDispatcher("/view/order/order-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID đơn hàng không hợp lệ");
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải chi tiết đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Hiển thị form tạo đơn hàng mới
     */
    private void showCreateOrderForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Lấy danh sách sản phẩm
            List<Product> products = productDAO.getAllProducts();
            
            request.setAttribute("products", products);
            request.setAttribute("pageTitle", "Tạo đơn hàng mới");
            request.getRequestDispatcher("/view/order/order-create.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải form tạo đơn: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }
    
    /**
     * Tạo đơn hàng mới
     */
    private void createOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            HttpSession session = request.getSession();
            String userIdStr = (String) session.getAttribute("userId");
            
            if (userIdStr == null) {
                response.sendRedirect(request.getContextPath() + "/auth/login");
                return;
            }
            
            int userId = Integer.parseInt(userIdStr);
            int shopId = 1; // Default shop
            
            // Lấy thông tin từ form
            String[] productIds = request.getParameterValues("productId");
            String[] quantities = request.getParameterValues("quantity");
            
            if (productIds == null || quantities == null || productIds.length == 0) {
                request.setAttribute("error", "Vui lòng chọn ít nhất một sản phẩm");
                showCreateOrderForm(request, response);
                return;
            }
            
            // Tạo order details
            List<OrderDetail> orderDetails = new ArrayList<>();
            double totalAmount = 0;
            
            for (int i = 0; i < productIds.length; i++) {
                int productId = Integer.parseInt(productIds[i]);
                int quantity = Integer.parseInt(quantities[i]);
                
                if (quantity <= 0) continue;
                
                Product product = productDAO.getProductById(productId);
                if (product != null) {
                    double subtotal = product.getPrice() * quantity;
                    totalAmount += subtotal;
                    
                    OrderDetail detail = new OrderDetail();
                    detail.setProductId(productId);
                    detail.setQuantity(quantity);
                    detail.setSubtotal(subtotal);
                    orderDetails.add(detail);
                }
            }
            
            if (orderDetails.isEmpty()) {
                request.setAttribute("error", "Không có sản phẩm nào được chọn");
                showCreateOrderForm(request, response);
                return;
            }
            
            // Tạo order
            Order order = new Order();
            order.setOrderDate(new Timestamp(System.currentTimeMillis()));
            order.setTotalAmount(totalAmount);
            order.setStatus("Pending");
            order.setUserId(userId);
            order.setShopId(shopId);
            
            int orderId = orderDAO.createOrder(order, orderDetails);
            
            if (orderId > 0) {
                session.setAttribute("ms", "Tạo đơn hàng #" + orderId + " thành công!");
                response.sendRedirect(request.getContextPath() + "/order/detail/" + orderId);
            } else {
                request.setAttribute("error", "Không thể tạo đơn hàng");
                showCreateOrderForm(request, response);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tạo đơn hàng: " + e.getMessage());
            showCreateOrderForm(request, response);
        }
    }
    
    /**
     * Cập nhật trạng thái đơn hàng
     */
    private void updateOrderStatus(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {
        
        try {
            String orderIdStr = pathInfo.substring("/update-status/".length());
            int orderId = Integer.parseInt(orderIdStr);
            
            String status = request.getParameter("status");
            
            if (status == null || status.trim().isEmpty()) {
                request.getSession().setAttribute("error", "Trạng thái không hợp lệ");
            } else {
                boolean updated = orderDAO.updateOrderStatus(orderId, status);
                
                if (updated) {
                    request.getSession().setAttribute("ms", "Cập nhật trạng thái đơn hàng thành công!");
                } else {
                    request.getSession().setAttribute("error", "Không thể cập nhật trạng thái");
                }
            }
            
            response.sendRedirect(request.getContextPath() + "/order/detail/" + orderId);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/order/list");
        }
    }
}
