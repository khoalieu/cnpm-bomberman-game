package entity.animateentity.character.development_test_uc3;

import entity.animateentity.Bomb;
import entity.animateentity.character.Bomber;
import entity.staticentity.Grass;
import graphics.Sprite;
import javafx.application.Platform;
import map.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import variables.Variables.DIRECTION;

import static org.junit.jupiter.api.Assertions.*;

class BombKickTest {
    private Map gameMap;
    private Bomber player;

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

        player = new Bomber(32, 32, Sprite.PLAYER_DOWN[0], new FakeKeyInput(DIRECTION.NONE));
        // Ép tham chiếu Bomber vào Map để tránh lỗi NullPointerException
        try {
            java.lang.reflect.Field playerField = Map.class.getDeclaredField("player");
            playerField.setAccessible(true);
            playerField.set(gameMap, player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Mặc định Level 1
        try {
            java.lang.reflect.Field levelField = Map.class.getDeclaredField("levelNumber");
            levelField.setAccessible(true);
            levelField.set(null, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPlayerKickBombSlides() {
        // Đặt quả bom tại ô lưới (2, 1) -> vị trí pixel (64, 32)
        Bomb bomb = new Bomb(2, 1, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setBlock(true);

        // Người chơi có khả năng đá bom (Kick Ability)
        player.hasKickAbility = true;

        // Thiết lập người chơi di chuyển sang hướng PHẢI tiến vào ô của bom
        player.setPosition(32 + 20, 32); // Ngay cạnh quả bom (bom bắt đầu tại pixel 64)
        ((FakeKeyInput) player.keyInput).setDirection(DIRECTION.RIGHT);
        player.setDirection();
        player.setVelocity(2, 0);

        // Gọi checkCollision, gián tiếp kích hoạt hàm handleBombBlocking xử lý va chạm bom
        player.checkCollision();

        // Xác minh quả bom bắt đầu chuyển sang trạng thái di chuyển/trượt (isMoving = true)
        try {
            java.lang.reflect.Field isMovingField = Bomb.class.getDeclaredField("isMoving");
            isMovingField.setAccessible(true);
            boolean isMoving = (boolean) isMovingField.get(bomb);
            assertTrue(isMoving, "Quả bom phải bắt đầu trượt di chuyển (isMoving = true) khi người chơi có kỹ năng đá bom");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testPlayerKickBombBlocked() {
        // Đặt quả bom tại ô lưới (2, 1) -> vị trí pixel (64, 32)
        Bomb bomb = new Bomb(2, 1, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setBlock(true);

        // Người chơi không có khả năng đá bom (hasKickAbility = false)
        player.hasKickAbility = false;

        // Thiết lập người chơi di chuyển sang hướng PHẢI tiến vào ô của bom
        player.setPosition(32 + 20, 32);
        ((FakeKeyInput) player.keyInput).setDirection(DIRECTION.RIGHT);
        player.setDirection();
        player.setVelocity(2, 0);

        player.checkCollision();

        // Xác minh quả bom vẫn đứng yên (isMoving = false)
        try {
            java.lang.reflect.Field isMovingField = Bomb.class.getDeclaredField("isMoving");
            isMovingField.setAccessible(true);
            boolean isMoving = (boolean) isMovingField.get(bomb);
            assertFalse(isMoving, "Quả bom không được di chuyển khi người chơi không có kỹ năng đá bom");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
