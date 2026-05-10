# Hướng dẫn Triển khai (Deployment Guide) - Game Bomberman

Tài liệu này hướng dẫn cách chạy ứng dụng một cách độc lập, giả lập môi trường triển khai thực tế thông qua Docker.

Vì Game Bomberman là một ứng dụng Desktop App, việc deployment (triển khai) ở đây tương đương với việc đóng gói toàn bộ mã nguồn, tài nguyên (hình ảnh, âm thanh) 
và các thư viện phụ thuộc thành một tệp thực thi duy nhất (Fat JAR / Executable JAR) để người dùng cuối có thể chạy dễ dàng mà không cần môi trường lập trình.

3.1. Đóng gói ứng dụng (Build process):

Mở Terminal hoặc Command Prompt tại thư mục gốc của dự án (nơi có file pom.xml).

Sử dụng công cụ Maven Wrapper có sẵn trong dự án để biên dịch và đóng gói ứng dụng. Gõ lệnh sau:

Đối với hệ điều hành Windows:

DOS
.\mvnw.cmd clean package
Đối với hệ điều hành Linux/macOS:

Bash
./mvnw clean package
Quá trình này sẽ dọn dẹp các tệp cũ (clean), sau đó biên dịch lại mã nguồn và đóng gói (package). Nếu thành công (BUILD SUCCESS), Maven sẽ sinh ra một thư mục target/. Trong đó sẽ chứa file .jar hoàn chỉnh của game (ví dụ: bomberman-1.0-SNAPSHOT-jar-with-dependencies.jar).