package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyMovementTest {

    static class Enemy {

        private String direction = "DOWN";

        public void update() {
            direction = "LEFT";
        }

        public String getDirection() {
            return direction;
        }
    }

    @Test
    void testEnemyDirectionUpdate() {

        Enemy enemy = new Enemy();

        enemy.update();

        assertEquals("LEFT", enemy.getDirection());
    }
}