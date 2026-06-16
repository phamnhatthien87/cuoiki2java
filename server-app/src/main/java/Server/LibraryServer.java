package Server;

import Server.service.AuthService;
import Server.service.BookService;
import Server.service.BorrowingService;
import Server.service.CategoryService;
import Server.service.StatisticService;
import model.Book;
import model.User;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LibraryServer {

    private static final LibraryServer INSTANCE = new LibraryServer();

    private final AuthService authService = new AuthService();
    private final BookService bookService = new BookService();
    private final BorrowingService borrowingService = new BorrowingService();
    private final StatisticService statisticService = new StatisticService();
    private final CategoryService categoryService = new CategoryService();

    private LibraryServer() {
    }

    public static LibraryServer getInstance() {
        return INSTANCE;
    }

    public User login(String username, String password) {
        return authService.login(username, password);
    }

    public boolean register(String username, String password) {
        return authService.register(username, password);
    }

    public List<User> getAllUsers() {
        return authService.getAllUsers();
    }

    public boolean createUser(String username, String password, String role) {
        return authService.createUser(username, password, role);
    }

    public boolean updateUser(int id, String username, String password, String role) {
        return authService.updateUser(id, username, password, role);
    }

    public boolean deleteUser(int id) {
        return authService.deleteUser(id);
    }

    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    public List<Book> searchBooks(String keyword) {
        return bookService.searchBooks(keyword);
    }

    public boolean addBook(Book book) {
        return bookService.addBook(book);
    }

    public boolean updateBook(Book book) {
        return bookService.updateBook(book);
    }

    public boolean deleteBook(int bookId) {
        return bookService.deleteBook(bookId);
    }

    public boolean exportBooks(Path file) {
        return bookService.exportBooks(file);
    }

    public boolean importBooks(Path file) {
        return bookService.importBooks(file);
    }

    public Map<String, Integer> getCategories() {
        return bookService.getCategories();
    }

    public List<String[]> getAllCategories() {
        return categoryService.getAllCategories();
    }

    public boolean addCategory(String name) {
        return categoryService.addCategory(name);
    }

    public boolean updateCategory(int id, String name) {
        return categoryService.updateCategory(id, name);
    }

    public boolean deleteCategory(int id) {
        return categoryService.deleteCategory(id);
    }

    public Map<String, Integer> getPublishers() {
        return bookService.getPublishers();
    }

    public boolean borrowBook(int userId, int bookId) {
        return borrowingService.borrowBook(userId, bookId);
    }

    public boolean returnBook(int borrowId, int bookId) {
        return borrowingService.returnBook(borrowId, bookId);
    }

    public boolean isBookBorrowed(int bookId) {
        return borrowingService.isBookBorrowed(bookId);
    }

    public List<String[]> getBorrowingHistory() {
        return borrowingService.getHistory();
    }

    public List<String[]> getOverdueBooks() {
        return borrowingService.getOverdueBooks();
    }

    public int countBorrowedByDate(LocalDate date) {
        return statisticService.countBorrowedByDate(date);
    }

    public int countBorrowedByMonth(int month, int year) {
        return statisticService.countBorrowedByMonth(month, year);
    }

    public int countBorrowedByYear(int year) {
        return statisticService.countBorrowedByYear(year);
    }

    public List<String[]> getBorrowReport(String type, LocalDate date, int month, int year) {
        return statisticService.getBorrowReport(type, date, month, year);
    }

    public boolean exportBorrowReport(Path file, List<String[]> rows) {
        return statisticService.exportBorrowReport(file, rows);
    }
}
