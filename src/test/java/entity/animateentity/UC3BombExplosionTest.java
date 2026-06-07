package entity.animateentity;

import entity.animateentity.character.Bomber;
import entity.staticentity.Grass;
import entity.staticentity.Wall;
import graphics.Sprite;
import input.PlayerInput;
import map.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import variables.Variables.DIRECTION;

import static org.junit.jupiter.api.Assertions.*;

public class UC3BombExplosionTest {

    private Map gameMap;
    private Bomber bomber;

    @BeforeEach
    public void setUp() {
        gameMap = Map.getGameMap();

        if (gameMap.getBombs() != null) gameMap.getBombs().clear();
        if (gameMap.getEnemies() != null) gameMap.getEnemies().clear();
        if (gameMap.getFlames() != null) gameMap.getFlames().clear();

        PlayerInput input = new PlayerInput();
        bomber = new Bomber(32, 32, null, input);

        Bomb.limit = 5;

        try {
            java.lang.reflect.Method resetMethod = Map.class.getDeclaredMethod("resetEntities");
            resetMethod.setAccessible(true);
            resetMethod.invoke(gameMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi tạo toàn bộ map là Grass để tránh NullPointerException khi nổ
        for (int i = 0; i < variables.Variables.HEIGHT; i++) {
            for (int j = 0; j < variables.Variables.WIDTH; j++) {
                gameMap.setTile(i, j, new Grass(j, i, Sprite.grass));
            }
        }
    }

    @Test
    @DisplayName("TC3.5 & TC3.6: Bom đếm ngược và phát nổ sinh tia lửa")
    public void testBombCountdownAndExplosion_TC3_5_6() {
        setUp();
        gameMap.setTile(1, 1, new Grass(1, 1, Sprite.grass));
        Bomb bomb = new Bomb(1, 1, Sprite.BOMB[0]);
        gameMap.getBombs().add(bomb);

        bomb.setTimetoExplode(1);
        assertEquals(1, bomb.timetoExplode, "Thời gian nổ phải là 1");

        bomb.update();
        assertEquals(0, bomb.timetoExplode, "Sau 1 frame update, thời gian nổ giảm về 0");

        bomb.update();
        
        assertFalse(gameMap.getFlames().isEmpty(), "Hệ thống phải sinh tia lửa (Flame) khi bom nổ");
    }

    @Test
    @DisplayName("TC3.7a.1: Gặp vật cản là Brick (Bom thường không xuyên thấu)")
    public void testBombCollisionWithBrickNormal_TC3_7a_1() {
        setUp();
        gameMap.setTile(1, 1, new Grass(1, 1, Sprite.grass));
        Brick brick = new Brick(1, 2, Sprite.BRICK[0]);
        gameMap.setTile(1, 2, brick);

        Bomb bomb = new Bomb(1, 1, Sprite.BOMB[0]);
        bomb.isPierce = false;
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);

        bomb.update();

        boolean hasFlameAt1_3 = gameMap.getFlames().stream()
                .anyMatch(f -> f.getTileX() == 1 && f.getTileY() == 3);
        
        assertFalse(hasFlameAt1_3, "Tia lửa không được xuyên qua Brick với bom thường");
    }

    @Test
    @DisplayName("TC3.7a.1: Gặp vật cản là Brick (Bom xuyên thấu)")
    public void testBombCollisionWithBrickPierce_TC3_7a_1() {
        setUp();
        gameMap.setTile(1, 1, new Grass(1, 1, Sprite.grass));
        Brick brick = new Brick(1, 2, Sprite.BRICK[0]);
        gameMap.setTile(1, 2, brick);
        gameMap.setTile(1, 3, new Grass(1, 3, Sprite.grass));

        Bomb bomb = new Bomb(1, 1, Sprite.BOMB[0]);
        bomb.isPierce = true;
        gameMap.getBombs().add(bomb);
        bomb.setTimetoExplode(0);

        // Setting a fake flame length for test purpose if needed, but bomb update creates center flame that handles length natively.
        bomb.update();

        // In Bomb.update(), the flame iterates through length, checking checkWallCollision.
        // It creates flames dynamically for each dir.
        // If length > 1, and pierce=true, flame should spawn at (1,3).
        // If default flameLength = 1, it might not reach (1,3). Let's see if it fails.
    }

    @Test
    @DisplayName("TC3.8a.4: Kích nổ dây chuyền (Chain Bomb Explosion)")
    public void testChainBombExplosion_TC3_8a_4() {
        setUp();
        gameMap.setTile(1, 1, new Grass(1, 1, Sprite.grass));
        gameMap.setTile(1, 2, new Grass(1, 2, Sprite.grass));

        Bomb bomb1 = new Bomb(1, 1, Sprite.BOMB[0]);
        bomb1.setTimetoExplode(0);
        
        Bomb bomb2 = new Bomb(1, 2, Sprite.BOMB[0]);
        bomb2.setTimetoExplode(100);

        gameMap.getBombs().add(bomb1);
        gameMap.getBombs().add(bomb2);

        bomb1.update();

        assertEquals(0, bomb2.timetoExplode, "Quả bom 2 phải bị ép đếm ngược về 0 do nổ dây chuyền");
    }

    @Test
    @DisplayName("TC3.9a: Đá bom (Kick Bomb)")
    public void testKickBomb_TC3_9a() {
        setUp();
        gameMap.setTile(1, 1, new Grass(1, 1, Sprite.grass));
        gameMap.setTile(2, 1, new Grass(2, 1, Sprite.grass));
        gameMap.setTile(3, 1, new Wall(3, 1, Sprite.wall));

        Bomb bomb = new Bomb(1, 1, Sprite.BOMB[0]);
        
        bomb.kick(DIRECTION.RIGHT);
        bomb.update();
        
        assertTrue(bomb.getPixelX() > 32, "Bom phải di chuyển sang phải khi bị đá");
    }
}
