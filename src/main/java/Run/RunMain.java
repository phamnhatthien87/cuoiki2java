package Run;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RunMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        var url = getClass().getResource("/View/login.fxml");
        System.out.println("FXML URL = " + url);

        FXMLLoader loader = new FXMLLoader(url);
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
