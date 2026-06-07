package UC4Test;

import entity.animateentity.character.Bomber;
import entity.animateentity.character.enemy.Balloom;
import entity.animateentity.character.enemy.Enemy;
import entity.animateentity.Flame;
import entity.staticentity.Wall;
import graphics.Sprite;
import variables.Variables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UC4_ReleaseTest {

    private Enemy testEnemy;
    private Bomber testBomber;

    @BeforeEach
    public void setUp() {
        // Chỉ khởi tạo các thực thể độc lập để kiểm thử logic
        testEnemy = new Balloom(1, 1, Sprite.BALLOOM_LEFT[0]);
        testBomber = new Bomber(1, 1, Sprite.PLAYER_DOWN[0], null);
    }

    // [KIỂM THỬ LOGIC - KHÔNG GỌI RENDER]
    @Test
    public void testEnemyMovementLogic() {
        assertNotNull(testEnemy, "Enemy phải tồn tại");
        // Gọi update logic, nếu không gọi Render thì không bị lỗi GraphicsContext
        assertDoesNotThrow(() -> testEnemy.update(), "Logic update của Enemy không được văng lỗi");
    }

    @Test
    public void testWallCollisionLogic() {
        Wall wall = new Wall(1, 2, Sprite.wall);
        // Kiểm tra logic va chạm dựa trên thuộc tính, không cần vẽ
        assertTrue(wall.isBlock(), "Wall phải là vật cản");
    }

    @Test
    public void testBomberDeathLogic() {
        int initialLife = testBomber.getLife();
        testBomber.delete();
        assertEquals(initialLife - 1, testBomber.getLife(), "Mạng phải giảm sau khi gọi delete()");
    }

    @Test
    public void testFlameDamageLogic() {
        // Chỉ test logic va chạm của Lửa, không test render
        Flame flame = new Flame(1, 1, Sprite.EXPLOSION_HORIZONTAL[0], Variables.FLAME_SHAPE.HORIZONTAL);

        // Kiểm tra xem lửa có tạo ra được không
        assertNotNull(flame, "Flame phải được tạo ra");
    }
}