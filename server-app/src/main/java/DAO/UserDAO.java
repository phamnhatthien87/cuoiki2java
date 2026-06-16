package DAO;

import Database.ConnectDB;
import Security.AESUtil;
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

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String dbUsername = rs.getString("username");
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");
                // Giải mã AES để lấy email rõ ràng hiển thị cho client
                String email = AESUtil.decrypt(rs.getString("email"));

                if (!isPasswordValid(password, storedPassword)) {
                    return null;
                }

                if (!PasswordUtil.isHashed(storedPassword)) {
                    updatePasswordHash(id, PasswordUtil.hashPassword(password));
                }

                if ("LIBRARIAN".equalsIgnoreCase(role)) {
                    return new Librarian(id, dbUsername, null, role, email);
                }

                return new Borrower(id, dbUsername, null, role, email);
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
        // Mã hoá AES email trước khi lưu database
        String encryptedEmail = AESUtil.encrypt(email);

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, encryptedEmail);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Loi dang ky: username co the da ton tai hoac cot password qua ngan.");
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
                // Giải mã AES để email hiển thị rõ ràng trên giao diện
                String email = AESUtil.decrypt(rs.getString("email"));
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        null,
                        rs.getString("role"),
                        email
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
        // Mã hoá AES email trước khi lưu database
        String encryptedEmail = AESUtil.encrypt(email);

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hashPassword(password));
            ps.setString(3, role);
            ps.setString(4, encryptedEmail);

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
        // Mã hoá AES email trước khi cập nhật database
        String encryptedEmail = AESUtil.encrypt(email);

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
                ps.setString(4, encryptedEmail);
                ps.setInt(5, id);
            } else {
                ps.setString(2, role);
                ps.setString(3, encryptedEmail);
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

    private boolean isPasswordValid(String rawPassword, String storedPassword) {

        if (PasswordUtil.isHashed(storedPassword)) {
            return PasswordUtil.verifyPassword(rawPassword, storedPassword);
        }

        return storedPassword != null && storedPassword.equals(rawPassword);
    }

    private void updatePasswordHash(int userId, String passwordHash) {

        String sql = "UPDATE Users SET password = ? WHERE id = ?";

        try (
                Connection conn = ConnectDB.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
