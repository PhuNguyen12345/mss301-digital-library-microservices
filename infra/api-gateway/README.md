# 🚪 Digilib API Gateway (`mss30-digilib-api-gateway`)

## 📖 Giới thiệu
**API Gateway** là điểm truy cập duy nhất (Single Entry Point) cho toàn bộ hệ thống Digital Library Management System. Phân hệ này chịu trách nhiệm tiếp nhận mọi request từ người dùng hoặc UI (React.js), sau đó định tuyến (routing) chính xác đến các Microservices nội bộ ở phía sau. 

Được thiết kế dựa trên **Spring Cloud Gateway**, service này tích hợp chặt chẽ với Eureka Server để tự động khám phá và định tuyến đến các dịch vụ mà không cần hardcode địa chỉ IP hay Port cố định.

## 🚀 Chức năng chính (Theo tiến độ)
* **Tuần 2 - Cơ sở hạ tầng:** Tự động định tuyến (Dynamic Routing) đến các service nội bộ thông qua Service Discovery.
* **Bảo mật:** Xác thực JWT theo Keycloak issuer trước khi request được forward.
* **CORS:** Chỉ cho phép các frontend origin được cấu hình.
* **Resilience:** Timeout, circuit breaker riêng cho từng downstream service, retry có backoff chỉ cho `GET`, và fallback JSON với HTTP `503/504`.

## 🛠 Tech Stack
* **Ngôn ngữ:** Java 21
* **Framework:** Spring Boot 3.x
* **Cloud Components:** Spring Cloud Gateway, Netflix Eureka Client
* **Quản lý thư viện:** Maven

## Resilience policy

* Timeout kết nối mặc định: `1s`; timeout phản hồi mặc định: `5s`.
* Auth có response timeout `15s`; download file có response timeout `30s`.
* Catalog, Loan, Member và Fine dùng circuit breaker độc lập.
* Mỗi downstream có semaphore bulkhead riêng; request bị từ chối ngay khi đạt giới hạn đồng thời.
* Chỉ `GET` được retry, tối đa 2 lần. Request ghi dữ liệu và auth không retry để tránh tạo thao tác trùng.
* Fallback không giả lập dữ liệu thành công; client nhận `UPSTREAM_UNAVAILABLE` (`503`) hoặc `UPSTREAM_TIMEOUT` (`504`).

Các ngưỡng timeout có thể override bằng các biến `GATEWAY_*_TIMEOUT` trong `.env` hoặc secret/config của môi trường triển khai.

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
