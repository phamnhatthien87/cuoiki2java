package Server.service;

import DAO.BorrowingDAO;

import java.util.List;

public class BorrowingService {

    private final BorrowingDAO borrowingDAO = new BorrowingDAO();

    private static final int LOAN_DAYS = 14;
    private static final int FINE_PER_DAY = 5000;

    public List<String[]> getHistory() {
        return borrowingDAO.getHistory();
    }

    public boolean borrowBook(int userId, int bookId) {
        if (userId <= 0 || bookId <= 0) {
            return false;
        }

        return borrowingDAO.borrowBookTransaction(userId, bookId);
    }

    public boolean returnBook(int borrowId, int bookId) {
        if (borrowId <= 0 || bookId <= 0) {
            return false;
        }

        return borrowingDAO.returnBookTransaction(borrowId, bookId);
    }

    public boolean isBookBorrowed(int bookId) {
        if (bookId <= 0) {
            return false;
        }

        return borrowingDAO.isBookBorrowed(bookId);
    }

    public List<String[]> getOverdueBooks() {
        return borrowingDAO.getOverdueBooks(LOAN_DAYS, FINE_PER_DAY);
    }
}
