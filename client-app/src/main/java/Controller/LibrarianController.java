package Controller;

import Client.LibraryClient;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import model.Book;
import model.User;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class LibrarianController {

    private static final int REPORT_MONTH_DEFAULT = LocalDate.now().getMonthValue();
    private static final int REPORT_YEAR_DEFAULT = LocalDate.now().getYear();

    private final LibraryClient client = LibraryClient.getInstance();

    private User librarian;
    private Map<String, Integer> categoryMap = new HashMap<>();
    private Map<String, Integer> publisherMap = new HashMap<>();
    private ObservableList<Book> allBooks = FXCollections.observableArrayList();
    private ObservableList<String[]> currentReportRows = FXCollections.observableArrayList();

    @FXML private Label lblTotalLoaned;

    @FXML private TextField txtBookId;
    @FXML private TextField txtTitle;
    @FXML private TextField txtAuthor;
    @FXML private TextField txtQuantity;
    @FXML private ComboBox<String> cbCategory;
    @FXML private ComboBox<String> cbPublisher;
    @FXML private TextField txtBookSearch;
    @FXML private ComboBox<String> cbBookFilterCategory;
    @FXML private ComboBox<String> cbBookSort;
    @FXML private TableView<Book> tblBooks;
    @FXML private TableColumn<Book, Integer> colId;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colAuthor;
    @FXML private TableColumn<Book, Integer> colQuantity;
    @FXML private TableColumn<Book, String> colCat;
    @FXML private TableColumn<Book, String> colPub;

    @FXML private TextField txtUserId;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtUserPassword;
    @FXML private ComboBox<String> cbUserRole;
    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colUserRole;

    @FXML private TextField txtCategoryId;
    @FXML private TextField txtCategoryName;
    @FXML private TableView<String[]> tblCategories;
    @FXML private TableColumn<String[], String> colCategoryId;
    @FXML private TableColumn<String[], String> colCategoryName;

    @FXML private TableView<String[]> tblHistory;
    @FXML private TableColumn<String[], String> colHistoryUser;
    @FXML private TableColumn<String[], String> colHistoryBook;
    @FXML private TableColumn<String[], String> colHistoryDate;
    @FXML private TableColumn<String[], String> colHistoryStatus;

    @FXML private DatePicker dpStatsDate;
    @FXML private TextField txtStatsMonth;
    @FXML private TextField txtStatsYear;
    @FXML private Label lblStatsDay;
    @FXML private Label lblStatsMonth;
    @FXML private Label lblStatsYear;

    @FXML private ComboBox<String> cbReportType;
    @FXML private DatePicker dpReportDate;
    @FXML private TextField txtReportMonth;
    @FXML private TextField txtReportYear;
    @FXML private TableView<String[]> tblReport;
    @FXML private TableColumn<String[], String> colReportUser;
    @FXML private TableColumn<String[], String> colReportBook;
    @FXML private TableColumn<String[], String> colReportDate;
    @FXML private TableColumn<String[], String> colReportStatus;

    @FXML private TableView<String[]> tblOverdue;
    @FXML private TableColumn<String[], String> colOverdueUser;
    @FXML private TableColumn<String[], String> colOverdueBook;
    @FXML private TableColumn<String[], String> colOverdueBorrowDate;
    @FXML private TableColumn<String[], String> colOverdueDueDate;
    @FXML private TableColumn<String[], String> colOverdueDays;
    @FXML private TableColumn<String[], String> colOverdueFine;
    @FXML private Label lblTotalFine;

    public void setLibrarian(User user) {
        this.librarian = user;
    }

    @FXML
    public void initialize() {
        initBookTab();
        initUserTab();
        initCategoryTab();
        initHistoryTab();
        initStatisticTab();
        initOverdueTab();

        loadStaticData();
        loadBooks();
        loadUsers();
        loadCategories();
        loadHistory();
        refreshStatistics();
        loadReport();
        loadOverdueBooks();
    }

    private void initBookTab() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCat.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colPub.setCellValueFactory(new PropertyValueFactory<>("publisherName"));

        tblBooks.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, selected) -> fillBookForm(selected)
        );

        cbBookSort.setItems(FXCollections.observableArrayList(
                "ID tang dan",
                "Ten sach A-Z",
                "Tac gia A-Z",
                "So luong tang",
                "So luong giam"
        ));
        cbBookSort.setValue("ID tang dan");

        txtBookSearch.textProperty().addListener((obs, oldValue, newValue) -> applyBookFilter());
        cbBookFilterCategory.valueProperty().addListener((obs, oldValue, newValue) -> applyBookFilter());
        cbBookSort.valueProperty().addListener((obs, oldValue, newValue) -> applyBookFilter());
    }

    private void initUserTab() {
        cbUserRole.setItems(FXCollections.observableArrayList("borrower", "librarian"));
        cbUserRole.setValue("borrower");

        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUserRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        tblUsers.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, selected) -> fillUserForm(selected)
        );
    }

    private void initCategoryTab() {
        colCategoryId.setCellValueFactory(c -> cell(c.getValue(), 0));
        colCategoryName.setCellValueFactory(c -> cell(c.getValue(), 1));

        tblCategories.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, selected) -> fillCategoryForm(selected)
        );
    }

    private void initHistoryTab() {
        colHistoryUser.setCellValueFactory(c -> cell(c.getValue(), 1));
        colHistoryBook.setCellValueFactory(c -> cell(c.getValue(), 2));
        colHistoryDate.setCellValueFactory(c -> cell(c.getValue(), 3));
        colHistoryStatus.setCellValueFactory(c -> cell(c.getValue(), 4));
    }

    private void initStatisticTab() {
        dpStatsDate.setValue(LocalDate.now());
        txtStatsMonth.setText(String.valueOf(REPORT_MONTH_DEFAULT));
        txtStatsYear.setText(String.valueOf(REPORT_YEAR_DEFAULT));

        cbReportType.setItems(FXCollections.observableArrayList("DAY", "MONTH", "YEAR"));
        cbReportType.setValue("MONTH");
        dpReportDate.setValue(LocalDate.now());
        txtReportMonth.setText(String.valueOf(REPORT_MONTH_DEFAULT));
        txtReportYear.setText(String.valueOf(REPORT_YEAR_DEFAULT));

        colReportUser.setCellValueFactory(c -> cell(c.getValue(), 0));
        colReportBook.setCellValueFactory(c -> cell(c.getValue(), 1));
        colReportDate.setCellValueFactory(c -> cell(c.getValue(), 2));
        colReportStatus.setCellValueFactory(c -> cell(c.getValue(), 3));
    }

    private void initOverdueTab() {
        colOverdueUser.setCellValueFactory(c -> cell(c.getValue(), 0));
        colOverdueBook.setCellValueFactory(c -> cell(c.getValue(), 1));
        colOverdueBorrowDate.setCellValueFactory(c -> cell(c.getValue(), 2));
        colOverdueDueDate.setCellValueFactory(c -> cell(c.getValue(), 3));
        colOverdueDays.setCellValueFactory(c -> cell(c.getValue(), 4));
        colOverdueFine.setCellValueFactory(c -> cell(c.getValue(), 5));
    }

    private SimpleStringProperty cell(String[] row, int index) {
        return new SimpleStringProperty(index < row.length && row[index] != null ? row[index] : "");
    }

    private void loadStaticData() {
        runTask(() -> {
            Map<String, Map<String, Integer>> data = new HashMap<>();
            data.put("categories", client.getCategories());
            data.put("publishers", client.getPublishers());
            return data;
        }, data -> {
            categoryMap = data.get("categories");
            publisherMap = data.get("publishers");

            cbCategory.setItems(FXCollections.observableArrayList(categoryMap.keySet()));
            cbPublisher.setItems(FXCollections.observableArrayList(publisherMap.keySet()));

            List<String> filterCategories = new ArrayList<>();
            filterCategories.add("Tat ca");
            filterCategories.addAll(categoryMap.keySet());
            cbBookFilterCategory.setItems(FXCollections.observableArrayList(filterCategories));
            cbBookFilterCategory.setValue("Tat ca");
        });
    }

    private void fillBookForm(Book book) {
        if (book == null) {
            return;
        }

        txtBookId.setText(String.valueOf(book.getId()));
        txtTitle.setText(book.getTitle());
        txtAuthor.setText(book.getAuthor());
        txtQuantity.setText(String.valueOf(book.getQuantity()));
        cbCategory.setValue(findNameById(categoryMap, book.getCategoryId(), book.getCategoryName()));
        cbPublisher.setValue(findNameById(publisherMap, book.getPublisherId(), book.getPublisherName()));
    }

    private String findNameById(Map<String, Integer> values, int id, String fallback) {
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            if (entry.getValue() == id) {
                return entry.getKey();
            }
        }

        return fallback;
    }

    @FXML
    public void addBookAction() {
        Book book = readBookForm(false);
        if (book == null) {
            return;
        }

        if (client.addBook(book)) {
            showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da them sach.");
            loadBooks();
            clearBookForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the them sach.");
        }
    }

    @FXML
    public void updateBookAction() {
        Book book = readBookForm(true);
        if (book == null) {
            return;
        }

        if (client.updateBook(book)) {
            showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da cap nhat sach.");
            loadBooks();
            clearBookForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the cap nhat sach.");
        }
    }

    @FXML
    public void deleteBookAction() {
        Book selected = tblBooks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long chon sach truoc.");
            return;
        }

        if (client.isBookBorrowed(selected.getId())) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Sach nay dang duoc muon.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xoa sach dang chon?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK && client.deleteBook(selected.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da xoa sach.");
            loadBooks();
            clearBookForm();
        }
    }

    private Book readBookForm(boolean requireId) {
        try {
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            String category = cbCategory.getValue();
            String publisher = cbPublisher.getValue();

            if (title.isEmpty() || author.isEmpty() || category == null || publisher == null) {
                showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long nhap day du thong tin sach.");
                return null;
            }

            Book book = new Book();
            if (requireId) {
                book.setId(Integer.parseInt(txtBookId.getText().trim()));
            }
            book.setTitle(title);
            book.setAuthor(author);
            book.setQuantity(quantity);
            book.setCategoryId(categoryMap.get(category));
            book.setPublisherId(publisherMap.get(publisher));
            return book;
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Du lieu sach khong hop le.");
            return null;
        }
    }

    private void loadBooks() {
        runTask(client::getAllBooks, books -> {
            allBooks.setAll(books);
            applyBookFilter();
        });
    }

    @FXML
    public void applyBookFilter() {
        String keyword = txtBookSearch == null ? "" : txtBookSearch.getText().trim().toLowerCase();
        String category = cbBookFilterCategory == null ? "Tat ca" : cbBookFilterCategory.getValue();
        String sort = cbBookSort == null ? "ID tang dan" : cbBookSort.getValue();

        List<Book> books = allBooks.stream()
                .filter(book -> keyword.isEmpty()
                        || contains(book.getTitle(), keyword)
                        || contains(book.getAuthor(), keyword))
                .filter(book -> category == null
                        || "Tat ca".equals(category)
                        || category.equals(book.getCategoryName()))
                .toList();

        List<Book> sortedBooks = new ArrayList<>(books);
        sortedBooks.sort(bookComparator(sort));
        tblBooks.setItems(FXCollections.observableArrayList(sortedBooks));
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private Comparator<Book> bookComparator(String sort) {
        if ("Ten sach A-Z".equals(sort)) {
            return Comparator.comparing(book -> text(book.getTitle()), String.CASE_INSENSITIVE_ORDER);
        }

        if ("Tac gia A-Z".equals(sort)) {
            return Comparator.comparing(book -> text(book.getAuthor()), String.CASE_INSENSITIVE_ORDER);
        }

        if ("So luong tang".equals(sort)) {
            return Comparator.comparingInt(Book::getQuantity);
        }

        if ("So luong giam".equals(sort)) {
            return Comparator.comparingInt(Book::getQuantity).reversed();
        }

        return Comparator.comparingInt(Book::getId);
    }

    private String text(String value) {
        return value == null ? "" : value;
    }

    @FXML
    public void resetFormAction() {
        clearBookForm();
    }

    @FXML
    public void exportBooksAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuat danh sach sach");
        chooser.setInitialFileName("books.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tep CSV", "*.csv"));

        File file = chooser.showSaveDialog(tblBooks.getScene().getWindow());
        if (file == null) {
            return;
        }

        runTask(() -> client.exportBooks(file.toPath()), success -> {
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da xuat danh sach sach.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Loi", "Khong the xuat danh sach sach.");
            }
        });
    }

    @FXML
    public void importBooksAction() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Nhap danh sach sach");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tep CSV", "*.csv"));

        File file = chooser.showOpenDialog(tblBooks.getScene().getWindow());
        if (file == null) {
            return;
        }

        runTask(() -> client.importBooks(file.toPath()), success -> {
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da nhap danh sach sach.");
                loadBooks();
            } else {
                showAlert(Alert.AlertType.ERROR, "Loi", "File CSV khong hop le hoac khong the nhap du lieu.");
            }
        });
    }

    private void clearBookForm() {
        txtBookId.clear();
        txtTitle.clear();
        txtAuthor.clear();
        txtQuantity.clear();
        cbCategory.getSelectionModel().clearSelection();
        cbPublisher.getSelectionModel().clearSelection();
        tblBooks.getSelectionModel().clearSelection();
    }

    private void fillUserForm(User user) {
        if (user == null) {
            return;
        }

        txtUserId.setText(String.valueOf(user.getId()));
        txtUsername.setText(user.getUsername());
        txtUserPassword.clear();
        cbUserRole.setValue(user.getRole());
    }

    @FXML
    public void addUserAction() {
        String username = txtUsername.getText().trim();
        String password = txtUserPassword.getText();
        String role = cbUserRole.getValue();

        if (client.createUser(username, password, role)) {
            showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da them tai khoan.");
            loadUsers();
            clearUserForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the them tai khoan. Kiem tra ten dang nhap/mat khau.");
        }
    }

    @FXML
    public void updateUserAction() {
        try {
            int id = Integer.parseInt(txtUserId.getText().trim());
            String username = txtUsername.getText().trim();
            String password = txtUserPassword.getText();
            String role = cbUserRole.getValue();

            if (client.updateUser(id, username, password, role)) {
                showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da cap nhat tai khoan.");
                loadUsers();
                clearUserForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Loi", "Khong the cap nhat tai khoan.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long chon tai khoan truoc.");
        }
    }

    @FXML
    public void deleteUserAction() {
        User selected = tblUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long chon tai khoan truoc.");
            return;
        }

        if (librarian != null && librarian.getId() == selected.getId()) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the xoa tai khoan dang dang nhap.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xoa tai khoan dang chon?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK && client.deleteUser(selected.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da xoa tai khoan.");
            loadUsers();
            clearUserForm();
        }
    }

    @FXML
    public void resetUserFormAction() {
        clearUserForm();
    }

    private void clearUserForm() {
        txtUserId.clear();
        txtUsername.clear();
        txtUserPassword.clear();
        cbUserRole.setValue("borrower");
        tblUsers.getSelectionModel().clearSelection();
    }

    private void loadUsers() {
        runTask(client::getAllUsers, users -> tblUsers.setItems(FXCollections.observableArrayList(users)));
    }

    private void fillCategoryForm(String[] category) {
        if (category == null) {
            return;
        }

        txtCategoryId.setText(category[0]);
        txtCategoryName.setText(category[1]);
    }

    @FXML
    public void addCategoryAction() {
        String name = txtCategoryName.getText().trim();

        runTask(() -> client.addCategory(name), success -> {
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da them the loai.");
                afterCategoryChanged();
            } else {
                showAlert(Alert.AlertType.ERROR, "Loi", "Khong the them the loai.");
            }
        });
    }

    @FXML
    public void updateCategoryAction() {
        try {
            int id = Integer.parseInt(txtCategoryId.getText().trim());
            String name = txtCategoryName.getText().trim();

            runTask(() -> client.updateCategory(id, name), success -> {
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da cap nhat the loai.");
                    afterCategoryChanged();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Loi", "Khong the cap nhat the loai.");
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long chon the loai truoc.");
        }
    }

    @FXML
    public void deleteCategoryAction() {
        String[] selected = tblCategories.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long chon the loai truoc.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xoa the loai dang chon?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        int id = Integer.parseInt(selected[0]);
        runTask(() -> client.deleteCategory(id), success -> {
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da xoa the loai.");
                afterCategoryChanged();
            } else {
                showAlert(Alert.AlertType.ERROR, "Loi", "Khong the xoa the loai dang duoc sach su dung.");
            }
        });
    }

    @FXML
    public void resetCategoryFormAction() {
        clearCategoryForm();
    }

    private void afterCategoryChanged() {
        clearCategoryForm();
        loadCategories();
        loadStaticData();
        loadBooks();
    }

    private void clearCategoryForm() {
        txtCategoryId.clear();
        txtCategoryName.clear();
        tblCategories.getSelectionModel().clearSelection();
    }

    private void loadCategories() {
        runTask(client::getAllCategories, categories ->
                tblCategories.setItems(FXCollections.observableArrayList(categories))
        );
    }

    @FXML
    public void loadHistory() {
        runTask(client::getBorrowingHistory, history -> {
            tblHistory.setItems(FXCollections.observableArrayList(history));

            long count = history.stream()
                    .filter(row -> row.length > 4 && "borrowing".equalsIgnoreCase(row[4]))
                    .count();
            lblTotalLoaned.setText("Dang muon: " + count);
        });
    }

    @FXML
    public void handleReturnBook() {
        String[] selected = tblHistory.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long chon mot luot muon.");
            return;
        }

        if ("returned".equalsIgnoreCase(selected[4])) {
            showAlert(Alert.AlertType.INFORMATION, "Thong bao", "Sach nay da duoc tra.");
            return;
        }

        int borrowId = Integer.parseInt(selected[0]);
        int bookId = Integer.parseInt(selected[5]);

        if (client.returnBook(borrowId, bookId)) {
            showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da tra sach.");
            loadHistory();
            loadBooks();
            loadOverdueBooks();
            refreshStatistics();
        } else {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the tra sach.");
        }
    }

    @FXML
    public void refreshStatistics() {
        try {
            LocalDate date = dpStatsDate.getValue();
            int month = Integer.parseInt(txtStatsMonth.getText().trim());
            int year = Integer.parseInt(txtStatsYear.getText().trim());

            runTask(() -> new int[]{
                    client.countBorrowedByDate(date),
                    client.countBorrowedByMonth(month, year),
                    client.countBorrowedByYear(year)
            }, values -> {
                lblStatsDay.setText(String.valueOf(values[0]));
                lblStatsMonth.setText(String.valueOf(values[1]));
                lblStatsYear.setText(String.valueOf(values[2]));
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Bo loc thong ke khong hop le.");
        }
    }

    @FXML
    public void loadReport() {
        try {
            String type = cbReportType.getValue();
            LocalDate date = dpReportDate.getValue();
            int month = Integer.parseInt(txtReportMonth.getText().trim());
            int year = Integer.parseInt(txtReportYear.getText().trim());

            runTask(() -> client.getBorrowReport(type, date, month, year), rows -> {
                currentReportRows = FXCollections.observableArrayList(rows);
                tblReport.setItems(currentReportRows);
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Bo loc bao cao khong hop le.");
        }
    }

    @FXML
    public void exportReportAction() {
        if (currentReportRows.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long tai bao cao truoc khi xuat file.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Xuat bao cao");
        chooser.setInitialFileName("bao-cao-muon-sach.csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Tep CSV", "*.csv"));

        File file = chooser.showSaveDialog(tblReport.getScene().getWindow());
        if (file == null) {
            return;
        }

        if (client.exportBorrowReport(file.toPath(), currentReportRows)) {
            showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Da xuat bao cao.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the xuat bao cao.");
        }
    }

    @FXML
    public void loadOverdueBooks() {
        runTask(client::getOverdueBooks, rows -> {
            tblOverdue.setItems(FXCollections.observableArrayList(rows));

            int totalFine = rows.stream()
                    .mapToInt(row -> Integer.parseInt(row[5]))
                    .sum();
            lblTotalFine.setText("Tong tien phat: " + totalFine + " VND");
        });
    }

    private <T> void runTask(Callable<T> job, Consumer<T> onSuccess) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return job.call();
            }
        };

        task.setOnSucceeded(event -> onSuccess.accept(task.getValue()));
        task.setOnFailed(event -> showAlert(Alert.AlertType.ERROR, "Loi", "Khong the ket noi server hoac xu ly du lieu."));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
