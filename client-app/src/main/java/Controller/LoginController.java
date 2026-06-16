package Controller;

import Client.LibraryClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.time.LocalDateTime;

public class LoginController {

    private final LibraryClient client = LibraryClient.getInstance();
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCK_SECONDS = 30;

    private int failedAttempts = 0;
    private LocalDateTime lockedUntil;

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Button btnLogin;

    @FXML
    private void handleLogin() {
        if (isTemporarilyLocked()) {
            showAlert(Alert.AlertType.WARNING, "Tam khoa", "Ban dang nhap sai nhieu lan. Vui long thu lai sau it phut.");
            return;
        }

        String username = txtUser.getText().trim();
        String password = txtPass.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thong bao", "Vui long nhap day du tai khoan va mat khau.");
            return;
        }

        User user = client.login(username, password);
        if (user == null) {
            handleFailedLogin();
            return;
        }

        failedAttempts = 0;
        openMainScreen(user);
    }

    private void handleFailedLogin() {
        failedAttempts++;

        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            lockedUntil = LocalDateTime.now().plusSeconds(LOCK_SECONDS);
            btnLogin.setDisable(true);
            showAlert(Alert.AlertType.ERROR, "Tam khoa dang nhap", "Ban da dang nhap sai 3 lan. He thong khoa tam thoi 30 giay.");
            Thread unlockThread = new Thread(() -> {
                try {
                    Thread.sleep(LOCK_SECONDS * 1000L);
                    javafx.application.Platform.runLater(() -> {
                        failedAttempts = 0;
                        lockedUntil = null;
                        btnLogin.setDisable(false);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            unlockThread.setDaemon(true);
            unlockThread.start();
            return;
        }

        int remaining = MAX_FAILED_ATTEMPTS - failedAttempts;
        showAlert(Alert.AlertType.ERROR, "Dang nhap that bai", "Sai tai khoan hoac mat khau. Con " + remaining + " lan thu.");
    }

    private boolean isTemporarilyLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }

    @FXML
    private void openSignUp() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/signup.fxml"));
            changeScene(root, "Dang ky tai khoan");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the mo man hinh dang ky.");
        }
    }

    private void openMainScreen(User user) {
        String role = user.getRole().toLowerCase();

        if ("borrower".equals(role)) {
            openBorrowerUI(user);
            return;
        }

        if ("librarian".equals(role)) {
            openLibrarianUI(user);
            return;
        }

        showAlert(Alert.AlertType.ERROR, "Loi", "Role khong hop le.");
    }

    private void openBorrowerUI(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/borrower.fxml"));
            Parent root = loader.load();

            BorrowerController controller = loader.getController();
            controller.setBorrower(user);

            changeScene(root, "He thong - Nguoi muon");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the mo giao dien nguoi muon.");
        }
    }

    private void openLibrarianUI(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/librarian.fxml"));
            Parent root = loader.load();

            LibrarianController controller = loader.getController();
            controller.setLibrarian(user);

            changeScene(root, "He thong - Thu thu");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the mo giao dien thu thu.");
        }
    }

    private void changeScene(Parent root, String title) {
        Stage stage = (Stage) txtUser.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
