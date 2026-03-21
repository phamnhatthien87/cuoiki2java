package Controller;

import DAO.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

public class LoginController {

    private model.User librarian;


    @FXML
    private TextField txtUser;

    @FXML
    private PasswordField txtPass;

    @FXML
    private void handleLogin() {
        User u = new UserDAO().login(
                txtUser.getText(),
                txtPass.getText()
        );

        if (u != null) {
            System.out.println("Login success: " + u.getRole());

            //  Nếu là borrower thì mở giao diện borrower.fxml
            if ("borrower".equalsIgnoreCase(u.getRole())) {
                openBorrowerUI(u);
            }

            //  Nếu là librarian thì mở giao diện librarian.fxml
            if ("librarian".equalsIgnoreCase(u.getRole())) {
                openLibrarianUI(u);
            }

        } else {
            System.out.println("Login failed");
        }
    }


    // chuyển sang giao diện người mượn
    private void openBorrowerUI(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/borrower.fxml"));
            Parent root = loader.load();

            BorrowerController controller = loader.getController();
            controller.setBorrower(user);

            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   // chuyển sang giao diện thủ thư
    private void openLibrarianUI(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/librarian.fxml"));
            Parent root = loader.load();


            LibrarianController controller = loader.getController();
            controller.setLibrarian(user);

            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void openSignUp() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/signup.fxml"));
            Stage stage = (Stage) txtUser.getScene().getWindow(); // txtUser là TextField trong login.fxml
            stage.setScene(new Scene(root));
            stage.setTitle("Đăng ký tài khoản");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
