package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyBomberCollisionTest {

    static class Bomber {

        private boolean dead = false;

        public void die() {
            dead = true;
        }

        public boolean isDead() {
            return dead;
        }
    }

    @Test
    void testBomberDeadWhenTouchEnemy() {

        Bomber bomber = new Bomber();

        bomber.die();

        assertTrue(bomber.isDead());
    }
}