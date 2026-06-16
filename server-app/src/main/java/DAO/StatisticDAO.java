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
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatisticDAO {

    private static final Logger LOGGER = Logger.getLogger(StatisticDAO.class.getName());

    public int byDate(LocalDate date) {
        String sql = "SELECT COUNT(*) FROM Borrowings WHERE CAST(borrowDate AS DATE) = ?";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setDate(1, Date.valueOf(date));
            return readCount(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the thong ke theo ngay.", e);
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
            return readCount(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the thong ke theo thang.", e);
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
            return readCount(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the thong ke theo nam.", e);
            return 0;
        }
    }

    public List<String[]> reportByDate(LocalDate date) {
        String sql =
                reportSql("CAST(br.borrowDate AS DATE) = ?") +
                        "ORDER BY br.borrowDate DESC";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setDate(1, Date.valueOf(date));
            return readReportRows(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the tai bao cao theo ngay.", e);
            return new ArrayList<>();
        }
    }

    public List<String[]> reportByMonth(int month, int year) {
        String sql =
                reportSql("MONTH(br.borrowDate) = ? AND YEAR(br.borrowDate) = ?") +
                        "ORDER BY br.borrowDate DESC";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, month);
            statement.setInt(2, year);
            return readReportRows(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the tai bao cao theo thang.", e);
            return new ArrayList<>();
        }
    }

    public List<String[]> reportByYear(int year) {
        String sql =
                reportSql("YEAR(br.borrowDate) = ?") +
                        "ORDER BY br.borrowDate DESC";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, year);
            return readReportRows(statement);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the tai bao cao theo nam.", e);
            return new ArrayList<>();
        }
    }

    private int readCount(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    private String reportSql(String condition) {
        return "SELECT u.username, b.title, br.borrowDate, br.status " +
                "FROM Borrowings br " +
                "JOIN Users u ON br.userId = u.id " +
                "JOIN Books b ON br.bookId = b.id " +
                "WHERE " + condition + " ";
    }

    private List<String[]> readReportRows(PreparedStatement statement) throws SQLException {
        List<String[]> rows = new ArrayList<>();

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

        return rows;
    }
}
