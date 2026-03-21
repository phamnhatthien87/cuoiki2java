package Controller;

import DAO.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {
        @FXML private TextField txtNewUser;
        @FXML private PasswordField txtNewPass;
        @FXML private PasswordField txtConfirmPass;

        @FXML
        public void handleSignUp() {
            String user = txtNewUser.getText().trim();
            String pass = txtNewPass.getText();
            String confirm = txtConfirmPass.getText();

            // 1. Kiểm tra rỗng
            if (user.isEmpty() || pass.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "V please nhập đầy đủ Username và Password!");
                return;
            }

            // 2. Kiểm tra khớp mật khẩu
            if (!pass.equals(confirm)) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Mật khẩu xác nhận không khớp!");
                return;
            }

            // 3. Gọi hàm register từ UserDAO
            UserDAO dao = new UserDAO();
            boolean isSuccess = dao.register(user, pass);

            if (isSuccess) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký thành công! Chào mừng " + user);
                try {
                    backToLogin(); // Đăng ký xong tự quay về màn hình Login
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Tên đăng nhập đã tồn tại hoặc lỗi hệ thống!");
            }
        }

        @FXML
        public void backToLogin() throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/View/login.fxml"));
            Stage stage = (Stage) txtNewUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng nhập");
        }

        private void showAlert(Alert.AlertType type, String title, String content) {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        }
    }
