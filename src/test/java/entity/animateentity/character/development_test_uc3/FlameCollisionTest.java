package entity.animateentity.character.development_test_uc3;

import entity.animateentity.Bomb;
import entity.animateentity.Brick;
import entity.animateentity.Flame;
import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Balloom;
import entity.staticentity.Grass;
import entity.staticentity.SpeedItem;
import graphics.Sprite;
import javafx.application.Platform;
import map.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static variables.Variables.DIRECTION.NONE;

class FlameCollisionTest {
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
        if (gameMap.getFlames() != null) gameMap.getFlames().clear();
        if (gameMap.getEnemies() != null) gameMap.getEnemies().clear();
        if (gameMap.getItems() != null) gameMap.getItems().clear();

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

        player = new Bomber(32, 32, Sprite.PLAYER_DOWN[0], new FakeKeyInput(NONE));
        // Force Bomber reference in Map
        try {
            java.lang.reflect.Field playerField = Map.class.getDeclaredField("player");
            playerField.setAccessible(true);
            playerField.set(gameMap, player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Flame.flameLength = 1;
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
    void testFlameCollisionWithBrick() {
        Brick brick = new Brick(2, 3, Sprite.BRICK[0]);
        gameMap.setTile(3, 2, brick);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);

        bomb.update();

        assertTrue(brick.isDestroyed(), "Gạch mềm bị chạm bởi tia lửa phải được chuyển sang trạng thái destroyed = true");
    }

    @Test
    void testFlameCollisionWithBrickRevealingItem() {
        // Hidden item at (2, 3) - defaults to block=true
        SpeedItem item = new SpeedItem(2, 3, Sprite.BRICK[0]);
        gameMap.setTile(3, 2, item);
        gameMap.getItems().add(item);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);

        bomb.update();

        // Under brick, the item block becomes false (revealed)
        assertFalse(item.isBlock(), "Vật phẩm ẩn phải được hiển thị (block = false) khi gạch bị phá");
    }

    @Test
    void testFlameCollisionWithEnemy() {
        Balloom enemy = new Balloom(2, 3, Sprite.BALLOOM_LEFT[0]);
        gameMap.getEnemies().add(enemy);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);

        bomb.update();

        // Flame check collision
        gameMap.getFlames().forEach(Flame::checkCollison);

        assertTrue(enemy.isDestroyed(), "Quái vật chạm tia lửa phải bị tiêu diệt");
    }

    @Test
    void testFlameCollisionWithPlayer() {
        // Player is at grid (2, 3) -> pixel (64, 96)
        player.setPosition(64, 96);
        player.hasShield = false;
        player.isFlamePass = false;

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);

        bomb.update();

        // Flame check collision
        gameMap.getFlames().forEach(Flame::checkCollison);

        assertTrue(player.isDestroyed(), "Người chơi không bất tử chạm tia lửa phải bị tiêu diệt");
    }

    @Test
    void testFlameCollisionWithPlayerShielded() {
        // Player is at grid (2, 3) -> pixel (64, 96)
        player.setPosition(64, 96);
        player.hasShield = true; // Shield active
        int initialLife = player.getLife();

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);

        bomb.update();

        // Flame check collision
        gameMap.getFlames().forEach(Flame::checkCollison);

        assertFalse(player.isDestroyed(), "Người chơi bất tử (hasShield = true) chạm tia lửa không bị tiêu diệt");
        assertEquals(initialLife, player.getLife());
    }

    @Test
    void testFlameCollisionWithOtherBombChain() {
        Bomb bomb1 = new Bomb(2, 2, Sprite.BOMB[0]);
        Bomb bomb2 = new Bomb(2, 3, Sprite.BOMB[0]);
        bomb2.setTimetoExplode(100);

        gameMap.getBombs().add(bomb1);
        gameMap.getBombs().add(bomb2);

        bomb1.setTimetoExplode(0);
        bomb1.update();

        // bomb2 is forced to 0
        try {
            java.lang.reflect.Field field = Bomb.class.getDeclaredField("timetoExplode");
            field.setAccessible(true);
            int value = (int) field.get(bomb2);
            assertEquals(0, value, "Quả bom thứ 2 phải bị ép đếm ngược về 0 ngay lập tức (Nổ dây chuyền)");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testFlameCollisionWithRevealedItem() {
        // Revealed item at (2, 3)
        SpeedItem item = new SpeedItem(2, 3, Sprite.powerup_speed);
        item.setBlock(false); // Already revealed
        gameMap.getItems().add(item);
        gameMap.setTile(3, 2, item);

        Bomb bomb = new Bomb(2, 2, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);

        bomb.update();

        // Flame check collision / interactWith
        // Inside Flame.java, it checks:
        // else if (entity instanceof Item) {
        //    if (!entity.isBlock()) {
        //        destroyItemWhenBombExplodes(entity);
        //    }
        // }
        // destroyItemWhenBombExplodes calls entity.remove() and item.delete() if Map.getLevelNumber() <= 2.
        // Let's verify:
        assertTrue(item.isRemoved(), "Ở Level 1/2, vật phẩm lộ diện khi chạm tia lửa phải bị thiêu rụi (remove)");
    }
}
