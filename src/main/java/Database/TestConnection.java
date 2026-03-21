package Database;

import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {
        Connection conn = ConnectDB.getConnection();

        if (conn != null) {
            System.out.println(" Kết nối database thành công ");
        } else {
            System.out.println(" Kết nối database thất bại ");
        }

        // Đóng kết nối
        try {
            if (conn != null) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
