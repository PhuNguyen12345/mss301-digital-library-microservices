# 🧭 Digilib Service Discovery (`mss301-digilib-eureka-server`)

## 📖 Giới thiệu
**Eureka Server** đóng vai trò là "Trái tim" (Service Registry) của toàn bộ hệ thống kiến trúc Microservices trong dự án **Digital Library Management System**. 

Thay vì các service phải gọi nhau thông qua địa chỉ IP và Port cố định (hardcode), tất cả các service (Book, Loan, Member, v.v.) khi khởi động sẽ tự động đăng ký tên của chúng với Eureka Server. Nhờ đó, các service có thể dễ dàng tìm kiếm và giao tiếp với nhau một cách linh hoạt, kể cả khi hệ thống mở rộng (scale) hay thay đổi cổng.

## 🚀 Vai trò trong hệ thống
* **Service Registry:** Lưu trữ danh sách các instances của tất cả microservices đang hoạt động.
* **Health Monitoring:** Liên tục nhận "heartbeat" (nhịp tim) từ các service client để đảm bảo chúng vẫn đang sống. Nếu một service bị sập, Eureka sẽ tự động gỡ nó khỏi danh sách.
* **Tích hợp:** Làm việc trực tiếp với **API Gateway** để giúp định tuyến request từ người dùng đến đúng service cần thiết.

## 🛠 Tech Stack
* **Ngôn ngữ:** Java 17
* **Framework:** Spring Boot 3.x
* **Cloud Components:** Spring Cloud Netflix Eureka Server
* **Quản lý thư viện:** Maven

## ⚙️ Cài đặt & Khởi chạy

### Yêu cầu hệ thống
* JDK 17
* Maven 3.8+
* ⚠️ **Lưu ý quan trọng:** Đây là service hạ tầng cốt lõi. **BẮT BUỘC** phải chạy Eureka Server lên đầu tiên trước khi khởi động bất kỳ service nào khác trong dự án (như API Gateway, Book Service, v.v.).

### Các bước thực hiện
1. **Clone repository về máy:**
   ```bash
   git clone https://github.com/PhuNguyen12345/mss301-digilib-eureka-server.git
   cd mss301-digilib-eureka-server
