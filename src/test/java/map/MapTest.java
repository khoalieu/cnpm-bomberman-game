package map;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class MapTest {
    private Map gameMap;

    @BeforeAll
    public static void initJFX() {
        try {
            // Bật môi trường đồ họa của JavaFX lên
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Bỏ qua lỗi nếu Toolkit đã được khởi tạo từ trước
        }
    }

    @BeforeEach
    public void setUp() {
        // Lấy instance của Map theo đúng thiết kế (Singleton pattern) trong MainGame của bạn
        gameMap = Map.getGameMap();
    }

    // ==============================
    // TC1.1: Khởi tạo và tải màn chơi thành công
    // ==============================
    @Test
    public void testTC1_1_LoadMapSuccess() {
        // Cần chuẩn bị trước 1 file LevelTest.txt hợp lệ trong thư mục test
        String validPath = "src/main/resources/levels/Level1.txt";

        // 1. Kiểm tra hàm createMap chạy trơn tru, không văng lỗi
        assertDoesNotThrow(() -> {
            gameMap.createMap(validPath);
        }, "Lỗi: Hệ thống ném ra ngoại lệ khi đọc file hợp lệ!");

        // 2. Kiểm tra dữ liệu sau khi load (Phần này bạn linh hoạt gọi các hàm get() có trong class Map của bạn)
        // Ví dụ (nếu class Map của bạn có các hàm này):
        assertNotNull(gameMap.getPlayer(), "Bomber chưa được khởi tạo");

        // Kiểm tra xem mảng tiles đã được cấp phát bộ nhớ chưa
        assertNotNull(gameMap.getTiles(), "Mảng gạch/tường (tiles) chưa được khởi tạo");

        // Kiểm tra xem phần tử đầu tiên của mảng có dữ liệu không (chứng tỏ đã đọc được ký tự)
        assertNotNull(gameMap.getTiles()[0][0], "Bản đồ chưa có dữ liệu gạch/tường");

        assertTrue(gameMap.getEnemies().size() >= 0, "Danh sách quái vật chưa được khởi tạo");
    }

    // ==============================
    // TC1.2: Tệp dữ liệu màn chơi không tồn tại
    // ==============================
    @Test
    public void testTC1_2_FileNotFound() {
        // Truyền vào một đường dẫn tệp cố tình làm sai
        String invalidPath = "src/main/resources/levels/Level_Fake_Khong_Ton_Tai.txt";

        assertThrows(FileNotFoundException.class, () -> {
            gameMap.createMap(invalidPath);
        }, "Lỗi: Hệ thống không phát hiện được việc mất file map!");
    }

    // ==============================
    // TC1.3: Tệp dữ liệu bị sai định dạng (Tùy chọn thêm)
    // ==============================
    @Test
    public void testTC1_3_WrongFormat() {
        // Tạo 1 file txt nhưng bên trong chứa chữ linh tinh, không ra hình ma trận
        String brokenPath = "src/main/resources/levels/Level_Loi_Dinh_Dang.txt";

        // Tùy thuộc vào cách bạn code trong hàm createMap() mà nó sẽ ném ra lỗi gì (VD: NumberFormatException, ArrayIndexOutOfBoundsException...)
        // Ở đây ví dụ bắt lỗi Exception chung
        assertThrows(Exception.class, () -> {
            gameMap.createMap(brokenPath);
        }, "Lỗi: Hệ thống vẫn load được file bị sai định dạng ma trận!");
    }

}
