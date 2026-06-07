package UC5Test;

import entity.animateentity.character.Bomber;
import entity.staticentity.Portal;
import graphics.Sprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UC5_DevelopmentTest {

    private Bomber testBomber;

    @BeforeEach
    public void setUp() {
        // Khởi tạo nhân vật chính để test các trạng thái phiên chơi
        testBomber = new Bomber(1, 1, Sprite.PLAYER_DOWN[0], null);
    }

    // =====================================
    // TC5.1: Kiểm tra logic trừ mạng (Luồng UC5.3a)
    // =====================================
    @Test
    public void testUC5_3a_BomberRevivalLogic() {
        int initialLife = testBomber.getLife();

        // Hành động: Giả lập nhân vật chịu sát thương chí mạng
        testBomber.delete();

        // Kết quả mong đợi: Hệ thống phát hiện và giảm số mạng hiện tại đi 1
        assertEquals(initialLife - 1, testBomber.getLife(),
                "TC5.1: Mạng của Bomber phải bị trừ 1 khi gọi hàm xử lý cái chết");
    }

    // =====================================
    // TC5.2: Kiểm tra đếm ngược thời gian vật phẩm (Luồng UC5.4)
    // =====================================
    @Test
    public void testUC5_4_BuffTimerCountdown() {
        // Hành động: Giả lập Bomber nhặt được vật phẩm (thời hạn 600 frames)
        testBomber.passWall = true;
        testBomber.passWallTimer = 600;

        // Gọi hàm update() để hệ thống xử lý logic game loop
        testBomber.update();

        // Kết quả mong đợi: Thời gian hiệu lực tự động giảm để chuẩn bị thu hồi
        assertEquals(599, testBomber.passWallTimer,
                "TC5.2: Bộ đếm thời gian (timer) của vật phẩm phải tự động giảm sau mỗi frame");
    }

    // =====================================
    // TC5.3: Kiểm tra khóa Portal khi còn quái (Luồng UC5.5a)
    // =====================================
    @Test
    public void testUC5_5a_PortalRemainsLocked() {
        // Hành động: Đặt cổng Portal lên bản đồ
        Portal testPortal = new Portal(2, 2, Sprite.portal);

        // Kết quả mong đợi: Mặc định cổng chưa được kích hoạt khi mới tạo
        assertFalse(testPortal.isAccessAble(),
                "TC5.3: Cổng Portal phải ở trạng thái khóa (false) khi chưa đủ điều kiện");
    }

    // =====================================
    // TC5.4: Kiểm tra mở Portal khi hết quái (Luồng UC5.6)
    // =====================================
    @Test
    public void testUC5_6_PortalActivation() {
        // Hành động: Khởi tạo Portal và gọi update() trong môi trường map giả lập (size enemies = 0)
        Portal testPortal = new Portal(2, 2, Sprite.portal);
        testPortal.update();

        // Kết quả mong đợi: Portal tự động mở khóa
        assertTrue(testPortal.isAccessAble(),
                "TC5.4: Cổng Portal phải được kích hoạt (true) khi quái vật bị tiêu diệt hết");
    }
}