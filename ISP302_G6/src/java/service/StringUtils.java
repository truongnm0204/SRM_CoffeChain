/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * Lớp tiện ích cho các thao tác với chuỗi
 */
public class StringUtils {
    
    // Các mẫu regex để kiểm tra định dạng
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+84|0)[0-9]{9,10}$"
    );
    
    // Yêu cầu mật khẩu: ít nhất 8 ký tự, có ít nhất 1 chữ số, 1 chữ thường, 1 chữ hoa
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
    );
    
    /**
     * Kiểm tra chuỗi có null hoặc rỗng không
     * @param str Chuỗi cần kiểm tra
     * @return true nếu chuỗi null hoặc rỗng
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Kiểm tra chuỗi không null và không rỗng
     * @param str Chuỗi cần kiểm tra
     * @return true nếu chuỗi không null và không rỗng
     */
    public static boolean isNotNullAndNotEmpty(String str) {
        return !isNullOrEmpty(str);
    }
    
    /**
     * Kiểm tra tất cả chuỗi đều không null và không rỗng
     * @param strings Các chuỗi cần kiểm tra
     * @return true nếu tất cả chuỗi đều không null và không rỗng
     */
    public static boolean areAllNotNullAndNotEmpty(String... strings) {
        if (strings == null) return false;
        for (String str : strings) {
            if (isNullOrEmpty(str)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Kiểm tra mật khẩu có đáp ứng yêu cầu bảo mật không
     * @param password Mật khẩu cần kiểm tra
     * @return true nếu mật khẩu hợp lệ
     */
    public static boolean isValidPassword(String password) {
        if (isNullOrEmpty(password)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Kiểm tra định dạng email có hợp lệ không
     * @param email Email cần kiểm tra
     * @return true nếu định dạng email hợp lệ
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Kiểm tra định dạng số điện thoại có hợp lệ không (định dạng Việt Nam)
     * @param phone Số điện thoại cần kiểm tra
     * @return true nếu định dạng số điện thoại hợp lệ
     */
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Kiểm tra một chuỗi có tồn tại trong mảng chuỗi không (phân biệt hoa thường)
     * @param target Chuỗi cần tìm
     * @param array Mảng để tìm kiếm
     * @return true nếu chuỗi tồn tại trong mảng
     */
    public static boolean contains(String target, String[] array) {
        if (target == null || array == null) {
            return false;
        }
        for (String str : array) {
            if (target.equals(str)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Kiểm tra một chuỗi có tồn tại trong mảng chuỗi không (không phân biệt hoa thường)
     * @param target Chuỗi cần tìm
     * @param array Mảng để tìm kiếm
     * @return true nếu chuỗi tồn tại trong mảng (không phân biệt hoa thường)
     */
    public static boolean containsIgnoreCase(String target, String[] array) {
        if (target == null || array == null) {
            return false;
        }
        for (String str : array) {
            if (str != null && target.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Mã hóa mật khẩu sử dụng SHA-256
     * @param password Mật khẩu văn bản thuần
     * @return Mật khẩu đã mã hóa SHA-256 dạng thập lục phân
     */
    public static String hashPassword(String password) {
        if (isNullOrEmpty(password)) {
            return null;
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            // Chuyển đổi mảng byte thành chuỗi thập lục phân
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Thuật toán SHA-256 không khả dụng", e);
        }
    }
    public static void main(String[] args) {
        
        System.out.println(StringUtils.hashPassword("Son@1234"));
    }
    
    /**
     * Mã hóa mật khẩu với salt sử dụng SHA-256
     * @param password Mật khẩu văn bản thuần
     * @param salt Salt để thêm vào mật khẩu
     * @return Mật khẩu đã mã hóa SHA-256 với salt
     */
    public static String hashPasswordWithSalt(String password, String salt) {
        if (isNullOrEmpty(password)) {
            return null;
        }
        
        String saltedPassword = password + (salt != null ? salt : "");
        return hashPassword(saltedPassword);
    }
    
    /**
     * Xác minh mật khẩu với hash
     * @param password Mật khẩu văn bản thuần
     * @param hash Mật khẩu đã mã hóa để so sánh
     * @return true nếu mật khẩu khớp với hash
     */
    public static boolean verifyPassword(String password, String hash) {
        if (isNullOrEmpty(password) || isNullOrEmpty(hash)) {
            return false;
        }
        
        String hashedInput = hashPassword(password);
        return hash.equals(hashedInput);
    }
    
    /**
     * Xác minh mật khẩu với hash và salt
     * @param password Mật khẩu văn bản thuần
     * @param hash Mật khẩu đã mã hóa để so sánh
     * @param salt Salt được sử dụng trong hash gốc
     * @return true nếu mật khẩu khớp với hash
     */
    public static boolean verifyPasswordWithSalt(String password, String hash, String salt) {
        if (isNullOrEmpty(password) || isNullOrEmpty(hash)) {
            return false;
        }
        
        String hashedInput = hashPasswordWithSalt(password, salt);
        return hash.equals(hashedInput);
    }
    
    /**
     * Tạo salt ngẫu nhiên để mã hóa mật khẩu
     * @param length Độ dài của salt
     * @return Chuỗi salt ngẫu nhiên
     */
    public static String generateSalt(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder salt = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            salt.append(chars.charAt(index));
        }
        
        return salt.toString();
    }
    
    /**
     * Viết hoa chữ cái đầu tiên của chuỗi
     * @param str Chuỗi cần viết hoa
     * @return Chuỗi với chữ cái đầu được viết hoa
     */
    public static String capitalize(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        
        if (str.length() == 1) {
            return str.toUpperCase();
        }
        
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    /**
     * Cắt ngắn chuỗi đến độ dài chỉ định với dấu ba chấm
     * @param str Chuỗi cần cắt ngắn
     * @param maxLength Độ dài tối đa
     * @return Chuỗi đã cắt ngắn với "..." nếu cần
     */
    public static String truncate(String str, int maxLength) {
        if (isNullOrEmpty(str) || str.length() <= maxLength) {
            return str;
        }
        
        if (maxLength <= 3) {
            return str.substring(0, maxLength);
        }
        
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Xóa tất cả khoảng trắng khỏi chuỗi
     * @param str Chuỗi cần xử lý
     * @return Chuỗi không có khoảng trắng
     */
    public static String removeWhitespace(String str) {
        if (isNullOrEmpty(str)) {
            return str;
        }
        return str.replaceAll("\\s+", "");
    }
    
    /**
     * Kiểm tra chuỗi chỉ chứa chữ số
     * @param str Chuỗi cần kiểm tra
     * @return true nếu chuỗi chỉ chứa chữ số
     */
    public static boolean isNumeric(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        return str.matches("\\d+");
    }
    
    /**
     * Kiểm tra chuỗi chỉ chứa ký tự chữ cái
     * @param str Chuỗi cần kiểm tra
     * @return true nếu chuỗi chỉ chứa chữ cái
     */
    public static boolean isAlphabetic(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        return str.matches("[a-zA-Z]+");
    }
    
    /**
     * Kiểm tra chuỗi chỉ chứa ký tự chữ cái và số
     * @param str Chuỗi cần kiểm tra
     * @return true nếu chuỗi chỉ chứa chữ cái và số
     */
    public static boolean isAlphanumeric(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        return str.matches("[a-zA-Z0-9]+");
    }
}
