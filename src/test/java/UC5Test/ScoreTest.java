package UC5Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreTest {

    static class ScoreManager {

        private int score = 0;

        public void addScore(int value) {
            score += value;
        }

        public int getScore() {
            return score;
        }
    }

    @Test
    void testIncreaseScore() {

        ScoreManager manager = new ScoreManager();

        manager.addScore(100);

        assertEquals(100, manager.getScore());
    }

    @Test
    void testIncreaseMultipleTimes() {

        ScoreManager manager = new ScoreManager();

        manager.addScore(100);
        manager.addScore(200);

        assertEquals(300, manager.getScore());
    }
}
