IF DB_ID(N'quanlythuvien') IS NULL
BEGIN
    CREATE DATABASE quanlythuvien;
END;
GO

USE quanlythuvien;
GO

IF OBJECT_ID(N'dbo.View_BorrowingDetails', N'V') IS NOT NULL
    DROP VIEW dbo.View_BorrowingDetails;
GO

IF OBJECT_ID(N'dbo.Borrowings', N'U') IS NOT NULL
    DROP TABLE dbo.Borrowings;

IF OBJECT_ID(N'dbo.Books', N'U') IS NOT NULL
    DROP TABLE dbo.Books;

IF OBJECT_ID(N'dbo.Publishers', N'U') IS NOT NULL
    DROP TABLE dbo.Publishers;

IF OBJECT_ID(N'dbo.Categories', N'U') IS NOT NULL
    DROP TABLE dbo.Categories;

IF OBJECT_ID(N'dbo.Users', N'U') IS NOT NULL
    DROP TABLE dbo.Users;
GO

CREATE TABLE Users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL,
    -- Email duoc ma hoa AES 2 chieu truoc khi luu, giai ma khi hien thi
    email NVARCHAR(500) NULL,
    CONSTRAINT CK_Users_Role CHECK (role IN (N'librarian', N'borrower'))
);

CREATE TABLE Categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    categoryName NVARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE Publishers (
    id INT IDENTITY(1,1) PRIMARY KEY,
    publisherName NVARCHAR(100) NOT NULL UNIQUE,
    address NVARCHAR(200) NULL
);

CREATE TABLE Books (
    id INT IDENTITY(101,1) PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    author NVARCHAR(100) NULL,
    quantity INT NOT NULL DEFAULT 0,
    loaned_out INT NOT NULL DEFAULT 0,
    categoryId INT NOT NULL,
    publisherId INT NOT NULL,
    CONSTRAINT CK_Books_Quantity CHECK (quantity >= 0),
    CONSTRAINT CK_Books_LoanedOut CHECK (loaned_out >= 0),
    CONSTRAINT FK_Book_Category FOREIGN KEY (categoryId) REFERENCES Categories(id),
    CONSTRAINT FK_Book_Publisher FOREIGN KEY (publisherId) REFERENCES Publishers(id)
);

CREATE TABLE Borrowings (
    id INT IDENTITY(1,1) PRIMARY KEY,
    userId INT NOT NULL,
    bookId INT NOT NULL,
    borrowDate DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    returnDate DATE NULL,
    status NVARCHAR(20) NOT NULL DEFAULT N'borrowing',
    CONSTRAINT CK_Borrowings_Status CHECK (status IN (N'borrowing', N'returned')),
    CONSTRAINT FK_Borrow_User FOREIGN KEY (userId) REFERENCES Users(id),
    CONSTRAINT FK_Borrow_Book FOREIGN KEY (bookId) REFERENCES Books(id)
);
GO

CREATE VIEW View_BorrowingDetails AS
SELECT
    Br.id AS BorrowID,
    U.username AS BorrowerName,
    B.title AS BookTitle,
    Br.borrowDate AS BorrowDate,
    Br.status AS Status,
    Br.bookId AS BookID
FROM Borrowings Br
JOIN Users U ON Br.userId = U.id
JOIN Books B ON Br.bookId = B.id;
GO

-- Mat khau: admin=Admin@123456, nthien07=Nthien@2024abc, nhathien=Nhathien@2024, admin01/02/03=Admin@123456
-- Email cot duoi da duoc ma hoa AES voi key 'LibraryApp@2025!' - chi doc duoc qua AESUtil.decrypt()
INSERT INTO Users (username, password, role, email) VALUES
(N'admin',    N'PBKDF2$65536$SqJhGU4LiUttPCzMe5iyYw$0AMLJe7z8aUS4kVO/DgvGQ', N'librarian', N'q/4nLvCDWU+8wqTrEWCb3T4OJwwERs0OzXm3NLvPFKs='),
(N'nthien07', N'PBKDF2$65536$LDNblNI3Hu1r0pTW8TR59Q$qWHZi//Soxey31B5j7hvJw', N'borrower',  N'C9Cx/4C3dj1d2h0XgpZM0eNtnPFv2b73FdQZV7WUQb0='),
(N'nhathien', N'PBKDF2$65536$eYfhq1tRcQClA3wTVzJIyA$gKFGllePkBzquZv75O0nmA', N'borrower',  N'C9Cx/4C3dj1d2h0XgpZM0eNtnPFv2b73FdQZV7WUQb0='),
(N'admin01',  N'PBKDF2$65536$GhRb3apsGihXf2UaS19A5A$K7ulKvwkV4kJJQr4ozw7GA', N'librarian', NULL),
(N'admin02',  N'PBKDF2$65536$bUoKiExpru95igmSycA5bg$j3kaVGpiP9a7UrfBEcuj2A', N'librarian', NULL),
(N'admin03',  N'PBKDF2$65536$ryLqTOt2VPatUBy979WZNA$F1VVCbjc1V0ZYnZSBtoThw', N'librarian', NULL);

INSERT INTO Categories (categoryName) VALUES
(N'Công nghệ thông tin'),
(N'Toán học'),
(N'Giáo trình'),
(N'Lập trình Java'),
(N'Cơ sở dữ liệu'),
(N'Trí tuệ nhân tạo'),
(N'Kỹ năng sống'),
(N'Phát triển bản thân'),
(N'Kinh tế - Tài chính'),
(N'Ngoại ngữ (IELTS/TOEIC)'),
(N'Văn học Việt Nam'),
(N'Tiểu thuyết nước ngoài');

INSERT INTO Publishers (publisherName, address) VALUES
(N'NXB Giáo dục', NULL),
(N'NXB Trẻ', NULL),
(N'NXB Kim Đồng', NULL),
(N'NXB Tổng hợp TP.HCM', NULL),
(N'NXB Lao Động', NULL),
(N'NXB Phụ Nữ', NULL),
(N'NXB Đại học Quốc gia', NULL),
(N'NXB Nhã Nam', NULL),
(N'NXB Alphabooks', NULL);

INSERT INTO Books (title, author, quantity, loaned_out, categoryId, publisherId) VALUES
(N'Java Core Căn Bản', N'Nguyễn Văn A', 12, 0, 4, 7),
(N'Lập Trình Java Nâng Cao', N'Trần Văn B', 8, 0, 4, 7),
(N'Cơ Sở Dữ Liệu', N'Lê Văn C', 10, 0, 5, 1),
(N'Nhập Môn Trí Tuệ Nhân Tạo', N'Phạm Văn D', 6, 0, 6, 7),
(N'Tư Duy Nhanh Và Chậm', N'Daniel Kahneman', 5, 0, 8, 9),
(N'Tôi Tài Giỏi, Bạn Cũng Thế', N'Adam Khoo', 7, 0, 7, 2),
(N'Dế Mèn Phiêu Lưu Ký', N'Tô Hoài', 9, 0, 11, 3),
(N'IELTS Foundation', N'Rachael Roberts', 4, 0, 10, 1);

INSERT INTO Borrowings (userId, bookId, borrowDate, returnDate, status) VALUES
(2, 101, DATEADD(day, -3, CAST(GETDATE() AS DATE)), NULL, N'borrowing'),
(3, 103, DATEADD(day, -20, CAST(GETDATE() AS DATE)), NULL, N'borrowing'),
(2, 105, DATEADD(day, -15, CAST(GETDATE() AS DATE)), DATEADD(day, -5, CAST(GETDATE() AS DATE)), N'returned');

UPDATE Books
SET loaned_out = loaned_out + 1,
    quantity = CASE WHEN quantity > 0 THEN quantity - 1 ELSE quantity END
WHERE id IN (101, 103);
GO
