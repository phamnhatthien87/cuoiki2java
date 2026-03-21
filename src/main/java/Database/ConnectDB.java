package Database;
import java.sql.Connection;
import java.sql.DriverManager;
public class ConnectDB {
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;databaseName=quanlythuvien;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "123456789";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
