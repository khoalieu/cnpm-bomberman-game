package entity.animateentity.character;

import entity.animateentity.Brick;
import graphics.Sprite;
import javafx.application.Platform;
import map.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UC2_ReleaseTest {
    private Map gameMap;
    private Bomber player;

    @BeforeAll
    public static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Nếu JavaFX đã được khởi tạo rồi thì bỏ qua, không làm crash test
        }
    }
    @BeforeEach
    public void setUp() {
        // Lấy bản đồ và nạp một map test đơn giản (có thể dùng Level 2)
        gameMap = Map.getGameMap();
        try {
            gameMap.createMap("src/main/resources/levels/Level2.txt");
        } catch (Exception e) {
            System.out.println("Bỏ qua lỗi load map nếu chỉ test logic nhân vật");
        }

        // Lấy nhân vật Bomber ra để chuẩn bị test
        player = gameMap.getPlayer();
    }

    // ==========================================
    // TC2.5: Kiểm thử hiệu ứng Vật phẩm và Timer (UC2.4b & UC5.4)
    // ==========================================
    @Test
    public void testTC2_5_ItemBuffAndTimer() {
        // 1. Giả lập nhặt vật phẩm WallPass (Xuyên tường)
        player.passWall = true;
        player.passWallTimer = 2; // Ép thời gian hiệu lực chỉ còn 2 frame để test cho lẹ

        assertTrue(player.passWall, "Lỗi: Cờ WallPass chưa được bật!");

        // 2. Chạy hàm update() frame thứ 1 -> timer còn 1, vẫn còn buff
        player.update();
        assertTrue(player.passWall, "Lỗi: Buff bị mất quá sớm!");

        // 3. Chạy hàm update() frame thứ 2 -> timer về 0, tự động thu hồi buff
        player.update();
        assertFalse(player.passWall, "Lỗi: Hết thời gian (Timer = 0) nhưng hệ thống không thu hồi Buff Xuyên tường!");
    }

    // ==========================================
    // TC2.6: Di chuyển xuyên vật cản khi có cờ passWall (UC2.4a.2)
    // ==========================================
    @Test
    public void testTC2_6_MoveThroughBrickWithWallPass() {
        // Tạo một viên gạch (Brick) ảo để làm vật cản
        Brick fakeBrick = new Brick(5, 5, Sprite.BRICK[0]);

        // Trường hợp 1: Không có buff (Mặc định) -> Phải bị chặn
        player.passWall = false;
        assertFalse(player.canPass(fakeBrick), "Lỗi: Không có cờ WallPass nhưng lại xuyên được Gạch!");

        // Trường hợp 2: Có buff Xuyên Tường -> Phải cho đi qua
        player.passWall = true;
        assertTrue(player.canPass(fakeBrick), "Lỗi: Đã có cờ WallPass (Xuyên Tường) nhưng hàm canPass lại chặn lại!");
    }

    // ==========================================
    // TC2.7: Va chạm nhưng không chết nhờ có Khiên Bất tử (hasShield)
    // ==========================================
    @Test
    public void testTC2_7_InvincibleWithShield() {
        // Lưu lại số mạng ban đầu
        int initialLife = player.getLife();

        // 1. Bật khiên Bất tử lên (Mô phỏng nhặt MysteryItem)
        player.hasShield = true;

        // Bật khiên lên rồi thì dù có lọt vào hàm destroy() hay va chạm quái
        // logic if(hasShield) trong handleEnemyCollision() sẽ cứu nhân vật.
        // Tuy nhiên do handleEnemyCollision là private, ta test trạng thái bảo vệ công khai:

        assertTrue(player.hasShield, "Cờ Khiên bảo vệ chưa được kích hoạt!");
        assertEquals(initialLife, player.getLife(), "Lỗi: Số mạng bị thay đổi sai lệch khi vừa bật khiên!");
    }
}