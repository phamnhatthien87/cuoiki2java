package Client;

import model.Book;
import model.User;
import shared.network.Request;
import shared.network.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryClient {

    private static final LibraryClient INSTANCE = new LibraryClient();
    private static final Logger LOGGER = Logger.getLogger(LibraryClient.class.getName());

    private static final String HOST = "localhost";
    private static final int PORT = 9000;

    private LibraryClient() {
    }

    public static LibraryClient getInstance() {
        return INSTANCE;
    }

    public User login(String username, String password) {
        return (User) send("LOGIN", username, password);
    }

    public boolean register(String username, String password, String email) {
        return toBoolean(send("REGISTER", username, password, email));
    }

    public List<User> getAllUsers() {
        return toList(send("GET_ALL_USERS"));
    }

    public boolean createUser(String username, String password, String role, String email) {
        return toBoolean(send("CREATE_USER", username, password, role, email));
    }

    public boolean updateUser(int id, String username, String password, String role, String email) {
        return toBoolean(send("UPDATE_USER", id, username, password, role, email));
    }

    public boolean deleteUser(int id) {
        return toBoolean(send("DELETE_USER", id));
    }

    public List<Book> getAllBooks() {
        return toList(send("GET_ALL_BOOKS"));
    }

    public List<Book> searchBooks(String keyword) {
        return toList(send("SEARCH_BOOKS", keyword));
    }

    public boolean addBook(Book book) {
        return toBoolean(send("ADD_BOOK", book));
    }

    public boolean updateBook(Book book) {
        return toBoolean(send("UPDATE_BOOK", book));
    }

    public boolean deleteBook(int bookId) {
        return toBoolean(send("DELETE_BOOK", bookId));
    }

    public boolean exportBooks(Path file) {
        return toBoolean(send("EXPORT_BOOKS", file.toString()));
    }

    public boolean importBooks(Path file) {
        return toBoolean(send("IMPORT_BOOKS", file.toString()));
    }

    public Map<String, Integer> getCategories() {
        Object data = send("GET_CATEGORIES");
        if (data instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> categories = (Map<String, Integer>) map;
            return categories;
        }
        return new HashMap<>();
    }

    public List<String[]> getAllCategories() {
        return toList(send("GET_ALL_CATEGORIES"));
    }

    public boolean addCategory(String name) {
        return toBoolean(send("ADD_CATEGORY", name));
    }

    public boolean updateCategory(int id, String name) {
        return toBoolean(send("UPDATE_CATEGORY", id, name));
    }

    public boolean deleteCategory(int id) {
        return toBoolean(send("DELETE_CATEGORY", id));
    }

    public Map<String, Integer> getPublishers() {
        Object data = send("GET_PUBLISHERS");
        if (data instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Integer> publishers = (Map<String, Integer>) map;
            return publishers;
        }
        return new HashMap<>();
    }

    public boolean borrowBook(int userId, int bookId) {
        return toBoolean(send("BORROW_BOOK", userId, bookId));
    }

    public boolean returnBook(int borrowId, int bookId) {
        return toBoolean(send("RETURN_BOOK", borrowId, bookId));
    }

    public boolean isBookBorrowed(int bookId) {
        return toBoolean(send("IS_BOOK_BORROWED", bookId));
    }

    public List<String[]> getBorrowingHistory() {
        return toList(send("GET_BORROWING_HISTORY"));
    }

    public List<String[]> getOverdueBooks() {
        return toList(send("GET_OVERDUE_BOOKS"));
    }

    public int countBorrowedByDate(LocalDate date) {
        return toInt(send("COUNT_BORROWED_BY_DATE", date));
    }

    public int countBorrowedByMonth(int month, int year) {
        return toInt(send("COUNT_BORROWED_BY_MONTH", month, year));
    }

    public int countBorrowedByYear(int year) {
        return toInt(send("COUNT_BORROWED_BY_YEAR", year));
    }

    public List<String[]> getBorrowReport(String type, LocalDate date, int month, int year) {
        return toList(send("GET_BORROW_REPORT", type, date, month, year));
    }

    public boolean exportBorrowReport(Path file, List<String[]> rows) {
        return toBoolean(send("EXPORT_BORROW_REPORT", file.toString(), new ArrayList<>(rows)));
    }

    private Object send(String action, Object... params) {
        try (
                Socket socket = new Socket(HOST, PORT);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            out.flush();
            out.writeObject(new Request(action, params));
            out.flush();

            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                Response response = (Response) in.readObject();

                if (response.isSuccess()) {
                    return response.getData();
                }

                LOGGER.warning(response.getMessage());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Khong ket noi duoc server " + HOST + ":" + PORT, e);
        }

        return null;
    }

    private boolean toBoolean(Object value) {
        return value instanceof Boolean result && result;
    }

    private int toInt(Object value) {
        return value instanceof Integer result ? result : 0;
    }

    private <T> List<T> toList(Object value) {
        if (value instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            List<T> values = (List<T>) list;
            return values;
        }
        return new ArrayList<>();
    }
}
