# 📚 Digilib Book Catalog Service (`mss301-digilib-book-service`)

## 📖 Giới thiệu
**Book Catalog Service** là một Core Business Service trong hệ thống **Digital Library Management System**. Service này chịu trách nhiệm quản lý toàn bộ vòng đời và thông tin của tài liệu trong thư viện, bao gồm sách vật lý và tài liệu số (e-book).

Đây là service độc lập, sở hữu cơ sở dữ liệu riêng (Database per service) để đảm bảo tính toàn vẹn của danh mục sách, đồng thời cung cấp dữ liệu cho các service khác (như `loan-service`) thông qua giao tiếp Inter-service.

## 🚀 Chức năng chính
* **Quản lý danh mục (CRUD):** Thêm sách mới, cập nhật thông tin (tiêu đề, tác giả, ISBN), xóa/ẩn sách.
* **Quản lý kho:** Theo dõi tổng số lượng sách (`total_copies`) và số lượng sách sẵn sàng cho mượn (`available_copies`).
* **Tìm kiếm & Phân loại:** Hỗ trợ tìm kiếm sách theo từ khóa, tác giả, hoặc thể loại.

## 🛠 Tech Stack
* **Ngôn ngữ:** Java 21
* **Framework:** Spring Boot 3.x
* **Database:** MySQL (hoặc PostgreSQL) + Spring Data JPA
* **Cloud Components:** Netflix Eureka Discovery Client
* **Khác:** Lombok, Spring Web, Maven

## ⚙️ Cài đặt & Khởi chạy

### Yêu cầu hệ thống
* JDK 21
* Maven 3.8+
* Hệ quản trị CSDL MySQL đang chạy ở local (hoặc qua Docker).
* ⚠️ **Lưu ý:** Service `mss301-digilib-eureka-server` phải đang được chạy hoàn tất để Book Service có thể đăng ký thành công.

### Các bước thực hiện
1. **Clone repository về máy:**
   ```bash
   git clone https://github.com/PhuNguyen12345/mss301-digilib-book-service.git
   cd mss301-digilib-book-service
