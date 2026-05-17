package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyDestroyedTest {

    // Class giả lập Enemy
    static class Enemy {

        // Trạng thái bị tiêu diệt
        private boolean destroyed = false;

        // Hàm xử lý Enemy bị Flame tiêu diệt
        public void destroy() {

            // Chuyển trạng thái destroyed
            destroyed = true;
        }

        // Trả về trạng thái Enemy
        public boolean isDestroyed() {
            return destroyed;
        }
    }

    @Test
    void testEnemyDestroyedByFlame() {

        // Tạo Enemy mới
        Enemy enemy = new Enemy();

        // Mô phỏng Flame tiêu diệt Enemy
        enemy.destroy();

        // Kiểm tra Enemy đã bị destroy chưa
        assertTrue(enemy.isDestroyed());
    }
}