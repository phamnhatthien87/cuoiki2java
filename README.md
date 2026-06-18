# Quản Lý Thư Viện

Dự án Hệ thống Quản lý Thư viện bao gồm hai ứng dụng Server (máy chủ) và Client (máy khách) giao tiếp với nhau. Dự án sử dụng Java, JavaFX cho giao diện và SQL Server cho cơ sở dữ liệu.

Dưới đây là các thông tin chi tiết về cách thiết lập, chạy dự án và danh sách các chức năng hiện có.

---

## 1. Cách cài đặt cơ sở dữ liệu (Database)
Dự án sử dụng **SQL Server**. Để thiết lập cơ sở dữ liệu, hãy làm theo các bước sau:
1. Mở **SQL Server Management Studio (SSMS)**.
2. Mở file script SQL (ví dụ: `ck01.sql` được đính kèm) chứa các câu lệnh tạo database `quanlythuvien`, tạo các bảng, và chèn dữ liệu mẫu.
3. Bôi đen và chạy toàn bộ script để hoàn thành việc khởi tạo CSDL.
4. **Cấu hình kết nối**: Mặc định, Server sẽ kết nối đến CSDL thông qua cổng `1433`, tài khoản `sa`, mật khẩu `123456789`. 
   - Nếu bạn dùng mật khẩu khác, hãy thiết lập các biến môi trường `DB_URL`, `DB_USER`, `DB_PASSWORD` hoặc sửa trực tiếp trong file `server-app/src/main/java/Database/ConnectDB.java`.

---

## 2. Cách chạy Server
1. Mở dự án bằng IDE (như IntelliJ IDEA, Eclipse) hoặc dùng Terminal.
2. Build dự án bằng Maven ở thư mục gốc: `mvn clean install`
3. Đi tới module `server-app`.
4. Tìm và chạy class main: `Server.ServerMain`.
5. Đảm bảo Server khởi động thành công và đang lắng nghe các kết nối từ Client.

---

## 3. Cách chạy Client
1. **Lưu ý:** Client chỉ có thể đăng nhập hoặc lấy dữ liệu khi Server **đã chạy**.
2. Đi tới module `client-app`.
3. Tìm và chạy class: `Client.ClientLauncher` (hoặc `Client.ClientMain`).
4. Hoặc nếu bạn dùng lệnh Maven, có thể di chuyển vào thư mục `client-app` và chạy lệnh: `mvn javafx:run`.
5. Sau khi chạy, giao diện đăng nhập (Login) sẽ hiện ra.

---

## 4. Tài khoản đăng nhập mẫu
Trong CSDL đã có sẵn một số tài khoản để bạn kiểm tra các chức năng:

**Tài khoản Thủ thư (Librarian - Quản lý):**
- Username: `admin` | Password: `123`
- Username: `admin01` | Password: `123`
- Username: `admin02` | Password: `nthien`
- Username: `admin03` | Password: `12345`

**Tài khoản Người mượn (Borrower - Độc giả):**
- Username: `nthien07` | Password: `123456789`
- Username: `nhathien` | Password: `nthien`

---

## 5. Các chức năng đã hoàn thiện

### Chức năng chung:
- **Đăng nhập (Login):** Xác thực người dùng và điều hướng vào giao diện tương ứng theo quyền (Thủ thư / Người mượn).
- **Đăng ký (Sign up):** Cho phép độc giả tạo tài khoản mới.

### Dành cho Thủ thư (Librarian):
- **Quản lý Sách:** Xem, thêm, sửa, xóa thông tin sách trong thư viện.
- **Quản lý Thể loại:** Cập nhật các thể loại sách.
- **Quản lý Tài khoản:** Xem danh sách người dùng.
- **Lịch sử mượn trả:** Theo dõi lịch sử độc giả mượn và trả sách.
- **Thống kê và Báo cáo:** Cung cấp thông tin tổng quan.
- **Quá hạn và tiền phạt:** Xử lý và quản lý các lượt mượn sách quá hạn.

### Dành cho Người mượn (Borrower):
- **Danh mục Sách:** Xem danh sách toàn bộ các sách hiện có trong thư viện.
- **Tìm kiếm:** Tìm kiếm nhanh sách theo tên sách hoặc thể loại.
- **Mượn Sách:** Chọn sách từ danh sách và xác nhận mượn sách.

---

## 6. Các thư viện sử dụng

Dự án được quản lý bằng Maven và chia thành các module (`shared`, `server-app`, `client-app`). Các thư viện chính bao gồm:
- **JavaFX 21.0.6** (`javafx-controls`, `javafx-fxml`, `javafx-web`, `javafx-swing`): Xây dựng giao diện người dùng mượt mà ở phía Client-app.
- **Microsoft SQL Server JDBC Driver** (`mssql-jdbc` version `13.2.1.jre11`): Hỗ trợ ứng dụng Java ở Server kết nối và thao tác với cơ sở dữ liệu SQL Server.
- **JUnit 5** (`junit-jupiter` version `5.12.1`): Phục vụ viết và thực thi các bài kiểm thử tự động.
- Các plugin của **Maven** như `maven-compiler-plugin`, `maven-shade-plugin`, và `javafx-maven-plugin` để biên dịch, đóng gói và chạy ứng dụng một cách thuận tiện.
