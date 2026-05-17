package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyCoordinateTest {

    // Class giả lập Enemy để test cập nhật tọa độ
    static class Enemy {

        // Tọa độ pixel ban đầu
        private int pixelX = 32;
        private int pixelY = 32;

        // Hàm di chuyển Enemy
        public void move() {

            // Enemy di chuyển thêm 4 pixel
            pixelX += 4;
            pixelY += 4;
        }

        // Lấy tọa độ X
        public int getPixelX() {
            return pixelX;
        }

        // Lấy tọa độ Y
        public int getPixelY() {
            return pixelY;
        }
    }

    @Test
    void testEnemyCoordinateUpdate() {

        // Tạo Enemy mới
        Enemy enemy = new Enemy();

        // Cho Enemy di chuyển
        enemy.move();

        // Kiểm tra tọa độ X có cập nhật đúng không
        assertEquals(36, enemy.getPixelX());

        // Kiểm tra tọa độ Y có cập nhật đúng không
        assertEquals(36, enemy.getPixelY());
    }
}