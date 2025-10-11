/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.ArrayList;
import java.util.List;
import model.Setting;
import service.JDBCUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author DELL
 */
public class SettingDAO {

    public List<Setting> getSettingsByType(String settingType) {
        List<Setting> settings = new ArrayList<>();
        // Tên bảng PascalCase, tên cột chữ thường
        String sql = "SELECT * FROM \"Setting\" WHERE settingtype = ? AND status = 'Active' ORDER BY settingvalue ASC";

        try (Connection conn = JDBCUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, settingType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Setting setting = new Setting();
                    // Sửa lại tên cột thành chữ thường khi đọc dữ liệu
                    setting.setSettingID(rs.getInt("settingid"));
                    setting.setSettingType(rs.getString("settingtype"));
                    setting.setSettingValue(rs.getString("settingvalue"));
                    setting.setDescription(rs.getString("description"));
                    setting.setStatus(rs.getString("status"));
                    settings.add(setting);
                }
            }
        } catch (SQLException e) {
            JDBCUtils.printSQLException(e);
        }
        return settings;
    }

    public static void main(String[] args) {
        // Khởi tạo đối tượng DAO
        SettingDAO settingDAO = new SettingDAO();

        System.out.println("--- Bắt đầu kiểm tra hàm getSettingsByType ---");

        // Loại setting chúng ta muốn kiểm tra, ví dụ: 'UNIT'
        String typeToTest = "UNIT";
        System.out.println("\nĐang thử lấy tất cả các setting có type = '" + typeToTest + "'...");

        try {
            // Gọi hàm chức năng
            List<Setting> result = settingDAO.getSettingsByType(typeToTest);

            // Kiểm tra kết quả
            if (result != null) {
                System.out.println("✅ THÀNH CÔNG! Hàm đã chạy mà không gây ra lỗi.");
                System.out.println("  -> Tìm thấy: " + result.size() + " bản ghi (đúng như mong đợi trên DB trống).");
            } else {
                System.out.println("❌ THẤT BẠI! Hàm trả về giá trị null.");
            }

        } catch (Exception e) {
            System.err.println("❌ ĐÃ XẢY RA LỖI KHI THỰC THI HÀM:");
            e.printStackTrace();
        }

        System.out.println("\n--- Kết thúc kiểm tra ---");
    }
}
