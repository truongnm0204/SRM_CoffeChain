/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.User;
import dao.UserDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Service xử lý logic đăng nhập và xác thực
 *
 * @author fptshop
 */
public class LoginServices {

    private UserDAO userDAO;

    public LoginServices() {
        this.userDAO = new UserDAO();
    }

    /**
     * Đăng nhập với username và password
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu thuần
     * @return List<String>: [0]=success/error, [1]=message, [2]=token (nếu
     * thành công), [3]=userId, [4]=userRole
     */
    public List<String> login(String email, String password) {
        List<String> result = new ArrayList<>();

        // Validate input
        if (StringUtils.isNullOrEmpty(email) || StringUtils.isNullOrEmpty(password)) {
            result.add("error");
            result.add("Tên đăng nhập và mật khẩu không được để trống");
            return result;
        }

        // Hash password để so sánh với database
        String hashedPassword = StringUtils.hashPassword(password);

        // Xác thực với database
        User user = userDAO.authenticateUser(email, hashedPassword);

        if (user == null) {
            result.add("error");
            result.add("Tên đăng nhập hoặc mật khẩu không đúng");
            return result;
        }

        // Kiểm tra trạng thái tài khoản
        if (!"Active".equals(user.getStatus())) {
            result.add("error");
            result.add("Tài khoản đã bị khóa hoặc vô hiệu hóa");
            return result;
        }

        // Tạo JWT token
        String userId = user.getUserID() != null ? user.getUserID().toString() : "";
        String token = JWTUtils.createToken(userId, user.getUserName(), "user");

        if (StringUtils.isNullOrEmpty(token)) {
            result.add("error");
            result.add("Lỗi tạo token xác thực");
            return result;
        }

        result.add("success");
        result.add("Đăng nhập thành công");
        result.add(token);
        result.add(userId);
        result.add(user.getRoleSettingName()); // role mặc định

        return result;
    }

    /**
     * Đăng ký tài khoản mới
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu thuần
     * @param email Email
     * @param fullName Họ tên
     * @return List<String>: [0]=success/error, [1]=message
     */
    public List<String> register(String username, String password, String email, String fullName) {
        List<String> result = new ArrayList<>();

        // Validate input
        if (!StringUtils.areAllNotNullAndNotEmpty(username, password, email, fullName)) {
            result.add("error");
            result.add("Tất cả các trường đều bắt buộc");
            return result;
        }

        // Validate format
        if (!StringUtils.isValidEmail(email)) {
            result.add("error");
            result.add("Định dạng email không hợp lệ");
            return result;
        }

        if (!StringUtils.isValidPassword(password)) {
            result.add("error");
            result.add("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, thường và số");
            return result;
        }

        // Kiểm tra username đã tồn tại
        if (userDAO.isUsernameExists(username)) {
            result.add("error");
            result.add("Tên đăng nhập đã tồn tại");
            return result;
        }

        // Kiểm tra email đã tồn tại
        if (userDAO.isEmailExists(email)) {
            result.add("error");
            result.add("Email đã được sử dụng");
            return result;
        }

        // Hash password
        String hashedPassword = StringUtils.hashPassword(password);

        // Tạo user mới
        User newUser = new User(username, email, hashedPassword, fullName);

        // Lưu vào database
        boolean created = userDAO.createUser(newUser);

        if (created) {
            result.add("success");
            result.add("Đăng ký thành công");
        } else {
            result.add("error");
            result.add("Lỗi khi tạo tài khoản");
        }

        return result;
    }

    /**
     * Xác thực token JWT
     *
     * @param token JWT token
     * @return List<String>: Success=[0]=success, [1]=userId, [2]=username,
     * [3]=role; Error=[0]=error, [1]=message
     */
    public List<String> validateToken(String token) {
        List<String> result = new ArrayList<>();

        if (StringUtils.isNullOrEmpty(token)) {
            result.add("error");
            result.add("Token không được để trống");
            return result;
        }

        try {
            // Verify token
            if (!JWTUtils.validateToken(token)) {
                result.add("error");
                result.add("Token không hợp lệ hoặc đã hết hạn");
                return result;
            }

            // Lấy thông tin user từ token
            String userId = JWTUtils.getUserId(token);
            String username = JWTUtils.getUsername(token);
            String role = JWTUtils.getRole(token);

            if (StringUtils.isNullOrEmpty(userId) || StringUtils.isNullOrEmpty(username)) {
                result.add("error");
                result.add("Token không chứa thông tin user hợp lệ");
                return result;
            }

            // Verify user exists in database
            User user = userDAO.findByUserId(userId);
            if (user == null) {
                result.add("error");
                result.add("Không tìm thấy người dùng");
                return result;
            }

            result.add("success");
            result.add(userId);
            result.add(username);
            result.add(role != null ? role : "user");

            return result;

        } catch (Exception e) {
            result.add("error");
            result.add("Lỗi xác thực token: " + e.getMessage());
            return result;
        }
    }

    /**
     * Đổi mật khẩu
     *
     * @param userId ID người dùng
     * @param oldPassword Mật khẩu cũ
     * @param newPassword Mật khẩu mới
     * @return List<String>: [0]=success/error, [1]=message
     */
    public List<String> changePassword(String userId, String oldPassword, String newPassword) {
        List<String> result = new ArrayList<>();

        if (!StringUtils.areAllNotNullAndNotEmpty(userId, oldPassword, newPassword)) {
            result.add("error");
            result.add("Tất cả các trường đều bắt buộc");
            return result;
        }

        if (!StringUtils.isValidPassword(newPassword)) {
            result.add("error");
            result.add("Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm chữ hoa, thường và số");
            return result;
        }

        // Lấy thông tin user hiện tại
        User user = userDAO.findByUserId(userId);
        if (user == null) {
            result.add("error");
            result.add("Không tìm thấy người dùng");
            return result;
        }

        // Verify mật khẩu cũ
        if (!StringUtils.verifyPassword(oldPassword, user.getPasswordHash())) {
            result.add("error");
            result.add("Mật khẩu cũ không đúng");
            return result;
        }

        // Hash mật khẩu mới
        String hashedNewPassword = StringUtils.hashPassword(newPassword);

        // Cập nhật trong database
        boolean updated = userDAO.changePassword(userId, hashedNewPassword);

        if (updated) {
            result.add("success");
            result.add("Đổi mật khẩu thành công");
        } else {
            result.add("error");
            result.add("Lỗi khi cập nhật mật khẩu");
        }

        return result;
    }

    /**
     * Refresh JWT token
     *
     * @param token Token cũ
     * @return List<String>: Success=[0]=success, [1]=newToken; Error=[0]=error,
     * [1]=message
     */
    public List<String> refreshToken(String token) {
        List<String> result = new ArrayList<>();

        if (StringUtils.isNullOrEmpty(token)) {
            result.add("error");
            result.add("Token không được để trống");
            return result;
        }

        try {
            String newToken = JWTUtils.refreshToken(token);
            if (newToken != null) {
                result.add("success");
                result.add(newToken);
            } else {
                result.add("error");
                result.add("Không thể refresh token");
            }
        } catch (Exception e) {
            result.add("error");
            result.add("Lỗi khi refresh token: " + e.getMessage());
        }

        return result;
    }

    public boolean checkEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) {
            return false;
        }
        return userDAO.checkExistEmail(email);
    }
}
