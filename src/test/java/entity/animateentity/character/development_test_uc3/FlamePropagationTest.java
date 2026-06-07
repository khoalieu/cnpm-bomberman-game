package entity.animateentity.character.development_test_uc3;

import entity.animateentity.Bomb;
import entity.animateentity.Brick;
import entity.animateentity.Flame;
import entity.staticentity.Grass;
import entity.staticentity.Wall;
import graphics.Sprite;
import javafx.application.Platform;
import map.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlamePropagationTest {
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
        Flame.flameLength = 1;
    }

    @Test
    void testFlamePropagationNormalOnGrass() {
        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        bomb.isPierce = false;
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);

        bomb.update();

        // 4 hướng: Lên (2, 1), Xuống (2, 3), Trái (1, 2), Phải (3, 2), Tâm (2, 2)
        boolean hasCenter = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 2 && f.getTileY() == 2);
        boolean hasUp = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 2 && f.getTileY() == 1);
        boolean hasDown = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 2 && f.getTileY() == 3);
        boolean hasLeft = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 1 && f.getTileY() == 2);
        boolean hasRight = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 3 && f.getTileY() == 2);

        assertTrue(hasCenter);
        assertTrue(hasUp);
        assertTrue(hasDown);
        assertTrue(hasLeft);
        assertTrue(hasRight);
    }

    @Test
    void testFlamePropagationBlockedByBrickNormal() {
        // Gạch mềm tại ô lưới (2, 3) (Hướng xuống)
        Brick brick = new Brick(2, 3, Sprite.BRICK[0]);
        gameMap.setTile(3, 2, brick);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        bomb.isPierce = false;
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);
        Flame.flameLength = 2; // Độ dài tia lửa = 2 để kiểm tra xem có lan xa hơn không

        bomb.update();

        // Hướng xuống bị chặn, không có tia lửa tại ô lưới (2, 4)
        boolean hasFlameAt2_4 = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 2 && f.getTileY() == 4);
        assertFalse(hasFlameAt2_4, "Tia lửa của bom thường không được đi xuyên qua Brick");
    }

    @Test
    void testFlamePropagationPierceBrick() {
        // Gạch mềm tại ô lưới (2, 3) (Hướng xuống)
        Brick brick = new Brick(2, 3, Sprite.BRICK[0]);
        gameMap.setTile(3, 2, brick);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        bomb.isPierce = true; // Bom xuyên thấu (Pierce Bomb)
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);
        Flame.flameLength = 2; // Độ dài tia lửa = 2

        bomb.update();

        // Hướng xuống có Gạch mềm nhưng bom xuyên thấu giúp tia lửa tại ô lưới (2, 4) VẪN được sinh ra
        boolean hasFlameAt2_4 = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 2 && f.getTileY() == 4);
        assertTrue(hasFlameAt2_4, "Tia lửa của bom xuyên thấu phải đi xuyên qua Brick");
    }

    @Test
    void testFlamePropagationBlockedByWall() {
        // Tường cứng tại ô lưới (2, 3) (Hướng xuống)
        Wall wall = new Wall(2, 3, Sprite.wall);
        gameMap.setTile(3, 2, wall);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        bomb.isPierce = true; // Bom xuyên thấu (Pierce Bomb)
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);
        Flame.flameLength = 2; // Độ dài tia lửa = 2

        bomb.update();

        // Hướng xuống có Tường cứng, nên bom xuyên thấu cũng không thể đi qua. Không có tia lửa tại ô lưới (2, 4).
        boolean hasFlameAt2_4 = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 2 && f.getTileY() == 4);
        assertFalse(hasFlameAt2_4, "Tia lửa của bom xuyên thấu cũng không được đi xuyên qua Wall (Tường cứng)");
    }
}
