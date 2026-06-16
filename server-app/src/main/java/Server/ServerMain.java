package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerMain {

    private static final int DEFAULT_PORT = 9000;
    private static final int DEFAULT_THREADS = 10;

    public static void main(String[] args) {
        int port = getInt("SERVER_PORT", DEFAULT_PORT);
        int threads = getInt("SERVER_THREADS", DEFAULT_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Library server dang chay tai port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(new ClientHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static int getInt(String key, int defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
