package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

    private static final String URL =
            getEnvOrDefault(
                    "DB_URL",
                    "jdbc:sqlserver://localhost:1433;databaseName=quanlythuvien;encrypt=true;trustServerCertificate=true"
            );
    private static final String USER = getEnvOrDefault("DB_USER", "sa");
    private static final String PASSWORD = getEnvOrDefault("DB_PASSWORD", "123456789");

    private ConnectDB() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
