package entity.animateentity.character.development_test_uc3;

import entity.animateentity.Bomb;
import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Balloom;
import entity.staticentity.Grass;
import entity.staticentity.Wall;
import graphics.Sprite;
import javafx.application.Platform;
import map.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static variables.Variables.DIRECTION.NONE;

class BombPlacementTest {
    private Map gameMap;
    private Bomber bomber;

    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Bỏ qua nếu JavaFX đã được khởi động trước đó
        }
    }

    @BeforeEach
    public void setUp() {
        gameMap = Map.getGameMap();

        if (gameMap.getBombs() != null) gameMap.getBombs().clear();
        if (gameMap.getEnemies() != null) gameMap.getEnemies().clear();

        try {
            java.lang.reflect.Method resetMethod = Map.class.getDeclaredMethod("resetEntities");
            resetMethod.setAccessible(true);
            resetMethod.invoke(gameMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi tạo bản đồ toàn bộ là ô Cỏ để tránh lỗi NullPointerException
        for (int i = 0; i < variables.Variables.HEIGHT; i++) {
            for (int j = 0; j < variables.Variables.WIDTH; j++) {
                gameMap.setTile(i, j, new Grass(j, i, Sprite.grass));
            }
        }

        bomber = new Bomber(32, 32, Sprite.PLAYER_DOWN[0], new FakeKeyInput(NONE));
        Bomb.limit = 1;
    }

    @Test
    void testPlaceBombNormalFlow() {
        // Người chơi ở vị trí pixel (32, 32) -> ô lưới (1, 1)
        bomber.placeBombAt(32, 32);

        assertEquals(1, gameMap.getBombs().size(), "Hệ thống phải khởi tạo đối tượng Bom tại vị trí của Bomber");
        Bomb bomb = gameMap.getBombs().get(0);
        assertEquals(1, bomb.getTileX());
        assertEquals(1, bomb.getTileY());
        assertFalse(bomb.isPierce, "Bom mặc định không có thuộc tính Xuyên thấu");
    }

    @Test
    void testPlaceBombExceedLimit() {
        // Đặt quả bom đầu tiên
        bomber.placeBombAt(32, 32);
        assertEquals(1, gameMap.getBombs().size());

        // Thử đặt quả bom thứ hai tại vị trí khác (64, 32) -> ô lưới (2, 1)
        bomber.placeBombAt(64, 32);
        assertEquals(1, gameMap.getBombs().size(), "Hệ thống phải bỏ qua lệnh đặt bom thứ 2 do vượt quá giới hạn tối đa");
    }

    @Test
    void testPlaceBombInvalidPosition_OccupiedByBomb() {
        // Đặt quả bom đầu tiên
        bomber.placeBombAt(32, 32);
        assertEquals(1, gameMap.getBombs().size());

        // Cấu hình giới hạn bom là 2 để chúng ta có thể thử đặt quả bom thứ hai
        Bomb.limit = 2;

        // Thử đặt bom tại cùng một ô lưới đã bị chiếm dụng bởi quả bom đầu tiên
        bomber.placeBombAt(32, 32);
        assertEquals(1, gameMap.getBombs().size(), "Không được phép đặt bom trùng vị trí với bom khác");
    }

    @Test
    void testPlaceBombInvalidPosition_OccupiedByEnemy() {
        // Tạo một quái vật tại ô lưới (2, 1) -> pixel (64, 32)
        Balloom balloom = new Balloom(2, 1, Sprite.BALLOOM_LEFT[0]);
        gameMap.getEnemies().add(balloom);

        // Di chuyển bomber đến vị trí (64, 32) và thử đặt bom
        bomber.setPosition(64, 32);
        bomber.placeBombAt(64, 32);

        assertEquals(0, gameMap.getBombs().size(), "Không được phép đặt bom trùng vị trí với Quái vật");
    }

    @Test
    void testPlaceBombInvalidPosition_OccupiedByWall() {
        // Đặt một bức Tường cứng tại ô lưới (2, 1) -> pixel (64, 32)
        gameMap.setTile(1, 2, new Wall(2, 1, Sprite.wall));

        // Di chuyển bomber đến vị trí (64, 32) và thử đặt bom
        bomber.setPosition(64, 32);
        bomber.placeBombAt(64, 32);

        assertEquals(0, gameMap.getBombs().size(), "Không được phép đặt bom trùng vị trí với Tường cứng");
    }

    @Test
    void testPlaceBombPierceAttribute() {
        // Kích hoạt trạng thái buff xuyên thấu cho Bomber
        bomber.hasPierceBomb = true;

        bomber.placeBombAt(32, 32);

        assertEquals(1, gameMap.getBombs().size());
        Bomb bomb = gameMap.getBombs().get(0);
        assertTrue(bomb.isPierce, "Quả bom được gán thuộc tính Xuyên thấu khi Bomber đang có buff");
    }
}
