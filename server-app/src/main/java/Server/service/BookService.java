package Server.service;

import DAO.BookDAO;
import DAO.CategoryDAO;
import DAO.PublisherDAO;
import model.Book;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookService {

    private final BookDAO bookDAO = new BookDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PublisherDAO publisherDAO = new PublisherDAO();

    public List<Book> getAllBooks() {
        return bookDAO.getAllBooks();
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllBooks();
        }

        return bookDAO.searchBooks(keyword.trim());
    }

    public boolean addBook(Book book) {
        if (book == null
                || isBlank(book.getTitle())
                || isBlank(book.getAuthor())
                || book.getQuantity() < 0
                || book.getCategoryId() <= 0
                || book.getPublisherId() <= 0) {
            return false;
        }

        return bookDAO.insert(book);
    }

    public boolean updateBook(Book book) {
        if (book == null
                || book.getId() <= 0
                || isBlank(book.getTitle())
                || isBlank(book.getAuthor())
                || book.getQuantity() < 0
                || book.getCategoryId() <= 0
                || book.getPublisherId() <= 0) {
            return false;
        }

        return bookDAO.update(book);
    }

    public boolean deleteBook(int bookId) {
        if (bookId <= 0) {
            return false;
        }

        return bookDAO.delete(bookId);
    }

    public Map<String, Integer> getCategories() {
        return categoryDAO.getAllToMap();
    }

    public Map<String, Integer> getPublishers() {
        return publisherDAO.getAllPublishers();
    }

    public boolean exportBooks(Path file) {
        if (file == null) {
            return false;
        }

        List<String> lines = new ArrayList<>();
        lines.add("title,author,quantity,categoryId,publisherId");

        for (Book book : getAllBooks()) {
            lines.add(String.join(",",
                    csv(book.getTitle()),
                    csv(book.getAuthor()),
                    String.valueOf(book.getQuantity()),
                    String.valueOf(book.getCategoryId()),
                    String.valueOf(book.getPublisherId())
            ));
        }

        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean importBooks(Path file) {
        if (file == null || !Files.exists(file)) {
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            List<Book> books = readBooksFromCsv(lines);

            for (Book book : books) {
                if (book == null
                        || isBlank(book.getTitle())
                        || isBlank(book.getAuthor())
                        || book.getQuantity() < 0
                        || book.getCategoryId() <= 0
                        || book.getPublisherId() <= 0) {
                    return false;
                }
            }

            return bookDAO.insertAll(books);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Book> readBooksFromCsv(List<String> lines) {
        List<Book> books = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null || line.isBlank()) {
                continue;
            }

            List<String> values = parseCsvLine(line);
            if (values.size() != 5) {
                throw new IllegalArgumentException("Dong CSV khong hop le: " + (i + 1));
            }

            Book book = new Book();
            book.setTitle(values.get(0));
            book.setAuthor(values.get(1));
            book.setQuantity(Integer.parseInt(values.get(2)));
            book.setCategoryId(Integer.parseInt(values.get(3)));
            book.setPublisherId(Integer.parseInt(values.get(4)));
            books.add(book);
        }

        return books;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    value.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ',' && !quoted) {
                values.add(value.toString());
                value.setLength(0);
            } else {
                value.append(ch);
            }
        }

        values.add(value.toString());
        return values;
    }

    private String csv(String value) {
        String safe = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + safe + "\"";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
