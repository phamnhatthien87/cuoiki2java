package DAO;

import Database.ConnectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {


    /**
     * Lấy danh sách lịch sử mượn trả từ View để hiển thị lên TableView
     * String[] bao gồm: {id, username, title, borrowDate, status, bookId}
     */
    public List<String[]> getHistory() {
        List<String[]> list = new ArrayList<>();
        // View_BorrowingDetails đã được tạo ở SQL để Join các bảng lại
        String sql = "SELECT * FROM View_BorrowingDetails ORDER BY BorrowDate DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{
                        rs.getString("BorrowID"),
                        rs.getString("BorrowerName"),
                        rs.getString("BookTitle"),
                        rs.getString("BorrowDate"),
                        rs.getString("Status"),
                        rs.getString("BookID")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // thực hiện nghiệp vụ trả sách
    public boolean returnBookTransaction(int borrowId, int bookId) {
        String updateBorrow = "UPDATE Borrowings SET status = 'returned', returnDate = GETDATE() WHERE id = ?";
        String updateStock = "UPDATE Books SET quantity = quantity + 1, loaned_out = loaned_out - 1 WHERE id = ?";

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(updateBorrow);
                 PreparedStatement ps2 = con.prepareStatement(updateStock)) {

                ps1.setInt(1, borrowId);
                ps2.setInt(1, bookId);

                int r1 = ps1.executeUpdate();
                int r2 = ps2.executeUpdate();

                if (r1 > 0 && r2 > 0) {
                    con.commit();
                    return true;
                } else {
                    con.rollback();
                    return false;
                }
            } catch (SQLException e) {
                con.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // hàm nghiệp vụ mượn sách
    public boolean borrowBookTransaction(int userId, int bookId) {
        String insertBorrow = "INSERT INTO Borrowings (userId, bookId, borrowDate, status) VALUES (?, ?, GETDATE(), 'borrowing')";
        String updateBook = "UPDATE Books SET quantity = quantity - 1, loaned_out = loaned_out + 1 WHERE id = ? AND quantity > 0";

        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps1 = con.prepareStatement(insertBorrow);
                 PreparedStatement ps2 = con.prepareStatement(updateBook)) {

                ps1.setInt(1, userId);
                ps1.setInt(2, bookId);
                ps2.setInt(1, bookId);

                int r1 = ps1.executeUpdate();
                int r2 = ps2.executeUpdate();

                if (r1 > 0 && r2 > 0) {
                    con.commit();
                    return true;
                } else {
                    con.rollback();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // kiểm tra sách đã được mượn hay chưa để thực hiện việc xóa sách
    public boolean isBookBorrowed(int bookId) {
        String sql = "SELECT COUNT(*) FROM borrowings WHERE bookId = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}