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
            // Ignore if JavaFX already started
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

        // Initialize grid to Grass to avoid NPE
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

        // 4 directions: Up (2, 1), Down (2, 3), Left (1, 2), Right (3, 2), Center (2, 2)
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
        // Brick at (2, 3) (Down direction)
        Brick brick = new Brick(2, 3, Sprite.BRICK[0]);
        gameMap.setTile(3, 2, brick);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        bomb.isPierce = false;
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);
        Flame.flameLength = 2; // Flame length = 2 to see if it propagates further

        bomb.update();

        // Down direction is blocked, so no flame at (2, 4)
        boolean hasFlameAt2_4 = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 2 && f.getTileY() == 4);
        assertFalse(hasFlameAt2_4, "Tia lửa của bom thường không được đi xuyên qua Brick");
    }

    @Test
    void testFlamePropagationPierceBrick() {
        // Brick at (2, 3) (Down direction)
        Brick brick = new Brick(2, 3, Sprite.BRICK[0]);
        gameMap.setTile(3, 2, brick);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        bomb.isPierce = true; // Pierce Bomb
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);
        Flame.flameLength = 2; // Flame length = 2

        bomb.update();

        // Down direction is pierced, so flame at (2, 4) IS spawned
        boolean hasFlameAt2_4 = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 2 && f.getTileY() == 4);
        assertTrue(hasFlameAt2_4, "Tia lửa của bom xuyên thấu phải đi xuyên qua Brick");
    }

    @Test
    void testFlamePropagationBlockedByWall() {
        // Wall at (2, 3) (Down direction)
        Wall wall = new Wall(2, 3, Sprite.wall);
        gameMap.setTile(3, 2, wall);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        bomb.isPierce = true; // Pierce Bomb
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);
        Flame.flameLength = 2; // Flame length = 2

        bomb.update();

        // Down direction has Wall, so even Pierce Bomb cannot pass Wall. No flame at (2, 4).
        boolean hasFlameAt2_4 = gameMap.getFlames().stream().anyMatch(f -> f.getTileX() == 2 && f.getTileY() == 4);
        assertFalse(hasFlameAt2_4, "Tia lửa của bom xuyên thấu cũng không được đi xuyên qua Wall (Tường cứng)");
    }
}
