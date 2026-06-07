# Third-Party Notices and Licenses

Dự án này ("Bomberman Game") có sử dụng một số mã nguồn, thư viện mã nguồn mở và tài nguyên bên thứ ba. Dưới đây là thông tin chi tiết về giấy phép của các thành phần đó.

## 1. Thư viện mã nguồn mở (Open Source Libraries)

Các thư viện dưới đây được quản lý thông qua Maven (`pom.xml`) và tự động được liên kết khi biên dịch:

### 1.1. JavaFX
- **Mô tả:** Nền tảng xây dựng giao diện đồ họa.
- **Thư viện:** `javafx-controls`, `javafx-fxml`, `javafx-media` (org.openjfx)
- **Giấy phép:** [GPLv2 with Classpath Exception](https://openjdk.org/legal/gplv2+ce.html)

### 1.2. ControlsFX
- **Mô tả:** Thư viện mở rộng UI controls cho JavaFX.
- **Thư viện:** `controlsfx` (org.controlsfx)
- **Giấy phép:** [3-Clause BSD License](https://opensource.org/licenses/BSD-3-Clause)

### 1.3. BootstrapFX
- **Mô tả:** Thư viện hỗ trợ CSS theo phong cách Bootstrap cho JavaFX.
- **Thư viện:** `bootstrapfx-core` (org.kordamp.bootstrapfx)
- **Giấy phép:** [MIT License](https://opensource.org/licenses/MIT)

### 1.4. JUnit Jupiter
- **Mô tả:** Thư viện hỗ trợ viết Unit Test.
- **Thư viện:** `junit-jupiter-api`, `junit-jupiter-engine` (org.junit.jupiter)
- **Giấy phép:** [Eclipse Public License v2.0 (EPL-2.0)](https://www.eclipse.org/legal/epl-2.0/)

---

## 2. Tài nguyên Game (Game Assets)

*(LƯU Ý DÀNH CHO NHÓM PHÁT TRIỂN: Hãy điền thông tin chính xác vào phần này. Dưới đây là form mẫu bắt buộc phải có khi public game lên GitHub để tránh vi phạm bản quyền hình ảnh/âm thanh)*

### 2.1. Đồ họa (Sprites & Textures)
- Các hình ảnh nhân vật (Bomberman, Quái vật, Bomb), gạch (Brick), tường (Wall)... được sử dụng trong thư mục `src/main/resources/sprites` và `src/main/resources/textures`.
- **Nguồn gốc / Bản quyền:** Toàn bộ bản quyền thiết kế gốc của trò chơi Bomberman thuộc về **Konami Digital Entertainment** (trước đây là Hudson Soft). 
- **Tuyên bố sử dụng:** Dự án này được tạo ra **HOÀN TOÀN VÌ MỤC ĐÍCH GIÁO DỤC VÀ HỌC TẬP**, không có bất kỳ yếu tố thương mại hay trục lợi nào.

### 2.2. Âm thanh (Audio & Sounds)
- Các file âm thanh tiếng nổ, tiếng đi lại, nhạc nền trong thư mục `src/main/resources/sounds`.
- **Nguồn gốc / Bản quyền:** (Ghi rõ nếu bạn tải từ freesound.org hoặc lấy từ game gốc).
- **Giấy phép:** Dùng cho mục đích học tập phi thương mại (Educational/Non-commercial use).

### 2.3. Phông chữ (Fonts)
- Các font chữ đặc biệt sử dụng trong thư mục `src/main/resources/fonts`.
- **Giấy phép:** (Ghi tên Font và link tải gốc, ví dụ: OFL - Open Font License nếu là Google Fonts).
