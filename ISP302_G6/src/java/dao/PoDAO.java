/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.PurchaseOrder;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.PurchaseOrderDetail;
import model.User;
import service.JDBCUtils;
import java.sql.Statement;

/**
 *
 * @author DELL
 */
public class PoDAO {

    /**
     * Hàm chính để lấy danh sách PO, hỗ trợ tìm kiếm, lọc theo trạng thái và
     * phân trang.
     *
     * @param searchTerm Từ khóa tìm kiếm (mã PO, tên NCC, tên người tạo). Có
     * thể null.
     * @param status Trạng thái cần lọc. Có thể null.
     * @param page Trang hiện tại (bắt đầu từ 1).
     * @param pageSize Số lượng bản ghi trên mỗi trang.
     * @return Danh sách các đối tượng PurchaseOrder.
     */
    public List<PurchaseOrder> searchAndFilterPurchaseOrders(String searchTerm, String status, int page, int pageSize) {
        List<PurchaseOrder> poList = new ArrayList<>();

        String baseSql = "SELECT po.*, s.\"suppliername\", u.\"fullname\" AS creatorname "
                + "FROM \"PurchaseOrder\" po "
                + "LEFT JOIN \"Supplier\" s ON po.\"supplierid\" = s.\"supplierid\" "
                + "LEFT JOIN \"User\" u ON po.\"createdby\" = u.\"userid\"";

        StringBuilder sqlBuilder = new StringBuilder(baseSql);
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (status != null && !status.isEmpty()) {
            sqlBuilder.append(" WHERE po.\"status\" = ?");
            params.add(status);
            hasWhere = true;
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(hasWhere ? " AND (" : " WHERE (");
            sqlBuilder.append("CAST(po.\"purchaseorderid\" AS TEXT) ILIKE ? OR s.\"suppliername\" ILIKE ? OR u.\"fullname\" ILIKE ?)");
            String likeTerm = "%" + searchTerm + "%";
            params.add(likeTerm);
            params.add(likeTerm);
            params.add(likeTerm);
        }

        sqlBuilder.append(" ORDER BY po.\"orderdate\" DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    poList.add(mapResultSetToPurchaseOrder(rs));
                }
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return poList;
    }

    public int getPurchaseOrderCount(String searchTerm, String status) {
        int count = 0;
        String baseSql = "SELECT COUNT(*) FROM \"PurchaseOrder\" po "
                + "LEFT JOIN \"Supplier\" s ON po.\"supplierid\" = s.\"supplierid\" "
                + "LEFT JOIN \"User\" u ON po.\"createdby\" = u.\"userid\"";

        StringBuilder sqlBuilder = new StringBuilder(baseSql);
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (status != null && !status.isEmpty()) {
            sqlBuilder.append(" WHERE po.\"status\" = ?");
            params.add(status);
            hasWhere = true;
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(hasWhere ? " AND (" : " WHERE (");
            sqlBuilder.append("CAST(po.\"purchaseorderid\" AS TEXT) ILIKE ? OR s.\"suppliername\" ILIKE ? OR u.\"fullname\" ILIKE ?)");
            String likeTerm = "%" + searchTerm + "%";
            params.add(likeTerm);
            params.add(likeTerm);
            params.add(likeTerm);
        }

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return count;
    }

    private PurchaseOrder mapResultSetToPurchaseOrder(ResultSet rs) throws SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setPurchaseOrderID(rs.getInt("purchaseorderid"));
        po.setOrderDate(rs.getTimestamp("orderdate"));

        BigDecimal totalAmount = rs.getBigDecimal("totalamount");
        if (totalAmount != null) {
            po.setTotalAmount(totalAmount);
        }

        po.setStatus(rs.getString("status"));
        po.setSupplierID(rs.getInt("supplierid"));
        po.setCreatedBy(rs.getInt("createdby"));

        po.setSupplierName(rs.getString("suppliername"));
        po.setCreatorName(rs.getString("creatorname"));

        return po;
    }

    public boolean createPurchaseOrder(PurchaseOrder po, List<PurchaseOrderDetail> details) {
        String sqlPO = "INSERT INTO \"PurchaseOrder\" (\"OrderDate\", \"ExpectedDeliveryDate\", \"TotalAmount\", \"Status\", \"SupplierID\", \"CreatedBy\") VALUES (?, ?, ?, ?, ?, ?)";
        String sqlPODetail = "INSERT INTO \"PurchaseOrderDetail\" (\"PurchaseOrderID\", \"IngredientID\", \"Quantity\", \"PricePerUnit\") VALUES (?, ?, ?, ?)";
        Connection conn = null;

        try {
            conn = JDBCUtils.getConnection();
            // Bắt đầu một Transaction, tắt chế độ tự động commit
            conn.setAutoCommit(false);

            // --- BƯỚC 1: Thêm bản ghi PurchaseOrder chính và lấy ID vừa tạo ---
            int generatedPoId = 0;
            try (PreparedStatement psPO = conn.prepareStatement(sqlPO, Statement.RETURN_GENERATED_KEYS)) {
                psPO.setTimestamp(1, po.getOrderDate());
                psPO.setDate(2, (Date) po.getExpectedDeliveryDate());
                psPO.setBigDecimal(3, po.getTotalAmount());
                psPO.setString(4, po.getStatus());
                psPO.setInt(5, po.getSupplierID());
                psPO.setInt(6, po.getCreatedBy());

                int affectedRows = psPO.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = psPO.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            generatedPoId = generatedKeys.getInt(1); // Lấy ID của PO vừa được tạo
                        }
                    }
                }

                if (generatedPoId == 0) {
                    throw new SQLException("Tạo Purchase Order thất bại, không nhận được ID.");
                }
            }

            // --- BƯỚC 2: Thêm các bản ghi PurchaseOrderDetail chi tiết ---
            try (PreparedStatement psPODetail = conn.prepareStatement(sqlPODetail)) {
                for (PurchaseOrderDetail detail : details) {
                    psPODetail.setInt(1, generatedPoId); // Sử dụng ID của PO vừa tạo
                    psPODetail.setInt(2, detail.getIngredientID());
                    psPODetail.setBigDecimal(3, detail.getQuantity());
                    psPODetail.setBigDecimal(4, detail.getPricePerUnit());
                    psPODetail.addBatch(); // Thêm vào một lô để thực thi cùng lúc
                }
                psPODetail.executeBatch(); // Thực thi tất cả các lệnh INSERT cùng lúc
            }

            // --- BƯỚC 3: Nếu tất cả thành công, commit transaction ---
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo Purchase Order, đang rollback transaction...");
            JDBCUtils.printSQLException(e);
            if (conn != null) {
                try {
                    // Nếu có lỗi, hủy bỏ tất cả các thay đổi trong transaction này
                    conn.rollback();
                } catch (SQLException ex) {
                    JDBCUtils.printSQLException(ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    // Luôn luôn trả lại chế độ auto-commit và đóng kết nối
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    JDBCUtils.printSQLException(e);
                }
            }
        }
    }

    public PurchaseOrder findPurchaseOrderById(int poId) {
        // Sửa lại các tên cột có dấu gạch dưới (_) thành viết liền
        String sql = "SELECT po.*, s.suppliername, u.fullname AS creatorname "
                + "FROM \"PurchaseOrder\" po "
                + "LEFT JOIN \"Supplier\" s ON po.supplierid = s.supplierid "
                + "LEFT JOIN \"User\" u ON po.createdby = u.userid "
                + "WHERE po.purchaseorderid = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, poId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Giả sử hàm map của bạn cũng đã được sửa để đọc tên cột chữ thường
                    return mapResultSetToPurchaseOrder(rs);
                }
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return null;
    }

    /**
     * Lấy danh sách tất cả các chi tiết (items) của một Purchase Order.
     *
     * @param poId ID của đơn hàng.
     * @return List các đối tượng PurchaseOrderDetail.
     */
    public List<PurchaseOrderDetail> findDetailsByPoId(int poId) {
        List<PurchaseOrderDetail> details = new ArrayList<>();
        // Sửa lại các tên cột có dấu gạch dưới (_) thành viết liền
        String sql = "SELECT pod.*, i.ingredientname "
                + "FROM \"PurchaseOrderDetail\" pod "
                + "JOIN \"Ingredient\" i ON pod.ingredientid = i.ingredientid "
                + "WHERE pod.purchaseorderid = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, poId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrderDetail detail = new PurchaseOrderDetail();
                    detail.setPurchaseOrderID(rs.getInt("purchaseorderid"));
                    detail.setIngredientID(rs.getInt("ingredientid"));
                    detail.setQuantity(rs.getBigDecimal("quantity"));
                    detail.setPricePerUnit(rs.getBigDecimal("priceperunit"));
                    detail.setIngredientName(rs.getString("ingredientname"));
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return details;
    }

    // Thêm hàm này vào file DAO/PurchaseOrderDAO.java
    /**
     * Cập nhật một đơn hàng đã có, bao gồm cả các chi tiết của nó. Sử dụng
     * transaction: Xóa hết chi tiết cũ, sau đó thêm lại danh sách chi tiết mới.
     *
     * @param po Đối tượng PurchaseOrder chứa thông tin chung cần cập nhật.
     * @param details Danh sách các PurchaseOrderDetail mới.
     * @return true nếu cập nhật thành công.
     */
    public boolean updatePurchaseOrder(PurchaseOrder po, List<PurchaseOrderDetail> details) {
        String sqlUpdatePO = "UPDATE \"PurchaseOrder\" SET supplierid = ?, expecteddeliverydate = ?, totalamount = ? WHERE purchaseorderid = ?";
        String sqlDeleteDetails = "DELETE FROM \"PurchaseOrderDetail\" WHERE purchaseorderid = ?";
        String sqlInsertDetail = "INSERT INTO \"PurchaseOrderDetail\" (purchaseorderid, ingredientid, quantity, priceperunit) VALUES (?, ?, ?, ?)";
        Connection conn = null;

        try {
            conn = JDBCUtils.getConnection();
            // Bắt đầu transaction
            conn.setAutoCommit(false);

            // --- BƯỚC 1: Cập nhật thông tin chung của PurchaseOrder ---
            try (PreparedStatement psUpdatePO = conn.prepareStatement(sqlUpdatePO)) {
                psUpdatePO.setInt(1, po.getSupplierID());
//                psUpdatePO.setDate(2, po.getExpectedDeliveryDate());
                psUpdatePO.setDate(2, new java.sql.Date(po.getExpectedDeliveryDate().getTime()));
                psUpdatePO.setBigDecimal(3, po.getTotalAmount());
                psUpdatePO.setInt(4, po.getPurchaseOrderID());
                psUpdatePO.executeUpdate();
            }

            // --- BƯỚC 2: Xóa tất cả các chi tiết cũ của đơn hàng này ---
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDeleteDetails)) {
                psDelete.setInt(1, po.getPurchaseOrderID());
                psDelete.executeUpdate();
            }

            // --- BƯỚC 3: Thêm lại danh sách chi tiết mới ---
            try (PreparedStatement psInsertDetail = conn.prepareStatement(sqlInsertDetail)) {
                for (PurchaseOrderDetail detail : details) {
                    psInsertDetail.setInt(1, po.getPurchaseOrderID());
                    psInsertDetail.setInt(2, detail.getIngredientID());
                    psInsertDetail.setBigDecimal(3, detail.getQuantity());
                    psInsertDetail.setBigDecimal(4, detail.getPricePerUnit());
                    psInsertDetail.addBatch();
                }
                psInsertDetail.executeBatch();
            }

            // Nếu mọi thứ thành công, commit transaction
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật Purchase Order, đang rollback transaction...");
            JDBCUtils.printSQLException(e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    JDBCUtils.printSQLException(ex);
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    JDBCUtils.printSQLException(e);
                }
            }
        }
    }

}
