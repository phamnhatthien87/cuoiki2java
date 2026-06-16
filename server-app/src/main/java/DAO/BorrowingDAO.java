package DAO;

import Database.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BorrowingDAO {

    private static final Logger LOGGER = Logger.getLogger(BorrowingDAO.class.getName());

    public List<String[]> getHistory() {
        String sql = "SELECT * FROM View_BorrowingDetails ORDER BY BorrowDate DESC";
        List<String[]> history = new ArrayList<>();

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                history.add(new String[]{
                        resultSet.getString("BorrowID"),
                        resultSet.getString("BorrowerName"),
                        resultSet.getString("BookTitle"),
                        resultSet.getString("BorrowDate"),
                        resultSet.getString("Status"),
                        resultSet.getString("BookID")
                });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the tai lich su muon tra.", e);
        }

        return history;
    }

    public boolean borrowBookTransaction(int userId, int bookId) {
        String updateBook =
                "UPDATE Books " +
                        "SET quantity = quantity - 1, loaned_out = loaned_out + 1 " +
                        "WHERE id = ? AND quantity > 0";
        String insertBorrowing =
                "INSERT INTO Borrowings (userId, bookId, borrowDate, status) " +
                        "VALUES (?, ?, GETDATE(), 'borrowing')";

        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);

            try (
                    PreparedStatement updateBookStatement = connection.prepareStatement(updateBook);
                    PreparedStatement insertBorrowingStatement = connection.prepareStatement(insertBorrowing)
            ) {
                updateBookStatement.setInt(1, bookId);

                insertBorrowingStatement.setInt(1, userId);
                insertBorrowingStatement.setInt(2, bookId);

                boolean success = updateBookStatement.executeUpdate() > 0
                        && insertBorrowingStatement.executeUpdate() > 0;
                finishTransaction(connection, success);
                return success;
            } catch (SQLException e) {
                connection.rollback();
                LOGGER.log(Level.WARNING, "Khong the muon sach.", e);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the mo transaction muon sach.", e);
        }

        return false;
    }

    public boolean returnBookTransaction(int borrowId, int bookId) {
        String updateBorrowing =
                "UPDATE Borrowings " +
                        "SET status = 'returned', returnDate = GETDATE() " +
                        "WHERE id = ?";
        String updateBook =
                "UPDATE Books " +
                        "SET quantity = quantity + 1, " +
                        "loaned_out = CASE WHEN loaned_out > 0 THEN loaned_out - 1 ELSE 0 END " +
                        "WHERE id = ?";

        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);

            try (
                    PreparedStatement updateBorrowingStatement = connection.prepareStatement(updateBorrowing);
                    PreparedStatement updateBookStatement = connection.prepareStatement(updateBook)
            ) {
                updateBorrowingStatement.setInt(1, borrowId);
                updateBookStatement.setInt(1, bookId);

                boolean success = updateBorrowingStatement.executeUpdate() > 0
                        && updateBookStatement.executeUpdate() > 0;
                finishTransaction(connection, success);
                return success;
            } catch (SQLException e) {
                connection.rollback();
                LOGGER.log(Level.WARNING, "Khong the tra sach.", e);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the mo transaction tra sach.", e);
        }

        return false;
    }

    public boolean isBookBorrowed(int bookId) {
        String sql = "SELECT COUNT(*) FROM Borrowings WHERE bookId = ? AND status = 'borrowing'";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, bookId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the kiem tra trang thai muon sach.", e);
            return false;
        }
    }

    public List<String[]> getOverdueBooks(int loanDays, int finePerDay) {
        String sql =
                "SELECT br.id AS BorrowID, u.username AS BorrowerName, b.title AS BookTitle, " +
                        "br.borrowDate AS BorrowDate, " +
                        "DATEADD(day, ?, br.borrowDate) AS DueDate, " +
                        "DATEDIFF(day, DATEADD(day, ?, br.borrowDate), GETDATE()) AS OverdueDays " +
                        "FROM Borrowings br " +
                        "JOIN Users u ON br.userId = u.id " +
                        "JOIN Books b ON br.bookId = b.id " +
                        "WHERE br.status = 'borrowing' " +
                        "AND DATEADD(day, ?, br.borrowDate) < GETDATE() " +
                        "ORDER BY OverdueDays DESC";
        List<String[]> overdueBooks = new ArrayList<>();

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, loanDays);
            statement.setInt(2, loanDays);
            statement.setInt(3, loanDays);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int overdueDays = resultSet.getInt("OverdueDays");
                    overdueBooks.add(new String[]{
                            resultSet.getString("BorrowerName"),
                            resultSet.getString("BookTitle"),
                            resultSet.getString("BorrowDate"),
                            resultSet.getString("DueDate"),
                            String.valueOf(overdueDays),
                            String.valueOf(overdueDays * finePerDay)
                    });
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the tai danh sach sach qua han.", e);
        }

        return overdueBooks;
    }

    private void finishTransaction(Connection connection, boolean success) throws SQLException {
        if (success) {
            connection.commit();
        } else {
            connection.rollback();
        }
    }
}
