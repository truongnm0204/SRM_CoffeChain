package dao;

import model.Product;
import model.ProductIngredient;
import util.JDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO cho Product
 */
public class ProductDAO {

    private static final String SELECT_ALL_PRODUCTS = "SELECT * FROM product ORDER BY productid";
    private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM product WHERE productid = ?";
    private static final String SELECT_PRODUCT_BY_SKU = "SELECT * FROM product WHERE sku = ?";
    private static final String SELECT_PRODUCTS_BY_IDS = "SELECT * FROM product WHERE productid = ANY(?)";
    private static final String INSERT_PRODUCT_SQL = "INSERT INTO product (productname, sku, price, description) VALUES (?, ?, ?, ?)";
    private static final String INSERT_PRODUCT_INGREDIENT_SQL = "INSERT INTO productingredient (productid, ingredientid, requiredquantity) VALUES (?, ?, ?)";
    private static final String SELECT_INGREDIENTS_FOR_PRODUCT_SQL = "SELECT pi.ingredientid, pi.requiredquantity, i.ingredientname FROM productingredient pi JOIN ingredient i ON pi.ingredientid = i.ingredientid WHERE pi.productid = ?";
    private static final String UPDATE_PRODUCT_SQL = "UPDATE product SET productname = ?, sku = ?, price = ?, description = ? WHERE productid = ?";
    private static final String DELETE_PRODUCT_INGREDIENTS_SQL = "DELETE FROM productingredient WHERE productid = ?";
    private static final String DELETE_PRODUCT_SQL = "DELETE FROM product WHERE productid = ?";

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
     * Lấy sản phẩm theo SKU
     */
    public Product getProductBySku(String sku) {
        Product product = null;

        try (Connection connection = JDBCUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PRODUCT_BY_SKU)) {

            preparedStatement.setString(1, sku);
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

    /**
     * Lấy danh sách nguyên liệu cần thiết cho một sản phẩm
     */
    public List<ProductIngredient> getIngredientsForProduct(int productId) {
        List<ProductIngredient> productIngredients = new ArrayList<>();
        try (Connection connection = JDBCUtils.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SELECT_INGREDIENTS_FOR_PRODUCT_SQL)) {
            preparedStatement.setInt(1, productId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ProductIngredient pi = new ProductIngredient();
                pi.setIngredientId(rs.getInt("ingredientid"));
                pi.setRequiredQuantity(rs.getDouble("requiredquantity"));
                pi.setIngredientName(rs.getString("ingredientname"));
                productIngredients.add(pi);
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return productIngredients;
    }

    /**
     * Thêm sản phẩm mới và các nguyên liệu liên quan
     */
    public int addProduct(Product product, List<ProductIngredient> productIngredients) {
        int productId = -1;
        Connection connection = null;
        PreparedStatement psProduct = null;
        PreparedStatement psProductIngredient = null;
        ResultSet rs = null;

        try {
            connection = JDBCUtils.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Insert product
            psProduct = connection.prepareStatement(INSERT_PRODUCT_SQL, Statement.RETURN_GENERATED_KEYS);
            psProduct.setString(1, product.getProductName());
            psProduct.setString(2, product.getSku());
            psProduct.setDouble(3, product.getPrice());
            psProduct.setString(4, product.getDescription());
            int affectedRows = psProduct.executeUpdate();

            if (affectedRows > 0) {
                rs = psProduct.getGeneratedKeys();
                if (rs.next()) {
                    productId = rs.getInt(1);
                }
            }

            // Insert product ingredients
            if (productId != -1 && productIngredients != null && !productIngredients.isEmpty()) {
                psProductIngredient = connection.prepareStatement(INSERT_PRODUCT_INGREDIENT_SQL);
                for (ProductIngredient pi : productIngredients) {
                    psProductIngredient.setInt(1, productId);
                    psProductIngredient.setInt(2, pi.getIngredientId());
                    psProductIngredient.setDouble(3, pi.getRequiredQuantity());
                    psProductIngredient.addBatch();
                }
                psProductIngredient.executeBatch();
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (psProduct != null)
                    psProduct.close();
                if (psProductIngredient != null)
                    psProductIngredient.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                JDBCUtils.printSQLException(e);
            }
        }
        return productId;
    }

    /**
     * Cập nhật sản phẩm và các nguyên liệu liên quan
     */
    public boolean updateProduct(Product product, List<ProductIngredient> productIngredients) {
        boolean rowUpdated = false;
        Connection connection = null;
        PreparedStatement psProduct = null;
        PreparedStatement psDeleteIngredients = null;
        PreparedStatement psInsertIngredients = null;

        try {
            connection = JDBCUtils.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Update product
            psProduct = connection.prepareStatement(UPDATE_PRODUCT_SQL);
            psProduct.setString(1, product.getProductName());
            psProduct.setString(2, product.getSku());
            psProduct.setDouble(3, product.getPrice());
            psProduct.setString(4, product.getDescription());
            psProduct.setInt(5, product.getProductId());
            rowUpdated = psProduct.executeUpdate() > 0;

            // Delete existing product ingredients
            psDeleteIngredients = connection.prepareStatement(DELETE_PRODUCT_INGREDIENTS_SQL);
            psDeleteIngredients.setInt(1, product.getProductId());
            psDeleteIngredients.executeUpdate();

            // Insert new product ingredients
            if (productIngredients != null && !productIngredients.isEmpty()) {
                psInsertIngredients = connection.prepareStatement(INSERT_PRODUCT_INGREDIENT_SQL);
                for (ProductIngredient pi : productIngredients) {
                    psInsertIngredients.setInt(1, product.getProductId());
                    psInsertIngredients.setInt(2, pi.getIngredientId());
                    psInsertIngredients.setDouble(3, pi.getRequiredQuantity());
                    psInsertIngredients.addBatch();
                }
                psInsertIngredients.executeBatch();
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            try {
                if (psProduct != null)
                    psProduct.close();
                if (psDeleteIngredients != null)
                    psDeleteIngredients.close();
                if (psInsertIngredients != null)
                    psInsertIngredients.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                JDBCUtils.printSQLException(e);
            }
        }
        return rowUpdated;
    }

    /**
     * Xóa sản phẩm và các nguyên liệu liên quan
     */
    public boolean deleteProduct(int productId) {
        boolean rowDeleted = false;
        Connection connection = null;
        PreparedStatement psDeleteIngredients = null;
        PreparedStatement psDeleteProduct = null;

        try {
            connection = JDBCUtils.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Delete product ingredients first
            psDeleteIngredients = connection.prepareStatement(DELETE_PRODUCT_INGREDIENTS_SQL);
            psDeleteIngredients.setInt(1, productId);
            psDeleteIngredients.executeUpdate();

            // Delete product
            psDeleteProduct = connection.prepareStatement(DELETE_PRODUCT_SQL);
            psDeleteProduct.setInt(1, productId);
            rowDeleted = psDeleteProduct.executeUpdate() > 0;

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    Logger.getLogger(ProductDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            try {
                if (psDeleteIngredients != null)
                    psDeleteIngredients.close();
                if (psDeleteProduct != null)
                    psDeleteProduct.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                JDBCUtils.printSQLException(e);
            }
        }
        return rowDeleted;
    }
}
