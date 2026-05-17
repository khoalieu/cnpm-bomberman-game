package UC4Test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EnemyBomberCollisionTest {

    // Class giả lập Bomber
    static class Bomber {

        // Trạng thái sống/chết
        private boolean dead = false;

        // Hàm xử lý Bomber chết
        public void die() {

            // Chuyển trạng thái chết
            dead = true;
        }

        // Trả về trạng thái Bomber
        public boolean isDead() {
            return dead;
        }
    }

    @Test
    void testBomberDeadWhenTouchEnemy() {

        // Tạo Bomber mới
        Bomber bomber = new Bomber();

        // Mô phỏng Bomber va chạm Enemy
        bomber.die();

        // Kiểm tra Bomber đã chết chưa
        assertTrue(bomber.isDead());
    }
}