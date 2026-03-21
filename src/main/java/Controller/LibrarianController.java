package Controller;

import DAO.BookDAO;
import DAO.BorrowingDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Book;
import model.User;
import java.util.List;

public class LibrarianController {

    // các biến quản lý sách
    @FXML private TextField txtBookId, txtTitle, txtAuthor, txtQuantity;
    @FXML private TableView<Book> tblBooks;
    @FXML private TableColumn<Book, Integer> colId, colQuantity;
    @FXML private TableColumn<Book, String> colTitle, colAuthor;

    // các biến thống kê
    @FXML private TableView<String[]> tblHistory;
    @FXML private TableColumn<String[], String> colHistoryUser;
    @FXML private TableColumn<String[], String> colHistoryBook;
    @FXML private TableColumn<String[], String> colHistoryDate;
    @FXML private TableColumn<String[], String> colHistoryStatus;
    @FXML private Label lblTotalLoaned;

    private User librarian;

    public void setLibrarian(User user) {
        this.librarian = user;
        System.out.println("Librarian logged in: " + user.getUsername());
    }

    @FXML
    public void initialize() {
        // Init Tab 1: Books
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // logic load sách được gọi riêng để thực hiện nhiều lần
        showBooksAction();
    }


    @FXML
    public void addBookAction() {
        try {
            int id = Integer.parseInt(txtBookId.getText());
            String title = txtTitle.getText();
            String author = txtAuthor.getText();
            int quantity = Integer.parseInt(txtQuantity.getText());

            BookDAO dao = new BookDAO();
            dao.insert(new Book(id, title, author, quantity));

            showAlert("Success", "Book added successfully!");
            showBooksAction(); // Cập nhật lại bảng
        } catch (Exception e) {
            showAlert("Error", "Invalid input!");
        }
    }

    @FXML
    public void showBooksAction() {
        BookDAO dao = new BookDAO();
        tblBooks.getItems().clear();
        tblBooks.getItems().addAll(dao.getAllBooks());
    }

    @FXML
    // code cho button reset form điền thông tin sách
    public void resetFormAction() {
        txtBookId.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtQuantity.clear();
    }

    @FXML
    // code cho logic xóa sách
    public void deleteBookAction() {
        Book selectedBook = tblBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Thông báo", "Vui lòng chọn một cuốn sách để xóa!");
            return;
        }
        BorrowingDAO borrowingDAO = new BorrowingDAO();
        boolean isBorrowed = borrowingDAO.isBookBorrowed(selectedBook.getId());

        if (isBorrowed) {
            showAlert("Không thể xóa",
                    "Sách này đang hoặc đã được mượn, không thể xóa!");
            return;
        }
        try {
            new BookDAO().delete(selectedBook.getId());
            showBooksAction();
            showAlert("Thành công", "Đã xóa sách!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // tab 2 : lịch sử và thống kê

    @FXML
    // load lịch sử mượn sách
    public void loadHistoryData() {
        BorrowingDAO dao = new BorrowingDAO();
        List<String[]> data = dao.getHistory();

        // Thiết lập cách hiển thị dữ liệu cho mảng String[]
        colHistoryUser.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[1]));
        colHistoryBook.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[2]));
        colHistoryDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[3]));
        colHistoryStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[4]));

        tblHistory.setItems(FXCollections.observableArrayList(data));

        // Cập nhật nhãn thống kê số sách đang mượn
        long count = data.stream().filter(row -> row[4].equalsIgnoreCase("borrowing")).count();
        lblTotalLoaned.setText("Sách đang được mượn: " + count);
    }

    @FXML
    // hàm xác nhận trả sách
    public void handleReturnBook() {
        String[] selected = tblHistory.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn một dòng mượn để trả sách!");
            return;
        }

        // Kiểm tra nếu đã trả rồi thì không cho trả nữa
        if (selected[4].equalsIgnoreCase("returned")) {
            showAlert("Thông báo", "Sách này đã được trả rồi!");
            return;
        }

        int borrowId = Integer.parseInt(selected[0]);
        int bookId = Integer.parseInt(selected[5]);

        BorrowingDAO dao = new BorrowingDAO();
        if (dao.returnBookTransaction(borrowId, bookId)) {
            showAlert("Thành công", "Đã xác nhận trả sách và cập nhật kho!");
            loadHistoryData(); // Làm mới bảng lịch sử
            showBooksAction(); // Làm mới bảng kho sách để thấy quantity tăng lên
        } else {
            showAlert("Lỗi", "Không thể thực hiện trả sách!");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }
}