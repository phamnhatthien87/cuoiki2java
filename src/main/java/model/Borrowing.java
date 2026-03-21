package model;

import java.time.LocalDate;
// class lưu trữ dữ liệu( ngày mượn,người mượn, ngày trả,...)
public class Borrowing {
    private int id;
    private int userId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String status;


    public Borrowing(int id, int userId, int bookId,
                     LocalDate borrowDate, LocalDate returnDate, String status) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }


    public Borrowing(int userId, int bookId, LocalDate borrowDate, String status) {
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.status = status;
    }


    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getBookId() { return bookId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
}
