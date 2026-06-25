package DAO;

import Database.ConnectDB;
import Security.PasswordUtil;
import model.Borrower;
import model.Librarian;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User login(String username, String password) {
        String sql = "SELECT id, username, password, role, email FROM Users WHERE username = ?";

        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String dbUsername = rs.getString("username");
                    String storedPassword = rs.getString("password");
                    String role = rs.getString("role");
                    String email = rs.getString("email");

                    // Kiểm tra mật khẩu (hỗ trợ cả mật khẩu băm và mật khẩu thường)
                    boolean isValid = false;
                    if (PasswordUtil.isHashed(storedPassword)) {
                        isValid = PasswordUtil.verifyPassword(password, storedPassword);
                    } else {
                        isValid = storedPassword != null && storedPassword.equals(password);
                    }

                    if (!isValid) {
                        return null;
                    }

                    // Tự động nâng cấp lên mật khẩu băm nếu đang là mật khẩu thường
                    if (!PasswordUtil.isHashed(storedPassword)) {
                        String updateSql = "UPDATE Users SET password = ? WHERE id = ?";
                        try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                            updatePs.setString(1, PasswordUtil.hashPassword(password));
                            updatePs.setInt(2, id);
                            updatePs.executeUpdate();
                        }
                    }

                    if ("LIBRARIAN".equalsIgnoreCase(role)) {
                        return new Librarian(id, dbUsername, null, role, email);
                    }
                    return new Borrower(id, dbUsername, null, role, email);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(String username, String password) {
        return register(username, password, null);
    }

    public boolean register(String username, String password, String email) {
        String sql = "INSERT INTO Users (username, password, role, email) VALUES (?, ?, 'borrower', ?)";
        String hashedPassword = PasswordUtil.hashPassword(password);

        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Loi dang ky: username co the da ton tai.");
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, role, email FROM Users ORDER BY id";

        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    null,
                    rs.getString("role"),
                    rs.getString("email")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean createUser(String username, String password, String role) {
        return createUser(username, password, role, null);
    }

    public boolean createUser(String username, String password, String role, String email) {
        String sql = "INSERT INTO Users (username, password, role, email) VALUES (?, ?, ?, ?)";

        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hashPassword(password));
            ps.setString(3, role);
            ps.setString(4, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(int id, String username, String password, String role) {
        return updateUser(id, username, password, role, null);
    }

    public boolean updateUser(int id, String username, String password, String role, String email) {
        boolean updatePassword = password != null && !password.isBlank();
        String sql = updatePassword
                ? "UPDATE Users SET username = ?, password = ?, role = ?, email = ? WHERE id = ?"
                : "UPDATE Users SET username = ?, role = ?, email = ? WHERE id = ?";

        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            if (updatePassword) {
                ps.setString(2, PasswordUtil.hashPassword(password));
                ps.setString(3, role);
                ps.setString(4, email);
                ps.setInt(5, id);
            } else {
                ps.setString(2, role);
                ps.setString(3, email);
                ps.setInt(4, id);
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM Users WHERE id = ?";

        try (
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
