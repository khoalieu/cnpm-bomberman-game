package UC5Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LevelTest {

    static class GameLevel {

        private int currentLevel = 1;

        public void nextLevel() {
            currentLevel++;
        }

        public int getCurrentLevel() {
            return currentLevel;
        }
    }

    @Test
    void testNextLevel() {

        GameLevel level = new GameLevel();

        level.nextLevel();

        assertEquals(2, level.getCurrentLevel());
    }
}
