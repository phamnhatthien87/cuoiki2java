package model;

import java.io.Serializable;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String author;
    private int quantity;
    private int categoryId;
    private int publisherId;
    private String categoryName; // Để hiển thị tên trên TableView
    private String publisherName; // Để hiển thị tên trên TableView

    // Constructor mặc định (tùy chọn)
    public Book() {}

    // Constructor để thêm mới sách (Dùng khi INSERT)
    public Book(int id, String title, String author, int quantity, int categoryId, int publisherId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.publisherId = publisherId;
    }

    // Constructor để hiển thị danh sách (Dùng khi SELECT JOIN)
    public Book(int id, String title, String author, int quantity, String categoryName, String publisherName) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
        this.categoryName = categoryName;
        this.publisherName = publisherName;
    }

    // --- GETTERS (Lấy dữ liệu) ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getQuantity() { return quantity; }
    public int getCategoryId() { return categoryId; }
    public int getPublisherId() { return publisherId; }
    public String getCategoryName() { return categoryName; }
    public String getPublisherName() { return publisherName; }

    // --- SETTERS (Thiết lập dữ liệu - GIẢI QUYẾT LỖI CỦA BẠN) ---
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }
}
