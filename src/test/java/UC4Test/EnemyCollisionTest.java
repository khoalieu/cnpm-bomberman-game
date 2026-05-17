package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyCollisionTest {

    static class Enemy {

        private boolean collision = false;

        public void collideWall() {
            collision = true;
        }

        public boolean isCollision() {
            return collision;
        }
    }

    @Test
    void testEnemyCollisionWithWall() {

        Enemy enemy = new Enemy();

        enemy.collideWall();

        assertTrue(enemy.isCollision());
    }
}