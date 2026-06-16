package model;

public class Borrower extends User {

    public Borrower(int id, String username, String password, String role) {
        super(id, username, password, role);
    }

    public Borrower(int id, String username, String password, String role, String email) {
        super(id, username, password, role, email);
    }

}
