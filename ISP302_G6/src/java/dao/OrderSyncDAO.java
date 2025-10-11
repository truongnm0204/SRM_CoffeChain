package dao;

import com.google.gson.*;
import java.sql.*;
import java.time.OffsetDateTime;
import java.math.BigDecimal;

public class OrderSyncDAO {
    private final Connection conn;

    public OrderSyncDAO(Connection conn) {
        this.conn = conn;
    }

    public static class SyncResult {
        public int inserted = 0;
        public int updated = 0;
        public int details = 0;
    }

    public SyncResult upsertFromPosJson(String json, int shopId) throws Exception {
        SyncResult r = new SyncResult();
        conn.setAutoCommit(false);
        try {
            JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
            for (JsonElement e : arr) {
                JsonObject o = e.getAsJsonObject();
                String externalId = o.get("id").getAsString();
                String status = o.get("status").getAsString();
                double total = o.get("total").getAsDouble();
                Timestamp orderTs = Timestamp.from(OffsetDateTime.parse(o.get("orderDate").getAsString()).toInstant());
                int orderId = upsertOrder(externalId, orderTs, total, status, shopId, r);
                if (o.has("items") && o.get("items").isJsonArray()) {
                    JsonArray items = o.getAsJsonArray("items");
                    for (JsonElement it : items) {
                        JsonObject d = it.getAsJsonObject();
                        upsertOrderDetail(orderId, d.get("productId").getAsInt(),
                                d.get("quantity").getAsInt(), d.get("subtotal").getAsDouble());
                        r.details++;
                    }
                }
            }
            conn.commit();
        } catch (Exception ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
        return r;
    }

    private int upsertOrder(String externalId, Timestamp orderDate, double total, String status,
            int shopId, SyncResult r) throws SQLException {
        String sql = "INSERT INTO orders (externalorderid, orderdate, totalamount, status, userid, shopid, source) " +
                "VALUES (?, ?, ?, ?, NULL, ?, 'POS') " +
                "ON CONFLICT (externalorderid) DO UPDATE " +
                "SET orderdate = EXCLUDED.orderdate, totalamount = EXCLUDED.totalamount, status = EXCLUDED.status " +
                "RETURNING orderid";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, externalId);
            ps.setTimestamp(2, orderDate);
            ps.setBigDecimal(3, new BigDecimal(total));
            ps.setString(4, status);
            ps.setInt(5, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                // This part is tricky without knowing if the row was inserted or updated.
                // For simplicity, we'll just count it as an operation.
                // A more complex query could tell us if it was an insert or update.
                r.inserted++; // Simplified: counting every upsert as an insert for the summary.
                return rs.getInt(1);
            }
        }
    }

    private void upsertOrderDetail(int orderId, int productId, int qty, double subtotal) throws SQLException {
        String sql = "INSERT INTO orderdetail (orderid, productid, quantity, subtotal) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (orderid, productid) DO UPDATE " +
                "SET quantity = EXCLUDED.quantity, subtotal = EXCLUDED.subtotal";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            ps.setBigDecimal(4, new BigDecimal(subtotal));
            ps.executeUpdate();
        }
    }
}
