package dao;


import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.JDBCUtils;
import util.StringUtils;

/**
 * Data Access Object cho User - xử lý các thao tác database liên quan đến người
 * dùng
 */
public class UserDAO {

    /**
     * Xác thực người dùng bằng username và password
     *
     * @param email    Tên đăng nhập
     * @param password Mật khẩu (đã hash)
     * @return User object nếu xác thực thành công, null nếu thất bại
     */
    public User authenticateUser(String email, String password) {
        if (StringUtils.isNullOrEmpty(email) || StringUtils.isNullOrEmpty(password)) {
            return null;
        }

        // Đã hợp nhất: Dùng tên cột viết thường
        String sql = "SELECT u.userid, u.username, u.email, u.passwordhash, u.fullname, "
                + "u.phonenumber, u.address, u.status, u.rolesettingid, u.shopid, "
                + "s.settingvalue AS \"rolename\" "
                + "FROM \"user\" u "
                + "JOIN setting s ON s.settingid = u.rolesettingid "
                + "WHERE u.email = ? AND u.passwordhash = ? AND u.status = 'Active'";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi xác thực người dùng: " + e.getMessage());
        }

        return null;
    }

    /**
     * Tìm người dùng theo username
     *
     * @param username Tên đăng nhập
     * @return User object hoặc null nếu không tìm thấy
     */
    public User findByUsername(String username) {
        if (StringUtils.isNullOrEmpty(username)) {
            return null;
        }

        // Đã hợp nhất: Dùng tên cột viết thường
        String sql = "SELECT userid, username, email, passwordhash, fullname, "
                + "phonenumber, address, status, rolesettingid, shopid "
                + "FROM \"user\" WHERE username = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng theo username: " + e.getMessage());
        }

        return null;
    }

    /**
     * Tìm người dùng theo user ID
     *
     * @param userId ID người dùng
     * @return User object hoặc null nếu không tìm thấy
     */
    public User findByUserId(String userId) {
        if (StringUtils.isNullOrEmpty(userId)) {
            return null;
        }

        try {
            Integer userIdInt = Integer.parseInt(userId);
            return findByUserId(userIdInt);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Tìm người dùng theo user ID (Integer)
     *
     * @param userId ID người dùng
     * @return User object hoặc null nếu không tìm thấy
     */
    public User findByUserId(Integer userId) {
        if (userId == null) {
            return null;
        }

        // Đã hợp nhất: Dùng tên cột viết thường
        String sql = "SELECT userid, username, email, passwordhash, fullname, "
                + "phonenumber, address, status, rolesettingid, shopid "
                + "FROM \"user\" WHERE userid = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng theo ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Tìm người dùng theo email
     *
     * @param email Email
     * @return User object hoặc null nếu không tìm thấy
     */
    public User findByEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) {
            return null;
        }

        // Cần thống nhất cột SQL: dùng các cột chuẩn của bảng "User"
        // Dựa trên các hàm khác, tôi điều chỉnh lại SQL cho phù hợp với schema "User"
        String sql = "SELECT userid, username, email, passwordhash, fullname, phonenumber, status, rolesettingid, shopid "
                + "FROM \"user\" WHERE email = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm người dùng theo email: " + e.getMessage());
        }

        return null;
    }

    /**
     * Tạo người dùng mới
     *
     * @param user Thông tin người dùng
     * @return true nếu tạo thành công
     */
    public boolean createUser(User user) {
        if (user == null || StringUtils.isNullOrEmpty(user.getUserName())
                || StringUtils.isNullOrEmpty(user.getPasswordHash())) {
            return false;
        }

        // Đã hợp nhất: Dùng tên cột viết thường
        String sql = "INSERT INTO \"user\" (username, email, passwordhash, fullname, "
                + "phonenumber, address, status, rolesettingid, shopid) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getAddress());
            ps.setString(7, user.getStatus() != null ? user.getStatus() : "Active");

            if (user.getRoleSettingID() != null) {
                ps.setInt(8, user.getRoleSettingID());
            } else {
                ps.setNull(8, java.sql.Types.INTEGER);
            }

            if (user.getShopID() != null) {
                ps.setInt(9, user.getShopID());
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo người dùng: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin người dùng
     *
     * @param user Thông tin người dùng
     * @return true nếu cập nhật thành công
     */
    public boolean updateUser(User user) {
        if (user == null || StringUtils.isNullOrEmpty(user.getUserId())) {
            return false;
        }

        // Chú ý: Dùng schema cột của bảng "User" (PostgreSQL) thay vì "users"
        // (MySQL/chuẩn)
        String sql = "UPDATE \"user\" SET email = ?, fullname = ?, phonenumber = ?, "
                + "status = ?, rolesettingid = ?, shopid = ? WHERE userid = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getStatus());

            // Xử lý RoleSettingID
            if (user.getRoleSettingID() != null) {
                ps.setInt(5, user.getRoleSettingID());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            // Xử lý ShopID
            if (user.getShopID() != null) {
                ps.setInt(6, user.getShopID());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            // Chú ý: Cần chuyển userId từ String sang Integer nếu cột userid là Integer
            try {
                ps.setInt(7, Integer.parseInt(user.getUserId()));
            } catch (NumberFormatException e) {
                System.err.println("UserID không hợp lệ: " + user.getUserId());
                return false;
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật người dùng: " + e.getMessage());
            return false;
        }
    }

    /**
     * Thay đổi mật khẩu người dùng
     *
     * @param userId            ID người dùng
     * @param newHashedPassword Mật khẩu mới đã hash
     * @return true nếu thành công
     */
    public boolean changePassword(String userId, String newHashedPassword) {
        if (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(newHashedPassword)) {
            return false;
        }

        // Chú ý: Dùng cột "PasswordHash" trong bảng "User" thay vì "password" trong
        // bảng "users"
        String sql = "UPDATE \"user\" SET passwordhash = ? WHERE userid = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHashedPassword);

            // Chú ý: Cần chuyển userId từ String sang Integer nếu cột userid là Integer
            try {
                ps.setInt(2, Integer.parseInt(userId));
            } catch (NumberFormatException e) {
                System.err.println("UserID không hợp lệ: " + userId);
                return false;
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi thay đổi mật khẩu: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra username đã tồn tại chưa
     *
     * @param username Tên đăng nhập
     * @return true nếu đã tồn tại
     */
    public boolean isUsernameExists(String username) {
        if (StringUtils.isNullOrEmpty(username)) {
            return false;
        }

        // Đã hợp nhất: Dùng tên cột viết thường
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE username = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra username: " + e.getMessage());
        }

        return false;
    }

    /**
     * Kiểm tra email đã tồn tại chưa
     *
     * @param email Email
     * @return true nếu đã tồn tại
     */
    public boolean isEmailExists(String email) {
        if (StringUtils.isNullOrEmpty(email)) {
            return false;
        }

        // Đã hợp nhất: Dùng tên cột viết thường
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE email = ?";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra email: " + e.getMessage());
        }

        return false;
    }

    /**
     * Lấy danh sách tất cả người dùng
     *
     * @return List User
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        // Cần thống nhất cột SQL: dùng các cột chuẩn của bảng "User"
        String sql = "SELECT userid, username, email, passwordhash, fullname, "
                + "phonenumber, address, status, rolesettingid, shopid FROM \"user\"";

        try (Connection conn = JDBCUtils.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách người dùng: " + e.getMessage());
        }

        return users;
    }

    /**
     * Map ResultSet thành User object
     *
     * @param rs ResultSet
     * @return User object
     * @throws SQLException
     */
    public boolean checkExistEmail(String email) {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE email = ?";
        int row = 0;
        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email); // ✅ Gán tham số trước khi thực thi
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    row = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra email tồn tại: " + e.getMessage());
            return false;
        }

        return row == 1; // ✅ nên kiểm tra > 0 thay vì == 1
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        try {
            // Đã hợp nhất: Dùng tên cột viết thường (vì SQL đã dùng chữ thường)
            user.setUserID(rs.getInt("userid"));
            user.setUserName(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("passwordhash"));
            user.setFullName(rs.getString("fullname"));
            user.setPhoneNumber(rs.getString("phonenumber"));
            user.setAddress(rs.getString("address"));
            user.setStatus(rs.getString("status"));

            // Lấy role name nếu query có trả về alias 'rolename'
            String roleName = rs.getString("rolename");
            if (roleName != null && !roleName.trim().isEmpty()) {
                user.setRoleSettingName(roleName);

                // Handle nullable foreign keys
                // Đã hợp nhất: Dùng tên cột viết thường
                int roleSettingID = rs.getInt("rolesettingid");
                if (!rs.wasNull()) {
                    user.setRoleSettingID(roleSettingID);
                }

                // Đã hợp nhất: Dùng tên cột viết thường
                int shopID = rs.getInt("shopid");
                if (!rs.wasNull()) {
                    user.setShopID(shopID);
                }
            }
        } catch (SQLException e) {
            // Nếu có lỗi khi đọc metadata/column thì bỏ qua việc set roleName
        }
        return user;
    }

}
