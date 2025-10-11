/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import service.ConfigService;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

public class JDBCUtils {

    //private static String jdbcURL = "jdbc:mysql://localhost:3306/demo";
    private static String jdbcURL = ConfigService.getInstance().getDatabaseUrl();

    private static String jdbcUsername = ConfigService.getInstance().getDatabaseUsername();
    //private static String jdbcURL = "jdbc:postgresql://db.omouttsnihhsjbstlbbe.supabase.co:5432/postgres";
    //private static String jdbcUsername = "postgres";
    private static String jdbcPassword = ConfigService.getInstance().getDatabasePassword();

    public static Connection getConnection() {
        Connection connection = null;
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            Class.forName(ConfigService.getInstance().getDatabaseDriver());

            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }

    public static Date getSQLDate(LocalDate date) {
        return java.sql.Date.valueOf(date);
    }

    public static LocalDate getUtilDate(Date sqlDate) {
        return sqlDate.toLocalDate();
    }

    public static void main(String[] args) {
        System.out.println("Đang lấy thông tin từ ConfigService...");
        System.out.println("Database URL: " + ConfigService.getInstance().getDatabaseUrl());

        System.out.println("\nĐang thử kết nối tới database...");

        try (Connection conn = JDBCUtils.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ KẾT NỐI TỚI SUPABASE THÀNH CÔNG!");
            } else {
                System.out.println("❌ Kết nối thất bại. Connection object là null.");
            }
        } catch (Exception e) {
            System.err.println("❌ ĐÃ XẢY RA LỖI KHI KẾT NỐI:");
            e.printStackTrace();
        }
    }
}
