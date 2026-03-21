module com.example.cuoiki {

    // ===== JavaFX =====
    requires javafx.controls;
    requires javafx.fxml;

    // ===== JDBC =====
    requires java.sql;

    // ===== UI libs (bạn có thể giữ hoặc xóa nếu không dùng) =====
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    // ===== OPEN cho FXML (BẮT BUỘC) =====
    opens Controller to javafx.fxml;
    opens model to javafx.base;

    // ===== EXPORT (Main + Controller) =====
    exports Run;
    exports Controller;
}
