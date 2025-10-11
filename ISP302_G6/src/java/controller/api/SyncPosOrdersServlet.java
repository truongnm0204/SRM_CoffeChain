package controller.api;

import dao.OrderSyncDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import util.JDBCUtils;
import util.PosClient;

@WebServlet(name = "SyncPosOrdersServlet", urlPatterns = { "/api/sync-pos-orders" })
public class SyncPosOrdersServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        // Check login status based on existing project convention
        if (session == null || session.getAttribute("isLoggedIn") == null
                || !((Boolean) session.getAttribute("isLoggedIn"))) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn chưa đăng nhập hoặc phiên đã hết hạn.");
            return;
        }

        String shopIdStr = req.getParameter("shopId");
        if (shopIdStr == null || shopIdStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu tham số 'shopId'.");
            return;
        }
        int shopId = Integer.parseInt(shopIdStr);

        try (Connection conn = JDBCUtils.getConnection()) {

            String authUrl = null, ordersUrl = null, clientId = null, clientSecret = null, cachedToken = null;
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
                        tokenExp = (t == null) ? null : t.toInstant();
                    }
                }
            }

            if (authUrl == null || ordersUrl == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Shop chưa được cấu hình POS endpoint.");
                return;
            }

            String token = cachedToken;
            boolean needLogin = (token == null) || (tokenExp == null)
                    || Instant.now().isAfter(tokenExp.minusSeconds(60));
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
            try (PrintWriter out = resp.getWriter()) {
                out.printf("{\"inserted\":%d,\"updated\":%d,\"details\":%d}", r.inserted, r.updated, r.details);
                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi server khi đồng bộ: " + e.getMessage());
        }
    }
}
