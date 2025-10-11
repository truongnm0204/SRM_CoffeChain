package dao;

import model.Product;
import util.JDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho Product
 */
public class ProductDAO {
    
    private static final String SELECT_ALL_PRODUCTS = "SELECT * FROM product ORDER BY productid";
    private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM product WHERE productid = ?";
    private static final String SELECT_PRODUCTS_BY_IDS = "SELECT * FROM product WHERE productid = ANY(?)";

    /**
     * Lấy tất cả sản phẩm
     */
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PRODUCTS)) {
            
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("productid"));
                product.setProductName(rs.getString("productname"));
                product.setSku(rs.getString("sku"));
                product.setPrice(rs.getDouble("price"));
                product.setDescription(rs.getString("description"));
                products.add(product);
            }
            
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        
        return products;
    }

    /**
     * Lấy sản phẩm theo ID
     */
    public Product getProductById(int productId) {
        Product product = null;
        
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PRODUCT_BY_ID)) {
            
            preparedStatement.setInt(1, productId);
            ResultSet rs = preparedStatement.executeQuery();
            
            if (rs.next()) {
                product = new Product();
                product.setProductId(rs.getInt("productid"));
                product.setProductName(rs.getString("productname"));
                product.setSku(rs.getString("sku"));
                product.setPrice(rs.getDouble("price"));
                product.setDescription(rs.getString("description"));
            }
            
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        
        return product;
    }

    /**
     * Lấy nhiều sản phẩm theo danh sách IDs
     */
    public List<Product> getProductsByIds(List<Integer> productIds) {
        List<Product> products = new ArrayList<>();
        
        if (productIds == null || productIds.isEmpty()) {
            return products;
        }
        
        try (Connection connection = JDBCUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PRODUCTS_BY_IDS)) {
            
            Array array = connection.createArrayOf("INTEGER", productIds.toArray());
            preparedStatement.setArray(1, array);
            ResultSet rs = preparedStatement.executeQuery();
            
            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("productid"));
                product.setProductName(rs.getString("productname"));
                product.setSku(rs.getString("sku"));
                product.setPrice(rs.getDouble("price"));
                product.setDescription(rs.getString("description"));
                products.add(product);
            }
            
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        
        return products;
    }
}

