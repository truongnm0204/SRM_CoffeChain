package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Permission;
import util.JDBCUtils;

/**
 * DAO cho Permission và RolePermission
 */
public class PermissionDAO {

    public List<Permission> getAllPermissions() {
        List<Permission> list = new ArrayList<>();
        String sql = "SELECT p.permissionid, p.permissionname, p.permission_path AS permissionpath, p.description "
                   + "FROM permission p ORDER BY p.permissionid";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPermission(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getAllPermissions: " + e.getMessage());
        }
        return list;
    }

    public Permission findById(int id) {
        String sql = "SELECT p.permissionid, p.permissionname, p.permission_path AS permissionpath, p.description "
                   + "FROM permission p WHERE p.permissionid = ?";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToPermission(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi findById Permission: " + e.getMessage());
        }
        return null;
    }

    public Permission findByName(String name) {
        String sql = "SELECT p.permissionid, p.permissionname, p.permission_path AS permissionpath, p.description "
                   + "FROM permission p WHERE p.permissionname = ?";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapResultSetToPermission(rs);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi findByName Permission: " + e.getMessage());
        }
        return null;
    }

    public List<Permission> getPermissionsByRoleSettingId(int roleSettingId) {
        List<Permission> list = new ArrayList<>();
        String sql = "SELECT p.permissionid, p.permissionname, p.permission_path AS permissionpath, p.description "
                   + "FROM permission p "
                   + "JOIN rolepermission rp ON rp.permissionid = p.permissionid "
                   + "WHERE rp.rolesettingid = ? ORDER BY p.permissionid";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleSettingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPermission(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getPermissionsByRoleSettingId: " + e.getMessage());
        }
        return list;
    }

    public List<Permission> getPermissionsByRoleName(String roleName) {
        List<Permission> list = new ArrayList<>();
        String sql = "SELECT p.permissionid, p.permissionname, p.permission_path AS permissionpath, p.description "
                   + "FROM permission p "
                   + "JOIN rolepermission rp ON rp.permissionid = p.permissionid "
                   + "JOIN setting s ON s.settingid = rp.rolesettingid "
                   + "WHERE s.settingvalue = ? ORDER BY p.permissionid";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPermission(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi getPermissionsByRoleName: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy toàn bộ quyền của 1 user theo userId bằng cách lấy RoleSettingID của user
     * và join qua bảng rolepermission -> permission.
     *
     * @param userId ID người dùng (integer)
     * @return List<Permission> (rỗng nếu không có quyền)
     */
    public List<Permission> getPermissionsByUserId(int userId) {
        List<Permission> list = new ArrayList<>();
        String sql = "SELECT p.permissionid, p.permissionname, p.permission_path AS permissionpath, p.description "
                   + "FROM permission p "
                   + "JOIN rolepermission rp ON rp.permissionid = p.permissionid "
                   + "JOIN \"user\" u ON u.rolesettingid = rp.rolesettingid "
                   + "WHERE u.userid = ? ORDER BY p.permissionid";

        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPermission(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi getPermissionsByUserId: " + e.getMessage());
        }

        return list;
    }

    public boolean assignPermissionToRole(int roleSettingId, int permissionId) {
        String sql = "INSERT INTO rolepermission (rolesettingid, permissionid) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleSettingId);
            ps.setInt(2, permissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi assignPermissionToRole: " + e.getMessage());
            return false;
        }
    }

    public boolean removePermissionFromRole(int roleSettingId, int permissionId) {
        String sql = "DELETE FROM rolepermission WHERE rolesettingid = ? AND permissionid = ?";
        try (Connection conn = JDBCUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleSettingId);
            ps.setInt(2, permissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi removePermissionFromRole: " + e.getMessage());
            return false;
        }
    }

    private Permission mapResultSetToPermission(ResultSet rs) throws SQLException {
        Permission p = new Permission();
        p.setPermissionID(rs.getInt("permissionid"));
        p.setPermissionName(rs.getString("permissionname"));
        // alias là permissionpath
        p.setPermissionPath(rs.getString("permissionpath"));
        p.setDescription(rs.getString("description"));
        return p;
    }
}
