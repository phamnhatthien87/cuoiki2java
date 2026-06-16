package model;

public class Librarian extends User {

    public Librarian(int id, String username, String password, String role) {
        super(id, username, password, role);
    }

    public Librarian(int id, String username, String password, String role, String email) {
        super(id, username, password, role, email);
    }

}
