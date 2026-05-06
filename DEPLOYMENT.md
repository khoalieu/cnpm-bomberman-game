# Hướng dẫn Triển khai (Deployment Guide) - Game Bomberman

Tài liệu này hướng dẫn cách chạy ứng dụng một cách độc lập, giả lập môi trường triển khai thực tế thông qua Docker.

## Lựa chọn 1: Chạy trực tiếp qua file thực thi (Jar)
Đây là cách cơ bản nhất để chạy game mà không cần mở IDE.

1.  **Đóng gói ứng dụng (Build):**
    Sử dụng Maven wrapper có sẵn trong dự án để build ra file `.jar` (file chạy của Java):
    ```bash
    ./mvnw clean package