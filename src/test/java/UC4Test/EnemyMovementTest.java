package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyMovementTest {

    // Class giả lập Enemy để test chức năng di chuyển
    static class Enemy {

        // Hướng di chuyển mặc định
        private String direction = "DOWN";

        // Hàm update() mô phỏng AI đổi hướng di chuyển
        public void update() {

            // Enemy đổi hướng sang LEFT
            direction = "LEFT";
        }

        // Trả về hướng hiện tại của Enemy
        public String getDirection() {
            return direction;
        }
    }

    @Test
    void testEnemyDirectionUpdate() {

        // Tạo Enemy mới
        Enemy enemy = new Enemy();

        // Gọi update() để cập nhật hướng di chuyển
        enemy.update();

        // Kiểm tra Enemy có đổi hướng đúng không
        assertEquals("LEFT", enemy.getDirection());
    }
}