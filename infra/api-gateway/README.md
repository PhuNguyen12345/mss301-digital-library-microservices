# 🚪 Digilib API Gateway (`mss30-digilib-api-gateway`)

## 📖 Giới thiệu
**API Gateway** là điểm truy cập duy nhất (Single Entry Point) cho toàn bộ hệ thống Digital Library Management System. Phân hệ này chịu trách nhiệm tiếp nhận mọi request từ người dùng hoặc UI (React.js), sau đó định tuyến (routing) chính xác đến các Microservices nội bộ ở phía sau. 

Được thiết kế dựa trên **Spring Cloud Gateway**, service này tích hợp chặt chẽ với Eureka Server để tự động khám phá và định tuyến đến các dịch vụ mà không cần hardcode địa chỉ IP hay Port cố định.

## 🚀 Chức năng chính (Theo tiến độ)
* **Tuần 2 - Cơ sở hạ tầng:** Tự động định tuyến (Dynamic Routing) đến các service nội bộ thông qua Service Discovery.
* **Tuần 5 - Xử lý lỗi:** Global Exception Handling cho toàn bộ luồng request đầu vào.
* **Tuần 7 - Bảo mật:** Là chốt chặn đầu tiên kiểm tra và xác thực **JWT Token**, áp dụng Role-Based Access Control (RBAC) trước khi cho phép request đi sâu vào hệ thống.
* **Xử lý chung:** Cấu hình CORS (Cross-Origin Resource Sharing) để Frontend gọi API không bị lỗi.

## 🛠 Tech Stack
* **Ngôn ngữ:** Java 21
* **Framework:** Spring Boot 3.x
* **Cloud Components:** Spring Cloud Gateway, Netflix Eureka Client
* **Quản lý thư viện:** Maven

## ⚙️ Cài đặt & Khởi chạy

### Yêu cầu hệ thống
* JDK 21
* Maven 3.8+
* **Lưu ý cực kỳ quan trọng:** Service `mss30-digilib-eureka-server` phải đang được chạy hoàn tất trước khi khởi động API Gateway.

### Các bước thực hiện
1. **Clone repository về máy:**
   ```bash
   git clone https://github.com/PhuNguyen12345/mss301-digilib-api-gateway.git
   cd mss301-digilib-api-gateway