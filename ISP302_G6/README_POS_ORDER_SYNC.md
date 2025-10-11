# 🔄 POS → Warehouse Integration: Fetch Orders and Sync to Database

## 🧩 1️⃣ Mục tiêu
Tích hợp **POS Server (JSON Server + JWT)** với **Web Quản lý Kho (JSP/Servlet + PostgreSQL)**.  
Mục tiêu: tự động lấy đơn hàng từ POS, xác thực bằng JWT, và ghi vào cơ sở dữ liệu của hệ thống kho.

---

## ⚙️ 2️⃣ Chuẩn bị cơ sở dữ liệu

### 2.1 Thêm cấu hình POS cho từng shop
```sql
ALTER TABLE shop 
  ADD COLUMN apiendpoint VARCHAR(255),
  ADD COLUMN pos_auth_url VARCHAR(255),
  ADD COLUMN client_id VARCHAR(100),
  ADD COLUMN client_secret VARCHAR(200),
  ADD COLUMN pos_token TEXT,
  ADD COLUMN pos_token_exp TIMESTAMP;

UPDATE shop
SET apiendpoint   = 'http://localhost:4000/orders',
    pos_auth_url  = 'http://localhost:4000/auth/login',
    client_id     = 'warehouse_system',
    client_secret = 'abc123'
WHERE shopid = 1;
```

### 2.2 Thêm khóa liên kết để tránh ghi trùng đơn POS
```sql
ALTER TABLE orders ADD COLUMN externalorderid VARCHAR(100) UNIQUE;
ALTER TABLE orders ADD COLUMN source VARCHAR(20) DEFAULT 'POS';
```

---

## 📦 3️⃣ JSON từ POS
`GET /orders` trả về:
```json
[
  {
    "id": 1,
    "shopId": 1,
    "status": "Completed",
    "total": 95000,
    "orderDate": "2025-10-08T08:30:00",
    "items": [
      { "productId": 1, "quantity": 1, "subtotal": 45000 },
      { "productId": 2, "quantity": 1, "subtotal": 40000 }
    ]
  }
]
```

---

## 🧠 4️⃣ Thư viện cần thiết
- `gson-2.10.1.jar` – để parse JSON  
- JDK 11+ – dùng `HttpClient`

---

## 🔑 5️⃣ Tạo tiện ích gọi POS (login + fetch)

### 📄 `PosClient.java`
```java
package util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class PosClient {
    public static class PosToken {
        public final String token;
        public final Instant expiresAt;
        public PosToken(String token, Instant exp) { this.token = token; this.expiresAt = exp; }
    }
    private static final HttpClient http = HttpClient.newHttpClient();

    public static PosToken login(String authUrl, String clientId, String clientSecret) throws Exception {
        String body = "{\"client_id\":\"" + clientId + "\",\"client_secret\":\"" + clientSecret + "\"}";
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(authUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new RuntimeException("POS login failed: HTTP " + res.statusCode());
        JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
        return new PosToken(json.get("token").getAsString(), Instant.now().plusSeconds(55 * 60));
    }

    public static String fetchOrders(String ordersUrl, String bearerToken) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(ordersUrl))
                .header("Authorization", "Bearer " + bearerToken)
                .GET()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200)
            throw new RuntimeException("Fetch orders failed: HTTP " + res.statusCode());
        return res.body();
    }
}
```

---

## 🧩 6️⃣ DAO xử lý ghi dữ liệu

### 📄 `OrderSyncDAO.java`
```java
package dao;

import com.google.gson.*;
import java.sql.*;
import java.time.OffsetDateTime;

public class OrderSyncDAO {
    private final Connection conn;
    public OrderSyncDAO(Connection conn) { this.conn = conn; }

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
                JsonArray items = o.getAsJsonArray("items");
                for (JsonElement it : items) {
                    JsonObject d = it.getAsJsonObject();
                    upsertOrderDetail(orderId, d.get("productId").getAsInt(),
                            d.get("quantity").getAsInt(), d.get("subtotal").getAsDouble());
                    r.details++;
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
            ps.setBigDecimal(3, new java.math.BigDecimal(total));
            ps.setString(4, status);
            ps.setInt(5, shopId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                r.inserted++;
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
            ps.setBigDecimal(4, new java.math.BigDecimal(subtotal));
            ps.executeUpdate();
        }
    }
}
```

---

## 🧩 7️⃣ Servlet đồng bộ

### 📄 `SyncPosOrdersServlet.java`
```java
package controller.api;

import util.DBContext;
import util.PosClient;
import dao.OrderSyncDAO;
import java.io.*;
import java.sql.*;
import java.time.Instant;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class SyncPosOrdersServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("authUser") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn chưa đăng nhập");
            return;
        }

        int shopId = Integer.parseInt(req.getParameter("shopId"));

        try (Connection conn = new DBContext().getConnection()) {

            String authUrl=null, ordersUrl=null, clientId=null, clientSecret=null, cachedToken=null;
            Instant tokenExp = null;

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT pos_auth_url, apiendpoint, client_id, client_secret, pos_token, pos_token_exp FROM shop WHERE shopid=?")) {
                ps.setInt(1, shopId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        authUrl = rs.getString(1);
                        ordersUrl = rs.getString(2);
                        clientId = rs.getString(3);
                        clientSecret = rs.getString(4);
                        cachedToken = rs.getString(5);
                        Timestamp t = rs.getTimestamp(6);
                        tokenExp = (t==null)? null : t.toInstant();
                    }
                }
            }

            if (authUrl==null || ordersUrl==null) {
                resp.sendError(400, "Shop chưa cấu hình POS endpoint");
                return;
            }

            String token = cachedToken;
            boolean needLogin = (token == null) || (tokenExp == null) || Instant.now().isAfter(tokenExp.minusSeconds(60));
            if (needLogin) {
                PosClient.PosToken tk = PosClient.login(authUrl, clientId, clientSecret);
                token = tk.token;
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE shop SET pos_token=?, pos_token_exp=? WHERE shopid=?")) {
                    ps.setString(1, token);
                    ps.setTimestamp(2, Timestamp.from(tk.expiresAt));
                    ps.setInt(3, shopId);
                    ps.executeUpdate();
                }
            }

            String jsonOrders = PosClient.fetchOrders(ordersUrl, token);
            OrderSyncDAO dao = new OrderSyncDAO(conn);
            OrderSyncDAO.SyncResult r = dao.upsertFromPosJson(jsonOrders, shopId);

            resp.setContentType("application/json; charset=UTF-8");
            PrintWriter out = resp.getWriter();
            out.printf("{\"inserted\":%d,\"updated\":%d,\"details\":%d}", r.inserted, r.updated, r.details);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "Lỗi sync: " + e.getMessage());
        }
    }
}
```

---

## ✅ 8️⃣ Cách test

### PowerShell
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/app/api/sync-pos-orders?shopId=1" -Method POST
```

### JS / Frontend
```js
fetch('/app/api/sync-pos-orders?shopId=1', { method: 'POST' })
  .then(r => r.json())
  .then(j => alert(`Đồng bộ POS: +${j.inserted}, cập nhật ${j.updated}`));
```

---

**Tác giả:**  
> 🧠 _Integration Guide by Đỗ Đường Bách_  
> 📅 Cập nhật: 2025-10-11  
> 🚀 Phiên bản: 1.1
