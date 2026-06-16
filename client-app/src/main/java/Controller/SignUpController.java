package Controller;

import Client.LibraryClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{4,30}$";

    private final LibraryClient client = LibraryClient.getInstance();

    @FXML private TextField txtNewUser;
    @FXML private PasswordField txtNewPass;
    @FXML private PasswordField txtConfirmPass;

    @FXML
    private void handleSignUp() {
        String username = txtNewUser.getText().trim();
        String password = txtNewPass.getText().trim();
        String confirmPassword = txtConfirmPass.getText().trim();

        if (!isValidInput(username, password, confirmPassword)) {
            return;
        }

        if (client.register(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Thanh cong", "Dang ky thanh cong.");
            backToLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "That bai", "Ten dang nhap da ton tai hoac mat khau khong hop le.");
        }
    }

    @FXML
    private void backToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/login.fxml"));
            Stage stage = (Stage) txtNewUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dang nhap");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Khong the quay lai man hinh dang nhap.");
        }
    }

    private boolean isValidInput(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Vui long nhap day du thong tin.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Mat khau xac nhan khong khop.");
            return false;
        }

        if (!username.matches(USERNAME_PATTERN)) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Ten dang nhap chi gom chu, so, dau _ va dai 4-30 ky tu.");
            return false;
        }

        if (password.length() < 10) {
            showAlert(Alert.AlertType.ERROR, "Loi", "Mat khau phai co it nhat 10 ky tu.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
