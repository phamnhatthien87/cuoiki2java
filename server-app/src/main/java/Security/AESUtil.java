package Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Tiện ích mã hoá/giải mã thông tin cá nhân nhạy cảm bằng thuật toán AES.
 * Sử dụng mã hoá 2 chiều (khác với hash 1 chiều của mật khẩu):
 *   - Mã hoá (Encrypt): Trước khi lưu vào database.
 *   - Giải mã (Decrypt): Sau khi đọc từ database để hiển thị ra UI.
 */
public final class AESUtil {

    // Khoá bí mật AES 16 ký tự = 128 bit
    private static final String SECRET_KEY = "LibraryApp@2025!";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private AESUtil() {
    }

    /**
     * Mã hoá một chuỗi văn bản thành chuỗi Base64 để lưu database.
     *
     * @param plainText Chuỗi cần mã hoá (VD: "user@example.com")
     * @return Chuỗi đã mã hoá (VD: "hJ3k..."), hoặc null nếu đầu vào trống
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isBlank()) {
            return null;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Khong the ma hoa du lieu.", e);
        }
    }

    /**
     * Giải mã một chuỗi Base64 từ database thành văn bản gốc.
     *
     * @param encryptedText Chuỗi đã mã hoá lấy từ database
     * @return Chuỗi văn bản gốc (VD: "user@example.com"), hoặc null nếu đầu vào trống
     */
    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isBlank()) {
            return null;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted);
        } catch (Exception e) {
            // Nếu dữ liệu cũ chưa được mã hoá, trả về nguyên bản
            return encryptedText;
        }
    }
}
