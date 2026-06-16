package Controller;

import Client.LibraryClient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Book;
import model.User;

public class BorrowerController {

    private final LibraryClient client = LibraryClient.getInstance();

    private User borrower;

    @FXML private TableView<Book> tableBooks;
    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, Integer> colAvailable;
    @FXML private TableColumn<Book, String> colCategory;
    @FXML private TableColumn<Book, String> colPublisher;
    @FXML private TextField txtSearch;

    public void setBorrower(User user) {
        this.borrower = user;
    }

    @FXML
    public void initialize() {
        initBookTable();
        loadBooksData();
        txtSearch.textProperty().addListener((obs, oldValue, newValue) -> handleSearch());
    }

    public void loadBooksData() {
        tableBooks.setItems(FXCollections.observableArrayList(client.getAllBooks()));
        tableBooks.refresh();
    }

    @FXML
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            loadBooksData();
            return;
        }

        tableBooks.setItems(FXCollections.observableArrayList(client.searchBooks(keyword)));
    }

    @FXML
    public void borrowBook() {
        if (borrower == null) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Chua xac dinh nguoi dung.");
            return;
        }

        Book selectedBook = tableBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long chon sach.");
            return;
        }

        if (selectedBook.getQuantity() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Sach da het.");
            return;
        }

        if (client.borrowBook(borrower.getId(), selectedBook.getId())) {
            loadBooksData();
            showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Ban da muon: " + selectedBook.getTitle());
        } else {
            showAlert(Alert.AlertType.ERROR, "That bai", "Khong the muon sach.");
        }
    }

    private void initBookTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisherName"));
        tableBooks.setRowFactory(table -> unavailableBookRow());
    }

    private TableRow<Book> unavailableBookRow() {
        return new TableRow<>() {
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                setStyle(!empty && book != null && book.getQuantity() <= 0 ? "-fx-background-color: #ffcccc;" : "");
            }
        };
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
