package entity.animateentity.character.development_test_uc3;

import entity.animateentity.Bomb;
import entity.staticentity.Grass;
import graphics.Sprite;
import javafx.application.Platform;
import map.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BombCountdownTest {
    private Map gameMap;

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
        if (gameMap.getFlames() != null) gameMap.getFlames().clear();

        try {
            java.lang.reflect.Method resetMethod = Map.class.getDeclaredMethod("resetEntities");
            resetMethod.setAccessible(true);
            resetMethod.invoke(gameMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi tạo bản đồ ô Cỏ để tránh lỗi NullPointerException
        for (int i = 0; i < variables.Variables.HEIGHT; i++) {
            for (int j = 0; j < variables.Variables.WIDTH; j++) {
                gameMap.setTile(i, j, new Grass(j, i, Sprite.grass));
            }
        }

        entity.animateentity.character.Bomber player = new entity.animateentity.character.Bomber(32, 32, Sprite.PLAYER_DOWN[0], new FakeKeyInput(variables.Variables.DIRECTION.NONE));
        try {
            java.lang.reflect.Field playerField = Map.class.getDeclaredField("player");
            playerField.setAccessible(true);
            playerField.set(gameMap, player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testBombCountdownDecrements() {
        Bomb bomb = new Bomb(1, 1, Sprite.BOMB[0]);
        bomb.setTimetoExplode(120);

        bomb.update();

        // Trong điều kiện bình thường, bomb.update() giảm timetoExplode nếu không phải là 0.
        // Vì thuộc tính timetoExplode là protected và chúng ta đang ở package khác, ta sử dụng reflection để truy cập nó.
        try {
            java.lang.reflect.Field field = Bomb.class.getDeclaredField("timetoExplode");
            field.setAccessible(true);
            int value = (int) field.get(bomb);
            assertEquals(119, value, "Thời gian đếm ngược phải giảm 1 đơn vị sau mỗi frame update");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testBombExplosionTriggered() {
        Bomb bomb = new Bomb(1, 1, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(1);

        // Frame 1: Giảm từ 1 về 0
        bomb.update();

        // Frame 2: timetoExplode đã bằng 0, kích nổ quả bom
        bomb.update();

        // Quả bom bị đánh dấu xóa khỏi bản đồ
        assertTrue(bomb.isRemoved(), "Quả bom phải bị đánh dấu loại bỏ (remove) sau khi nổ");
        gameMap.updateMap(); // Dọn dẹp thực thể bị xóa khỏi danh sách quản lý của bản đồ
        assertFalse(gameMap.getBombs().contains(bomb), "Quả bom phải biến mất khỏi danh sách bom hoạt động");

        // Tia lửa được tạo ra tại tâm vụ nổ
        assertFalse(gameMap.getFlames().isEmpty(), "Tia lửa (Flame) phải được tạo ra tại tâm vụ nổ");
        assertEquals(1, gameMap.getFlames().get(0).getTileX());
        assertEquals(1, gameMap.getFlames().get(0).getTileY());
    }
}
