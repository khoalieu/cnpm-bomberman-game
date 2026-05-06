# Hướng dẫn Cài đặt Môi trường Phát triển (Game Bomberman)

Tài liệu này hướng dẫn các thành viên trong nhóm cách thiết lập môi trường để phát triển mã nguồn của dự án.

## 1. Yêu cầu hệ thống (Prerequisites)
*(Liệt kê các phần mềm bắt buộc phải có)*
*   **Java Development Kit (JDK):** Yêu cầu phiên bản JDK 17 (hoặc phiên bản dự án bạn đang dùng). Link tải...
*   **Maven:** Công cụ quản lý thư viện (nếu dự án dùng Maven, tuy nhiên trong source bạn đưa có sẵn `mvnw` nên có thể bỏ qua bước cài Maven rời, chỉ cần dặn dùng `mvnw`).
*   **IDE (Môi trường phát triển tích hợp):** Khuyến nghị sử dụng IntelliJ IDEA hoặc Eclipse.
*   **Git:** Để quản lý mã nguồn.

## 2. Các bước cài đặt chi tiết
1.  **Clone mã nguồn:**
    ```bash
    git clone https://github.com/khoalieu/cnpm-bomberman-game.git
    cd cnpm-bomberman-game
    ```
2.  **Mở dự án trên IDE:**
    *   Mở IntelliJ IDEA -> Chọn *Open* -> Trỏ đến thư mục chứa file `pom.xml`.
    *   Đợi IDE tự động tải các thư viện (dependencies) thông qua Maven.
3.  **Cấu hình JDK trong IDE:**
    *   Đảm bảo Project SDK được trỏ đúng vào JDK 17 (Hoặc phiên bản cao hơn).

## 3. Chạy ứng dụng (Run)
*   Tìm đến class `Launcher.java` trong package `game`.
*   Nhấn chuột phải -> Chọn `Run 'Launcher.main()'`.

## 4. Xử lý lỗi thường gặp (Troubleshooting)
*   **Lỗi "Cannot find symbol" / Lỗi thư viện:** Hãy thử chạy lệnh `mvn clean install` (hoặc dùng nút Maven trong IDE) để tải lại thư viện.
*   **Lỗi Java Version:** Kiểm tra lại biến môi trường `JAVA_HOME`.