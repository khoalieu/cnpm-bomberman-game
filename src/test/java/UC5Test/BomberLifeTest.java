package UC5Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BomberLifeTest {

    static class Bomber {

        private int lives;
        private boolean alive;

        public Bomber(int lives) {
            this.lives = lives;
            this.alive = true;
        }

        public void hit() {

            lives--;

            if (lives <= 0) {
                alive = false;
            }
        }

        public int getLives() {
            return lives;
        }

        public boolean isAlive() {
            return alive;
        }
    }

    @Test
    void testBomberLoseLife() {

        Bomber bomber = new Bomber(3);

        bomber.hit();

        assertEquals(2, bomber.getLives());
    }

    @Test
    void testBomberDead() {

        Bomber bomber = new Bomber(1);

        bomber.hit();

        assertFalse(bomber.isAlive());
    }
}
