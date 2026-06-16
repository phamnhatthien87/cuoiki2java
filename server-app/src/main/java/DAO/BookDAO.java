package DAO;

import Database.ConnectDB;
import model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDAO {

    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());

    private static final String BOOK_SELECT =
            "SELECT b.id, b.title, b.author, b.quantity, b.categoryId, b.publisherId, " +
                    "c.categoryName, p.publisherName " +
                    "FROM Books b " +
                    "LEFT JOIN Categories c ON b.categoryId = c.id " +
                    "LEFT JOIN Publishers p ON b.publisherId = p.id ";

    public List<Book> getAllBooks() {
        String sql = BOOK_SELECT + "ORDER BY b.id";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            return readBooks(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the tai danh sach sach.", e);
            return new ArrayList<>();
        }
    }

    public List<Book> searchBooks(String keyword) {
        String sql = BOOK_SELECT +
                "WHERE LOWER(b.title) LIKE ? OR LOWER(c.categoryName) LIKE ? " +
                "ORDER BY b.id";
        String searchValue = "%" + keyword.toLowerCase() + "%";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);

            try (ResultSet resultSet = statement.executeQuery()) {
                return readBooks(resultSet);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the tim sach.", e);
            return new ArrayList<>();
        }
    }

    public boolean insert(Book book) {
        String sql = "INSERT INTO Books (title, author, quantity, categoryId, publisherId) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            fillBookStatement(statement, book);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the them sach.", e);
            return false;
        }
    }

    public boolean insertAll(List<Book> books) {
        String sql = "INSERT INTO Books (title, author, quantity, categoryId, publisherId) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Book book : books) {
                    fillBookStatement(statement, book);
                    statement.addBatch();
                }

                statement.executeBatch();
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                LOGGER.log(Level.WARNING, "Khong the import danh sach sach.", e);
                return false;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the mo transaction import sach.", e);
            return false;
        }
    }

    public boolean update(Book book) {
        String sql = "UPDATE Books SET title = ?, author = ?, quantity = ?, categoryId = ?, publisherId = ? WHERE id = ?";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            fillBookStatement(statement, book);
            statement.setInt(6, book.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the cap nhat sach.", e);
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Books WHERE id = ?";

        try (
                Connection connection = ConnectDB.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Khong the xoa sach.", e);
            return false;
        }
    }

    private void fillBookStatement(PreparedStatement statement, Book book) throws SQLException {
        statement.setString(1, book.getTitle());
        statement.setString(2, book.getAuthor());
        statement.setInt(3, book.getQuantity());
        statement.setInt(4, book.getCategoryId());
        statement.setInt(5, book.getPublisherId());
    }

    private List<Book> readBooks(ResultSet resultSet) throws SQLException {
        List<Book> books = new ArrayList<>();

        while (resultSet.next()) {
            books.add(readBook(resultSet));
        }

        return books;
    }

    private Book readBook(ResultSet resultSet) throws SQLException {
        Book book = new Book(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getInt("quantity"),
                resultSet.getString("categoryName"),
                resultSet.getString("publisherName")
        );
        book.setCategoryId(resultSet.getInt("categoryId"));
        book.setPublisherId(resultSet.getInt("publisherId"));
        return book;
    }
}
