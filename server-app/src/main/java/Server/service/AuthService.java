package Server.service;

import DAO.UserDAO;
import model.User;

import java.util.List;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return null;
        }

        return userDAO.login(username.trim(), password);
    }

    public boolean register(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9_]{4,30}$") || password.length() < 10) {
            return false;
        }

        return userDAO.register(username.trim(), password);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean createUser(String username, String password, String role) {
        if (!isValidUserInput(username, role) || isBlank(password) || password.length() < 8) {
            return false;
        }

        return userDAO.createUser(username.trim(), password, normalizeRole(role));
    }

    public boolean updateUser(int id, String username, String password, String role) {
        if (id <= 0 || !isValidUserInput(username, role)) {
            return false;
        }

        if (password != null && !password.isBlank() && password.length() < 8) {
            return false;
        }

        return userDAO.updateUser(id, username.trim(), password, normalizeRole(role));
    }

    public boolean deleteUser(int id) {
        if (id <= 0) {
            return false;
        }

        return userDAO.deleteUser(id);
    }

    private boolean isValidUserInput(String username, String role) {
        return !isBlank(username)
                && username.matches("^[a-zA-Z0-9_]{4,30}$")
                && ("borrower".equalsIgnoreCase(role) || "librarian".equalsIgnoreCase(role));
    }

    private String normalizeRole(String role) {
        return "librarian".equalsIgnoreCase(role) ? "librarian" : "borrower";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
