package Database;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) {
        try (Connection connection = ConnectDB.getConnection()) {
            System.out.println("Ket noi database thanh cong.");
        } catch (SQLException e) {
            System.out.println("Ket noi database that bai: " + e.getMessage());
        }
    }
}
