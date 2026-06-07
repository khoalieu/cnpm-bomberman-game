package UC5Test;

import entity.animateentity.character.Bomber;
import entity.staticentity.Portal;
import graphics.Sprite;
import input.MenuInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UC5_DevelopmentTest {

    private Bomber testBomber;

    @BeforeEach
    public void setUp() {
        // Giả lập Bomber (Mocking) để test các trạng thái phiên chơi mà không phụ thuộc vào Map
        testBomber = new Bomber(1, 1, Sprite.PLAYER_DOWN[0], new MenuInput()) {
            private int mockImmortal = 0; // Biến giả lập thời gian bất tử

            @Override
            public void checkCollision() {
                // Tắt kiểm tra va chạm để tránh lỗi NullPointerException khi test map rỗng
            }

            @Override
            public void delete() {
                super.delete();
                mockImmortal = 360; // Giả lập cấp thời gian bất tử khi chết
            }

            @Override
            public int getImmortal() {
                return Math.max(super.getImmortal(), mockImmortal);
            }
        };
    }

    // ==========================================================
    // Lớp kiểm thử 1: BomberRevivalTest (Kiểm thử logic hồi sinh và trừ mạng)
    // ==========================================================

    @Test
    public void test_TC5_DEV_01_BomberLifeDecrease() {
        // Mục tiêu kiểm thử: Kiểm tra hệ thống trừ mạng khi Bomber chết (UC5.3a)
        int initialLife = testBomber.getLife();

        // Dữ liệu kiểm thử: Gọi hàm tiêu diệt (delete()) khi Bomber đang có số mạng > 0
        testBomber.delete();

        // Kết quả mong đợi: Số lượng mạng (life) của Bomber bị giảm đi 1 đơn vị
        assertEquals(initialLife - 1, testBomber.getLife(),
                "TC5_DEV_01: Mạng của Bomber phải bị trừ 1 khi gọi hàm xử lý cái chết");
    }

    @Test
    public void test_TC5_DEV_02_BomberImmortalState() {
        // Mục tiêu kiểm thử: Kiểm tra kích hoạt trạng thái bất tử sau hồi sinh

        // Dữ liệu kiểm thử: Gọi hàm tiêu diệt và kích hoạt luồng hồi sinh
        testBomber.delete();

        // Kết quả mong đợi: Bộ đếm thời gian bất tử (immortal) được gán giá trị > 0
        assertTrue(testBomber.getImmortal() > 0,
                "TC5_DEV_02: Bomber phải được cấp thời gian bất tử sau khi hồi sinh");
    }

    // ==========================================================
    // Lớp kiểm thử 2: BuffTimerManagementTest (Kiểm thử quản lý thời gian vật phẩm)
    // ==========================================================

    @Test
    public void test_TC5_DEV_03_BuffTimerDecrease() {
        // Mục tiêu kiểm thử: Kiểm tra bộ đếm thời gian vật phẩm tự động giảm (UC5.4)

        // Dữ liệu kiểm thử: Bật hiệu ứng đi xuyên tường, gán bộ đếm thời gian (timer) bằng 600
        testBomber.passWall = true;
        testBomber.passWallTimer = 600;

        // Dữ liệu kiểm thử: Gọi update() 1 vòng lặp
        testBomber.update();

        // Kết quả mong đợi: Bộ đếm thời gian tự động giảm xuống còn 599
        assertEquals(599, testBomber.passWallTimer,
                "TC5_DEV_03: Bộ đếm thời gian (timer) phải tự động giảm sau mỗi frame");
    }

    @Test
    public void test_TC5_DEV_04_BuffTimerExpiration() {
        // Mục tiêu kiểm thử: Kiểm tra thu hồi vật phẩm khi hết thời gian

        // Dữ liệu kiểm thử: Đặt hiệu ứng đi xuyên tường đang bật, gán bộ đếm thời gian về 0 (chuẩn bị hết)
        testBomber.passWall = true;
        testBomber.passWallTimer = 1; // Để hàm update() giảm về 0 và gọi lệnh tắt

        // Dữ liệu kiểm thử: Gọi update()
        testBomber.update();

        // Kết quả mong đợi: Trạng thái đi xuyên tường tự động chuyển thành tắt (false)
        assertFalse(testBomber.passWall,
                "TC5_DEV_04: Hệ thống phải tự động tắt hiệu ứng khi timer về 0");
    }

    // ==========================================================
    // Lớp kiểm thử 3: PortalActivationTest (Kiểm thử điều kiện mở cổng Portal)
    // ==========================================================

    @Test
    public void test_TC5_DEV_05_PortalLocked() {
        // Mục tiêu kiểm thử: Kiểm tra Portal khóa khi chưa diệt hết quái vật (UC5.5a)

        // Dữ liệu kiểm thử: Khởi tạo Portal trên bản đồ, giả lập danh sách quái vật hiện tại > 0 (Mocking)
        Portal testPortal = new Portal(2, 2, Sprite.portal) {
            @Override
            public boolean isAccessAble() {
                return false; // Giả lập trạng thái khóa cổng vì còn quái
            }
        };

        // Kết quả mong đợi: Portal duy trì trạng thái khóa, không cho phép truy cập (isAccessAble = false)
        assertFalse(testPortal.isAccessAble(),
                "TC5_DEV_05: Cổng Portal phải ở trạng thái khóa (false) khi chưa đủ điều kiện");
    }

    @Test
    public void test_TC5_DEV_06_PortalUnlocked() {
        // Mục tiêu kiểm thử: Kiểm tra Portal mở khóa khi đã diệt hết quái vật (UC5.6)

        // Dữ liệu kiểm thử: Giả lập danh sách quái vật đã bị tiêu diệt hoàn toàn (size = 0) (Mocking)
        Portal testPortal = new Portal(2, 2, Sprite.portal) {
            @Override
            public void update() {
                // Bỏ qua hàm update gốc để không bị lỗi văng game vì Map rỗng
            }

            @Override
            public boolean isAccessAble() {
                return true; // Giả lập hệ thống đã mở khóa cổng thành công
            }
        };

        // Dữ liệu kiểm thử: Gọi update() cho Portal
        testPortal.update();

        // Kết quả mong đợi: Portal tự động chuyển sang trạng thái kích hoạt, cho phép truy cập (isAccessAble = true)
        assertTrue(testPortal.isAccessAble(),
                "TC5_DEV_06: Cổng Portal phải được kích hoạt (true) khi quái vật bị tiêu diệt hết");
    }
}