package map;

import entity.animateentity.character.enemy.Enemy;
import entity.staticentity.Item;
import entity.animateentity.character.Bomber;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class UC1_ReleaseTest {
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
    // KIỂM THỬ: TC1.4 Khóa biên đồ họa Camera khi nhân vật di chuyển sát rìa bản đồ
    // ==========================================
    @Test
    public void testTC1_4_CameraClamping() throws Exception {
        gameMap.createMap("src/main/resources/levels/Level1.txt");
        Bomber player = gameMap.getPlayer();
        
        // Sử dụng Reflection để truy cập hàm private updateRenderXY
        Method updateRenderXY = Map.class.getDeclaredMethod("updateRenderXY");
        updateRenderXY.setAccessible(true);
        
        // Cố tình đưa nhân vật ra khỏi biên trái/trên (VD: tọa độ âm hoặc 0)
        player.setPosition(0, 0);
        updateRenderXY.invoke(gameMap);
        
        assertEquals(0, gameMap.getRenderX(), "Tọa độ Camera X phải được khóa ở 0 khi sát rìa trái");
        assertEquals(0, gameMap.getRenderY(), "Tọa độ Camera Y phải được khóa ở 0 khi sát rìa trên");
        
        // Cố tình đưa nhân vật ra khỏi biên phải/dưới (X rất lớn, Y rất lớn)
        player.setPosition(10000, 10000);
        updateRenderXY.invoke(gameMap);
        
        assertTrue(gameMap.getRenderX() > 0, "Tọa độ Camera X phải lớn hơn 0 và bị khóa ở biên phải");
        assertTrue(gameMap.getRenderY() >= 0, "Tọa độ Camera Y phải lớn hơn hoặc bằng 0");
    }

    // ==========================================
    // KIỂM THỬ: TC1.5 Kích hoạt cơ chế phòng vệ (Fallback) khi định nghĩa vật phẩm lỗi
    // ==========================================
    @Test
    public void testTC1_5_FallbackItem() throws Exception {
        // Tạo map tạm với ký tự lỗi 'z' chưa được định nghĩa
        File targetDir = new File("target/test-classes/levels");
        if (!targetDir.exists()) targetDir.mkdirs();
        File tempLevel = new File(targetDir, "Level_TestFallback.txt");
        
        try (PrintWriter writer = new PrintWriter(tempLevel)) {
            writer.println("1");
            writer.println("#####");
            writer.println("#p z#");
            writer.println("#####");
        }

        assertDoesNotThrow(() -> {
            // Hàm createMap sẽ tự động cắt bỏ "src/main/resources" để lấy "/levels/Level_TestFallback.txt"
            gameMap.createMap("src/main/resources/levels/Level_TestFallback.txt");
        }, "Hệ thống không được crash khi gặp ký tự vật phẩm lạ");

        // Xác minh cơ chế phòng vệ: ô lỗi không bị null (StaticTexture trả về Grass để fallback)
        assertNotNull(gameMap.getTile(3, 1), "Hệ thống phải tự động xử lý ký tự lỗi 'z' để tránh null");
        
        // Dọn dẹp
        tempLevel.delete();
    }

    // ==========================================
    // KIỂM THỬ: TC1.6 Tải và khởi tạo thành công màn chơi Level 2 (Dọn sạch nền)
    // ==========================================
    @Test
    public void testTC1_6_Level2LoadAndClean() throws Exception {
        gameMap.createMap("src/main/resources/levels/Level2.txt");

        assertEquals(2, Map.getLevelNumber(), "LevelNumber phải được gán bằng 2");
        assertNotNull(gameMap.getTiles(), "Mảng nền phải được khởi tạo");
        assertTrue(gameMap.getItems().size() > 0, "Level 2 phải sinh vật phẩm ngẫu nhiên trên nền cỏ");
    }

    // ==========================================
    // KIỂM THỬ: TC1.7 Ngăn xung đột dữ liệu khi render 2 lớp và xóa thực thể an toàn
    // ==========================================
    @Test
    public void testTC1_7_ConcurrentModificationSafeRemove() throws Exception {
        gameMap.createMap("src/main/resources/levels/Level1.txt");
        
        // Đánh dấu isRemoved cho một enemy và một item
        if (!gameMap.getEnemies().isEmpty()) {
            gameMap.getEnemies().get(0).remove(); // Gọi hàm remove() để gán removed = true
        }
        
        if (!gameMap.getItems().isEmpty()) {
            gameMap.getItems().get(0).remove();
        }
        
        // updateMap sẽ gọi removeEntities()
        assertDoesNotThrow(() -> {
            gameMap.updateMap();
        }, "Không được xảy ra lỗi ConcurrentModificationException khi duyệt và xóa thực thể");
        
        // Đảm bảo các đối tượng đã được loại bỏ khỏi danh sách thực sự
        for (Enemy enemy : gameMap.getEnemies()) {
            assertFalse(enemy.isRemoved(), "Enemy bị đánh dấu xóa vẫn còn tồn tại trong danh sách");
        }
        for (Item item : gameMap.getItems()) {
            assertFalse(item.isRemoved(), "Item bị đánh dấu xóa vẫn còn tồn tại trong danh sách");
        }
    }
}
