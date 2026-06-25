package DAO;

import Database.ConnectDB;
import model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.id, b.title, b.author, b.quantity, b.categoryId, b.publisherId, " +
                     "c.categoryName, p.publisherName " +
                     "FROM Books b " +
                     "LEFT JOIN Categories c ON b.categoryId = c.id " +
                     "LEFT JOIN Publishers p ON b.publisherId = p.id " +
                     "ORDER BY b.id";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
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
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.id, b.title, b.author, b.quantity, b.categoryId, b.publisherId, " +
                     "c.categoryName, p.publisherName " +
                     "FROM Books b " +
                     "LEFT JOIN Categories c ON b.categoryId = c.id " +
                     "LEFT JOIN Publishers p ON b.publisherId = p.id " +
                     "WHERE LOWER(b.title) LIKE ? OR LOWER(c.categoryName) LIKE ? " +
                     "ORDER BY b.id";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            String searchValue = "%" + keyword.toLowerCase() + "%";
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
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
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public boolean insert(Book book) {
        String sql = "INSERT INTO Books (title, author, quantity, categoryId, publisherId) VALUES (?, ?, ?, ?, ?)";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setInt(3, book.getQuantity());
            statement.setInt(4, book.getCategoryId());
            statement.setInt(5, book.getPublisherId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertAll(List<Book> books) {
        String sql = "INSERT INTO Books (title, author, quantity, categoryId, publisherId) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = ConnectDB.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Book book : books) {
                    statement.setString(1, book.getTitle());
                    statement.setString(2, book.getAuthor());
                    statement.setInt(3, book.getQuantity());
                    statement.setInt(4, book.getCategoryId());
                    statement.setInt(5, book.getPublisherId());
                    statement.addBatch();
                }

                statement.executeBatch();
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Book book) {
        String sql = "UPDATE Books SET title = ?, author = ?, quantity = ?, categoryId = ?, publisherId = ? WHERE id = ?";

        try (
            Connection connection = ConnectDB.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setInt(3, book.getQuantity());
            statement.setInt(4, book.getCategoryId());
            statement.setInt(5, book.getPublisherId());
            statement.setInt(6, book.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
}
