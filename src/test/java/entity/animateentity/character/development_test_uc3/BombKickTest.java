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
            // Ignore if JavaFX already started
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

        // Initialize grid to Grass to avoid NPE
        for (int i = 0; i < variables.Variables.HEIGHT; i++) {
            for (int j = 0; j < variables.Variables.WIDTH; j++) {
                gameMap.setTile(i, j, new Grass(j, i, Sprite.grass));
            }
        }

        player = new Bomber(32, 32, Sprite.PLAYER_DOWN[0], new FakeKeyInput(DIRECTION.NONE));
        // Force Bomber reference in Map
        try {
            java.lang.reflect.Field playerField = Map.class.getDeclaredField("player");
            playerField.setAccessible(true);
            playerField.set(gameMap, player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Level 1 by default
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
        // Place a bomb at (2, 1) -> pixel (64, 32)
        Bomb bomb = new Bomb(2, 1, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setBlock(true);

        // Player has Kick Ability
        player.hasKickAbility = true;

        // Position player moving RIGHT into the bomb
        player.setPosition(32 + 20, 32); // right next to bomb (bomb starts at 64)
        ((FakeKeyInput) player.keyInput).setDirection(DIRECTION.RIGHT);
        player.setDirection();
        player.setVelocity(2, 0);

        // Call checkCollision, which internally calls handleBombBlocking
        player.checkCollision();

        // Verify that the bomb has isMoving = true
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
        // Place a bomb at (2, 1) -> pixel (64, 32)
        Bomb bomb = new Bomb(2, 1, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setBlock(true);

        // Player does NOT have Kick Ability
        player.hasKickAbility = false;

        // Position player moving RIGHT into the bomb
        player.setPosition(32 + 20, 32);
        ((FakeKeyInput) player.keyInput).setDirection(DIRECTION.RIGHT);
        player.setDirection();
        player.setVelocity(2, 0);

        player.checkCollision();

        // Verify that the bomb remains stationary
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
