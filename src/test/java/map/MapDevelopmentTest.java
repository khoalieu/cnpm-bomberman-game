package map;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class MapDevelopmentTest {
    private Map gameMap;

    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Bỏ qua nếu JavaFX đã khởi chạy
        }
    }

    @BeforeEach
    public void setUp() {
        gameMap = Map.getGameMap();
    }

    // ==========================================
    // KIỂM THỬ LUỒNG CHÍNH: UC1.0 -> UC1.5 (Khởi tạo map thành công)
    // ==========================================
    @Test
    public void testUC1_MainFlow_LoadMapSuccess() {
        assertDoesNotThrow(() -> {
            // Đảm bảo ông có file Level1.txt thật ở đường dẫn này
            gameMap.createMap("src/main/resources/levels/Level1.txt");
        }, "Hệ thống không được phép ném lỗi khi đường dẫn map hợp lệ!");

        // Kiểm tra UC1.4 & UC1.5: Các thực thể phải được khởi tạo
        assertNotNull(gameMap.getTile(0, 0), "Dữ liệu mảng đồ họa tĩnh (tiles) không được null");
        assertNotNull(gameMap.getPlayer(), "Phải khởi tạo được nhân vật Bomber (Player)");
    }

    // ==========================================
    // KIỂM THỬ NGOẠI LỆ: UC1.2a - Không tìm thấy file bản đồ
    // ==========================================
    @Test
    public void testUC1_2a_FileNotFoundException() {
        // Cố tình đưa một đường dẫn rác không tồn tại
        String fakePath = "src/main/resources/levels/FileNayKhongTonTai.txt";

        // Yêu cầu (Assert) hệ thống PHẢI ném ra lỗi FileNotFoundException y như trong tài liệu mô tả
        assertThrows(FileNotFoundException.class, () -> {
            gameMap.createMap(fakePath);
        }, "Lỗi: Hệ thống không phát hiện ra ngoại lệ khi mất file map!");
    }

    // ==========================================
    // KIỂM THỬ TÍNH NĂNG: UC1.5a - Sinh vật phẩm ngẫu nhiên Level 2
    // ==========================================
    @Test
    public void testUC1_5a_Level2RandomItemGeneration() {
        try {
            // Ép hệ thống nạp Level 2
            gameMap.createMap("src/main/resources/levels/Level2.txt");

            int itemCount = gameMap.getItems().size();

            // Theo mô tả UC1.5a, hệ thống sẽ sinh tối đa 7 vật phẩm (hoặc ít hơn tùy số ô cỏ)
            assertTrue(itemCount > 0, "Level 2 phải rải vật phẩm ngẫu nhiên lên map!");
            assertTrue(itemCount <= 7, "Số lượng vật phẩm ngẫu nhiên không được vượt quá 7!");

        } catch (FileNotFoundException e) {
            fail("Cảnh báo: Không tìm thấy file Level2.txt để kiểm thử!");
        }
    }
}