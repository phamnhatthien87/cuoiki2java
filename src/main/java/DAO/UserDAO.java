package DAO;

import Database.ConnectDB;
import model.*;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public User login(String username, String password) {

        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String role = rs.getString("role");

                if ("LIBRARIAN".equalsIgnoreCase(role)) {
                    return new Librarian(id, username, password, role);
                } else {
                    return new Borrower(id, username, password, role);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // hàm thêm dữ liệu vào bảng
    public boolean register(String username, String password) {

        String sql = "INSERT INTO Users (username, password, role) VALUES (?, ?, 'borrower')";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            int result = ps.executeUpdate();
            return result > 0; // Trả về true nếu đăng ký thành công
        } catch (Exception e) {
            System.out.println("Lỗi đăng ký: Username có thể đã tồn tại.");
            return false;
        }
    }
}
