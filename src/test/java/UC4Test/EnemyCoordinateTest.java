package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyCoordinateTest {

    static class Enemy {

        private int pixelX = 32;
        private int pixelY = 32;

        public void move() {
            pixelX += 4;
            pixelY += 4;
        }

        public int getPixelX() {
            return pixelX;
        }

        public int getPixelY() {
            return pixelY;
        }
    }

    @Test
    void testEnemyCoordinateUpdate() {

        Enemy enemy = new Enemy();

        enemy.move();

        assertEquals(36, enemy.getPixelX());
        assertEquals(36, enemy.getPixelY());
    }
}