package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyDestroyedTest {

    static class Enemy {

        private boolean destroyed = false;

        public void destroy() {
            destroyed = true;
        }

        public boolean isDestroyed() {
            return destroyed;
        }
    }

    @Test
    void testEnemyDestroyedByFlame() {

        Enemy enemy = new Enemy();

        enemy.destroy();

        assertTrue(enemy.isDestroyed());
    }
}