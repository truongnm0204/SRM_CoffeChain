/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author DELL
 */
import model.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import service.JDBCUtils;

/**
 *
 * @author DELL
 */
public class SupplierDAO {

    /**
     * Lấy danh sách tất cả các nhà cung cấp đang ở trạng thái 'Active'.
     *
     * @return một List các đối tượng Supplier.
     */
    public List<Supplier> getAllSuppliers() {
        List<Supplier> supplierList = new ArrayList<>();
        // Lấy các nhà cung cấp đang hoạt động và sắp xếp theo tên
        String sql = "SELECT * FROM \"Supplier\" WHERE \"status\" = 'Active' ORDER BY \"suppliername\" ASC";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierID(rs.getInt("SupplierID"));
                supplier.setSupplierName(rs.getString("SupplierName"));
                supplier.setContactEmail(rs.getString("ContactEmail"));
                supplier.setPhone(rs.getString("Phone"));
                supplier.setAddress(rs.getString("Address"));
                supplier.setStatus(rs.getString("Status"));
                supplierList.add(supplier);
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return supplierList;
    }

}
