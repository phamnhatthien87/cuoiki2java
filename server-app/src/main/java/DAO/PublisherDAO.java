package DAO;

import Database.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PublisherDAO {

    public Map<String, Integer> getAllPublishers() {
        String sql = "SELECT id, publisherName FROM Publishers ORDER BY publisherName";
        Map<String, Integer> publishers = new HashMap<>();

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                publishers.put(
                        resultSet.getString("publisherName"),
                        resultSet.getInt("id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return publishers;
    }
}
