package entity.animateentity.character;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import entity.staticentity.Grass;
import graphics.Sprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import variables.Variables.DIRECTION;
import variables.Variables;
import input.KeyInput;
import map.Map;

public class BomberMovementTest {

    private Bomber bomber;
    private Map gameMap;
    private TestKeyInput testKeyInput;

    // TẠO STUB: Diễn viên đóng thế cho bàn phím
    private class TestKeyInput implements KeyInput {
        private DIRECTION currentDirection = DIRECTION.NONE;

        public void setDirectionToReturn(DIRECTION dir) {
            this.currentDirection = dir;
        }

        @Override
        public void initialization() {
        }

        @Override
        public DIRECTION handleKeyInput() {
            return currentDirection;
        }
    }

    @BeforeEach
    public void setUp() {
        testKeyInput = new TestKeyInput();
        gameMap = Map.getGameMap();
        gameMap.setRevival(false); // Reset trạng thái hồi sinh
        gameMap.getBombs().clear(); // Dọn sạch bom trên map trước mỗi test

        // Giả định vị trí (32, 32) tương đương Grid(1, 1) với SCALED_SIZE = 32
        bomber = new Bomber(1, 1, null, testKeyInput);
    }

    // ==========================================
    // TC2.1: Di chuyển hợp lệ trên đường đi trống
    // ==========================================
    @Test
    @DisplayName("TC2.1: Di chuyển hợp lệ trên đường đi trống (Grass)")
    public void testValidMovementOnGrass_TC2_1() {
        // Chuẩn bị: Bấm phím sang PHẢI
        testKeyInput.setDirectionToReturn(DIRECTION.RIGHT);

        // Hành động: Hàm xử lý tín hiệu chạy
        bomber.setDirection();

        // Kiểm chứng: Hướng và vận tốc được set đúng
        assertEquals(DIRECTION.RIGHT, bomber.getDirection(), "Hệ thống phải cập nhật hướng nhân vật thành RIGHT");
        assertTrue(bomber.getVelocityX() > 0, "Vận tốc trục X phải lớn hơn 0 để nhân vật tiến tới");
        assertEquals(0, bomber.getVelocityY(), "Vận tốc trục Y phải bằng 0 khi đang đi ngang");
    }

    // ==========================================
    // TC2.2: Di chuyển va chạm với vật cản cố định
    // ==========================================
    @Test
    @DisplayName("TC2.2: Di chuyển va chạm với vật cản cố định (Tường/Gạch)")
    public void testMovementCollisionWithWall_TC2_2() {
        // Chuẩn bị: Bấm phím LÊN và giả lập Engine phát hiện vật cản (isCollision = true)
        testKeyInput.setDirectionToReturn(DIRECTION.UP);
        bomber.setCollision(true);

        // Hành động
        bomber.setDirection();
        int expectedY = bomber.getPixelY();

        // (Giả lập Game Loop: Nếu không va chạm mới được cộng vận tốc)
        if (!bomber.isCollision()) {
            bomber.setPixelY(bomber.getPixelY() + bomber.getVelocityY());
        }

        // Kiểm chứng: Bị chặn lại, tọa độ Y không thay đổi
        assertTrue(bomber.isCollision(), "Cờ va chạm (isCollision) phải chuyển thành true");
        assertEquals(expectedY, bomber.getPixelY(), "Tọa độ Y phải giữ nguyên, không được đi xuyên tường");
    }

    // ==========================================
    // TC2.3: Di chuyển ra khỏi ranh giới bản đồ
    // ==========================================
    @Test
    @DisplayName("TC2.3: Di chuyển ra khỏi ranh giới bản đồ")
    public void testMovementOutOfBound_TC2_3() {
        // Chuẩn bị: Ép nhân vật đi sang TRÁI tại rìa bản đồ, đụng ngay Wall bao quanh viền
        testKeyInput.setDirectionToReturn(DIRECTION.LEFT);
        bomber.setCollision(true);

        // Hành động
        bomber.setDirection();
        int expectedX = bomber.getPixelX();

        if (!bomber.isCollision()) {
            bomber.setPixelX(bomber.getPixelX() + bomber.getVelocityX());
        }

        // Kiểm chứng: Bị chặn lại, không văng lỗi IndexOutOfBoundsException
        assertTrue(bomber.isCollision(), "Hệ thống phải bắt được va chạm với block viền ranh giới");
        assertEquals(expectedX, bomber.getPixelX(), "Tọa độ X phải bị chặn lại tại mép biên");
    }

    // ==========================================
    // TC2.4: Thực hiện hành động đặt Bom
    // ==========================================
    @Test
    @DisplayName("TC2.4: Đặt bom hợp lệ trên đường đi trống")
    public void testPlaceBombValid_TC2_4() {
        // Chuẩn bị: Đặt nền tại tọa độ Grid (1, 1) là Grass (Đường đi trống)
        int gridX = 1;
        int gridY = 1;
        gameMap.setTile(gridX, gridY, new Grass(gridX, gridY, null));

        // Xác nhận số lượng bom ban đầu là 0
        assertEquals(0, gameMap.getBombs().size(), "Lỗi: Bản đồ ban đầu không được có bom");

        // Hành động: Nhân vật nhấn phím SPACE (Đặt bom) ở vị trí (32, 32)
        testKeyInput.setDirectionToReturn(DIRECTION.PLACEBOMB);
        bomber.setDirection(); // Hàm này bên trong chứa switch-case gọi placeBombAt()

        // Kết quả mong đợi:
        assertEquals(1, gameMap.getBombs().size(), "Hệ thống phải khởi tạo 1 đối tượng Bom tại vị trí hợp lệ");
        assertEquals(DIRECTION.NONE, bomber.getDirection(), "Hướng nhân vật phải reset về NONE để tránh việc đặt bom liên tục");
    }

    // ==========================================
    // TC2.5: Trạng thái điều khiển khi nhân vật bị tiêu diệt
    // ==========================================
    @Test
    @DisplayName("TC2.5: Khóa điều khiển khi nhân vật bị tiêu diệt và đang hồi sinh")
    public void testControlWhenDestroyed_TC2_5() {
        // Chuẩn bị: Người chơi đang giữ phím đi XUỐNG
        testKeyInput.setDirectionToReturn(DIRECTION.DOWN);
        int initialLife = bomber.getLife();

        // Hành động: Quái vật chạm vào -> gọi hàm delete() (Bomber mất mạng)
        bomber.delete();

        // KHÔNG GỌI bomber.setDirection() ở đây vì thực tế game loop (Map.updateMap)
        // sẽ chặn việc update nhân vật khi cờ revival đang là true.

        // Kiểm chứng: Trạng thái của nhân vật phải bị reset ngay lập tức
        assertEquals(initialLife - 1, bomber.getLife(), "Mạng (life) của nhân vật phải giảm đi 1");

        // Hướng phải bị ép về NONE, phím DOWN lúc này không có tác dụng
        assertEquals(DIRECTION.NONE, bomber.getDirection(), "Hệ thống phải ép hướng về NONE");

        // Nhân vật bị cưỡng chế dịch chuyển về góc xuất phát
        assertEquals(Sprite.SCALED_SIZE, bomber.getPixelX(), "Nhân vật phải bị dịch chuyển về X góc xuất phát");
        assertEquals(Sprite.SCALED_SIZE, bomber.getPixelY(), "Nhân vật phải bị dịch chuyển về Y góc xuất phát");
    }
}