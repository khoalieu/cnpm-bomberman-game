package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyCollisionTest {

    // Class giả lập Enemy để test va chạm
    static class Enemy {

        // Biến lưu trạng thái va chạm
        private boolean collision = false;

        // Hàm mô phỏng Enemy va chạm vật cản
        public void collideWall() {

            // Khi va chạm thì collision = true
            collision = true;
        }

        // Trả về trạng thái va chạm
        public boolean isCollision() {
            return collision;
        }
    }

    @Test
    void testEnemyCollisionWithWall() {

        // Tạo Enemy mới
        Enemy enemy = new Enemy();

        // Mô phỏng va chạm với Wall
        enemy.collideWall();

        // Kiểm tra hệ thống có phát hiện va chạm không
        assertTrue(enemy.isCollision());
    }
}   