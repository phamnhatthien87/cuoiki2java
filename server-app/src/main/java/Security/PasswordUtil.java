package Security;

import java.security.MessageDigest;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Khong the ma hoa mat khau", e);
        }
    }

    public static boolean verifyPassword(String password, String storedPassword) {
        if (password == null || storedPassword == null) {
            return false;
        }
        return hashPassword(password).equals(storedPassword);
    }

    public static boolean isHashed(String storedPassword) {
        // SHA-256 hex string is exactly 64 characters long
        return storedPassword != null && storedPassword.length() == 64 && storedPassword.matches("^[0-9a-fA-F]+$");
    }
}
