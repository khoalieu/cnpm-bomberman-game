package UC5Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimerTest {

    static class GameTimer {

        private int time;

        public GameTimer(int time) {
            this.time = time;
        }

        public void decreaseTime() {
            if (time > 0) {
                time--;
            }
        }

        public int getTime() {
            return time;
        }

        public boolean isGameOver() {
            return time <= 0;
        }
    }

    @Test
    void testTimerDecrease() {

        GameTimer timer = new GameTimer(100);

        timer.decreaseTime();

        assertEquals(99, timer.getTime());
    }

    @Test
    void testGameOverWhenTimeZero() {

        GameTimer timer = new GameTimer(1);

        timer.decreaseTime();

        assertTrue(timer.isGameOver());
    }
}