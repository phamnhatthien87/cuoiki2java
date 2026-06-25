package DAO;

import Database.ConnectDB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAO {

    public int byDate(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM Borrowings WHERE CAST(borrowDate AS DATE) = ?";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int byMonth(int month, int year) {
        String sql = "SELECT COUNT(*) FROM Borrowings WHERE MONTH(borrowDate) = ? AND YEAR(borrowDate) = ?";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, month);
            statement.setInt(2, year);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int byYear(int year) {
        String sql = "SELECT COUNT(*) FROM Borrowings WHERE YEAR(borrowDate) = ?";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, year);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<String[]> reportByDate(LocalDate date) {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT u.username, b.title, br.borrowDate, br.status " +
                     "FROM Borrowings br " +
                     "JOIN Users u ON br.userId = u.id " +
                     "JOIN Books b ON br.bookId = b.id " +
                     "WHERE CAST(br.borrowDate AS DATE) = ? " +
                     "ORDER BY br.borrowDate DESC";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setDate(1, Date.valueOf(date));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new String[]{
                        resultSet.getString("username"),
                        resultSet.getString("title"),
                        resultSet.getString("borrowDate"),
                        resultSet.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<String[]> reportByMonth(int month, int year) {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT u.username, b.title, br.borrowDate, br.status " +
                     "FROM Borrowings br " +
                     "JOIN Users u ON br.userId = u.id " +
                     "JOIN Books b ON br.bookId = b.id " +
                     "WHERE MONTH(br.borrowDate) = ? AND YEAR(br.borrowDate) = ? " +
                     "ORDER BY br.borrowDate DESC";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, month);
            statement.setInt(2, year);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new String[]{
                        resultSet.getString("username"),
                        resultSet.getString("title"),
                        resultSet.getString("borrowDate"),
                        resultSet.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<String[]> reportByYear(int year) {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT u.username, b.title, br.borrowDate, br.status " +
                     "FROM Borrowings br " +
                     "JOIN Users u ON br.userId = u.id " +
                     "JOIN Books b ON br.bookId = b.id " +
                     "WHERE YEAR(br.borrowDate) = ? " +
                     "ORDER BY br.borrowDate DESC";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, year);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    rows.add(new String[]{
                        resultSet.getString("username"),
                        resultSet.getString("title"),
                        resultSet.getString("borrowDate"),
                        resultSet.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
