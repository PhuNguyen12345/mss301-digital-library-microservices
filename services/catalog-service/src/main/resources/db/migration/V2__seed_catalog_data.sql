-- =========================
-- 1. INSERT categories
-- =========================
INSERT INTO categories (category_id, category_name, description, is_deleted) VALUES
(1, 'Công nghệ thông tin', 'Sách về lập trình, cơ sở dữ liệu, mạng máy tính và phát triển phần mềm.', FALSE),
(2, 'Kinh tế', 'Sách về quản trị kinh doanh, marketing, tài chính và khởi nghiệp.', FALSE),
(3, 'Văn học', 'Sách truyện, tiểu thuyết, thơ ca và các tác phẩm văn học.', FALSE),
(4, 'Giáo dục', 'Sách về phương pháp giảng dạy, tâm lý học giáo dục và nghiên cứu giáo dục.', FALSE),
(5, 'Khoa học', 'Sách về vật lý, hóa học, sinh học và kiến thức khoa học phổ thông.', FALSE);

-- =========================
-- 2. INSERT classifications
-- =========================
INSERT INTO classifications
(classification_id, classification_system, classification_name, classification_code, is_deleted) VALUES
(1, 'DDC', 'Tin học, thông tin và tác phẩm tổng quát', 0, FALSE),
(2, 'DDC', 'Khoa học xã hội', 300, FALSE),
(3, 'DDC', 'Ngôn ngữ', 400, FALSE),
(4, 'DDC', 'Khoa học tự nhiên', 500, FALSE),
(5, 'DDC', 'Văn học', 800, FALSE);

-- =========================
-- 3. INSERT books
-- =========================
INSERT INTO books
(book_id, isbn, title, author, publisher, publication_year, edition, book_status,
 created_at, updated_at, language, description, cover_image_url,
 category_id, classification_id, is_deleted)
VALUES
(1, '9786041230001', 'Nhập môn Lập trình Java', 'Nguyễn Văn Minh', 'Nhà xuất bản Giáo dục Việt Nam', 2021, 'Tái bản lần 1', 'ACTIVE',
 '2026-06-17 08:00:00', '2026-06-17 08:15:00', 'Tiếng Việt',
 'Cuốn sách cung cấp kiến thức cơ bản về lập trình Java, biến, hàm, lớp, đối tượng và xử lý ngoại lệ.',
 '/files/images/lap-trinh-java.jpg',
 1, 1, FALSE),

(2, '9786041230002', 'Cơ sở Dữ liệu Căn bản', 'Trần Thị Lan', 'Nhà xuất bản Đại học Quốc gia', 2020, 'Ấn bản thứ 2', 'ACTIVE',
 '2026-06-17 08:20:00', '2026-06-17 08:35:00', 'Tiếng Việt',
 'Sách trình bày các khái niệm về cơ sở dữ liệu, mô hình quan hệ, SQL, khóa chính, khóa ngoại và chuẩn hóa dữ liệu.',
 '/files/images/co-so-du-lieu.jpg',
 1, 1, FALSE),

(3, '9786041230003', 'Phân tích và Thiết kế Hệ thống', 'Lê Hoàng Nam', 'Nhà xuất bản Thống kê', 2022, 'Ấn bản đầu tiên', 'ACTIVE',
 '2026-06-17 08:40:00', '2026-06-17 08:55:00', 'Tiếng Việt',
 'Tài liệu hướng dẫn phân tích yêu cầu, thiết kế hệ thống, xây dựng sơ đồ UML và mô hình dữ liệu.',
 '/files/images/phan-tich-thiet-ke.jpg',
 1, 1, FALSE),

(4, '9786041230004', 'Quản trị Kinh doanh Hiện đại', 'Phạm Quốc Huy', 'Nhà xuất bản Kinh tế Quốc dân', 2019, 'Tái bản lần 2', 'ACTIVE',
 '2026-06-17 09:00:00', '2026-06-17 09:15:00', 'Tiếng Việt',
 'Cuốn sách giới thiệu các nguyên lý quản trị doanh nghiệp, quản trị nhân sự, chiến lược và vận hành kinh doanh.',
 '/files/images/quan-tri-kinh-doanh.jpg',
 2, 2, FALSE),

(5, '9786041230005', 'Marketing Căn bản', 'Đỗ Thị Mai', 'Nhà xuất bản Lao động', 2021, 'Ấn bản thứ 1', 'ACTIVE',
 '2026-06-17 09:20:00', '2026-06-17 09:35:00', 'Tiếng Việt',
 'Sách cung cấp kiến thức nền tảng về marketing, hành vi khách hàng, phân khúc thị trường và xây dựng thương hiệu.',
 '/files/images/marketing-can-ban.jpg',
 2, 2, FALSE),

(6, '9786041230006', 'Tuổi Trẻ Đáng Giá Bao Nhiêu', 'Rosie Nguyễn', 'Nhà xuất bản Hội Nhà Văn', 2018, 'Tái bản', 'ACTIVE',
 '2026-06-17 09:40:00', '2026-06-17 09:55:00', 'Tiếng Việt',
 'Một cuốn sách truyền cảm hứng về học tập, trải nghiệm, phát triển bản thân và định hướng tương lai cho người trẻ.',
 '/files/images/tuoi-tre-dang-gia-bao-nhieu.jpg',
 3, 5, FALSE),

(7, '9786041230007', 'Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', 'Nhà xuất bản Kim Đồng', 2020, 'Tái bản', 'ACTIVE',
 '2026-06-17 10:00:00', '2026-06-17 10:15:00', 'Tiếng Việt',
 'Tác phẩm văn học thiếu nhi nổi tiếng kể về hành trình phiêu lưu và trưởng thành của nhân vật Dế Mèn.',
 '/files/images/de-men-phieu-luu-ky.jpg',
 3, 5, FALSE),

(8, '9786041230008', 'Tâm lý học Giáo dục', 'Nguyễn Thị Hoa', 'Nhà xuất bản Giáo dục Việt Nam', 2022, 'Ấn bản thứ 1', 'ACTIVE',
 '2026-06-17 10:20:00', '2026-06-17 10:35:00', 'Tiếng Việt',
 'Sách trình bày các vấn đề về tâm lý người học, động cơ học tập, phương pháp giáo dục và ứng dụng trong giảng dạy.',
 '/files/images/tam-ly-hoc-giao-duc.jpg',
 4, 2, FALSE),

(9, '9786041230009', 'Phương pháp Nghiên cứu Khoa học', 'Vũ Minh Đức', 'Nhà xuất bản Đại học Sư phạm', 2021, 'Tái bản lần 1', 'ACTIVE',
 '2026-06-17 10:40:00', '2026-06-17 10:55:00', 'Tiếng Việt',
 'Tài liệu hướng dẫn xây dựng đề tài nghiên cứu, câu hỏi nghiên cứu, phương pháp thu thập và phân tích dữ liệu.',
 '/files/images/nghien-cuu-khoa-hoc.jpg',
 4, 2, FALSE),

(10, '9786041230010', 'Khám phá Vũ trụ', 'Hoàng Anh Tuấn', 'Nhà xuất bản Khoa học và Kỹ thuật', 2020, 'Ấn bản đầu tiên', 'ACTIVE',
 '2026-06-17 11:00:00', '2026-06-17 11:15:00', 'Tiếng Việt',
 'Cuốn sách giới thiệu kiến thức phổ thông về hệ mặt trời, thiên hà, hố đen và các hiện tượng trong vũ trụ.',
 '/files/images/kham-pha-vu-tru.jpg',
 5, 4, FALSE);

-- =========================
-- 4. INSERT book_copies
-- =========================
INSERT INTO book_copies
(copy_id, barcode, shelf_location, acquisition_date, copy_status, book_id, is_deleted)
VALUES
(1, 'TV000001', 'A1-01', '2024-01-10', 'AVAILABLE', 1, FALSE),
(2, 'TV000002', 'A1-02', '2024-01-10', 'BORROWED', 1, FALSE),
(3, 'TV000003', 'A1-03', '2024-02-15', 'AVAILABLE', 2, FALSE),
(4, 'TV000004', 'A1-04', '2024-02-15', 'AVAILABLE', 2, FALSE),
(5, 'TV000005', 'A2-01', '2024-03-20', 'AVAILABLE', 3, FALSE),
(6, 'TV000006', 'B1-01', '2023-11-05', 'AVAILABLE', 4, FALSE),
(7, 'TV000007', 'B1-02', '2023-11-05', 'DAMAGED', 4, FALSE),
(8, 'TV000008', 'B2-01', '2023-10-12', 'AVAILABLE', 5, FALSE),
(9, 'TV000009', 'C1-01', '2024-04-01', 'AVAILABLE', 6, FALSE),
(10, 'TV000010', 'C1-02', '2024-04-02', 'BORROWED', 7, FALSE),
(11, 'TV000011', 'D1-01', '2024-05-08', 'AVAILABLE', 8, FALSE),
(12, 'TV000012', 'D1-02', '2024-05-10', 'AVAILABLE', 9, FALSE),
(13, 'TV000013', 'E1-01', '2024-05-15', 'AVAILABLE', 10, FALSE),
(14, 'TV000014', 'E1-02', '2024-06-01', 'DAMAGED', 10, FALSE);

-- =========================
-- 5. INSERT digital_resources
-- =========================
INSERT INTO digital_resources
(resource_id, file_format, resource_url, access_permission, uploaded_at, book_id, is_deleted)
VALUES
(1, 'PDF', '/files/resources/nhap-mon-java.pdf', 'MEMBER', '2026-06-17 13:00:00', 1, FALSE),
(2, 'PDF', '/files/resources/co-so-du-lieu.pdf', 'MEMBER', '2026-06-17 13:10:00', 2, FALSE),
(3, 'PDF', '/files/resources/phan-tich-thiet-ke-he-thong.pdf', 'MEMBER', '2026-06-17 13:20:00', 3, FALSE),
(4, 'PDF', '/files/resources/quan-tri-kinh-doanh.pdf', 'MEMBER', '2026-06-17 13:30:00', 4, FALSE),
(5, 'PDF', '/files/resources/marketing-can-ban.pdf', 'PUBLIC', '2026-06-17 13:40:00', 5, FALSE),
(6, 'PDF', '/files/resources/tuoi-tre-dang-gia-bao-nhieu.pdf', 'PUBLIC', '2026-06-17 13:50:00', 6, FALSE),
(7, 'PDF', '/files/resources/de-men-phieu-luu-ky.pdf', 'PUBLIC', '2026-06-17 14:00:00', 7, FALSE),
(8, 'PDF', '/files/resources/tam-ly-hoc-giao-duc.pdf', 'MEMBER', '2026-06-17 14:10:00', 8, FALSE),
(9, 'PDF', '/files/resources/phuong-phap-nghien-cuu.pdf', 'MEMBER', '2026-06-17 14:20:00', 9, FALSE),
(10, 'PDF', '/files/resources/kham-pha-vu-tru.pdf', 'PUBLIC', '2026-06-17 14:30:00', 10, FALSE);

-- =========================
-- 6. RESET IDENTITY SEQUENCES
-- =========================
SELECT setval(pg_get_serial_sequence('categories', 'category_id'),
              (SELECT MAX(category_id) FROM categories), TRUE);

SELECT setval(pg_get_serial_sequence('classifications', 'classification_id'),
              (SELECT MAX(classification_id) FROM classifications), TRUE);

SELECT setval(pg_get_serial_sequence('books', 'book_id'),
              (SELECT MAX(book_id) FROM books), TRUE);

SELECT setval(pg_get_serial_sequence('book_copies', 'copy_id'),
              (SELECT MAX(copy_id) FROM book_copies), TRUE);

SELECT setval(pg_get_serial_sequence('digital_resources', 'resource_id'),
              (SELECT MAX(resource_id) FROM digital_resources), TRUE);
