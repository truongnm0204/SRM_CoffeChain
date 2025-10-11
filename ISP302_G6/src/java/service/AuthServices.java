package service;

import dao.PermissionDAO;
import model.Permission;
import java.util.List;
import java.util.Collections;

/**
 * Service xử lý xác thực và lấy quyền (permissions) cho user
 */
public class AuthServices {
    private final PermissionDAO permissionDAO;

    public AuthServices() {
        this.permissionDAO = new PermissionDAO();
    }

    /**
     * Lấy danh sách quyền cho user theo userId (string)
     * @param userIdStr ID người dùng dưới dạng String
     * @return List<Permission> (rỗng nếu lỗi hoặc không có quyền)
     */
    public List<Permission> getPermissionsForUser(String userIdStr) {
        if (userIdStr == null || userIdStr.trim().isEmpty()) return Collections.emptyList();
        try {
            int userId = Integer.parseInt(userIdStr);
            return permissionDAO.getPermissionsByUserId(userId);
        } catch (NumberFormatException e) {
            System.err.println("AuthServices.getPermissionsForUser: invalid userId=" + userIdStr);
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("AuthServices.getPermissionsForUser error: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public static void main(String[] args) {
        AuthServices auth = new AuthServices();
        List<Permission> per = auth.getPermissionsForUser("1");
        System.out.println(per.size());
    }

    
}
        
