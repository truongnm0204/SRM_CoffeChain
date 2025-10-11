package dao;

import model.Order;
import model.OrderDetail;
import util.JDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho Order
 */
public class OrderDAO {
    
    private static final String SELECT_ALL_ORDERS = 
        "SELECT o.*, u.username, u.fullname, s.shopname " +
        "FROM orders o " +
        "LEFT JOIN \"user\" u ON o.userid = u.userid " +
        "LEFT JOIN shop s ON o.shopid = s.shopid " +
        "ORDER BY o.orderdate DESC";
    
    private static final String SELECT_ORDER_BY_ID = 
        "SELECT o.*, u.username, u.fullname, s.shopname " +
        "FROM orders o " +
        "LEFT JOIN \"user\" u ON o.userid = u.userid " +
        "LEFT JOIN shop s ON o.shopid = s.shopid " +
        "WHERE o.orderid = ?";
    
    private static final String SELECT_ORDERS_BY_USER = 
        "SELECT o.*, u.username, u.fullname, s.shopname " +
        "FROM orders o " +
        "LEFT JOIN \"user\" u ON o.userid = u.userid " +
        "LEFT JOIN shop s ON o.shopid = s.shopid " +
        "WHERE o.userid = ? " +
        "ORDER BY o.orderdate DESC";
    
    private static final String SELECT_ORDER_DETAILS = 
        "SELECT od.*, p.productname, p.sku, p.price " +
        "FROM orderdetail od " +
        "JOIN product p ON od.productid = p.productid " +
        "WHERE od.orderid = ?";
    
    private static final String INSERT_ORDER = 
        "INSERT INTO orders (orderdate, totalamount, status, userid, shopid) " +
        "VALUES (?, ?, ?, ?, ?) RETURNING orderid";
    
    private static final String INSERT_ORDER_DETAIL = 
        "INSERT INTO orderdetail (orderid, productid, quantity, subtotal) " +
        "VALUES (?, ?, ?, ?)";
    
    private static final String UPDATE_ORDER_STATUS = 
        "UPDATE orders SET status = ? WHERE orderid = ?";

    /**
     * Lấy tất cả đơn hàng
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_ORDERS)) {
            
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        
        return orders;
    }

    /**
     * Lấy đơn hàng theo ID
     */
    public Order getOrderById(int orderId) {
        Order order = null;
        
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ORDER_BY_ID)) {
            
            preparedStatement.setInt(1, orderId);
            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next()) {
                order = extractOrderFromResultSet(rs);
                // Lấy chi tiết đơn hàng
                order.setOrderDetails(getOrderDetails(orderId));
            }
            
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        
        return order;
    }

    /**
     * Lấy đơn hàng theo User ID
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ORDERS_BY_USER)) {
            
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        
        return orders;
    }

    /**
     * Lấy chi tiết đơn hàng
     */
    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ORDER_DETAILS)) {
            
            preparedStatement.setInt(1, orderId);
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderId(rs.getInt("orderid"));
                detail.setProductId(rs.getInt("productid"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setSubtotal(rs.getDouble("subtotal"));
                detail.setProductName(rs.getString("productname"));
                detail.setProductSku(rs.getString("sku"));
                detail.setProductPrice(rs.getDouble("price"));
                details.add(detail);
            }
            
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        
        return details;
    }

    /**
     * Tạo đơn hàng mới
     */
    public int createOrder(Order order, List<OrderDetail> orderDetails) {
        int orderId = -1;
        
        Connection connection = null;
        try {
            connection = JDBCUtils.getConnection();
            connection.setAutoCommit(false);
            
            // Insert order
            try (PreparedStatement ps = connection.prepareStatement(INSERT_ORDER)) {
                ps.setTimestamp(1, order.getOrderDate());
                ps.setDouble(2, order.getTotalAmount());
                ps.setString(3, order.getStatus());
                ps.setInt(4, order.getUserId());
                ps.setInt(5, order.getShopId());
                
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
            }
            
            // Insert order details
            if (orderId > 0 && orderDetails != null && !orderDetails.isEmpty()) {
                try (PreparedStatement ps = connection.prepareStatement(INSERT_ORDER_DETAIL)) {
                    for (OrderDetail detail : orderDetails) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, detail.getProductId());
                        ps.setInt(3, detail.getQuantity());
                        ps.setDouble(4, detail.getSubtotal());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            connection.commit();
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    JDBCUtils.printSQLException(ex);
                }
            }
            JDBCUtils.printSQLException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    JDBCUtils.printSQLException(e);
                }
            }
        }
        
        return orderId;
    }

    /**
     * Cập nhật trạng thái đơn hàng
     */
    public boolean updateOrderStatus(int orderId, String status) {
        boolean updated = false;
        
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_ORDER_STATUS)) {
            
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, orderId);
            
            updated = preparedStatement.executeUpdate() > 0;
            
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        
        return updated;
    }

    /**
     * Extract Order từ ResultSet
     */
    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("orderid"));
        order.setOrderDate(rs.getTimestamp("orderdate"));
        order.setTotalAmount(rs.getDouble("totalamount"));
        order.setStatus(rs.getString("status"));
        order.setUserId(rs.getInt("userid"));
        order.setShopId(rs.getInt("shopid"));
        
        // Thông tin mở rộng
        String fullname = rs.getString("fullname");
        String username = rs.getString("username");
        order.setUserName(fullname != null ? fullname : username);
        order.setShopName(rs.getString("shopname"));
        
        return order;
    }
}

