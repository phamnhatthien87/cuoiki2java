package DAO;

import Database.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryDAO {

    public List<String[]> findAll() {
        String sql = "SELECT id, categoryName FROM Categories ORDER BY id";
        List<String[]> categories = new ArrayList<>();

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                categories.add(new String[]{
                        String.valueOf(resultSet.getInt("id")),
                        resultSet.getString("categoryName")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public Map<String, Integer> getAllToMap() {
        String sql = "SELECT id, categoryName FROM Categories ORDER BY categoryName";
        Map<String, Integer> categories = new HashMap<>();

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                categories.put(
                        resultSet.getString("categoryName"),
                        resultSet.getInt("id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public boolean insert(String categoryName) {
        String sql = "INSERT INTO Categories (categoryName) VALUES (?)";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, categoryName);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(int id, String categoryName) {
        String sql = "UPDATE Categories SET categoryName = ? WHERE id = ?";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, categoryName);
            statement.setInt(2, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Categories WHERE id = ?";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
