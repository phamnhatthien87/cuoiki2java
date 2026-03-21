package Controller;

import DAO.BookDAO;
import DAO.BorrowingDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Book;
import model.User;
import javafx.scene.control.TableRow;
public class BorrowerController {

    private User borrower;

    @FXML
    private TableView<Book> tableBooks;

    @FXML
    private TableColumn<Book, Integer> colId;

    @FXML
    private TableColumn<Book, String> colTitle;

    @FXML
    private TableColumn<Book, String> colAuthor;

    @FXML
    private TableColumn<Book, Integer> colAvailable;

    public void setBorrower(User user){
        this.borrower = user;
        System.out.println("Borrower Login: " + user.getUsername());
    }

    @FXML
    public void initialize() {
        // gán dữ liệu cho các cột
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // hàm load dữ liệu
        loadBooksData();

        // Định dạng dòng khi sách hết (quantity = 0) sẽ có màu đỏ nhạt
        tableBooks.setRowFactory(tv -> new TableRow<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getQuantity() <= 0) {
                    setStyle("-fx-background-color: #ffcccc;");// set màu khi số lượng = 0;
                } else {
                    setStyle("");
                }
            }
        });
    }

    // Tách hàm load dữ liệu để có thể gọi lại nhiều lần
    public void loadBooksData() {
        ObservableList<Book> list = FXCollections.observableArrayList(new BookDAO().getAll());
        tableBooks.setItems(list);
        tableBooks.refresh();
    }

    @FXML
    // hàm xử lý sự kiện mượn sách
    //1.kiểm tra sách đã được chọn hay chưa
    //2.kiểm tra số lượng sách trong kho
    // 3. thực hiện mượn sách
    public void borrowBook() {
        Book selectedBook = tableBooks.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn sách trong bảng trước!");
            return;
        }
        if (selectedBook.getQuantity() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Sách này hiện đã hết, không thể mượn!");
            return;
        }
        BorrowingDAO borrowingDAO = new BorrowingDAO();
        // thực hiện mượn sách trong DB và trả về kết quả thành công hay không
        boolean success = borrowingDAO.borrowBookTransaction(borrower.getId(), selectedBook.getId());

        if (success) {
            // sử dụng hàm load book để cập nhật số lượng ( quantity -1)
            loadBooksData();
            String msg = "Thành công! Bạn đã mượn: " + selectedBook.getTitle();
            showAlert(Alert.AlertType.INFORMATION, "Thành công", msg);
            System.out.println(msg + " — by " + borrower.getUsername());
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Lỗi hệ thống, không thể thực hiện mượn sách!");
        }
    }

    // hàm hiển thị hộp thoại thông báo ALERT
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}