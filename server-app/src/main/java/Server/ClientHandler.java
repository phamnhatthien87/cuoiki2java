package Server;

import model.Book;
import shared.network.Request;
import shared.network.Response;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    private final Socket socket;
    private final LibraryServer server = LibraryServer.getInstance();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                Socket clientSocket = socket
        ) {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();

            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            Request request = (Request) in.readObject();

            Response response = handle(request);
            out.writeObject(response);
            out.flush();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Khong the xu ly ket noi client.", e);
        }
    }

    private Response handle(Request request) {
        try {
            String action = request.getAction();

            switch (action) {
                case "LOGIN":
                    return Response.ok(server.login(
                            (String) request.getParam(0),
                            (String) request.getParam(1)
                    ));

                case "REGISTER":
                    return Response.ok(server.register(
                            (String) request.getParam(0),
                            (String) request.getParam(1),
                            (String) request.getParam(2)  // email
                    ));

                case "GET_ALL_USERS":
                    return Response.ok(server.getAllUsers());

                case "CREATE_USER":
                    return Response.ok(server.createUser(
                            (String) request.getParam(0),
                            (String) request.getParam(1),
                            (String) request.getParam(2),
                            (String) request.getParam(3)  // email
                    ));

                case "UPDATE_USER":
                    return Response.ok(server.updateUser(
                            (Integer) request.getParam(0),
                            (String) request.getParam(1),
                            (String) request.getParam(2),
                            (String) request.getParam(3),
                            (String) request.getParam(4)  // email
                    ));

                case "DELETE_USER":
                    return Response.ok(server.deleteUser((Integer) request.getParam(0)));

                case "GET_ALL_BOOKS":
                    return Response.ok(server.getAllBooks());

                case "SEARCH_BOOKS":
                    return Response.ok(server.searchBooks((String) request.getParam(0)));

                case "ADD_BOOK":
                    return Response.ok(server.addBook((Book) request.getParam(0)));

                case "UPDATE_BOOK":
                    return Response.ok(server.updateBook((Book) request.getParam(0)));

                case "DELETE_BOOK":
                    return Response.ok(server.deleteBook((Integer) request.getParam(0)));

                case "EXPORT_BOOKS":
                    return Response.ok(server.exportBooks(Path.of((String) request.getParam(0))));

                case "IMPORT_BOOKS":
                    return Response.ok(server.importBooks(Path.of((String) request.getParam(0))));

                case "GET_CATEGORIES":
                    return Response.ok(server.getCategories());

                case "GET_ALL_CATEGORIES":
                    return Response.ok(server.getAllCategories());

                case "ADD_CATEGORY":
                    return Response.ok(server.addCategory((String) request.getParam(0)));

                case "UPDATE_CATEGORY":
                    return Response.ok(server.updateCategory(
                            (Integer) request.getParam(0),
                            (String) request.getParam(1)
                    ));

                case "DELETE_CATEGORY":
                    return Response.ok(server.deleteCategory((Integer) request.getParam(0)));

                case "GET_PUBLISHERS":
                    return Response.ok(server.getPublishers());

                case "BORROW_BOOK":
                    return Response.ok(server.borrowBook(
                            (Integer) request.getParam(0),
                            (Integer) request.getParam(1)
                    ));

                case "RETURN_BOOK":
                    return Response.ok(server.returnBook(
                            (Integer) request.getParam(0),
                            (Integer) request.getParam(1)
                    ));

                case "IS_BOOK_BORROWED":
                    return Response.ok(server.isBookBorrowed((Integer) request.getParam(0)));

                case "GET_BORROWING_HISTORY":
                    return Response.ok(server.getBorrowingHistory());

                case "GET_OVERDUE_BOOKS":
                    return Response.ok(server.getOverdueBooks());

                case "COUNT_BORROWED_BY_DATE":
                    return Response.ok(server.countBorrowedByDate((LocalDate) request.getParam(0)));

                case "COUNT_BORROWED_BY_MONTH":
                    return Response.ok(server.countBorrowedByMonth(
                            (Integer) request.getParam(0),
                            (Integer) request.getParam(1)
                    ));

                case "COUNT_BORROWED_BY_YEAR":
                    return Response.ok(server.countBorrowedByYear((Integer) request.getParam(0)));

                case "GET_BORROW_REPORT":
                    return Response.ok(server.getBorrowReport(
                            (String) request.getParam(0),
                            (LocalDate) request.getParam(1),
                            (Integer) request.getParam(2),
                            (Integer) request.getParam(3)
                    ));

                case "EXPORT_BORROW_REPORT":
                    @SuppressWarnings("unchecked")
                    List<String[]> rows = (List<String[]>) request.getParam(1);
                    return Response.ok(server.exportBorrowReport(
                            Path.of((String) request.getParam(0)),
                            rows
                    ));

                default:
                    return Response.fail("Action khong ton tai: " + action);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Server xu ly request bi loi.", e);
            return Response.fail("Server xu ly request bi loi: " + e.getMessage());
        }
    }
}
